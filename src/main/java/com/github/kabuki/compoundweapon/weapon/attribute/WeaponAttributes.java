package com.github.kabuki.compoundweapon.weapon.attribute;

import com.github.kabuki.compoundweapon.api.weapon.data.IAttribute;
import com.github.kabuki.compoundweapon.api.weapon.data.IWeaponAttributes;
import com.google.common.collect.Maps;

import java.util.Map;

public class WeaponAttributes implements IWeaponAttributes {

    private final Map<String, IAttribute> attributes = Maps.newHashMap();

    @Override
    public Map<String, IAttribute> toMap() {
        return attributes;
    }
}
