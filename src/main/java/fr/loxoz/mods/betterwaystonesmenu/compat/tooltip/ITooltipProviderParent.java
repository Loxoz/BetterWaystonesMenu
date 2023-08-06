package fr.loxoz.mods.betterwaystonesmenu.compat.tooltip;

import net.blay09.mods.waystones.client.gui.widget.ITooltipProvider;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public interface ITooltipProviderParent {
    default List<PositionedTooltip> getTooltips(boolean visible) {
        if (!(this instanceof ContainerEventHandler container)) return List.of();
        List<PositionedTooltip> tooltips = new ArrayList<>();
        for (var child : container.children()) {
            if (child instanceof ITooltipProvider provider) {
                if (!visible || provider.shouldShowTooltip()) {
                    tooltips.add(new PositionedTooltip(provider, this::getWidgetsTooltipOffset));
                }
            }
            if (child instanceof ITooltipProviderParent providerParent) {
                tooltips.addAll(providerParent.getTooltips(visible));
            }
        }
        return tooltips;
    }

    default List<PositionedTooltip> getTooltips() {
        return getTooltips(true);
    }

    default @NotNull TooltipOffset getWidgetsTooltipOffset() { return new TooltipOffset(0, 0); }
}
