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
        super.preInit(event);
        ModelManager.INSTANCE.initLoadDir(event.getModConfigurationDirectory().getParentFile());
        ModelLoaderRegistry.registerLoader(ModelManager.INSTANCE);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        ModelManager.INSTANCE.registerItemRender();
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
    }

    @Override
    public void serverStarting(FMLServerStartingEvent event) {
        super.serverStarting(event);
    }
}
