package fr.loxoz.mods.betterwaystonesmenu.widget;

import fr.loxoz.mods.betterwaystonesmenu.compat.CText;
import fr.loxoz.mods.betterwaystonesmenu.compat.IPositionedTooltipProvider;
import fr.loxoz.mods.betterwaystonesmenu.util.Formatting;
import fr.loxoz.mods.betterwaystonesmenu.util.WaystoneUtils;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.client.gui.widget.WaystoneButton;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.UnaryOperator;

public class BetterWaystoneButton extends WaystoneButton implements IPositionedTooltipProvider {
    private final IWaystone waystone;
    public boolean viewportVisible = true;
    private final Component fullMessage;
    private final List<Component> tooltip;
    private final @Nullable Vec3 viewingOrigin;
    private final @Nullable ResourceKey<Level> viewingDim;

    public BetterWaystoneButton(int x, int y, IWaystone waystone, int xpLevelCost, OnPress pressable, @Nullable Vec3 viewingOrigin, @Nullable ResourceKey<Level> viewingDim) {
        super(x, y, waystone, xpLevelCost, pressable);
        this.waystone = waystone;
        this.viewingOrigin = viewingOrigin;
        this.viewingDim = viewingDim;
        fullMessage = getMessage();
        setMessage(WaystoneUtils.getTrimmedWaystoneName(waystone, Minecraft.getInstance().font, (int) (width * 0.8f)));
        tooltip = computeTooltip();
    }

    @Override
    protected boolean clicked(double p_93681_, double p_93682_) {
        return viewportVisible && super.clicked(p_93681_, p_93682_);
    }

    @Override
    protected @NotNull MutableComponent createNarrationMessage() {
        return wrapDefaultNarrationMessage(fullMessage);
    }

    @Override
    public boolean isHoveredOrFocused() {
        return viewportVisible && super.isHoveredOrFocused();
    }

    @Override
    public boolean shouldShowTooltip() {
        return isHoveredOrFocused() && Screen.hasControlDown();
    }

    @Override
    public List<Component> getTooltip() {
        return tooltip;
    }

    public List<Component> computeTooltip() {
        UnaryOperator<Style> gray = style -> style.withColor(ChatFormatting.GRAY);

        List<Component> tooltip = new ArrayList<>(List.of(
                WaystoneUtils.getTrimmedWaystoneName(waystone, Minecraft.getInstance().font, 400),
                CText.literal(waystone.getWaystoneUid().toString()).withStyle(style -> style.withColor(ChatFormatting.DARK_GRAY))
        ));

        if (viewingOrigin != null) {
            String dist = Formatting.distance.format(viewingOrigin.distanceTo(Vec3.atBottomCenterOf(waystone.getPos())));
            tooltip.add(CText.translatable("gui.betterwaystonesmenu.waystone_selection.infos.pos_dist", dist).withStyle(gray));
        }

        tooltip.add(CText.translatable(
                "gui.betterwaystonesmenu.waystone_selection.infos.pos_at",
                waystone.getPos().toShortString()
        ).withStyle(gray));

        if (viewingDim != null && !viewingDim.equals(waystone.getDimension())) {
            tooltip.add(CText.translatable("gui.betterwaystonesmenu.waystone_selection.infos.dim_in",
                    waystone.getDimension().location().toString()
            ).withStyle(gray));
        }

        return Collections.unmodifiableList(tooltip);
    }
}
