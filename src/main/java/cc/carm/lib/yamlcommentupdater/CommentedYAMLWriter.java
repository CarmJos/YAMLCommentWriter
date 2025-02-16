package cc.carm.lib.yamlcommentupdater;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CommentedYAMLWriter {

    protected final @NotNull String separator;
    protected final int indentSize;
    protected final boolean commentEmpty;

    public CommentedYAMLWriter(@NotNull String separator, int indentSize, boolean commentEmpty) {
        this.separator = separator;
        this.indentSize = indentSize;
        this.commentEmpty = commentEmpty;
    }

    public String saveToString(@NotNull CommentedSection source) throws IOException {
        StringWriter writer = new StringWriter();
        write(source, new BufferedWriter(writer));
        return writer.toString();
    }

    public void saveToFile(@NotNull CommentedSection source, @NotNull File file) throws IOException {
        StringWriter writer = new StringWriter();
        write(source, new BufferedWriter(writer));
        Files.write(file.toPath(), writer.toString().getBytes(StandardCharsets.UTF_8));
    }

    protected void write(@NotNull CommentedSection source, @NotNull BufferedWriter writer) throws IOException {
        // File Header
        if (writeComments(writer, source.getHeaderComments(null), "", false, true)) {
            writer.newLine();
        }

        for (String rootKey : source.getKeys(null, false)) {
            write(source, writer, rootKey);
            writer.newLine();
            writer.newLine();
        }

        // File Footer
        writeComments(writer, source.getFooterComments(null), "", true, false);

        writer.close();
    }

    protected void write(@NotNull CommentedSection source, @NotNull BufferedWriter writer, @NotNull String fullKey) throws IOException {
        Object currentValue = source.getValue(fullKey);
        String indents = indents(fullKey);

        // header for current key
        writeComments(writer, source.getHeaderComments(fullKey), indents, false, true);

        String[] splitFullKey = fullKey.split(Pattern.quote(separator));
        String trailingKey = splitFullKey[splitFullKey.length - 1];

        String inlineComment = source.getInlineComment(fullKey);

        Set<String> innerKeys = source.getKeys(fullKey, false);
        if (innerKeys == null) {
            String yaml;
            if (currentValue == null) {
                yaml = (commentEmpty ? "# " : "") + fullKey + ": ";
            } else {
                yaml = source.serializeValue(trailingKey, currentValue);
                yaml = yaml.substring(0, yaml.length() - 1);
            }

            if (inlineComment != null && !inlineComment.isEmpty()) {
                if (yaml.contains("\n")) {
                    // For multi-line section
                    // InlineComment must be added to the end of the first line
                    String[] splitLine = yaml.split("\n", 2);
                    yaml = splitLine[0] + " # " + inlineComment + "\n" + splitLine[1];
                } else {
                    yaml += " # " + inlineComment;
                }
            }

            writer.write(indents + yaml.replace("\n", "\n" + indents));
        } else {
            if (commentEmpty && innerKeys.isEmpty()) {
                writer.write("# ");
            }

            writer.write(indents + trailingKey + ":");
            if (inlineComment != null && !inlineComment.isEmpty()) {
                writer.write(" # " + inlineComment);
            }
            if (innerKeys.isEmpty()) {
                writer.write(" {}");
            }
            writer.newLine();

            ArrayList<String> strings = new ArrayList<>(innerKeys);
            for (int i = 0; i < strings.size(); i++) {
                String innerKey = strings.get(i);
                write(source, writer, fullKey + "." + innerKey);
                if (i != strings.size()-1) {
                    writer.newLine();
                }
            }
        }

        // footer for current key
        writeComments(writer, source.getFooterComments(fullKey), indents, true, false);
    }

    protected boolean writeComments(@NotNull BufferedWriter writer, @Nullable List<String> comments, @NotNull String indents,
                                    boolean newLineBefore, boolean newLineAfter) throws IOException {
        String text = buildComments(comments, indents);
        if (text == null) return false;

        if (newLineBefore) writer.newLine();
        writer.write(text);
        if (newLineAfter) writer.newLine();
        return true;
    }

    protected String repeat(@NotNull String str, int count) {
        return IntStream.range(0, count).mapToObj(i -> str).collect(Collectors.joining());
    }

    protected String indents(@NotNull String key) {
        String[] args = key.split(Pattern.quote(this.separator));
        return repeat(" ", (args.length - 1) * indentSize);
    }

    protected @Nullable String buildComments(@Nullable List<String> comments,
                                             @NotNull String indents) {
        if (comments == null || comments.isEmpty()) return null;
        StringJoiner joiner = new StringJoiner(System.lineSeparator());
        for (String comment : comments) {
            if (comment.isEmpty()) joiner.add("");
            else joiner.add(indents + "# " + comment);
        }
        return joiner.toString();
    }

}
