package com.github.kabuki.compoundweapon.common.registries;

import com.github.kabuki.compoundweapon.weapon.skill.task.SkillTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SkillTaskRegistry extends AbstractRegistry<SkillTask> {
    private static final SkillTaskRegistry INSTANCE = new SkillTaskRegistry();
    private final Map<String, SkillTask> registry = new HashMap<String, SkillTask>() {
        @Override
        public SkillTask get(Object key) {
            return super.getOrDefault(key, SkillTask.MISSING);
        }

        @Override
        public SkillTask put(String key, SkillTask value) {
            Objects.requireNonNull(value);
            return super.put(key, value);
        }
    };

    private SkillTaskRegistry() {
    }

    public static SkillTaskRegistry getInstance() {
        return INSTANCE;
    }

    @Override
    public Map<String, SkillTask> getRegistry() {
        return registry;
    }

    public SkillTask getTypeTask(String taskName) {
        return (SkillTask) getRegistry().getOrDefault(taskName, SkillTask.MISSING).clone();
    }

    public SkillTask getProtoTypeTask(String taskName) {
        return getRegistry().getOrDefault(taskName, SkillTask.MISSING);
    }


}
