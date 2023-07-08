package fr.loxoz.mods.betterwaystonesmenu.widget;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class ClearableTextFieldWidget extends EditBox {
    public ClearableTextFieldWidget(Font textRenderer, int x, int y, int width, int height, Component message) {
        super(textRenderer, x, y, width, height, message);
    }

    @Override
    public boolean keyPressed(int p_94132_, int p_94133_, int p_94134_) {
        super.keyPressed(p_94132_, p_94133_, p_94134_);
        return this.canConsumeInput();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 1) {
            setValue("");
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
