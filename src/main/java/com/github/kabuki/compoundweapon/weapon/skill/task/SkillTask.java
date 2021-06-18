package com.github.kabuki.compoundweapon.weapon.skill.task;

import com.github.kabuki.compoundweapon.api.skill.service.ISkillContext;
import com.github.kabuki.compoundweapon.common.registries.SkillTaskRegistry;
import com.google.gson.JsonDeserializer;

public abstract class SkillTask implements Cloneable{
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

}
