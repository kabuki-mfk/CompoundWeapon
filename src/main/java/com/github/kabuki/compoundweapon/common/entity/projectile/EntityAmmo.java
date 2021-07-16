package com.github.kabuki.compoundweapon.common.entity.projectile;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityAmmo extends EntityThrowable {

    public EntityAmmo(World worldIn) {
        super(worldIn);
    }

    public EntityAmmo(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    public EntityAmmo(World worldIn, EntityLivingBase throwerIn) {
        super(worldIn, throwerIn);
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if(result.entityHit != null)
        {

        }
    }
}
