package com.github.kabuki.compoundweapon.common.registries;

import com.github.kabuki.compoundweapon.weapon.material.WeaponMaterial;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class MaterialRegistry extends AbstractRegistry<WeaponMaterial>{
    private static final MaterialRegistry INSTANCE = new MaterialRegistry();
    private final Map<String, WeaponMaterial> registry = new HashMap<>();

    private MaterialRegistry()
    {
    }

    public static MaterialRegistry getInstance()
    {
        return INSTANCE;
    }

    @Override
    public Map<String, WeaponMaterial> getRegistry() {
        return registry;
    }

    @Nullable
    public WeaponMaterial getMaterial(String name) {
        WeaponMaterial material = registry.get(name);
        return material == null ? null : (WeaponMaterial) material.clone();
    }

}
