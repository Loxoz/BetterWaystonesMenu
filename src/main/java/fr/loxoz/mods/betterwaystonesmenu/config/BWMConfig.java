package fr.loxoz.mods.betterwaystonesmenu.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class BWMConfig {
    public final ForgeConfigSpec.EnumValue<BWMSortMode> sortMode;
    public final ForgeConfigSpec.BooleanValue reducedMotion;
    public final ForgeConfigSpec.BooleanValue specialCharsFirst;
    public final ForgeConfigSpec.DoubleValue menuHeightScale;
    public final ForgeConfigSpec.BooleanValue disabled;

    public BWMConfig(ForgeConfigSpec.Builder builder) {
        sortMode = builder
                .comment("Waystone List Sorting Mode")
                .defineEnum("sortMode", BWMSortMode.NAME);
        reducedMotion = builder
                .comment("Disable scrollbar animation")
                .define("reducedMotion", false);
        specialCharsFirst = builder
                .comment("Put Special Characters at first for Sort by Name mode")
                .define("specialCharsFirst", true);
        menuHeightScale = builder
                .comment("Menu height scale in percentage")
                .defineInRange("menuHeightScale", 0.66d, 0.4d, 0.8d);
        disabled = builder
                .comment("Completely disable the menu")
                .define("disabled", false);
    }
}
