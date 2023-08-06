package fr.loxoz.mods.betterwaystonesmenu.gui.widget;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import fr.loxoz.mods.betterwaystonesmenu.compat.CText;
import fr.loxoz.mods.betterwaystonesmenu.compat.tooltip.IPositionedTooltipProvider;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BetterRemoveWaystoneButton extends Button implements IPositionedTooltipProvider {
    private static final ResourceLocation BEACON_GUI_TEXTURE = new ResourceLocation("textures/gui/container/beacon.png");
    public final boolean global;
    private boolean clickable = false;

    public BetterRemoveWaystoneButton(int x, int y, int width, int height, boolean global, OnPress onPress) {
        super(x, y, width, height, CText.empty(), onPress);
        this.global = global;
    }

    @Override
    public void renderButton(@NotNull PoseStack matrices, int mouseX, int mouseY, float partialTicks) {
        clickable = Screen.hasShiftDown() && isHoveredOrFocused();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
        int iv = getYImage(isHoveredOrFocused());
        blit(matrices, x, y, 0, 46 + iv * 20, width / 2, height);
        blit(matrices, x + width / 2, y, 200 - width / 2, 46 + iv * 20, width / 2, height);
        // int color_over = clickable ? 0x33dc2626 : 0x66000000;
        int color_over = clickable ? 0x33dc2626 : 0x99262626;
        int pad = clickable ? 1 : 0;
        fill(matrices, x + pad, y + pad, x + width - pad, y + height - pad, color_over);
        RenderSystem.setShaderTexture(0, BEACON_GUI_TEXTURE);
        float color = clickable ? 1f : 0.5f;
        RenderSystem.setShaderColor(color, color, color, color);
        int icon_w = 13;
        blit(matrices, x + (width - icon_w) / 2, y + (height - icon_w) / 2, 114, 223, icon_w, icon_w);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!clickable) return false;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void onPress() {
        if (!clickable) return;
        super.onPress();
    }

    @Override
    public void playDownSound(@NotNull SoundManager soundManager) {
        if (!clickable) return;
        super.playDownSound(soundManager);
    }

    @Override
    public @NotNull Component getMessage() {
        if (clickable) return CText.translatable("gui.waystones.waystone_selection.click_to_delete");
        return CText.translatable("gui.waystones.waystone_selection.hold_shift_to_delete");
    }

    @Override
    public boolean shouldShowTooltip() {
        return visible && isHoveredOrFocused();
    }

    @Override
    public List<Component> getTooltip() {
        List<Component> tooltip = Lists.newArrayList(getMessage());
        if (global) tooltip.add(CText.translatable("gui.waystones.waystone_selection.deleting_global_for_all"));
        return tooltip;
    }
}
