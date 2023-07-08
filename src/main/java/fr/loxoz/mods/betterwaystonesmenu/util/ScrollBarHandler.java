package fr.loxoz.mods.betterwaystonesmenu.util;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

// adapted from minecraft 1.20 ScrollableWidget (fabric yarn mappings)
public class ScrollBarHandler implements GuiEventListener, Widget {
    public boolean visible = true;
    public boolean hovered = false;
    public int width;
    public int height;
    public int x;
    public int y;
    public int contentHeight = 0;
    public double deltaYPerScroll = 16;
    private boolean focused = false;
    private double scrollY;
    private boolean scrollbarDragged;

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void setPos(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setPosSize(int x, int y, int width, int height) {
        this.setPos(x, y);
        this.setSize(width, height);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.visible) return false;
        boolean bl = this.isWithinBounds(mouseX, mouseY);
        boolean bl2 = this.overflows() && mouseX >= this.x && mouseX <= (this.x + this.width) && mouseY >= this.y && mouseY < (this.y + this.height);
        if (bl2 && button == 0) {
            this.scrollbarDragged = true;
            return true;
        }
        return bl || bl2;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            this.scrollbarDragged = false;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (!(this.visible && this.scrollbarDragged)) return false;
        if (mouseY < this.y) {
            this.setScrollY(0.0);
        } else if (mouseY > (this.y + this.height)) {
            this.setScrollY(this.getMaxScrollY());
        } else {
            int i = this.getScrollbarThumbHeight();
            double d = Math.max(1, this.getMaxScrollY() / (this.height - i));
            this.setScrollY(this.scrollY + deltaY * d);
        }
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (!this.visible) {
            return false;
        }
        this.setScrollY(this.scrollY - amount * this.deltaYPerScroll);
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        boolean bl = keyCode == GLFW.GLFW_KEY_UP;
        boolean bl2 = keyCode == GLFW.GLFW_KEY_DOWN;
        if (bl || bl2) {
            double d = this.scrollY;
            this.setScrollY(this.scrollY + (double)(bl ? -1 : 1) * this.deltaYPerScroll);
            if (d != this.scrollY) return true;
        }
        return false;
    }

    @Override
    public void render(@NotNull PoseStack matrices, int mouseX, int mouseY, float partialTicks) {
        hovered = isWithinBounds(mouseX, mouseY);
        drawScrollBarBg(matrices);
        drawScrollbarThumb(matrices);
    }

    public void drawScrollBarBg(PoseStack matrices) {
        GuiComponent.fill(matrices, this.x, this.y, this.x + this.width, this.y + this.height, 0xff111111);
        if (isFocused()) {
            int focusColor = 0xffffffff;
            GuiComponent.fill(matrices, this.x, this.y, this.x + this.width - 1, this.y + 1, focusColor);
            GuiComponent.fill(matrices, this.x + this.width, this.y, this.x + this.width - 1, this.y + this.height - 1, focusColor);
            GuiComponent.fill(matrices, this.x + this.width, this.y + this.height, this.x + 1, this.y + this.height - 1, focusColor);
            GuiComponent.fill(matrices, this.x, this.y + 1, this.x + 1, this.y + this.height, focusColor);
        }
    }

    private int getScrollbarThumbHeight() {
        return Mth.clamp((int)((float)(this.height * this.height) / contentHeight), 32, this.height);
    }

    public double getScrollY() { return this.scrollY; }
    public void setScrollY(double scrollY) {
        this.scrollY = Mth.clamp(scrollY, 0.0, this.getMaxScrollY());
    }

    protected int getMaxScrollY() {
        return Math.max(0, this.contentHeight - (this.height - 4));
    }

    private void drawScrollbarThumb(PoseStack matrices) {
        if (!overflows()) return;
        int i = this.getScrollbarThumbHeight();
        int k = this.x + this.width;
        int l = Math.max(this.y, (int)this.scrollY * (this.height - i) / this.getMaxScrollY() + this.y);
        int m = l + i;
        boolean hoveredOrFocused = hovered || focused || scrollbarDragged;
        GuiComponent.fill(matrices, this.x, l, k, m, hoveredOrFocused ? 0xffa0a0a0 : 0xff808080);
        GuiComponent.fill(matrices, this.x, l, k - 1, m - 1, hoveredOrFocused ? 0xffe0e0e0 : 0xffC0C0C0);
        /* GuiComponent.fill(matrices, this.x, l, k, m, hoveredOrFocused ? 0x99ffffff : 0x66ffffff);
        GuiComponent.fill(matrices, this.x, l, k - 1, m - 1, 0x66ffffff); */
    }

    public boolean isElementVisible(int elementTop, int elementBottom) {
        return elementBottom - this.scrollY >= y && elementTop - this.scrollY <= (y + this.height);
    }
    public boolean isElementFullyVisible(int elementTop, int elementBottom) {
        return elementBottom - this.scrollY <= (y + this.height) && elementTop - this.scrollY >= y;
    }

    public boolean isWithinBounds(double mouseX, double mouseY) {
        return mouseX >= this.x && mouseX < (this.x + this.width) && mouseY >= this.y && mouseY < (this.y + this.height);
    }

    public boolean overflows() {
        return contentHeight > height;
    }

    public boolean isFocused() { return focused; }
    @Override
    public boolean changeFocus(boolean bl) {
        if (visible && overflows()) {
            this.focused = !this.focused;
            return this.focused;
        }
        return false;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return hovered || isWithinBounds(mouseX, mouseY);
    }
}
