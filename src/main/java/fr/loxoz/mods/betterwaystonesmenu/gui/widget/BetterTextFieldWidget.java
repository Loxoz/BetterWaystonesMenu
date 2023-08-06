package fr.loxoz.mods.betterwaystonesmenu.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import fr.loxoz.mods.betterwaystonesmenu.compat.widget.WidgetCompat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class BetterTextFieldWidget extends EditBox implements WidgetCompat {
    public BetterTextFieldWidget(Font textRenderer, int x, int y, int width, int height, Component message) {
        super(textRenderer, x, y, width, height, message);
    }

    @Override
    public boolean keyPressed(int p_94132_, int p_94133_, int p_94134_) {
        super.keyPressed(p_94132_, p_94133_, p_94134_);
        return canConsumeInput();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 1) {
            setValue("");
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void renderButton(@NotNull PoseStack matrices, int mouseX, int mouseY, float partialTicks) {
        super.renderButton(matrices, mouseX, mouseY, partialTicks);
        if (!isVisible()) return;
        if (!getValue().isEmpty()) return;
        Minecraft.getInstance().font.drawShadow(matrices, getMessage(), x + 4, y + (height - 8f) / 2f, 0xff262626);
    }

    @Override
    public int getX() { return x; }
    @Override
    public void setX(int x) { this.x = x; }

    @Override
    public int getY() { return y; }
    @Override
    public void setY(int y) { this.y = y; }
}
