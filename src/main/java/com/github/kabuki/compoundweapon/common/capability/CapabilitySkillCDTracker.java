package com.github.kabuki.compoundweapon.common.capability;

import com.github.kabuki.compoundweapon.weapon.skill.service.ISkillCDTracker;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;

public class CapabilitySkillCDTracker {
    @CapabilityInject(ISkillCDTracker.class)
    public static Capability<ISkillCDTracker> SKILL_COOLDOWN_TRACKER;

    public static class Provider implements ICapabilityProvider {
        private final ISkillCDTracker instance;

        public Provider(ISkillCDTracker tracker) {
            this.instance = tracker;
        }

        @Override
        public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
            return capability == SKILL_COOLDOWN_TRACKER;
        }

        @Override
        public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
            return capability == SKILL_COOLDOWN_TRACKER ? SKILL_COOLDOWN_TRACKER.cast(instance) : null;
        }
    }

    public static class Storage implements Capability.IStorage<ISkillCDTracker>
    {
        @Nullable
        @Override
        public NBTBase writeNBT(Capability<ISkillCDTracker> capability, ISkillCDTracker instance, EnumFacing side) {
            return new NBTTagCompound();
        }

        @Override
        public void readNBT(Capability<ISkillCDTracker> capability, ISkillCDTracker instance, EnumFacing side,
                            NBTBase nbt) {
            //do nothing
        }
    }
}
