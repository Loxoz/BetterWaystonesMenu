package fr.loxoz.mods.betterwaystonesmenu.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;

public class QueryMatcher {
    private @NotNull String query;
    private String[] queryParts = null;

    public QueryMatcher(@NotNull String query) {
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
            queryParts = Arrays.stream(query.trim().split(" +")).map(String::toLowerCase).toArray(String[]::new);
        }
        return queryParts;
    }

    public boolean isEmpty() {
        return query.isEmpty();
    }

    public boolean isBlank() {
        return query.isBlank();
    }

    public boolean match(@Nullable String string) {
        if (string == null) return false;
        String lowerStr = string.toLowerCase();
        for (var part : getQueryParts()) {
            if (!lowerStr.contains(part)) return false;
        }
        return true;
    }
}
