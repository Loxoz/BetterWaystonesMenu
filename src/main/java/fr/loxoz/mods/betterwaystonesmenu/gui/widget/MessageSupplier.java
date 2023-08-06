package fr.loxoz.mods.betterwaystonesmenu.gui.widget;

import net.minecraft.network.chat.Component;

@FunctionalInterface
public interface MessageSupplier<T> {
    Component getMessageOf(T value);
}
