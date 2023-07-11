package fr.loxoz.mods.betterwaystonesmenu.util;

import fr.loxoz.mods.betterwaystonesmenu.compat.CText;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.MutableComponent;

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
        return CText.literal(out);
    }
}
