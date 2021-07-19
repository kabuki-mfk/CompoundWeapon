package com.github.kabuki.compoundweapon.api.weapon;

import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;

public class WeaponDamageSource extends DamageSource {
    private final Entity entity;
    private final IWeapon weapon;

    public WeaponDamageSource(Entity source, IWeapon weapon) {
        super("weapon");
        this.entity = source;
        this.weapon = weapon;
    }

    @Nullable
    @Override
    public Entity getTrueSource() { return entity; }

    @Override
    public Vec3d getDamageLocation() {
        return new Vec3d(this.entity.posX, this.entity.posY, this.entity.posZ);
    }

    public IWeapon getDamageWeapon() { return weapon; }
}
