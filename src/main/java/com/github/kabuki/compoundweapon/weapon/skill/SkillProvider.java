package com.github.kabuki.compoundweapon.weapon.skill;

import com.github.kabuki.compoundweapon.api.skill.SkillAPI;
import com.github.kabuki.compoundweapon.api.skill.*;
import com.github.kabuki.compoundweapon.api.skill.service.DelayedTask;
import com.github.kabuki.compoundweapon.api.skill.service.ISideTaskService;
import com.github.kabuki.compoundweapon.api.skill.service.ISkillContext;
import com.github.kabuki.compoundweapon.api.weapon.IWeapon;
import com.github.kabuki.compoundweapon.api.weapon.data.IAttribute;
import com.github.kabuki.compoundweapon.common.capability.CapabilitySkillCDTracker;
import com.github.kabuki.compoundweapon.weapon.skill.service.ISkillCDTracker;
import com.github.kabuki.compoundweapon.weapon.skill.service.SkillContextImpl;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Map;

public class SkillProvider implements ISkillProvider {

    private final AbstractCoolDownSlot cooldownTracker;

    public SkillProvider() {
        this(null);
    }

    public SkillProvider(AbstractCoolDownSlot slots)
    {
        this.cooldownTracker = slots;
    }

    private ISkillCDTracker getCDTracker(Entity entity)
    {
        ISkillCDTracker cdTracker = null;
        if(entity instanceof EntityPlayer)
        {
            cdTracker = entity.getCapability(CapabilitySkillCDTracker.SKILL_COOLDOWN_TRACKER, null);
            if(cdTracker == null)
            {
                throw new RuntimeException("cannot found capability of skillcooldown, this is a bug");
            }
        }
        return cdTracker;
    }

    @Override
    public ISkillSlot getSkills() {
        return cooldownTracker;
    }

    @Override
    public int hasApplyRelease(DeviceType type, EntityLivingBase entityLivingBase) {
        return cooldownTracker.hasSkillRelease(type, entityLivingBase);
    }

    @Override
    public void release(ISkillRelease skillRelease, World worldIn, BlockPos pos, Entity entityIn, ItemStack stack) {
        ISkillCDTracker cdTracker = getCDTracker(entityIn);
        ISkillSlot.StoreSlot slot = cooldownTracker.applyAndGetSlot(cdTracker, skillRelease);

        if(slot != null)
            this.releaseSkill(slot, worldIn, pos, entityIn, stack, cdTracker);
    }

    @Override
    public void release(int slot, World worldIn, BlockPos pos, EntityLivingBase entityIn, ItemStack stack) {
        ISkillCDTracker cdTracker = getCDTracker(entityIn);
        ISkillSlot.StoreSlot storeSlot = cooldownTracker.getStoreSlot(cdTracker, slot);

        if(storeSlot != null)
            this.releaseSkill(storeSlot, worldIn, pos, entityIn, stack, cdTracker);
    }

    private void releaseSkill(ISkillSlot.StoreSlot slot, World worldIn, BlockPos pos, Entity entityIn, ItemStack stack, ISkillCDTracker cdTracker)
    {
        if(entityIn != null)
        {
            ISkill skill = slot.getSkill();
            if(slot.isEmptySlot()) return;
            ISideTaskService service = SkillAPI.getTimerService(worldIn.isRemote ? Side.CLIENT : Side.SERVER);
            DelayedTask task = service.getTask(entityIn.getEntityId());
            if(task != null)
            {
                task.cancel(true);
            }
            else
            {
                ISkillContext context = createContext(worldIn, pos, entityIn, stack);

                service.scheduledRelease(entityIn.getEntityId(), skill.getDelay(), () -> {
                    skill.release(context);
                    if(cdTracker != null)
                    {
                        cooldownTracker.setCoolDownForSlot(cdTracker, slot.getSlot(), skill.getCoolDown());
                    }
                });
            }
        }
    }

    @Override
    public void update(World worldIn, Entity entityIn, ItemStack stack) {

    }

    protected ISkillContext createContext(World world, BlockPos pos, Entity entityIn, ItemStack stack) {
        ISkillContext context = new SkillContextImpl();
        if(stack.getItem() instanceof IWeapon)
        {
            Map<String, IAttribute> attributes = ((IWeapon)stack.getItem()).getMaterial().getAttributeInstance().toMap();
            for(Map.Entry<String, IAttribute> e : attributes.entrySet()) {
                context.setTagValue(e.getKey(), e.getValue());
            }
        }

        return context;
    }
}
