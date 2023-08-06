package fr.loxoz.mods.betterwaystonesmenu.compat.widget;

/**
 * Implements methods from Minecraft 1.19.3+ widgets
 */
public interface WidgetCompat {
    int getX();
    void setX(int var1);

    int getY();
    void setY(int var1);

    int getWidth();
    int getHeight();

    default void setPosition(int x, int y) {
        setX(x);
        setY(y);
    }
}
