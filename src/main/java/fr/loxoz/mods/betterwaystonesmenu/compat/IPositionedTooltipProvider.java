package fr.loxoz.mods.betterwaystonesmenu.compat;

import net.blay09.mods.waystones.client.gui.widget.ITooltipProvider;
import net.minecraft.client.gui.components.Button;

/**
 * using {@link ITooltipProvider} as base type for compatibility with the waystones mod
 */
public interface IPositionedTooltipProvider extends ITooltipProvider {
    default TooltipPos getTooltipPos(int mouseX, int mouseY) {
        if (this instanceof Button button) {
            if (button.isFocused()) {
                return new TooltipPos(button.x, button.y);
            }
        }
        return new TooltipPos(mouseX, mouseY);
    }
}
