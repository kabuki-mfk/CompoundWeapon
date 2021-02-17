package com.github.kabuki.compoundweapon.api.skill.service;

import net.minecraft.entity.Entity;

import java.util.List;

public interface ISkillContext {
    ISkillTag getTagValue(String name);

    void setTagValue(String name, Object value);

    List<Entity> getAttackEntities();

    Entity getTarget();

    void setTarget(Entity entity);
}
