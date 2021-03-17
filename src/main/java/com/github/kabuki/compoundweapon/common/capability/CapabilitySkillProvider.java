package com.github.kabuki.compoundweapon.common.capability;

import com.github.kabuki.compoundweapon.api.skill.ISkillProvider;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CapabilitySkillProvider {
    @CapabilityInject(ISkillProvider.class)
    public static Capability<ISkillProvider> SKILL_PROVIDER;

    public static class Provider implements ICapabilitySerializable<NBTTagCompound> {
        private final ISkillProvider instance;

        public Provider(ISkillProvider provider)
        {
            this.instance = provider;
        }

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
            return capability == SKILL_PROVIDER;
        }

        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
            return capability == SKILL_PROVIDER ? SKILL_PROVIDER.cast(instance) : null;
        }

        @Override
        public NBTTagCompound serializeNBT() {
            return new NBTTagCompound();
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt) {
            //do nothing
        }
    }

    public static class Storage implements Capability.IStorage<ISkillProvider> {

        @Nullable
        @Override
        public NBTBase writeNBT(Capability<ISkillProvider> capability, ISkillProvider instance, EnumFacing side) {
            return new NBTTagCompound();
        }

        @Override
        public void readNBT(Capability<ISkillProvider> capability, ISkillProvider instance, EnumFacing side, NBTBase nbt) {

        }
    }

}
