package com.github.kabuki.compoundweapon.weapon.attribute;

import com.github.kabuki.compoundweapon.api.weapon.data.IAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class DynamicAttribute implements IAttribute.IDynamicAttribute {

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
    {
    }

    @Override
    public void onEntitySwing(EntityLivingBase entityLiving, ItemStack stack)
    {
    }

    @Override
    public void onRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
    }
}
