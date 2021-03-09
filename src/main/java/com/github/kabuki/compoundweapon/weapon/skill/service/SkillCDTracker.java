package com.github.kabuki.compoundweapon.weapon.skill.service;

import com.github.kabuki.compoundweapon.api.skill.service.DelayedTask;
import com.github.kabuki.compoundweapon.weapon.skill.RealSkill;

import java.util.*;

public class SkillCDTracker implements ISkillCDTracker{
    private final List<ISkillCDEntry> cdEntries = new ArrayList<>();

    @Override
    public void setCD(RealSkill target, long ms) {
        for(ISkillCDEntry e : cdEntries)
        {
            if(e.getSkill().equals(target))
            {
                DelayedTask task = e.getCDTask();
                if(!task.isCancelled())
                    task.setDelay(ms);
            }
        }
        cdEntries.add(new SkillCDEntry(target, ms));
    }

    @Override
    public DelayedTask getCD(String skillName) {
        for(ISkillCDEntry e : cdEntries)
            if(e.getSkill().getName().equals(skillName))
                return e.getCDTask();
        return null;
    }

    @Override
    public boolean hasSkillCoolDown(String skillName) {
        return getCD(skillName) != null;
    }

    @Override
    public List<ISkillCDEntry> CDEntryList() {
        return cdEntries;
    }

    public static class SkillCDEntry implements ISkillCDEntry
    {
        private final RealSkill skill;
        private final DelayedTask cdTask;

        public SkillCDEntry(RealSkill skill, long delay) {
            this.skill = skill;
            this.cdTask = new DelayedTask(delay, () -> this.skill.setCD(0)) {
                @Override
                protected void onChangeDelay(long changed) {
                    skill.setCD(changed);
                }
            };
        }

        public RealSkill getSkill() {
            return skill;
        }

        public DelayedTask getCDTask() {
            return cdTask;
        }
    }
}
