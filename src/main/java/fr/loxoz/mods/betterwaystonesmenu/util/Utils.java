package fr.loxoz.mods.betterwaystonesmenu.util;

import com.mojang.blaze3d.vertex.PoseStack;
import fr.loxoz.mods.betterwaystonesmenu.compat.CText;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
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

    public static void drawOutline(PoseStack matrices, int x1, int y1, int x2, int y2, int color) {
        GuiComponent.fill(matrices, x1, y1, x2 - 1, y1 + 1, color);
        GuiComponent.fill(matrices, x2 - 1, y1, x2, y2 - 1, color);
        GuiComponent.fill(matrices, x2, y2 - 1, x1 + 1, y2, color);
        GuiComponent.fill(matrices, x1, y2, x1 + 1, y1 + 1, color);
    }
}
