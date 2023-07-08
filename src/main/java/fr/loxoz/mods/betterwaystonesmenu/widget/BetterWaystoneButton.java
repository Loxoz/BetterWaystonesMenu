package fr.loxoz.mods.betterwaystonesmenu.widget;

import fr.loxoz.mods.betterwaystonesmenu.screen.BetterWaystoneSelectionScreenBase;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.client.gui.widget.WaystoneButton;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;

public class BetterWaystoneButton extends WaystoneButton {
    public boolean viewportVisible = true;
    private Component fullMessage;

    public BetterWaystoneButton(int x, int y, IWaystone waystone, int xpLevelCost, OnPress pressable) {
        super(x, y, waystone, xpLevelCost, pressable);
        fullMessage = getMessage();
        setMessage(BetterWaystoneSelectionScreenBase.getTrimmedWaystoneName(waystone, Minecraft.getInstance().font, (int) (width * 0.8f)));
    }

    @Override
    protected boolean clicked(double p_93681_, double p_93682_) {
        return viewportVisible && super.clicked(p_93681_, p_93682_);
    }

    @Override
    protected @NotNull MutableComponent createNarrationMessage() {
        return wrapDefaultNarrationMessage(fullMessage);
    }
}
