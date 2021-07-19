package com.github.kabuki.compoundweapon.weapon;

import com.github.kabuki.compoundweapon.api.weapon.IWeapon;
import com.github.kabuki.compoundweapon.api.weapon.IWeaponMaterial;
import com.github.kabuki.compoundweapon.api.weapon.WeaponDamageSource;
import com.github.kabuki.compoundweapon.api.weapon.WeaponType;
import com.github.kabuki.compoundweapon.common.registries.WeaponRegistry;
import com.github.kabuki.compoundweapon.weapon.attribute.WeaponAttributeHelper;
import com.github.kabuki.compoundweapon.client.model.VariantMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.List;

public class Weapon extends Item implements IWeapon {
    public static WeaponRegistry REGISTRY = WeaponRegistry.getInstance();

    private List<VariantMapper> resources = java.util.Collections.emptyList();
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
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
        Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);
        return material.getAttributeModifiers(multimap, slot, stack);
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

    protected DamageSource causeWeaponDamageSource(Entity source) {
        return new WeaponDamageSource(source, this);
    }

    public void onAttack(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
    }

    public Weapon setResources(VariantMapper... resources) {
        return setResources(Lists.newArrayList(resources));
    }

    public Weapon setResources(List<VariantMapper> resources) {
        this.resources = resources;
        return this;
    }

    @Override
    public Collection<VariantMapper> getResource() {
        if(resources.isEmpty())
            return IWeapon.super.getResource();
        return resources;
    }
}
