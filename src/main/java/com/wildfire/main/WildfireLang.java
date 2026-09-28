/*
 * Wildfire's Female Gender Mod is a female gender mod created for Minecraft.
 * Copyright (C) 2023-present WildfireRomeo
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 */

package com.wildfire.main;

import com.wildfire.main.text.ILangEntry;

public enum WildfireLang implements ILangEntry {
    MOD_NAME("wildfire_gender.name"),
    PACK_DESCRIPTION("wildfire_gender.pack_description"),

    KEY_CATEGORY("category.wildfire_gender.generic"),
    KEY_CONFIG("key.wildfire_gender.gender_menu"),
    KEY_TOGGLE("key.wildfire_gender.toggle"),

    ARMOR_TOOLTIP("armor", "tooltip"),

    HURT_SOUND_SUBTITLE("hurt", "female"),

    PLAYER_LIST_MODE("wildfire_gender.always_show_list"),
    PLAYER_LIST_MODE_MOD_UI("always_show_list", "mod_ui_only"),
    PLAYER_LIST_MODE_MOD_UI_TOOLTIP("always_show_list", "mod_ui_only.tooltip"),
    PLAYER_LIST_MODE_TAB_LIST("always_show_list", "tab_list_open"),
    PLAYER_LIST_MODE_TAB_LIST_TOOLTIP("always_show_list", "tab_list_open.tooltip"),
    PLAYER_LIST_MODE_ALWAYS("always_show_list", "always"),
    PLAYER_LIST_MODE_ALWAYS_TOOLTIP("always_show_list", "always.tooltip"),

    OFF("label", "off"),
    ENABLED("label", "enabled"),
    DISABLED("label", "disabled"),
    GENDER("label", "gender"),
    FEMALE("label", "female"),
    MALE("label", "male"),
    OTHER("label", "other"),
    LABEL_WITH_CREATOR("label", "with_creator"),
    LABEL_WITH_CONTRIBUTOR("label", "with_contributor"),
    LABEL_WITH_BOTH("label", "with_both"),

    TOOLTIP_BOUNCE_WARNING("tooltip", "bounce_warning"),
    TOOLTIP_HIDE_IN_ARMOR("tooltip", "hide_in_armor"),
    TOOLTIP_HURT_SOUNDS("tooltip", "hurt_sounds"),
    TOOLTIP_OVERRIDE_PHYSICS_1("tooltip", "override_armor_physics.line1"),
    TOOLTIP_OVERRIDE_PHYSICS_2("tooltip", "override_armor_physics.line2"),
    TOOLTIP_HOLIDAY_THEMES_1("tooltip", "holiday_themes.line1"),

    NAME_TAG_CREATOR("nametag", "creator"),
    NAME_TAG_CONTRIBUTOR("name_tag", "contributor"),

    HOLIDAY_THEMES("misc", "holiday_themes"),

    BREAST_CUSTOMIZATION_DUAL_PHYSICS("breast_customization", "dual_physics"),
    BREAST_CUSTOMIZATION_TAB_CUSTOMIZATION("breast_customization", "tab_customization"),
    BREAST_CUSTOMIZATION_TAB_PHYSICS("breast_customization", "tab_physics"),
    BREAST_CUSTOMIZATION_TAB_MISC("breast_customization", "tab_miscellaneous"),
    PRESETS_ADD_NEW("breast_customization", "presets.add_new"),
    PRESETS_DELETE("breast_customization", "presets.delete"),

    WARDROBE("wardrobe", "title"),
    WARDROBE_PLAYERS_USING("wardrobe", "players_using_mod"),
    WARDROBE_SLIDER_BREAST_SIZE("wardrobe", "slider.breast_size"),
    WARDROBE_SLIDER_SEPARATION("wardrobe", "slider.separation"),
    WARDROBE_SLIDER_HEIGHT("wardrobe", "slider.height"),
    WARDROBE_SLIDER_DEPTH("wardrobe", "slider.depth"),
    WARDROBE_SLIDER_ROTATION("wardrobe", "slider.rotation"),

    CHAR_SETTINGS("char_settings", "title"),
    CHAR_SETTING_PHYSICS("char_settings", "physics"),
    CHAR_SETTING_HIDE_IN_ARMOR("char_settings", "hide_in_armor"),
    CHAR_SETTING_OVERRIDE_PHYSICS("char_settings", "override_armor_physics"),
    CHAR_SETTING_HURT_SOUNDS("char_settings", "hurt_sounds"),
    CHAR_SETTING_SHOW_ARMOR_STAT("char_settings", "show_armor_stat"),

    CANCER_AWARENESS("cancer_awareness", "title"),
    APPEARANCE_SETTINGS("appearance_settings", "title"),

    SLIDER_BOUNCE("slider", "bounce"),
    SLIDER_FLOPPY("slider", "floppy"),
    SLIDER_VOICE_PITCH("slider", "voice_pitch"),
    ;

    private final String key;

    WildfireLang(String type, String path) {
        this("wildfire_gender." + type + "." + path);
    }

    WildfireLang(String key) {
        this.key = key;
    }

    @Override
    public String getTranslationKey() {
        return key;
    }
}
