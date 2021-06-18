package com.github.kabuki.compoundweapon.logic;

import com.github.kabuki.compoundweapon.CompoundWeapon;
import com.github.kabuki.compoundweapon.api.skill.DeviceType;
import com.github.kabuki.compoundweapon.api.skill.SkillAPI;
import com.github.kabuki.compoundweapon.client.SkillTaskClientService;
import com.github.kabuki.compoundweapon.common.capability.CapabilitySkillCDTracker;
import com.github.kabuki.compoundweapon.weapon.skill.service.ISkillCDTracker;
import com.github.kabuki.compoundweapon.weapon.skill.service.SkillTaskServerService;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod.EventBusSubscriber(modid = CompoundWeapon.MOD_ID)
public class SkillHandler {

    @SubscribeEvent
    public static void onEntityUpdate(LivingEvent.LivingUpdateEvent event)
    {
        if(event.getEntityLiving() != null) return;

        ISkillCDTracker tracker = event.getEntityLiving().getCapability(CapabilitySkillCDTracker.SKILL_COOLDOWN_TRACKER, null);
        if (tracker == null) return;

        tracker.update();
    }

    @SubscribeEvent
    public static void onEntityLivingStartUseItem(LivingEntityUseItemEvent.Start event)
    {
        SkillAPI.releaseSkill(event.getItem(), event.getEntityLiving(), DeviceType.INTERACT);
    }

    @SubscribeEvent
    public static void updateTick(TickEvent.WorldTickEvent event)
    {
        SkillTaskServerService.getInstance().update();
        SkillTaskClientService.getInstance().update();
    }
}
