package com.github.kabuki.compoundweapon.weapon.skill;

import com.github.kabuki.compoundweapon.api.skill.ISkill;
import com.github.kabuki.compoundweapon.api.skill.ISkillRelease;
import com.github.kabuki.compoundweapon.api.skill.service.ISkillContext;

public class Skill implements ISkill {

    public final static Skill MISSING = new Skill();
    private String name;
    private long delay;
    private long cooldown;

    public boolean isMissing() {
        return this == MISSING;
    }

    @Override
    public ISkillRelease getDevice() {
        return null;
    }

    @Override
    public void release(ISkillContext context) {

    }
}
