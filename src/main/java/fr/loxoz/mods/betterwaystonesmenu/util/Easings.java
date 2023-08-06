package fr.loxoz.mods.betterwaystonesmenu.util;

public class Easings {
    public static final Easing LINEAR = v -> v;
    public static final Easing EASE_OUT_QUAD = v -> 1 - (1 - v) * (1 - v);
}
