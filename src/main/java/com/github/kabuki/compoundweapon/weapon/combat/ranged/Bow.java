package com.github.kabuki.compoundweapon.weapon.combat.ranged;

import com.github.kabuki.compoundweapon.api.weapon.IWeaponMaterial;
import com.github.kabuki.compoundweapon.api.weapon.WeaponType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public class Bow extends RangedWeapon {
    public Bow(WeaponType type, IWeaponMaterial material) {
        super(WeaponType.SWORD, material);
    }

    @Override
    protected void onShot(World worldIn, EntityLivingBase entityLiving) {

    }
}
