package com.github.kabuki.compoundweapon.weapon.skill;

import com.github.kabuki.compoundweapon.api.skill.ISkill;
import com.github.kabuki.compoundweapon.api.skill.ISkillRelease;
import com.github.kabuki.compoundweapon.api.skill.service.ISkillContext;

public class RealSkill implements ISkill {

    private final ISkill skill;
    private long delay;
    private long cd;

    public RealSkill(ISkill skillIn)
    {
        this.skill = skillIn;
        this.delay = skillIn.getDelay();
        this.cd = skillIn.getCoolDown();
    }

    @Override
    public ISkillRelease getDevice() {
        return null;
    }

    @Override
    public void release(ISkillContext context) {
        skill.release(context);
    }

    @Override
    public long getDelay() {
        return this.delay;
    }

    @Override
    public long getCoolDown() {
        return this.cd;
    }

    @Override
    public void setDelay(long delay) {
        this.delay = delay;
    }

    @Override
    public void setCooldown(long cooldown) {
        this.cd = cooldown;
    }

    @Override
    public String getName() {
        return skill.getName();
    }

    public boolean isMissing()
    {
        return this.skill == Skill.MISSING;
    }

    public void setCD(long ms) {
        this.cd = ms;
    }

    public long getCD() {
        return this.cd;
    }

    public ISkill getSkill() {
        return skill;
    }
}
