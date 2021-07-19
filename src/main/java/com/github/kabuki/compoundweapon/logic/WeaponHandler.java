package com.github.kabuki.compoundweapon.logic;

import com.github.kabuki.compoundweapon.CompoundWeapon;
import com.github.kabuki.compoundweapon.api.weapon.WeaponDamageSource;
import com.github.kabuki.compoundweapon.weapon.Weapon;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = CompoundWeapon.MOD_ID)
public class WeaponHandler {
    @SubscribeEvent
    public static void onAttack(LivingAttackEvent event)
    {
        if(event.getSource() instanceof WeaponDamageSource) return;

        Entity entity = event.getSource().getTrueSource();
        if(entity instanceof EntityLivingBase)
        {
            ItemStack stack = ((EntityLivingBase)entity).getHeldItemMainhand();
            if(stack.getItem() instanceof Weapon)
            {
                ((Weapon)stack.getItem()).onAttack(stack, event.getEntityLiving(), (EntityLivingBase) entity);
            }
        }
    }
}
