package com.github.kabuki.compoundweapon.api.skill;

import com.github.kabuki.compoundweapon.api.skill.service.ISkillContext;

public interface ISkill {

    ISkillRelease getDevice();

    void release(ISkillContext context);

    long getDelay();

    long getCoolDown();

    void setDelay(long delay);

    void setCooldown(long cooldown);

    String getName();
}
