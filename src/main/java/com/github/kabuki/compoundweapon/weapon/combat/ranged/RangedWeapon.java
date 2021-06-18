package com.github.kabuki.compoundweapon.weapon.combat.ranged;

import com.github.kabuki.compoundweapon.api.weapon.IWeaponMaterial;
import com.github.kabuki.compoundweapon.api.weapon.WeaponType;
import com.github.kabuki.compoundweapon.weapon.combat.CombatWeapon;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public abstract class RangedWeapon extends CombatWeapon {

    private Ammo ammoType;

    public RangedWeapon(WeaponType type, IWeaponMaterial material) {
        super(type, material);
    }

    public RangedWeapon(WeaponType type, IWeaponMaterial material, float damage, float speed) {
        super(type, material, damage, speed);
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 1000;
    }

    @Override
    public void release(World worldIn, EntityLivingBase entityIn, ItemStack stack) {

    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack ammo = findAmmo(playerIn);
        boolean flag = playerIn.capabilities.isCreativeMode;

        if(!ammo.isEmpty() || flag) {
            return new ActionResult<>(EnumActionResult.SUCCESS, ammo);
        }
        return new ActionResult<ItemStack>(EnumActionResult.FAIL, ammo);
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
        if(entityLiving instanceof EntityPlayer) {
            stack.damageItem(1, entityLiving);
            onShot(worldIn, entityLiving);

            ItemStack ammo = findAmmo((EntityPlayer) entityLiving);
            EntityThrowable entityAmmo = Ammo.castEntity(entityLiving, ammo);
            if(entityAmmo != null) {
                entityAmmo.shoot(entityLiving, entityLiving.rotationPitch, entityLiving.rotationYaw, 0.0F, 2.1F, 1.0F);
                worldIn.spawnEntity(entityAmmo);
            }
        }
    }

    protected ItemStack findAmmo(EntityPlayer playerIn)
    {
        if(isAmmo(playerIn.getHeldItem(EnumHand.MAIN_HAND))) {
            return playerIn.getHeldItem(EnumHand.MAIN_HAND);
        }
        else if(isAmmo(playerIn.getHeldItem(EnumHand.OFF_HAND))) {
            return playerIn.getHeldItem(EnumHand.OFF_HAND);
        }
        else if(!playerIn.inventory.isEmpty()) {
            for(int i = 0; i < playerIn.inventory.getSizeInventory(); i++)
            {
                ItemStack stack = playerIn.inventory.getStackInSlot(i);
                if(isAmmo(stack))
                {
                    return stack;
                }
            }
        }

        return ItemStack.EMPTY;
    }

    protected boolean isAmmo(ItemStack itemStack) {
        return itemStack.getItem() instanceof Ammo;
    }

    protected abstract void onShot(World worldIn, EntityLivingBase entityLiving);
}
