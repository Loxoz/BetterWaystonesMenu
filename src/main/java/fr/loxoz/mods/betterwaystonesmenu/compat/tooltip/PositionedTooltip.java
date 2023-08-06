package fr.loxoz.mods.betterwaystonesmenu.compat.tooltip;

import net.blay09.mods.waystones.client.gui.widget.ITooltipProvider;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.Supplier;

public class PositionedTooltip implements IPositionedTooltipProvider {
    private final ITooltipProvider target;
    private final Supplier<TooltipOffset> offset;

    public PositionedTooltip(ITooltipProvider target, Supplier<TooltipOffset> offset) {
        this.target = target;
        this.offset = offset;
    }

    public ITooltipProvider getTarget() { return target; }

    @Override
    public boolean shouldShowTooltip() {
        return target.shouldShowTooltip();
    }

    @Override
    public List<Component> getTooltip() {
        return target.getTooltip();
    }

    @Override
    public TooltipPos getTooltipPos(int mouseX, int mouseY, TooltipOffset widgetOffset) {
        if (target instanceof IPositionedTooltipProvider positioned) return positioned.getTooltipPos(mouseX, mouseY, offset.get().add(widgetOffset));
        return new TooltipPos(mouseX, mouseY);
    }
}
