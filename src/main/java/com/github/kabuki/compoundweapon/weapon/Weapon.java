package com.github.kabuki.compoundweapon.weapon;

import com.github.kabuki.compoundweapon.api.weapon.IWeapon;
import com.github.kabuki.compoundweapon.api.weapon.IWeaponMaterial;
import com.github.kabuki.compoundweapon.api.weapon.WeaponType;
import com.github.kabuki.compoundweapon.api.weapon.data.IWeaponAttributes;
import com.github.kabuki.compoundweapon.common.registries.WeaponRegistry;
import com.github.kabuki.compoundweapon.weapon.attribute.WeaponAttributeHelper;
import com.github.kabuki.compoundweapon.weapon.material.WeaponMaterial;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class Weapon extends Item implements IWeapon {
    public static WeaponRegistry REGISTRY = WeaponRegistry.getInstance();

    protected WeaponType type;
    protected IWeaponMaterial material;
    protected boolean isOverrideMaterial;
    private int durability;

    public Weapon(WeaponType type, IWeaponMaterial material)
    {
        this.type = type;
        this.material = material;
        setMaxStackSize(1);
        this.durability = WeaponAttributeHelper
                .getDataAttributeValue(material.getAttributeInstance(), "durability", Integer.class)
                .orElse(100);
        this.setMaxDamage(durability);
    }

    public void setIsOverrideMaterial()
    {
        this.isOverrideMaterial = true;
    }

    public void setOverrideDurability(int durability) {
        setIsOverrideMaterial();
        this.durability = durability;
    }

    @Override
    public WeaponType getType() {
        return type;
    }

    @Override
    public IWeaponMaterial getMaterial() {
        return material;
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return isOverrideMaterial ? durability : super.getMaxDamage(stack);
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        WeaponAttributeHelper.handleDynamicAttribute(material.getAttributeInstance(),
                attr -> attr.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected));
        super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    @Override
    public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack) {
        WeaponAttributeHelper.handleDynamicAttribute(material.getAttributeInstance(),
                attr -> attr.onEntitySwing(entityLiving, stack));
        return super.onEntitySwing(entityLiving, stack);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        WeaponAttributeHelper.handleDynamicAttribute(material.getAttributeInstance(),
                attr -> attr.onRightClick(worldIn, playerIn, handIn));
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

}
