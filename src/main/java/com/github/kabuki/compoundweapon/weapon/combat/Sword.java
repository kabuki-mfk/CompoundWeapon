package com.github.kabuki.compoundweapon.weapon.combat;

import com.github.kabuki.compoundweapon.api.weapon.IWeaponMaterial;
import com.github.kabuki.compoundweapon.api.weapon.WeaponType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class Sword extends CombatWeapon{

    public Sword(IWeaponMaterial material) {
        super(WeaponType.SWORD, material);
    }

    @Override
    public void release(World worldIn, EntityLivingBase entityIn, ItemStack stack) {

    }
}
