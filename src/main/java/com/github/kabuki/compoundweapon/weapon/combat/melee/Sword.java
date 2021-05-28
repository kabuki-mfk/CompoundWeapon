package com.github.kabuki.compoundweapon.weapon.combat.melee;

import com.github.kabuki.compoundweapon.api.weapon.IWeaponMaterial;
import com.github.kabuki.compoundweapon.api.weapon.WeaponType;
import com.github.kabuki.compoundweapon.weapon.combat.CombatWeapon;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.List;

public class Sword extends CombatWeapon {

    private boolean canSweep = true;

    public Sword(IWeaponMaterial material) {
        super(WeaponType.SWORD, material);
    }

    public Sword setCanSweep(boolean canSweep) {
        this.canSweep = canSweep;
        return this;
    }

    public float getSweepingDamageRatio(ItemStack stack)
    {
        return 1.0f;
    }

    @Override
    public void onAttack(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        if(canSweep)
        {
            List<EntityLivingBase> attackEntities = attacker.world.getEntitiesWithinAABB(EntityLivingBase.class, target.getEntityBoundingBox().grow(1.0D, 0.25D, 1.0D));
            for (EntityLivingBase entitylivingbase : attackEntities)
            {
                if (entitylivingbase != attacker && entitylivingbase != target && !attacker.isOnSameTeam(entitylivingbase) && attacker.getDistanceSq(entitylivingbase) < 9.0D)
                {
                    entitylivingbase.knockBack(attacker, 0.4F, MathHelper.sin(attacker.rotationYaw * 0.017453292F),
                            -MathHelper.cos(attacker.rotationYaw * 0.017453292F));
                    entitylivingbase.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) attacker), getSweepingDamageRatio(stack));
                }
            }
        }
    }

    @Override
    public void release(World worldIn, EntityLivingBase entityIn, ItemStack stack) {

    }
}
