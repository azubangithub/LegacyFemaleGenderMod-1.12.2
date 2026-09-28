package com.wildfire.main.text;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public interface ILangEntry extends IHasTranslationKey {

    default String asString(Object... args) {
        return ModTranslations.format(getTranslationKey(), convertArgs(args));
    }

    default ITextComponent translate(Object... args) {
        return new TextComponentString(ModTranslations.format(getTranslationKey(), convertArgs(args)));
    }

    default ITextComponent translate() {
        return new TextComponentString(ModTranslations.format(getTranslationKey()));
    }

    default ITextComponent translateColored(TextFormatting color, Object... args) {
        return translate(args).setStyle(new Style().setColor(color));
    }

    default ITextComponent translateColored(TextFormatting color) {
        return translate().setStyle(new Style().setColor(color));
    }

    /**
     * Convert boolean arguments to "Enabled"/"Disabled" translated text
     * for display purposes. Other arguments are passed through unchanged.
     */
    static Object[] convertArgs(Object[] args) {
        if (args == null || args.length == 0) return args;
        Object[] result = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof Boolean) {
                boolean val = (Boolean) args[i];
                result[i] = val
                        ? ModTranslations.format("wildfire_gender.label.enabled")
                        : ModTranslations.format("wildfire_gender.label.disabled");
            } else {
                result[i] = args[i];
            }
        }
        return result;
    }
}