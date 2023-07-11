package fr.loxoz.mods.betterwaystonesmenu.compat;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

/**
 * I usually make this utility file for large projects that could support multiple versions of Minecraft to make
 * maintenance easier across multiple versions of Minecraft and also because I like the Minecraft 1.19+ unique Text
 * class for all text components.
 */
public class CText {
    public static MutableComponent literal(String string) {
        return new TextComponent(string);
    }

    public static MutableComponent translatable(String key) {
        return new TranslatableComponent(key);
    }

    public static MutableComponent translatable(String key, Object ...args) {
        return new TranslatableComponent(key, args);
    }

    public static MutableComponent empty() {
        return new TextComponent("");
    }
}
