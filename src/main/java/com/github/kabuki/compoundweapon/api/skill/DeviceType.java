package com.github.kabuki.compoundweapon.api.skill;

import net.minecraft.util.text.TextFormatting;

public enum DeviceType {
    ATTACK(TextFormatting.YELLOW + "L"),
    INTERACT(TextFormatting.RED + "R"),
    NONE("");

    private String symbol;
    private DeviceType(String symbol)
    {
        this.symbol = symbol;
    }

    public String getSimpleChat()
    {
        return symbol;
    }
}
