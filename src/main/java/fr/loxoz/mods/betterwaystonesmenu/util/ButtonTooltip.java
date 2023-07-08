package fr.loxoz.mods.betterwaystonesmenu.util;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Function;

public class ButtonTooltip implements Button.OnTooltip {
    public static ButtonTooltip USING_MESSAGE = new ButtonTooltip(Button::getMessage);

    private final Function<Button, Component> tooltipSupplier;
    private Component cachedValue = null;

    public ButtonTooltip(Function<Button, Component> tooltipSupplier) {
        this.tooltipSupplier = tooltipSupplier;
    }

    @Override
    public void onTooltip(@NotNull Button button, @NotNull PoseStack matrices, int mouseX, int mouseY) {
        Minecraft client = Minecraft.getInstance();
        Screen screen = client.screen;
        if (screen == null) return;
        cachedValue = tooltipSupplier.apply(button);
        screen.renderTooltip(matrices, cachedValue, mouseX, mouseY);
    }

    @Override
    public void narrateTooltip(@NotNull Consumer<Component> consumer) {
        if (cachedValue != null) consumer.accept(cachedValue);
    }
}
