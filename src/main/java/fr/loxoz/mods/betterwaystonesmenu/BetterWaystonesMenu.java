package fr.loxoz.mods.betterwaystonesmenu;

import fr.loxoz.mods.betterwaystonesmenu.handler.ScreenOpenHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.NetworkConstants;

@Mod(BetterWaystonesMenu.MOD_ID)
public class BetterWaystonesMenu {
    public static final String MOD_ID = "betterwaystonesmenu";
    private static BetterWaystonesMenu instance = null;
    // private static final Logger LOGGER = LogUtils.getLogger();
    private ScreenOpenHandler screenOpenHandler = null;

    public static BetterWaystonesMenu inst() { return instance; }

    public BetterWaystonesMenu() {
        instance = this;
        // Make sure the mod being absent on the other network side does not cause the client to display the server as incompatible
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (remote, isServer) -> true));

        if (FMLEnvironment.dist == Dist.CLIENT) {
            this.screenOpenHandler = new ScreenOpenHandler();
            MinecraftForge.EVENT_BUS.register(screenOpenHandler);
        }
    }

    public void openOriginalScreen(Screen screen) {
        screenOpenHandler.ignoreNextMenu = true;
        Minecraft.getInstance().setScreen(screen);
    }
}
