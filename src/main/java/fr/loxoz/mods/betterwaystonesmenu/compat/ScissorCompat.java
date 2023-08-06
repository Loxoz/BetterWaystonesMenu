package fr.loxoz.mods.betterwaystonesmenu.compat;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;

import java.util.Objects;

public class ScissorCompat {
    public static void enableScissor(Minecraft minecraft, int x, int y, int width, int height) {
        Window win = Objects.requireNonNull(minecraft).getWindow();
        double scale = win.getGuiScale();
        int sx = (int) (x * scale);
        int sy = (int) (win.getHeight() - (y + height) * scale);
        int sw = (int) (width * scale);
        int sh = (int) (height * scale);
        RenderSystem.enableScissor(sx, sy, sw, sh);
    }

    public static void disableScissor() {
        RenderSystem.disableScissor();
    }
}
