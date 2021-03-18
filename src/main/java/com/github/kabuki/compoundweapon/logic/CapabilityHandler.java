package com.github.kabuki.compoundweapon.logic;

import com.github.kabuki.compoundweapon.CompoundWeapon;
import com.github.kabuki.compoundweapon.common.capability.CapabilitySkillCDTracker;
import com.github.kabuki.compoundweapon.weapon.skill.service.SkillCDTracker;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = CompoundWeapon.MOD_ID)
public class CapabilityHandler {
    public CapabilityHandler()
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public static void onAttachEntityCapability(AttachCapabilitiesEvent<Entity> event)
    {
        if(event.getObject() instanceof EntityPlayer)
        {
            event.addCapability(new ResourceLocation(CompoundWeapon.MOD_ID, "cd_tracker"),
                    new CapabilitySkillCDTracker.Provider(new SkillCDTracker()));
        }
    }
}
