package com.github.kabuki.compoundweapon.common.registries;

import com.github.kabuki.compoundweapon.api.weapon.IWeapon;

import java.util.HashMap;
import java.util.Map;

public class WeaponRegistry extends AbstractRegistry<IWeapon> {
    private static final WeaponRegistry INSTANCE = new WeaponRegistry();
    private final Map<String, IWeapon> registry = new HashMap<>();

    private WeaponRegistry()
    {
    }

    public static WeaponRegistry getInstance()
    {
        return INSTANCE;
    }

    @Override
    public Map<String, IWeapon> getRegistry() {
        return registry;
    }
}
