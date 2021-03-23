package com.github.kabuki.compoundweapon.api.skill;

import com.github.kabuki.compoundweapon.api.skill.DeviceType;
import com.github.kabuki.compoundweapon.api.skill.ISkillProvider;
import com.github.kabuki.compoundweapon.api.skill.SkillDamageSource;
import com.github.kabuki.compoundweapon.api.skill.service.ISideTaskService;
import com.github.kabuki.compoundweapon.api.weapon.ICombatWeapon;
import com.github.kabuki.compoundweapon.client.SkillTaskClientService;
import com.github.kabuki.compoundweapon.common.capability.CapabilitySkillProvider;
import com.github.kabuki.compoundweapon.weapon.skill.service.SkillTaskServerService;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.relauncher.Side;

public class SkillAPI {
    public static ISideTaskService getTimerService(Side side)
    {
        if(side == Side.SERVER)
        {
            return SkillTaskServerService.getInstance();
        }
        else
        {
            return SkillTaskClientService.getInstance();
        }
    }

    public static boolean releaseSkill(EntityLivingBase entitylivingbase)
    {
        DeviceType type;
        EnumHand hand = entitylivingbase.getActiveHand();
        ItemStack stack = entitylivingbase.getHeldItem(entitylivingbase.getActiveHand());
        if(!stack.isEmpty() && stack.hasCapability(CapabilitySkillProvider.SKILL_PROVIDER, null))
        {
            if(stack.getItem() instanceof ItemBow)
            {
                type = DeviceType.INTERACT;
            }
            else
            {
                type = hand == EnumHand.MAIN_HAND ? DeviceType.ATTACK : DeviceType.INTERACT;
            }

            return releaseSkill(stack, entitylivingbase, type);
        }
        else
        {
            return false;
        }
    }

    public static boolean releaseSkill(ItemStack itemStackHeldIn, EntityLivingBase entityLivingBase, DeviceType type)
    {
        ISkillProvider provider = itemStackHeldIn.getCapability(CapabilitySkillProvider.SKILL_PROVIDER, null);
        if(provider == null) return false;
        int slot = provider.hasApplyRelease(type, entityLivingBase);
        if(slot != -1)
        {
            provider.release(slot, entityLivingBase.world, entityLivingBase.getPosition(), entityLivingBase, itemStackHeldIn);
            if(itemStackHeldIn.getItem() instanceof ICombatWeapon)
            {
                ((ICombatWeapon)itemStackHeldIn.getItem()).release(entityLivingBase.world, entityLivingBase, itemStackHeldIn);
            }
            return true;
        }
        else
        {
            return false;
        }
    }

    public static DamageSource causeSkillDamageSource(Entity source)
    {
        return new SkillDamageSource(source);
    }
}
