package com.github.kabuki.compoundweapon.common.registries;

import com.github.kabuki.compoundweapon.CompoundWeapon;
import com.github.kabuki.compoundweapon.api.weapon.IWeapon;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Map;

@Mod.EventBusSubscriber(modid = CompoundWeapon.MOD_ID)
public class RegistryEventHandler {

    public RegistryEventHandler()
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public static void onRegisterItem(RegistryEvent.Register<Item> event)
    {
        for(Map.Entry<String, IWeapon> entry : WeaponRegistry.getInstance().getRegistry().entrySet())
        {
            if(entry.getValue() instanceof Item) {
                Item regWeapon = (Item) entry.getValue();
                event.getRegistry().register(regWeapon.setRegistryName(CompoundWeapon.MOD_ID, entry.getKey()));
            }
        }
    }
}
