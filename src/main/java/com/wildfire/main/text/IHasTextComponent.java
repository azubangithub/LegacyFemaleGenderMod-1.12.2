package com.wildfire.main.text;

import net.minecraft.util.text.ITextComponent;

public interface IHasTextComponent {
    ITextComponent getTextComponent();

    interface IHasEnumNameTextComponent extends IHasTextComponent {
    }
}