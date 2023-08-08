package fr.loxoz.mods.betterwaystonesmenu.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import fr.loxoz.mods.betterwaystonesmenu.compat.CText;
import fr.loxoz.mods.betterwaystonesmenu.compat.widget.TexturedButtonTooltipWidget;
import fr.loxoz.mods.betterwaystonesmenu.compat.widget.WidgetCompat;
import fr.loxoz.mods.betterwaystonesmenu.gui.widget.BetterRemoveWaystoneButton;
import fr.loxoz.mods.betterwaystonesmenu.gui.widget.BetterTextFieldWidget;
import fr.loxoz.mods.betterwaystonesmenu.gui.widget.ScrollableContainerWidget;
import fr.loxoz.mods.betterwaystonesmenu.util.QueryMatcher;
import fr.loxoz.mods.betterwaystonesmenu.util.Utils;
import fr.loxoz.mods.betterwaystonesmenu.util.WaystoneUtils;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.menu.WaystoneSelectionMenu;
import net.blay09.mods.waystones.network.message.RemoveWaystoneMessage;
import net.blay09.mods.waystones.network.message.SortWaystoneMessage;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BetterWaystoneRearrangeScreen extends AbstractBetterWaystoneScreen {
    protected final List<IWaystone> waystones;
    protected final Screen parent;
    private final Allowed allowed;
    protected ScrollableContainerWidget scrollable;
    protected DraggedButton draggedButton;
    protected DragContext dragContext = null;
    protected List<DropZone> dropZones = new ArrayList<>();
    protected BetterTextFieldWidget queryField;
    protected QueryMatcher queryMatcher = new QueryMatcher("");

    public BetterWaystoneRearrangeScreen(WaystoneSelectionMenu container, Inventory playerInventory, Screen parent, Allowed allowed) {
        super(container, playerInventory, CText.translatable("gui.betterwaystonesmenu.waystone_selection.rearrange"));
        this.waystones = container.getWaystones();
        this.parent = parent;
        this.allowed = allowed;
        //noinspection SuspiciousNameCombination
        imageWidth = imageHeight = CONTENT_WIDTH;
        if (!allowed.deletion && !allowed.sorting) {
            onClose();
        }
    }

    @Override
    protected void init() {
        imageWidth = CONTENT_WIDTH;
        imageHeight = (int) (height * menuHeightScale * 0.9);
        super.init();

        var backBtn = new TexturedButtonTooltipWidget(
                leftPos, topPos, 20, 20,
                40, 0, 20, MENU_TEXTURE, 256, 256,
                $ -> onClose(),
                parent instanceof AbstractBetterWaystoneScreen ?
                        CText.translatable("gui.betterwaystonesmenu.waystone_selection.back_to_waystones") :
                        CommonComponents.GUI_BACK
        );
        addRenderableWidget(backBtn);

        if (queryField == null) {
            queryField = new BetterTextFieldWidget(font, 0, 0, 100, 20, CText.translatable("gui.betterwaystonesmenu.waystone_selection.query_waystones"));
            queryField.setMaxLength(128);
        }
        queryField.setPosition(leftPos + backBtn.getWidth() + UI_GAP, topPos);
        queryField.setWidth(leftPos + imageWidth - queryField._getX());
        addRenderableWidget(queryField);

        if (scrollable == null) {
            scrollable = new ScrollableContainerWidget(0, 0, CONTENT_WIDTH, 0);
            if (inst().config().reducedMotion.get()) {
                scrollable.setAnimated(false);
            }
        }
        int scrollableY = topPos + queryField.getHeight() + UI_GAP;
        scrollable.setPosition(leftPos, scrollableY);
        scrollable.setHeight(topPos + imageHeight - queryField._getY());
        addRenderableWidget(scrollable);

        if (draggedButton == null) {
            draggedButton = new DraggedButton(0, 0, scrollable.getInnerWidth() - BTN_GAP - 20, 20);
            draggedButton.setAlpha(0.75f);
        }
        draggedButton.setPosition(scrollable.getX(), scrollable.getY());

        updateList();
    }

    public void updateList() {
        IWaystone prevFocusedWaystone = null;
        if (scrollable.getFocused() instanceof DraggableButton btn) {
            prevFocusedWaystone = btn.getWaystone();
        }

        scrollable.contents().clear();
        dropZones.clear();

        int y = 0;
        int content_h = 0;

        int i = -1;
        for (IWaystone waystone : waystones) {
            i++;

            // waystone button
            int btn_h = 20;
            int btn_w = scrollable.getInnerWidth() - BTN_GAP - (allowed.deletion ? 20 : 0);
            var msg = WaystoneUtils.getTrimmedWaystoneName(waystone, font, (int) (btn_w * 0.8f));
            if (waystone.isGlobal()) msg.withStyle(ChatFormatting.AQUA);
            var btn = new DraggableButton(0, y, btn_w, btn_h, msg, i, waystone);
            scrollable.contents().add(btn);

            if (Objects.equals(prevFocusedWaystone, waystone) && btn.changeFocus(true)) {
                scrollable.setFocused(btn);
                scrollable.scrollElementIntoView(btn);
            }

            // remove button
            //noinspection ConstantConditions
            if (allowed.deletion && (!waystone.isGlobal() || minecraft.player.getAbilities().instabuild)) {
                var btn_rm = new BetterRemoveWaystoneButton(btn_w, y, 20, 20, waystone.isGlobal(), $ -> {
                    Player player = Objects.requireNonNull(Minecraft.getInstance().player);
                    PlayerWaystoneManager.deactivateWaystone(player, waystone);
                    Balm.getNetworking().sendToServer(new RemoveWaystoneMessage(waystone.getWaystoneUid()));
                    updateList();
                });
                scrollable.contents().add(btn_rm);
            }

            dropZones.add(new DropZone(y, y + btn_h, i));

            y += 20 + BTN_GAP;
            int ch = btn.y + btn.getHeight();
            if (ch > content_h) content_h = ch;
        }

        scrollable.setContentHeight(content_h);
    }

    public boolean isDraggingWaystone() {
        return dragContext != null;
    }

    @Override
    protected void containerTick() {
        queryField.tick();
        if (!queryMatcher.getQuery().equals(queryField.getValue())) {
            queryMatcher.setQuery(queryField.getValue());
        }

        boolean reducedMotion = inst().config().reducedMotion.get();
        if (reducedMotion == scrollable.isAnimated()) {
            scrollable.setAnimated(!reducedMotion);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // force clicked draggable button in the scrollable container to get focused
        DraggableButton prevFocused = null;
        if (scrollable.getFocused() instanceof DraggableButton btn && btn.isFocused()) {
            prevFocused = btn;
        }
        if (super.mouseClicked(mouseX, mouseY, button)) {
            if (scrollable.getFocused() instanceof DraggableButton btn) {
                if (prevFocused != null && prevFocused != btn) {
                    prevFocused.changeFocus(true);
                }
                if (!btn.isFocused()) {
                    btn.changeFocus(true);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void render(@NotNull PoseStack matrices, int mouseX, int mouseY, float partialTicks) {
        renderBackground(matrices);
        // move the draggable button if dragged + scroll when reaching top or bottom
        // scrollEndsSize represents the auto scroll padding size when reaching top or bottom of scrollable area while dragging
        int scrollEndsSize = 20;
        if (isDraggingWaystone()) {
            if (scrollable.isMouseInside(mouseX, mouseY)) {
                if (mouseY < scrollable.getY() + scrollEndsSize) {
                    scrollable.scrollBy(1);
                }
                else if (mouseY > scrollable.getY() + scrollable.getHeight() - scrollEndsSize) {
                    scrollable.scrollBy(-1);
                }
            }

            draggedButton.setPosition(mouseX - dragContext.getOffsetX(), mouseY - dragContext.getOffsetY());
        }
        // version info
        drawVersionInfo(matrices);
        // bottom text info
        {
            var lines = font.split(CText.translatable("gui.betterwaystonesmenu.waystone_selection.drag_info"), (imageWidth * 2));
            int y = scrollable.getY() + scrollable.getHeight() + UI_GAP;
            for (var line : lines) {
                drawCenteredString(matrices, font, line, width/2, y, 0x66ffffff);
                y += font.lineHeight +  2;
            }
        }
        // widgets and labels
        super.render(matrices, mouseX, mouseY, partialTicks);
        // render top and bottom ends' auto scroll gradients
        if (isDraggingWaystone()) {
            int x1 = scrollable.getX();
            int x2 = x1 + scrollable.getInnerWidth();
            int clrEnd = 0x1affffff;
            int clrMid = 0x00ffffff;
            if (scrollable.getScrollY() > 0.1) {
                fillGradient(matrices, x1, scrollable.getY(), x2, scrollable.getY() + scrollEndsSize, clrEnd, clrMid);
            }
            if (scrollable.getScrollY() < (scrollable.getMaxScrollY() - 0.1)) {
                int y = scrollable.getY() + scrollable.getHeight() - scrollEndsSize;
                fillGradient(matrices, x1, y, x2, y + scrollEndsSize, clrMid, clrEnd);
            }
        }
        // render dragged button and hovered dropZone
        if (isDraggingWaystone()) {
            var hoveredZone = dragContext.getHoveredDropZone(mouseX, mouseY);
            if (hoveredZone != null && hoveredZone.idx != dragContext.getIndex()) {
                int outlineY = scrollable.getY() - (int) scrollable.getScrollY();
                Utils.drawOutline(matrices, scrollable.getX(), outlineY + hoveredZone.yStart(), scrollable.getX() + scrollable.getInnerWidth() - BTN_GAP, outlineY + hoveredZone.yEnd(), 0x99ffffff);
            }

            matrices.pushPose();
            matrices.translate(0, 0, 500);
            draggedButton.render(matrices, mouseX, mouseY, partialTicks);
            matrices.popPose();
        }
        // container slot's tooltip (unused for this menu)
        renderTooltip(matrices, mouseX, mouseY);
        // tooltips
        renderChildrenTooltip(matrices, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(@NotNull PoseStack matrices, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        matrices.pushPose();
        matrices.translate(-leftPos, -topPos, 0);
        drawCenteredString(matrices, font, getTitle().copy().withStyle(style -> style.withColor(ChatFormatting.GRAY)), width/2, topPos - font.lineHeight - UI_GAP, 0xffffff);
        matrices.popPose();
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (isDraggingWaystone()) {
            int index = dragContext.getIndex();
            var hoveredZone = dragContext.getHoveredDropZone(mouseX, mouseY);
            dragContext = null;

            if (hoveredZone != null && hoveredZone.idx != index) {
                swapWaystones(index, hoveredZone.idx);
            }
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    public void swapWaystones(int index, int otherIndex) {
        //noinspection ConstantConditions
        PlayerWaystoneManager.swapWaystoneSorting(minecraft.player, index, otherIndex);
        Balm.getNetworking().sendToServer(new SortWaystoneMessage(index, otherIndex));
        updateList();
    }

    public void shiftWaystone(int index, int offset, boolean shift) {
        if (index >= 0 && index < waystones.size()) {
            int otherIndex;
            if (shift) {
                otherIndex = offset == -1 ? -1 : waystones.size();
            } else {
                otherIndex = index + offset;
                if (otherIndex < 0 || otherIndex >= waystones.size()) {
                    return;
                }
            }

            swapWaystones(index, otherIndex);
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onClose() {
        if (parent == null) {
            super.onClose();
            return;
        }
        minecraft.setScreen(parent);
    }

    public void onDragStart(DraggableButton button, double mouseX, double mouseY) {
        dragContext = new DragContext(button.index, (int) mouseX - button.x, (int) mouseY - button.y);
        draggedButton.setMessage(button.getMessage());
    }

    public static record Allowed(boolean sorting, boolean deletion) {}

    public class DraggableButton extends Button {
        private final IWaystone waystone;
        private final int index;

        public DraggableButton(int x, int y, int width, int height, Component message, int index, IWaystone waystone) {
            super(x, y, width, height, message, $ -> {});
            this.waystone = waystone;
            this.index = index;
        }

        public IWaystone getWaystone() { return waystone; }
        public int getIndex() { return index; }

        public boolean isMouseInside(double mouseX, double mouseY) {
            return mouseX >= (double) x && mouseY >= (double) y && mouseX < (double) (x + getWidth()) && mouseY < (double) (y + getHeight());
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (!visible || !isValidClickButton(button)) return false;
            if (!isMouseInside(mouseX, mouseY)) return false;
            onDragStart(this, mouseX, mouseY);
            return true;
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCodes, int modifiers) {
            if (!isActive() || !visible) return false;
            if (keyCode == GLFW.GLFW_KEY_UP || keyCode == GLFW.GLFW_KEY_DOWN) {
                shiftWaystone(index, keyCode == GLFW.GLFW_KEY_UP ? -1 : 1, Screen.hasShiftDown());
                return true;
            }
            return false;
        }

        @Override
        public void onClick(double p_93371_, double p_93372_) {}
        @Override
        public void playDownSound(@NotNull SoundManager p_93665_) {}

        @Override
        protected int getYImage(boolean focused) {
            return 0;
        }

        @Override
        public void renderButton(@NotNull PoseStack matrices, int mouseX, int mouseY, float partialTicks) {
            if (dragContext != null && dragContext.index == index) return;
            matrices.pushPose();
            float prevAlpha = -1;
            if (!queryMatcher.isBlank() && !queryMatcher.match(getMessage().getContents())) { // `getContents()` is faster than `getString()`
                prevAlpha = alpha;
                alpha *= 0.5;
            }
            super.renderButton(matrices, mouseX, mouseY, partialTicks);
            if (prevAlpha >= 0) {
                alpha = prevAlpha;
            }
            matrices.popPose();
            if (isFocused()) {
                Utils.drawOutline(matrices, x, y, x + width, y + height, 0x66ffffff);
            }
        }
    }

    public static class DraggedButton extends Button implements WidgetCompat {
        public DraggedButton(int x, int y, int width, int height) {
            super(x, y, width, height, CText.empty(), $ -> {});
            active = false;
        }

        @Override
        public int getX() { return x; }
        @Override
        public void setX(int x) { this.x = x; }

        @Override
        public int getY() { return y; }
        @Override
        public void setY(int y) { this.y = y; }

        @Override
        public void renderButton(@NotNull PoseStack matrices, int mouseX, int mouseY, float partialTicks) {
            fill(matrices, getX(), getY(), getX() + getWidth(), getY() + getHeight(), 0x997f7f7f);
            super.renderButton(matrices, mouseX, mouseY, partialTicks);
        }
    }

    public static record DropZone(int yStart, int yEnd, int idx) {}
    public class DragContext {
        private final int index;
        private final int offsetX;
        private final int offsetY;

        public DragContext(int index, int offsetX, int offsetY) {
            this.index = index;
            this.offsetX = offsetX;
            this.offsetY = offsetY;
        }

        public int getIndex() { return index; }
        public int getOffsetX() { return offsetX; }
        public int getOffsetY() { return offsetY; }

        public DropZone getHoveredDropZone(double mouseX, double mouseY) {
            if (!scrollable.isMouseInside(mouseX, mouseY)) return null;

            double dragY = Mth.clamp(mouseY - scrollable.getY() + scrollable.getScrollY(), 0, scrollable.getContentHeight());
            for (var zone : dropZones) {
                if (dragY >= zone.yStart() && dragY < zone.yEnd()) {
                    return zone;
                }
            }

            return null;
        }
    }
}
