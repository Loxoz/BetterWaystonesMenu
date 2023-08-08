package fr.loxoz.mods.betterwaystonesmenu.gui;

import fr.loxoz.mods.betterwaystonesmenu.gui.screen.BetterSharestoneSelectionScreen;
import fr.loxoz.mods.betterwaystonesmenu.gui.screen.BetterWaystoneSelectionScreen;
import net.blay09.mods.waystones.client.gui.screen.SharestoneSelectionScreen;
import net.blay09.mods.waystones.client.gui.screen.WaystoneSelectionScreen;
import net.minecraft.world.entity.player.Inventory;

public class BWMScreenUpgrader {
    public static BetterWaystoneSelectionScreen createFrom(WaystoneSelectionScreen screen, Inventory playerInventory) {
        var new_screen = new BetterWaystoneSelectionScreen(screen.getMenu(), playerInventory, screen.getTitle());
        new_screen.setOriginalScreen(screen);
        return new_screen;
    }

    public static BetterSharestoneSelectionScreen createFrom(SharestoneSelectionScreen screen, Inventory playerInventory) {
        var new_screen = new BetterSharestoneSelectionScreen(screen.getMenu(), playerInventory, screen.getTitle());
        new_screen.setOriginalScreen(screen);
        return new_screen;
    }
}
