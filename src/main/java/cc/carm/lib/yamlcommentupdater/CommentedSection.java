package cc.carm.lib.yamlcommentupdater;


import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public interface CommentedSection {
    
    String serializeValue(@NotNull String key, @NotNull Object value);

    default @NotNull Set<String> getKeys() {
        return getKeys(null, true);
    }

    @Contract("null,_ -> !null;!null,_ -> _")
    @Nullable Set<String> getKeys(@Nullable String sectionKey, boolean deep);

    @Nullable Object getValue(@NotNull String key);

    @Nullable String getInlineComment(@NotNull String key);

    @Nullable
    List<String> getHeaderComments(@Nullable String key);

    @Nullable
    List<String> getFooterComments(@Nullable String key);

}
