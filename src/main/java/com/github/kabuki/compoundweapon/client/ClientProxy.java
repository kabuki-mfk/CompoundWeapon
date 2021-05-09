package com.github.kabuki.compoundweapon.client;

import com.github.kabuki.compoundweapon.client.model.ModelManager;
import com.github.kabuki.compoundweapon.common.CommonProxy;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

public class ClientProxy extends CommonProxy {
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        ModelManager.INSTANCE.initLoadDir(event.getModConfigurationDirectory().getParentFile());
        ModelLoaderRegistry.registerLoader(ModelManager.INSTANCE);
    }

    @Override
    public void init(FMLInitializationEvent event) {

    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {

    }

    @Override
    public void serverStarting(FMLServerStartingEvent event) {

    }
}
