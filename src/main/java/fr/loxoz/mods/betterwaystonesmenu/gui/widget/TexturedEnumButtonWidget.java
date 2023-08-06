package fr.loxoz.mods.betterwaystonesmenu.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import fr.loxoz.mods.betterwaystonesmenu.compat.CText;
import fr.loxoz.mods.betterwaystonesmenu.compat.tooltip.IPositionedTooltipProvider;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class TexturedEnumButtonWidget<T> extends Button implements IPositionedTooltipProvider {
    protected final List<T> values;
    protected final MessageSupplier<T> messageSupplier;
    protected T value;
    protected Consumer<T> changeListener = null;
    private final ResourceLocation texture;
    private final int u;
    private final int v;
    private final int textureW;
    private final int textureH;

    public TexturedEnumButtonWidget(int x, int y, int width, int height, T[] values, T value, @Nullable MessageSupplier<T> messageSupplier, ResourceLocation texture, int u, int v, int textureW, int textureH) {
        this(x, y, width, height, Arrays.asList(values), value, messageSupplier, texture, u, v, textureW, textureH);
    }

    public TexturedEnumButtonWidget(int x, int y, int width, int height, List<T> values, T value, @Nullable MessageSupplier<T> messageSupplier, ResourceLocation texture, int u, int v, int textureW, int textureH) {
        //noinspection ConstantConditions (no need for onPress since we handle it ourselves)
        super(x, y, width, height, CText.empty(), null);
        this.values = values;
        this.messageSupplier = messageSupplier;
        this.value = value;
        this.texture = texture;
        this.u = u;
        this.v = v;
        this.textureW = textureW;
        this.textureH = textureH;
    }

    public List<T> getValues() { return values; }
    public T getValue() { return value; }
    public void setValue(T value) {
        this.value = value;
        onChange();
    }
    public int getValueIndex() { return getValues().indexOf(value); }
    public void setValueIndex(int index) { setValue(getValues().get(index)); }
    public int shiftValueIndex(int delta) {
        int index = getValueIndex() + delta;
        index = index % values.size();
        if (index < 0) index = values.size() + index;
        setValueIndex(index);
        return index;
    }

    @Override
    public @NotNull Component getMessage() {
        if (messageSupplier == null) return super.getMessage();
        return messageSupplier.getMessageOf(getValue());
    }

    public TexturedEnumButtonWidget<T> onChange(Consumer<T> listener) {
        changeListener = listener;
        return this;
    }

    protected void onChange() {
        if (changeListener != null) changeListener.accept(getValue());
    }

    @Override
    public void onPress() {
        shiftValueIndex(Screen.hasShiftDown() ? -1 : 1);
    }

    @Override
    public void renderButton(@NotNull PoseStack matrices, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.enableDepthTest();
        blit(matrices, x, y, u + getValueIndex() * width, v + (isHoveredOrFocused() ? height : 0), width, height, textureW, textureH);
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
