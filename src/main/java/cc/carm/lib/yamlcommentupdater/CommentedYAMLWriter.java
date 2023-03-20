package cc.carm.lib.yamlcommentupdater;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CommentedYAMLWriter {

    protected static final @NotNull String SEPARATOR = ".";

    public static void writeWithComments(@NotNull CommentedYAML source, @NotNull File file) throws IOException {
        StringWriter writer = new StringWriter();
        writeWithComments(source, new BufferedWriter(writer));
        Files.write(file.toPath(), writer.toString().getBytes(StandardCharsets.UTF_8));
    }

    protected static void writeWithComments(@NotNull CommentedYAML source, @NotNull BufferedWriter writer) throws IOException {
        String configHeader = buildHeaderComments(source, null, "");
        if (configHeader != null) writer.write(configHeader);

        boolean lastNewline = configHeader != null;
        for (String fullKey : source.getKeys()) {
            Object currentValue = source.getValue(fullKey);

            String indents = buildIndents(fullKey);
            if (indents.length() == 0) {
                writer.write(System.lineSeparator() + (lastNewline ? "" : System.lineSeparator()));
            }

            String headerComments = buildHeaderComments(source, fullKey, indents);
            String inlineComment = source.getInlineComment(fullKey);

            if (headerComments != null) writer.write(headerComments);

            String[] splitFullKey = fullKey.split("[" + SEPARATOR + "]");
            String trailingKey = splitFullKey[splitFullKey.length - 1];

            Set<String> innerKeys = source.getKeys(fullKey, false);
            if (innerKeys != null) {
                writer.write(indents + trailingKey + ":");
                if (inlineComment != null && inlineComment.length() > 0) {
                    writer.write(" # " + inlineComment);
                }
                if (innerKeys.isEmpty()) {
                    writer.write(" {}");
                    if (indents.length() == 0) writer.newLine();
                }
                writer.newLine();
                continue;
            }

            String yaml;
            if (currentValue == null) {
                yaml = fullKey + SEPARATOR + " ";
            } else {
                yaml = source.serializeValue(trailingKey, currentValue);
                yaml = yaml.substring(0, yaml.length() - 1);
            }

            if (inlineComment != null && inlineComment.length() > 0) {
                if (yaml.contains("\n")) {
                    // section为多行内容，需要 InlineComment 加在首行末尾
                    String[] splitLine = yaml.split("\n", 2);
                    yaml = splitLine[0] + " # " + inlineComment + "\n" + splitLine[1];
                } else {
                    // 其他情况下就直接加载后面就好。
                    yaml += " # " + inlineComment;
                }
            }

            lastNewline = indents.length() != 0;
            writer.write(indents + yaml.replace("\n", "\n" + indents));
            if (lastNewline) writer.newLine();
        }

        writer.close();
    }

    protected static String buildIndents(@NotNull String key) {
        String[] splitKey = key.split("[" + SEPARATOR + "]");
        return IntStream.range(1, splitKey.length).mapToObj(i -> "  ").collect(Collectors.joining());
    }

    protected static @Nullable String buildHeaderComments(@NotNull CommentedYAML source,
                                                          @Nullable String path, @NotNull String indents) {
        List<String> comments = source.getHeaderComments(path);
        if (comments == null || comments.size() == 0) return null;

        StringJoiner joiner = new StringJoiner(System.lineSeparator());
        for (String comment : comments) {
            if (comment.length() == 0) joiner.add("");
            else joiner.add(indents + "# " + comment);
        }
        return joiner + System.lineSeparator();
    }

}
