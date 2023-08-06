package fr.loxoz.mods.betterwaystonesmenu.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
            queryParts = query.trim().split(" +");
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
        for (var part : getQueryParts()) {
            if (!string.contains(part)) return false;
        }
        return true;
    }
}
