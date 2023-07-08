package fr.loxoz.mods.betterwaystonesmenu.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Rect2i;

import java.util.Objects;

public class ScissorHelper {
    public static void enableScissor(Minecraft minecraft, Rect2i rect, int offsetX, int offsetY) {
        var win = Objects.requireNonNull(minecraft).getWindow();
        double scale = win.getGuiScale();
        int x = (int) (rect.getX() * scale);
        int y = (int) (win.getHeight() - ((rect.getY() + rect.getHeight()) * scale));
        int w = (int) (rect.getWidth() * scale);
        int h = (int) (rect.getHeight() * scale);
        RenderSystem.enableScissor(x, y, w, h);
    }

    public static void disableScissor() {
        RenderSystem.disableScissor();
    }
}
