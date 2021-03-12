package com.github.kabuki.compoundweapon.api.skill;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ISkillProvider {

    ISkillSlot getSkills();

    int hasApplyRelease(DeviceType type, EntityLivingBase entityLivingBase);

    void release(ISkillRelease skillRelease, World worldIn, BlockPos pos, Entity entityIn, ItemStack stack);

    void release(int slot, World worldIn, BlockPos pos, EntityLivingBase entityIn, ItemStack stack);

    void update(World worldIn, Entity entityIn, ItemStack stack);
}
