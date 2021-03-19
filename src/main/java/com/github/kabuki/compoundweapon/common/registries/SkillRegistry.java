package com.github.kabuki.compoundweapon.common.registries;

import com.github.kabuki.compoundweapon.weapon.skill.Skill;

import java.util.Map;

public class SkillRegistry extends AbstractRegistry<Skill> {

    private static final SkillRegistry INSTANCE = new SkillRegistry();
    private Map<String, Skill> registry;

    private SkillRegistry()
    {
    }

    public static SkillRegistry getInstance()
    {
        return INSTANCE;
    }

    @Override
    public Map<String, Skill> getRegistry() {
        return registry;
    }

    @Override
    public Skill getRegistryElement(String key) {
        Skill skill = registry.get(key);
        return skill == null ? Skill.MISSING : skill;
    }
}
