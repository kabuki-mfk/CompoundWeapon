package com.github.kabuki.compoundweapon.api.skill;

import net.minecraft.entity.EntityLivingBase;

import javax.annotation.Nullable;
import java.util.List;

public interface ISkillSlot {

    int size();

    boolean isEmpty();

    boolean addSkill(ISkill skill);

    boolean setSkill(int slot, ISkill skill);

    StoreSlot getSkillInSlot(int slot);

    List<StoreSlot> getSlots();

    @Nullable
    default StoreSlot applyAndGetSlot(ISkillRelease skillRelease)
    {
        for(StoreSlot slot : getSlots())
        {
            ISkill skill = slot.getSkill();
            if(skillRelease.equals(skill.getDevice()))
            {
                return slot;
            }
        }
        return null;
    }

    default int hasSkillRelease(DeviceType type, EntityLivingBase entityLivingBase)
    {
        for(StoreSlot slot : getSlots())
        {
            if(slot.getSkill().getDevice().accept(type, entityLivingBase))
                return slot.getSlot();
        }
        return -1;
    }

    interface StoreSlot
    {
        int getSlot();

        ISkill getSkill();

        boolean isEmptySlot();
    }
}
