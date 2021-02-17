package com.github.kabuki.compoundweapon.api.weapon;

import net.minecraft.util.ResourceLocation;

public interface IWeapon {
    default ResourceLocation getResource() { return new ResourceLocation("iron_sword"); }

    default WeaponType getType() { return WeaponType.SWORD; }

    default IWeaponMaterial getMaterial()
    {
        return null;
    }
}
