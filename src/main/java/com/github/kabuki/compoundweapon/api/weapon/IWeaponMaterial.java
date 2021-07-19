package com.github.kabuki.compoundweapon.api.weapon;

import com.github.kabuki.compoundweapon.api.weapon.data.IWeaponAttributes;
import com.google.common.collect.Multimap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public interface IWeaponMaterial {

    IWeaponAttributes getAttributeInstance();

    default Multimap<String, AttributeModifier> getAttributeModifiers(Multimap<String, AttributeModifier> map, EntityEquipmentSlot slot, ItemStack stack) {
        return map;
    }
}
