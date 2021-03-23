package com.github.kabuki.compoundweapon.weapon.combat;

import com.github.kabuki.compoundweapon.api.weapon.ICombatWeapon;
import com.github.kabuki.compoundweapon.api.weapon.IWeaponMaterial;
import com.github.kabuki.compoundweapon.api.weapon.WeaponType;
import com.github.kabuki.compoundweapon.api.weapon.data.IWeaponAttributes;
import com.github.kabuki.compoundweapon.common.capability.CapabilitySkillProvider;
import com.github.kabuki.compoundweapon.weapon.Weapon;
import com.github.kabuki.compoundweapon.weapon.attribute.Attribute;
import com.github.kabuki.compoundweapon.weapon.skill.AbstractCoolDownSlot;
import com.github.kabuki.compoundweapon.weapon.skill.SkillProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;

public abstract class CombatWeapon extends Weapon implements ICombatWeapon {

    private float attackDamage;
    private float attackSpeed;
    private AbstractCoolDownSlot skillslots = new AbstractCoolDownSlot.AsyncCoolDownSlot(getSkillSlotSize());

    public CombatWeapon(WeaponType type, IWeaponMaterial material) {
        this(type, material, 5, 1);
    }

    public CombatWeapon(WeaponType type, IWeaponMaterial material, float damage, float speed) {
        super(type, material);
        this.attackDamage = damage;
        this.attackSpeed = speed;
    }

    public void setOverrideDamage(float attackDamage) {
        setIsOverrideMaterial();
        this.attackDamage = attackDamage;

    }

    public void setOverrideSpeed(float attackSpeed) {
        setIsOverrideMaterial();
        this.attackSpeed = attackSpeed;
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return hasSkill() ? new CapabilitySkillProvider.Provider(new SkillProvider(skillslots)) :
                super.initCapabilities(stack, nbt);
    }

    public boolean hasSkill() {
        return true;
    }

    public int getSkillSlotSize() {
        return 4;
    }

    public AbstractCoolDownSlot getSkillslots() {
        return skillslots;
    }
}
