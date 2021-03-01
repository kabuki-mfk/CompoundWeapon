package com.github.kabuki.compoundweapon.api.weapon.data;

import com.github.kabuki.compoundweapon.api.data.IDataEntryBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public interface IAttribute {

    interface IDataAttribute extends IAttribute{
        IDataEntryBase getValue();

        void setValue(IDataEntryBase data);
    }

    interface IDynamicAttribute {
        void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected);

        void onEntitySwing(EntityLivingBase entityLiving, ItemStack stack);

        void onRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn);
    }
}
