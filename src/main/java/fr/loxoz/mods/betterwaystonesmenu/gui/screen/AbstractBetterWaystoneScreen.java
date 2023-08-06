package fr.loxoz.mods.betterwaystonesmenu.gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import fr.loxoz.mods.betterwaystonesmenu.BetterWaystonesMenu;
import fr.loxoz.mods.betterwaystonesmenu.compat.tooltip.ITooltipProviderParent;
import fr.loxoz.mods.betterwaystonesmenu.compat.tooltip.PositionedTooltip;
import fr.loxoz.mods.betterwaystonesmenu.compat.tooltip.TooltipPos;
import net.blay09.mods.waystones.menu.WaystoneSelectionMenu;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public abstract class AbstractBetterWaystoneScreen extends AbstractContainerScreen<WaystoneSelectionMenu> implements ITooltipProviderParent {
    public static int WAYSTONE_NAME_MAX_WIDTH = 260;
    public static int CONTENT_WIDTH = 200;
    public static int BTN_GAP = 2;
    public static int UI_GAP = 8;
    public static final ResourceLocation MENU_TEXTURE = new ResourceLocation(BetterWaystonesMenu.MOD_ID , "textures/gui/menu.png");
    public static float contentHeightPercent = 0.666f;

    public AbstractBetterWaystoneScreen(WaystoneSelectionMenu container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
    }

    protected void renderChildrenTooltip(@NotNull PoseStack matrices, int mouseX, int mouseY) {
        for (var provider : getTooltips()) {
            renderPositionedTooltip(provider, matrices, mouseX, mouseY);
        }
    }

    protected void renderPositionedTooltip(PositionedTooltip tooltip, PoseStack matrices, int mouseX, int mouseY) {
        TooltipPos pos = tooltip.getTooltipPos(mouseX, mouseY);
        renderTooltip(matrices, tooltip.getTooltip(), Optional.empty(), pos.x(), pos.y());
    }

    // patch mouseDragged because `AbstractContainerScreen` does not call it on children
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        // can be optimized by only calling `scrollable.mouseDragged`
        if (getFocused() != null && isDragging() && button == 0) {
            if (getFocused().mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    // unused
    @Override
    protected void renderBg(@NotNull PoseStack matrices, float delta, int mouseX, int mouseY) {}
}
