package com.github.kabuki.compoundweapon.weapon.skill;

import com.github.kabuki.compoundweapon.api.skill.ISkill;
import com.github.kabuki.compoundweapon.api.skill.ISkillSlot;
import com.google.common.collect.Lists;

import java.util.List;

public class SkillSlot implements ISkillSlot {

    protected StoreSlot[] slots;
    private int storeIn;

    public SkillSlot(int size) {
        slots = new StoreSlot[size];
        RealSkill defaultElement = new RealSkill(Skill.MISSING);
        for(int i = 0; i < size;)
            slots[i++] = createStoreSlot(i, defaultElement);
    }

    private void checkIndex(int idx) {
        if(idx < 0)
        {
            throw new IllegalArgumentException(String.format("index %d < 0, cannot be negative", idx));
        }
        if(idx >= slots.length)
        {
            throw new IndexOutOfBoundsException(String.format("index %d >= %d", idx, slots.length));
        }
    }

    private boolean checkConflict(ISkill skill) {
        int idx = 0;

        while(idx < slots.length)
        {
            if(slots[idx++].getSkill().getDevice().equals(skill.getDevice())) return true;
        }
        return false;
    }

    @Override
    public int size() {
        return slots.length;
    }

    @Override
    public boolean isEmpty() {
        for(int i = 0; i < this.size(); i++)
        {
            if(!slots[i].isEmptySlot()) return false;
        }
        return true;
    }

    @Override
    public boolean addSkill(ISkill skill) {
        if(skill == Skill.MISSING || storeIn == slots.length || checkConflict(skill))
        {
            return false;
        }

        slots[storeIn] = createStoreSlot(storeIn++, new RealSkill(skill));
        return true;
    }

    @Override
    public boolean setSkill(int slot, ISkill skill) {
        checkIndex(slot);
        if(checkConflict(skill)) return false;

        slots[slot] = createStoreSlot(slot, new RealSkill(skill));
        return true;
    }

    @Override
    public StoreSlot getSkillInSlot(int slot) {
        checkIndex(slot);
        return slots[slot];
    }

    @Override
    public List<StoreSlot> getSlots() {
        return Lists.newArrayList(slots);
    }

    protected StoreSlot createStoreSlot(int slot, RealSkill skill)
    {
        return new Slot(slot, skill);
    }

    public static class Slot implements ISkillSlot.StoreSlot
    {
        private final int slot;
        protected RealSkill skillIn;

        public Slot(int slot, RealSkill skill)
        {
            this.slot = slot;
            this.skillIn = skill;
        }

        @Override
        public int getSlot() {
            return slot;
        }

        @Override
        public ISkill getSkill() {
            return skillIn;
        }

        @Override
        public boolean isEmptySlot() {
            return skillIn.isMissing();
        }
    }
}
