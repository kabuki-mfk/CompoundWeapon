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

    public static DeviceType fromString(String str) {
        switch(str.toLowerCase())
        {
            case "attack":
                return ATTACK;
            case "interact":
                return INTERACT;
            default:
                return NONE;
        }
    }

    public String getSimpleChat()
    {
        return symbol;
    }
}
