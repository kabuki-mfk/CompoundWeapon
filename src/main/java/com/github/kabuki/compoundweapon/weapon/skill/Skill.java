package com.github.kabuki.compoundweapon.weapon.skill;

import com.github.kabuki.compoundweapon.api.skill.ISkill;
import com.github.kabuki.compoundweapon.api.skill.ISkillRelease;
import com.github.kabuki.compoundweapon.api.skill.service.ISkillContext;
import com.github.kabuki.compoundweapon.common.registries.SkillRegistry;

public class Skill implements ISkill {
    public static final SkillRegistry REGISTRY = SkillRegistry.getInstance();
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

    @Override
    public long getDelay() {
        return delay;
    }

    @Override
    public long getCoolDown() {
        return cooldown;
    }

    @Override
    public void setDelay(long delay) {
        this.delay = delay;
    }

    @Override
    public void setCooldown(long cooldown) {
        this.cooldown = cooldown;
    }

    @Override
    public String getName() {
        return name;
    }
}
