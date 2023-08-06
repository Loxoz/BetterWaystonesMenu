package fr.loxoz.mods.betterwaystonesmenu.compat.tooltip;

import net.blay09.mods.waystones.client.gui.widget.ITooltipProvider;
import net.minecraft.client.gui.components.AbstractWidget;

/**
 * using {@link ITooltipProvider} as base type for compatibility with the waystones mod
 */
public interface IPositionedTooltipProvider extends ITooltipProvider {
    default TooltipPos getTooltipPos(int mouseX, int mouseY) {
        return getTooltipPos(mouseX, mouseY, new TooltipOffset(0, 0));
    }

    default TooltipPos getTooltipPos(int mouseX, int mouseY, TooltipOffset widgetOffset) {
        if (this instanceof AbstractWidget widget && widget.isFocused()) {
            return getFromWidget(widget, widgetOffset);
        }
        return new TooltipPos(mouseX, mouseY);
    }

    static TooltipPos getFromWidget(AbstractWidget widget, TooltipOffset offset) {
        return new TooltipPos(widget.x + offset.x(), widget.y + offset.y());
    }
}
