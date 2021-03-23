package com.github.kabuki.compoundweapon.api.skill;

import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;

public class SkillDamageSource extends DamageSource {
    private Entity entity;

    public SkillDamageSource(Entity source) {
        super("skill");
        this.entity = source;
    }

    @Override
    public Entity getTrueSource() {
        return entity;
    }

    @Override
    public Vec3d getDamageLocation() {
        return new Vec3d(this.entity.posX, this.entity.posY, this.entity.posZ);
    }
}
