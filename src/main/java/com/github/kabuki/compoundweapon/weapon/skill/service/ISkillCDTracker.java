package com.github.kabuki.compoundweapon.weapon.skill.service;

import com.github.kabuki.compoundweapon.api.skill.service.DelayedTask;
import com.github.kabuki.compoundweapon.weapon.skill.RealSkill;

import java.util.Iterator;
import java.util.List;

public interface ISkillCDTracker {
    void setCD(RealSkill target, long ms);

    DelayedTask getCD(String skillName);

    boolean hasSkillCoolDown(String skillName);

    List<ISkillCDEntry> CDEntryList();

    default void update()
    {
        Iterator<ISkillCDEntry> ite = CDEntryList().iterator();
        while(ite.hasNext())
        {
            ISkillCDEntry e = ite.next();
            DelayedTask task = e.getCDTask();
            task.update();
            if(task.isCancelled() || task.isDone())
            {
                ite.remove();
            }
        }
    }

    interface ISkillCDEntry
    {
        RealSkill getSkill();

        DelayedTask getCDTask();
    }
}
