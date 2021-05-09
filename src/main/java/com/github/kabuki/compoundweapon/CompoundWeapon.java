package com.github.kabuki.compoundweapon;

import com.github.kabuki.compoundweapon.client.model.CustomResourceLocation;
import com.github.kabuki.compoundweapon.client.model.ModelManager;
import com.github.kabuki.compoundweapon.common.CommonProxy;
import com.github.kabuki.compoundweapon.designer.skill.SkillDeserializer;
import com.github.kabuki.compoundweapon.designer.weapon.WeaponDeserializer;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

@Mod(
        modid = CompoundWeapon.MOD_ID,
        name = CompoundWeapon.MOD_NAME,
        version = CompoundWeapon.VERSION,
        acceptedMinecraftVersions = "1.12.2"
)
public class CompoundWeapon {

    public static final String MOD_ID = "compound_weapon";
    public static final String MOD_NAME = "CompoundWeapon";
    public static final String VERSION = "1.0.0";

    @Mod.Instance(MOD_ID)
    public static CompoundWeapon INSTANCE;

    public static Logger LOGGER;

    @SidedProxy(
            clientSide = "com.github.kabuki.compoundweapon.client.ClientProxy",
            serverSide = "com.github.kabuki.compoundweapon.common.CommonProxy",
            modId = MOD_ID
    )
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LOGGER = event.getModLog();
        proxy.preInit(event);

        File dir = event.getModConfigurationDirectory().getParentFile();
        CustomResourceLocation.modelDir = new File(dir, "mods/CompoundWeapon/Custom/Model");
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @GameRegistry.ObjectHolder(MOD_ID)
    public static class Blocks {
    }

    @GameRegistry.ObjectHolder(MOD_ID)
    public static class Items {
    }
}
