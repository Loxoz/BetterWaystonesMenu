package fr.loxoz.mods.betterwaystonesmenu.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class Formatting {
    public static DecimalFormat distance = new DecimalFormat("#,###.#", getFormattingSymbols());

    public static DecimalFormatSymbols getFormattingSymbols() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(' ');
        symbols.setDecimalSeparator('.');
        return symbols;
    }
}
