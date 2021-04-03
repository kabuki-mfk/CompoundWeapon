package com.github.kabuki.compoundweapon.weapon.skill;

import com.github.kabuki.compoundweapon.api.skill.DeviceType;
import com.github.kabuki.compoundweapon.api.skill.ISkillRelease;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class SkillReleaseDevice implements ISkillRelease {
    public static final ISkillRelease MISS = new SkillReleaseDevice(DeviceType.NONE);
    private final DeviceType deviceType;
    private Combination combination;

    public SkillReleaseDevice(DeviceType deviceType) {
        this.deviceType = deviceType;
    }

    public SkillReleaseDevice(DeviceType type, Combination combination)
    {
        this(type);
        this.combination = combination;
    }

    public SkillReleaseDevice setCombination(Combination combo)
    {
        if(combination != null)
        {
            this.combination = combo;
        }
        return this;
    }

    @Override
    public boolean accept(DeviceType deviceType, EntityLivingBase entityLivingBase) {
        if(this.deviceType == deviceType)
        {
            return combination == null || combination.equalsState(entityLivingBase);
        }
        return false;
    }

    @Override
    public String getSimpleChat() {
        return deviceType.getSimpleChat() + (combination == null ? "" : combination.getSimpleChat());
    }

    public enum Combination
    {
        SNEAKING {
            @Override
            public boolean equalsState(EntityLivingBase entityLivingBase) {
                return entityLivingBase.isSneaking();
            }
        },
        JUMPING {
            @Override
            public boolean equalsState(EntityLivingBase entityLivingBase) {
                return !entityLivingBase.onGround;
            }
        },
        SPRINTING {
            @Override
            public boolean equalsState(EntityLivingBase entityLivingBase) {
                return entityLivingBase.isSprinting();
            }
        };

        public abstract boolean equalsState(EntityLivingBase entityLivingBase);

        @Nullable
        public static Combination fromString(String s)
        {
            switch (s.toLowerCase()) {
                case "sneaking":
                    return SNEAKING;
                case "jumping":
                    return JUMPING;
                case "sprinting":
                    return SPRINTING;
                default:
                    return null;
            }
        }

        @SideOnly(Side.CLIENT)
        public String getSimpleChat()
        {
            switch (this) {
                case SNEAKING:
                    return TextFormatting.GRAY + "N";
                case JUMPING:
                    return TextFormatting.GREEN + "J";
                case SPRINTING:
                    return TextFormatting.LIGHT_PURPLE + "S";
                default:
                    return "";
            }
        }
    }
}
