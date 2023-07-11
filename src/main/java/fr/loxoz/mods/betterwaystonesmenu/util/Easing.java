package fr.loxoz.mods.betterwaystonesmenu.util;

public class Easing {
    public static double easeOutQuad(double v) {
        return 1 - (1 - v) * (1 - v);
    }
}
