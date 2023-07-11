package fr.loxoz.mods.betterwaystonesmenu.util;

import fr.loxoz.mods.betterwaystonesmenu.compat.CText;
import net.blay09.mods.waystones.api.IWaystone;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

public class WaystoneUtils {
    public static Component getTrimmedWaystoneName(IWaystone waystone, Font font, int maxWidth) {
        if (!waystone.hasName()) return CText.translatable("gui.waystones.waystone_selection.unnamed_waystone");
        return Utils.trimTextWidth(waystone.getName(), font, maxWidth);
    }
}
