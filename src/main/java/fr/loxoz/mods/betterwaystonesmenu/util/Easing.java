package fr.loxoz.mods.betterwaystonesmenu.util;

@FunctionalInterface
public interface Easing {
    double getProgress(double v);

    default double getProgressSafe(double v) {
        return getProgress(Math.min(Math.max(v, 0), 1));
    }
}
