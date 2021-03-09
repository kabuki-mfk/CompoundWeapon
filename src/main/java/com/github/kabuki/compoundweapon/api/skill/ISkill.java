package com.github.kabuki.compoundweapon.api.skill;

import com.github.kabuki.compoundweapon.api.skill.service.ISkillContext;

public interface ISkill {

    ISkillRelease getDevice();

    void release(ISkillContext context);

    String getName();
}
