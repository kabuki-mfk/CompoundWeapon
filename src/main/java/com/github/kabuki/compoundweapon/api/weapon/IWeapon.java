package com.github.kabuki.compoundweapon.api.weapon;

import com.github.kabuki.compoundweapon.client.model.VariantMapper;
import net.minecraft.util.ResourceLocation;

import java.util.Collection;

public interface IWeapon {
    default Collection<VariantMapper> getResource() {
        return com.google.common.collect.Sets.newHashSet(
            new VariantMapper(0, "inventory", new ResourceLocation("iron_sword"))
        );
    }

    default WeaponType getType() { return WeaponType.SWORD; }

    default IWeaponMaterial getMaterial()
    {
        return null;
    }
}
