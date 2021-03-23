package com.github.kabuki.compoundweapon.common.registries;

import com.github.kabuki.compoundweapon.weapon.Weapon;

import java.util.HashMap;
import java.util.Map;

public class WeaponRegistry extends AbstractRegistry<Weapon> {
    private static final WeaponRegistry INSTANCE = new WeaponRegistry();
    private final Map<String, Weapon> registry = new HashMap<>();

    private WeaponRegistry()
    {
    }

    public static WeaponRegistry getInstance()
    {
        return INSTANCE;
    }

    @Override
    public Map<String, Weapon> getRegistry() {
        return registry;
    }
}
