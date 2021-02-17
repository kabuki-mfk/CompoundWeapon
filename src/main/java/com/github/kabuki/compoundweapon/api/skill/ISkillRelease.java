package com.github.kabuki.compoundweapon.api.skill;

import net.minecraft.entity.EntityLivingBase;

public interface ISkillRelease {

    boolean accept(DeviceType deviceType, EntityLivingBase entityLivingBase);

    String getSimpleChat();
}
