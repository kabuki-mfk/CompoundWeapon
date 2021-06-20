package com.github.kabuki.compoundweapon.weapon.skill.task;

import com.github.kabuki.compoundweapon.api.skill.service.ISkillContext;
import com.github.kabuki.compoundweapon.common.registries.SkillTaskRegistry;
import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializer;

import java.util.List;

public abstract class SkillTask implements Cloneable{
    private final static List<SkillTask> TASK_TYPE = Lists.newArrayList();
    public static SkillTaskRegistry REGISTRY = SkillTaskRegistry.getInstance();
    public static SkillTask MISSING = new SkillTask() {
        @Override
        public JsonDeserializer<? extends SkillTask> getDeserializer() {
            return (json, typeOfT, context) -> this;
        }

        @Override
        public void run(ISkillContext context) {
        }
    };

    private String taskName;
    private int priority = 1;

    public SkillTask() {
    }

    public SkillTask(String name)
    {
        this.taskName = name;
    }

    public SkillTask(String name, int priority)
    {
        this.taskName = name;
        this.priority = priority;
    }

    public final void setPriority(int priority) {
        if (priority <= 0)
            throw new IllegalArgumentException("priority must be positive");
        if (priority > 5)
            throw new IllegalArgumentException("priority is too big, limit is 5");

        this.priority = priority;
    }

    public String getName()
    {
        return taskName;
    }

    public void setName(String name)
    {
        this.taskName = name;
    }

    public int priority() {
        return priority;
    }

    public final boolean isMissing()
    {
        return this == MISSING;
    }

    @Override
    public Object clone() {
        Object clone = null;
        try {
            clone = super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return clone;
    }

    public abstract JsonDeserializer<? extends SkillTask> getDeserializer();


    public abstract void run(ISkillContext context);

    public static List<SkillTask> getTypeList() {
        return TASK_TYPE;
    }

    public static SkillTask getTypeFromName(String typeName) {
        for(SkillTask task : TASK_TYPE)
            if(task.getName().contains(typeName))
                return task;

        return MISSING;
    }

    public static void registerTaskType(SkillTask skillTask) {
        TASK_TYPE.add(skillTask);
    }

    public static void onRegisterTaskType() {
        registerTaskType(new RandomTask());
        registerTaskType(new TaskRange(null));
        registerTaskType(new TaskRange.Round());
        registerTaskType(new TaskRange.Square());
        registerTaskType(new TaskAmplifier());
        registerTaskType(new TaskDamage());
        registerTaskType(new TaskPolygon());
        registerTaskType(new TaskOffset(null));
        registerTaskType(new TaskOffset.OffsetForward());
        registerTaskType(new TaskOffset.OffsetStrafe());
        registerTaskType(new TaskOffset.OffsetVertical());
    }
}
