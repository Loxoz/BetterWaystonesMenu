package fr.loxoz.mods.betterwaystonesmenu.compat.tooltip;

public record TooltipOffset(int x, int y) {
    public TooltipOffset add(TooltipOffset other) {
        return new TooltipOffset(x() + other.x(), y() + other.y());
    }
}
