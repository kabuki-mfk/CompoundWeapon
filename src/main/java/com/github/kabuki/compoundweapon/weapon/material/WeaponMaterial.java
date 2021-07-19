package com.github.kabuki.compoundweapon.weapon.material;

import com.github.kabuki.compoundweapon.CompoundWeapon;
import com.github.kabuki.compoundweapon.api.weapon.IWeaponMaterial;
import com.github.kabuki.compoundweapon.api.weapon.data.IWeaponAttributes;
import com.github.kabuki.compoundweapon.common.registries.MaterialRegistry;
import com.github.kabuki.compoundweapon.weapon.attribute.Attribute;
import com.github.kabuki.compoundweapon.weapon.attribute.WeaponAttributes;
import com.google.common.collect.Multimap;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

import java.util.UUID;

public class WeaponMaterial implements IWeaponMaterial, Cloneable {
    public static final MaterialRegistry REGISTRY = MaterialRegistry.getInstance();
    protected static final UUID ATTACK_DAMAGE_MODIFIER = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
    protected static final UUID ATTACK_SPEED_MODIFIER = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");

    protected float damage;
    protected float speed;
    protected int durability;
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
    public Multimap<String, AttributeModifier> getAttributeModifiers(Multimap<String, AttributeModifier> map, EntityEquipmentSlot slot, ItemStack stack) {
        map.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", damage, 0));
        map.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", speed, 0));
        return map;
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
