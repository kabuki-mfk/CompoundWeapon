package com.github.kabuki.compoundweapon.api.weapon;

import com.github.kabuki.compoundweapon.api.weapon.data.IWeaponAttributes;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface ICombatWeapon extends IWeapon{

    IWeaponAttributes getAttributeInstance();

    void release(World worldIn, EntityLivingBase entityIn, ItemStack stack);

}
