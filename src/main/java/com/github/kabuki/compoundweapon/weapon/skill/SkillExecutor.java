package com.github.kabuki.compoundweapon.weapon.skill;

import com.github.kabuki.compoundweapon.api.skill.ISkillRelease;
import com.github.kabuki.compoundweapon.api.skill.service.ISkillContext;
import com.github.kabuki.compoundweapon.weapon.skill.task.SkillTask;
import com.google.common.collect.Lists;

import java.util.List;

public class SkillExecutor extends Skill {
    private final List<SkillTask> tasks = Lists.newArrayList();
    private int maxPriority = 1;

    public SkillExecutor() {
    }

    public SkillExecutor(ISkillRelease device) {
        super(device);
    }

    @Override
    public void release(ISkillContext context) {
        if(!tasks.isEmpty())
        {
            runTask(context, maxPriority);
        }
    }

    public final void addTask(SkillTask task)
    {
        tasks.add(task);
        maxPriority = Math.max(maxPriority, task.priority());
    }

    private void runTask(ISkillContext context, int p)
    {
        if(p > 1)
            runTask(context, p - 1);

        tasks.stream()
             .filter(t -> t.priority() == p)
             .forEach(t -> t.run(context));
    }
}
