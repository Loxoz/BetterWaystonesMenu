package fr.loxoz.mods.betterwaystonesmenu.util;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;

public class Utils {
    public static MutableComponent trimTextWidth(String text, Font font, int maxWidth) {
        String out = text;
        boolean trimmed = false;
        while (font.width(out) > maxWidth) {
            trimmed = true;
            out = out.substring(0, out.length()-1);
        }
        if (trimmed) {
            out = out.substring(0, out.length()-3) + "...";
        }
        return new TextComponent(out);
    }
}
