package fr.loxoz.mods.betterwaystonesmenu.compat.widget;

import fr.loxoz.mods.betterwaystonesmenu.compat.tooltip.IPositionedTooltipProvider;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class TexturedButtonTooltipWidget extends ImageButton implements IPositionedTooltipProvider {
    public TexturedButtonTooltipWidget(int x, int y, int width, int height, int u, int v, int hoverVOffset, ResourceLocation texture, int textureWidth, int textureHeight, Button.OnPress onClick, Component message) {
        super(x, y, width, height, u, v, hoverVOffset, texture, textureWidth, textureHeight, onClick, message);
    }

    @Override
    public boolean shouldShowTooltip() {
        return isHoveredOrFocused();
    }

    @Override
    public List<Component> getTooltip() {
        return List.of(getMessage());
    }
}
