package com.github.kabuki.compoundweapon.weapon.skill;

import com.github.kabuki.compoundweapon.api.skill.ISkillRelease;
import com.github.kabuki.compoundweapon.weapon.skill.service.ISkillCDTracker;

public abstract class AbstractCoolDownSlot extends SkillSlot {

    public AbstractCoolDownSlot(int size) {
        super(size);
    }

    public void setCoolDownForSlot(ISkillCDTracker cdTracker, int slot, long ms)
    {
        for(StoreSlot slotIn : getSlots())
        {
            ((CoolDownSlot)slotIn).notifySetCoolDown(cdTracker, slot, ms);
        }
    }

    public StoreSlot applyAndGetSlot(ISkillCDTracker cdTracker, ISkillRelease skillRelease)
    {
        StoreSlot storeSlot = this.applyAndGetSlot(skillRelease);
        if(storeSlot != null && cdTracker != null)
        {
            if(((CoolDownSlot)storeSlot).hasCoolDown(cdTracker))
            {
                storeSlot = null;
            }
        }
        return storeSlot;
    }

    public StoreSlot getStoreSlot(ISkillCDTracker cdTracker, int slot)
    {
        StoreSlot storeSlot = this.getSkillInSlot(slot);
        if (cdTracker != null && ((CoolDownSlot) storeSlot).hasCoolDown(cdTracker)) {
            storeSlot = null;
        }
        return storeSlot;
    }

    @Override
    protected StoreSlot createStoreSlot(int slot, RealSkill skill) {
        return new CoolDownSlot(slot, skill);
    }

    protected abstract void onSkillSlotNotifySet(StoreSlot skillInSlot, ISkillCDTracker cdTracker, int slot, long ms);

    public class CoolDownSlot extends SkillSlot.Slot
    {
        public CoolDownSlot(int slot, RealSkill skill) {
            super(slot, skill);
        }

        private void notifySetCoolDown(ISkillCDTracker cdTracker, int slot, long ms)
        {
            onSkillSlotNotifySet(this, cdTracker, slot, ms);
        }

        public long getCoolDown(ISkillCDTracker tracker)
        {
            return tracker.getCD(skillIn.getName()).getDelay();
        }

        public boolean hasCoolDown(ISkillCDTracker tracker)
        {
            return tracker.hasSkillCoolDown(skillIn.getName());
        }
    }

    public static class AsyncCoolDownSlot extends AbstractCoolDownSlot
    {
        public AsyncCoolDownSlot(int size) {
            super(size);
        }

        @Override
        protected void onSkillSlotNotifySet(StoreSlot skillInSlot, ISkillCDTracker cdTracker, int slot, long ms) {
            if(skillInSlot.getSlot() == slot)
            {
                RealSkill skill = (RealSkill)skillInSlot.getSkill();
                cdTracker.setCD(skill, ms);
            }
        }
    }
}
