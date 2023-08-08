package fr.loxoz.mods.betterwaystonesmenu;

import fr.loxoz.mods.betterwaystonesmenu.config.BWMConfig;
import fr.loxoz.mods.betterwaystonesmenu.handler.ScreenOpenHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigGuiHandler;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.network.NetworkConstants;

import java.util.Optional;

@Mod(BetterWaystonesMenu.MOD_ID)
public class BetterWaystonesMenu {
    public static final String MOD_ID = "betterwaystonesmenu";
    private static BetterWaystonesMenu instance = null;
    // private static final Logger LOGGER = LogUtils.getLogger();
    private ScreenOpenHandler screenOpenHandler = null;
    private final BWMConfig config;
    private final ForgeConfigSpec spec;
    private final ModContainer modContainer;

    public static BetterWaystonesMenu inst() { return instance; }

    public BetterWaystonesMenu() {
        instance = this;
        // Make sure the mod being absent on the other network side does not cause the client to display the server as incompatible
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (remote, isServer) -> true));

        if (FMLEnvironment.dist == Dist.CLIENT) {
            screenOpenHandler = new ScreenOpenHandler();
            MinecraftForge.EVENT_BUS.register(screenOpenHandler);
            var builder = new ForgeConfigSpec.Builder();
            config = new BWMConfig(builder);
            spec = builder.build();
            ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, spec);
        }
        else {
            config = null;
            spec = null;
        }

        modContainer = ModList.get().getModContainerById(MOD_ID).orElse(null);
    }

    public void openOriginalScreen(Screen screen) {
        screenOpenHandler.ignoreNextMenu = true;
        Minecraft.getInstance().setScreen(screen);
    }

    public BWMConfig config() { return config; }
    public ForgeConfigSpec configSpec() { return spec; }

    public ModContainer getModContainer() { return modContainer; }
    public IModInfo getModInfo() { return modContainer != null ? modContainer.getModInfo() : null; }
    public Optional<Screen> getConfigScreen(Minecraft minecraft, Screen parent) {
        var info = getModInfo();
        if (info == null) return Optional.empty();
        return ConfigGuiHandler.getGuiFactoryFor(info).map(f -> f.apply(minecraft, parent));
    }
    public Optional<Screen> getConfigScreen(Minecraft minecraft) {
        return getConfigScreen(minecraft, minecraft.screen);
    }
}
