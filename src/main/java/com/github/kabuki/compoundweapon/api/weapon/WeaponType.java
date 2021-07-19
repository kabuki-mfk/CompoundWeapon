package com.github.kabuki.compoundweapon.api.weapon;

import com.github.kabuki.compoundweapon.CompoundWeapon;
import com.github.kabuki.compoundweapon.api.skill.ISkill;
import com.github.kabuki.compoundweapon.client.model.ModelType;
import com.github.kabuki.compoundweapon.client.model.VariantMapper;
import com.github.kabuki.compoundweapon.weapon.Weapon;
import com.github.kabuki.compoundweapon.weapon.combat.CombatWeapon;
import com.github.kabuki.compoundweapon.weapon.combat.melee.Sword;
import com.google.common.collect.Lists;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

public enum WeaponType {
    SWORD("sword", new AbstractWeaponBuilder<IWeapon>() {
        @Override
        protected IWeapon preBuild() {
            return new Sword(material);
        }
    });

    private final String typeName;
    private final AbstractWeaponBuilder<? extends IWeapon> factory;

    WeaponType(String name, AbstractWeaponBuilder<? extends IWeapon> weaponFactory) {
        this.typeName = name;
        this.factory = weaponFactory;
    }

    public String getTypeName() {
        return typeName;
    }

    public AbstractWeaponBuilder<? extends IWeapon> builder() {
        return (AbstractWeaponBuilder<? extends IWeapon>) factory.clone();
    }

    public abstract static class AbstractWeaponBuilder<T extends IWeapon> implements Cloneable {
        protected IWeaponMaterial material;
        protected final List<ISkill> skills = Lists.newArrayList();
        protected String name;

        protected float speed;
        protected float damage;
        protected int durability;
        protected List<VariantMapper> model = Lists.newArrayList();

        protected static final byte DAMAGE = 0x01;
        protected static final byte DURABILITY = 0x02;
        protected static final byte SPEED = 0x04;
        private byte override_flag = 0;
        
        protected void setOverrideFlag(byte flag) {
            override_flag |= flag;
        }

        protected boolean hasOverrideFlag(byte flag) {
            return (override_flag & flag) != 0;
        }

        public AbstractWeaponBuilder<T> material(IWeaponMaterial material) {
            this.material = checkNotNull(material, "material");
            return this;
        }

        public AbstractWeaponBuilder<T> speed(float speed) {
            this.speed = speed;
            setOverrideFlag(SPEED);
            return this;
        }

        public AbstractWeaponBuilder<T> damage(float damage) {
            this.damage = damage;
            setOverrideFlag(DAMAGE);
            return this;
        }

        public AbstractWeaponBuilder<T> durability(int durability) {
            this.durability = durability;
            setOverrideFlag(DURABILITY);
            return this;
        }

        public AbstractWeaponBuilder<T> skill(ISkill skill) {
            Objects.requireNonNull(skill, "skill");
            if(skills.contains(skill))
                throw new IllegalArgumentException("skill already exists");
            skills.add(skill);
            return this;
        }

        public AbstractWeaponBuilder<T> name(String name) {
            this.name = checkNotNull(name, "name");
            return this;
        }

        public AbstractWeaponBuilder<T> model(String model) {
            if(StringUtils.isNullOrEmpty(model)) throw new IllegalArgumentException("parameter is null or empty");

            if(model.contains(":")) {
                ModelResourceLocation modelResourceLocation = new ModelResourceLocation(model);
                this.model.add(new VariantMapper(0, Pair.of(modelResourceLocation.getVariant(), modelResourceLocation)));
            }
            else {
                this.model.add(new VariantMapper(0, "inventory",
                        model, model.endsWith(".obj") ? ModelType.OBJ : ModelType.JSON));
            }
            return this;
        }

        public T build() {
            T weapon = preBuild();

            if(weapon instanceof Weapon) {
                Weapon w = ((Weapon) weapon);
                w.setResources(model);
                if(hasOverrideFlag(DURABILITY))
                {
                    w.setOverrideDurability(durability);
                }
            }

            if(weapon instanceof CombatWeapon) {
                CombatWeapon cw = ((CombatWeapon) weapon);
                for(ISkill skill : skills)
                    cw.getSkillSlots().addSkill(skill);
                if(hasOverrideFlag(DAMAGE))
                {
                    cw.setOverrideDamage(damage);
                }
                if(hasOverrideFlag(SPEED))
                {
                    cw.setOverrideSpeed(speed);
                }
            }

            return weapon;
        }

        protected abstract T preBuild();

        @Override
        protected Object clone() {
            Object clone = null;
            try {
                clone = super.clone();
            } catch (CloneNotSupportedException e) {
                CompoundWeapon.LOGGER.error("it cannot happen", e);
            }
            return clone;
        }
    }
}
