package fr.loxoz.mods.betterwaystonesmenu.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class BWMConfig {
    public final ForgeConfigSpec.EnumValue<BWMSortMode> sortMode;
    public final ForgeConfigSpec.BooleanValue reducedMotion;
    public final ForgeConfigSpec.BooleanValue focusSearch;
    public final ForgeConfigSpec.DoubleValue menuHeightScale;
    // advanced
    public final ForgeConfigSpec.BooleanValue specialCharsFirst;
    public final ForgeConfigSpec.BooleanValue weightedSearch;
    // disabled
    public final ForgeConfigSpec.BooleanValue disabled;

    public BWMConfig(ForgeConfigSpec.Builder builder) {
        sortMode = builder
                .comment("Waystone List Sorting Mode")
                .defineEnum("sortMode", BWMSortMode.NAME);
        focusSearch = builder
                .comment("Focus Search bar when the menu opens")
                .define("focusSearch", true);
        reducedMotion = builder
                .comment("Disable scrollbar animation")
                .define("reducedMotion", false);
        menuHeightScale = builder
                .comment("Menu height scale in percentage")
                .defineInRange("menuHeightScale", 0.66d, 0.4d, 0.8d);

        builder.push("advanced");
        specialCharsFirst = builder
                .comment("Put Special Characters at first for Sort by Name mode")
                .define("specialCharsFirst", true);
        weightedSearch = builder
                .comment("Show most relevant search results first")
                .define("weightedSearch", true);
        builder.pop();

        disabled = builder
                .comment("Completely disable the menu")
                .define("disabled", false);
    }
}
