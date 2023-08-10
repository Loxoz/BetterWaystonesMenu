package fr.loxoz.mods.betterwaystonesmenu.util.query;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;

public class PartsQueryMatcher implements IQueryMatcher {
    private @NotNull String query;
    private String[] queryParts = null;

    public PartsQueryMatcher() { this(""); }
    public PartsQueryMatcher(@NotNull String query) {
        Objects.requireNonNull(query);
        this.query = query;
    }

    public @NotNull String getQuery() { return query; }
    public void setQuery(@NotNull String query) {
        Objects.requireNonNull(query);
        this.query = query;
        queryParts = null;
    }
    public String[] getQueryParts() {
        if (queryParts == null) {
            queryParts = Arrays.stream(query.trim().split("[\\u202F\\u00A0\\s]+")).map(String::toLowerCase).toArray(String[]::new);
        }
        return queryParts;
    }

    // TODO: make search ignore accents and special letters ("é" -> "e")

    public boolean match(@Nullable String string) {
        if (string == null) return false;
        String lowerStr = string.toLowerCase();
        for (var part : getQueryParts()) {
            if (!lowerStr.contains(part)) return false;
        }
        return true;
    }

    @Override
    public float matchScore(@Nullable String string) {
        if (string == null || string.isEmpty()) return 0;
        String lowerStr = string.toLowerCase();
        int matchCount = 0;
        for (var part : getQueryParts()) {
            if (!lowerStr.contains(part)) return 0;
            matchCount += part.length();
        }
        if (matchCount < 1) return 0;
        return (float) matchCount / string.length();
    }
}
