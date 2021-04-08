package com.github.kabuki.compoundweapon.common.registries;

import com.github.kabuki.compoundweapon.api.skill.ISkill;
import com.github.kabuki.compoundweapon.weapon.skill.Skill;

import java.util.HashMap;
import java.util.Map;

public class SkillRegistry extends AbstractRegistry<ISkill> {

    private static final SkillRegistry INSTANCE = new SkillRegistry();
    private final Map<String, ISkill> registry = new HashMap<>();

    private SkillRegistry()
    {
    }

    public static SkillRegistry getInstance()
    {
        return INSTANCE;
    }

    @Override
    public Map<String, ISkill> getRegistry() {
        return registry;
    }

    @Override
    public ISkill getRegistryElement(String key) {
        ISkill skill = registry.get(key);
        return skill == null ? Skill.MISSING : skill;
    }
}
