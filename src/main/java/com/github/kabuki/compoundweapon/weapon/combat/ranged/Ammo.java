package com.github.kabuki.compoundweapon.weapon.combat.ranged;

import com.github.kabuki.compoundweapon.common.entity.projectile.EntityAmmo;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Objects;

public class Ammo extends Item {

    public Ammo()
    {
        this.setMaxStackSize(getMaxCount());
    }

    public int getMaxCount()
    {
        return 32;
    }

    public static EntityThrowable castEntity(EntityLivingBase throwerIn, ItemStack itemAmmo)
    {
        Objects.requireNonNull(throwerIn);
        if(itemAmmo.getItem() instanceof Ammo) {
            itemAmmo.shrink(1);
            return new EntityAmmo(throwerIn.world);
        }
        return null;
    }
}
