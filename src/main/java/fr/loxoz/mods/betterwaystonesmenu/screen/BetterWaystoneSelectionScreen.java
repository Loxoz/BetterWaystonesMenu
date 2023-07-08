package fr.loxoz.mods.betterwaystonesmenu.screen;

import net.blay09.mods.waystones.menu.WaystoneSelectionMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/**
 * The upgraded version of {@link net.blay09.mods.waystones.client.gui.screen.WaystoneSelectionScreen}
 */
public class BetterWaystoneSelectionScreen extends BetterWaystoneSelectionScreenBase {
    public BetterWaystoneSelectionScreen(WaystoneSelectionMenu container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
    }
}
