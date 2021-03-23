package com.github.kabuki.compoundweapon.common;

import com.github.kabuki.compoundweapon.api.skill.ISkillProvider;
import com.github.kabuki.compoundweapon.common.capability.CapabilitySkillCDTracker;
import com.github.kabuki.compoundweapon.common.capability.CapabilitySkillProvider;
import com.github.kabuki.compoundweapon.weapon.skill.SkillProvider;
import com.github.kabuki.compoundweapon.weapon.skill.service.ISkillCDTracker;
import com.github.kabuki.compoundweapon.weapon.skill.service.SkillCDTracker;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

public class CommonProxy {
    public void preInit(FMLPreInitializationEvent event) {
        CapabilityManager.INSTANCE.register(ISkillProvider.class, new CapabilitySkillProvider.Storage(), SkillProvider::new);
        CapabilityManager.INSTANCE.register(ISkillCDTracker.class, new CapabilitySkillCDTracker.Storage(), SkillCDTracker::new);
    }

    public void init(FMLInitializationEvent event) {

    }

    public void postInit(FMLPostInitializationEvent event) {

    }

    public void serverStarting(FMLServerStartingEvent event) {

    }
}
