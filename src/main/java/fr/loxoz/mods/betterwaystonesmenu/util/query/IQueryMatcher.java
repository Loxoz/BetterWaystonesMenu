package fr.loxoz.mods.betterwaystonesmenu.util.query;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IQueryMatcher {
    @NotNull String getQuery();
    void setQuery(@NotNull String query);

    default boolean isEmpty() { return getQuery().isEmpty(); }
    default boolean isBlank() { return getQuery().isBlank(); }

    boolean match(@Nullable String string);
    float matchScore(@Nullable String string);
}
