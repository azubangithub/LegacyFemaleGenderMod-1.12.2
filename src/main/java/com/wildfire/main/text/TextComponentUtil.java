package com.wildfire.main.text;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class TextComponentUtil {

    private TextComponentUtil() {
    }

    public static ITextComponent getString(String text) {
        return new TextComponentString(text);
    }

    public static ITextComponent translate(String key, Object... args) {
        return new TextComponentTranslation(key, args);
    }

    public static ITextComponent build(Object... components) {
        ITextComponent result = null;
        Style currentStyle = new Style();
        for (Object component : components) {
            if (component instanceof TextFormatting) {
                currentStyle.setColor((TextFormatting) component);
            } else {
                ITextComponent current;
                if (component instanceof IHasTextComponent) {
                    current = ((IHasTextComponent) component).getTextComponent().createCopy();
                } else if (component instanceof ITextComponent) {
                    current = ((ITextComponent) component).createCopy();
                } else if (component instanceof IHasTranslationKey) {
                    String key = ((IHasTranslationKey) component).getTranslationKey();
                    current = new TextComponentString(ModTranslations.format(key));
                } else {
                    current = getString(String.valueOf(component));
                }
                if (currentStyle.getColor() != null) {
                    current.setStyle(currentStyle.createShallowCopy());
                }
                if (result == null) {
                    result = current;
                } else {
                    result.appendSibling(current);
                }
            }
        }
        return result != null ? result : getString("");
    }
}