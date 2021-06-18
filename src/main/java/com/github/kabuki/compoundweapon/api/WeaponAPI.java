package com.github.kabuki.compoundweapon.api;

import com.github.kabuki.compoundweapon.api.weapon.IWeapon;
import com.github.kabuki.compoundweapon.api.weapon.WeaponType;
import com.github.kabuki.compoundweapon.common.registries.WeaponRegistry;
import net.minecraftforge.common.util.EnumHelper;

public class WeaponAPI {
    public static WeaponType createWeaponType(String typeName, WeaponType.AbstractWeaponBuilder<? extends IWeapon> typeBuilder)
    {
        return EnumHelper.addEnum(WeaponType.class, typeName,
                new Class<?>[] {String.class, WeaponType.AbstractWeaponBuilder.class},
                typeName, typeBuilder);
    }

    public static void registerWeapon(String registryName, IWeapon weapon)
    {
        WeaponRegistry.getInstance().register(registryName, weapon);
    }
}
