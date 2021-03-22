package com.github.kabuki.compoundweapon.weapon.material;

import com.github.kabuki.compoundweapon.CompoundWeapon;
import com.github.kabuki.compoundweapon.api.weapon.IWeaponMaterial;
import com.github.kabuki.compoundweapon.api.weapon.data.IWeaponAttributes;
import com.github.kabuki.compoundweapon.common.registries.MaterialRegistry;
import com.github.kabuki.compoundweapon.weapon.attribute.Attribute;
import com.github.kabuki.compoundweapon.weapon.attribute.WeaponAttributes;

public class WeaponMaterial implements IWeaponMaterial, Cloneable {
    public static final MaterialRegistry REGISTRY = MaterialRegistry.getInstance();

    private IWeaponAttributes attributes = new WeaponAttributes();
    private String name;

    public WeaponMaterial(float damage, float speed, int durability) {
        attributes.setAttribute("damage", new Attribute(damage));
        attributes.setAttribute("speed", new Attribute(speed));
        attributes.setAttribute("durability", new Attribute(durability));
    }

    public WeaponMaterial(String name, float damage, float speed, int durability) {
        this(damage, speed, durability);
        setName(name);
    }

    public WeaponMaterial(String name, IWeaponAttributes attributes) {
        setName(name);
        this.attributes = attributes;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public IWeaponAttributes getAttributeInstance() {
        return attributes;
    }

    @Override
    public Object clone() {
        Object clone = null;
        try {
            clone = super.clone();
        } catch (CloneNotSupportedException e) {
            CompoundWeapon.LOGGER.error("it cannot be happen");
        }
        return clone;
    }
}
