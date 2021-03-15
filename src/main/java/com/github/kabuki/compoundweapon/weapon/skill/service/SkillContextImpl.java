package com.github.kabuki.compoundweapon.weapon.skill.service;

import com.github.kabuki.compoundweapon.api.skill.service.ISkillContext;
import com.github.kabuki.compoundweapon.api.skill.service.ISkillTag;
import com.github.kabuki.compoundweapon.common.data.DataCompound;
import com.github.kabuki.compoundweapon.utils.Factories;
import com.google.common.collect.Lists;
import net.minecraft.entity.Entity;

import java.util.List;

public class SkillContextImpl implements ISkillContext {
    private final DataCompound dataCompound = new DataCompound();
    private final List<Entity> attackEntities = Lists.newArrayList();
    private Entity target;

    @Override
    public ISkillTag getTagValue(String name) {
        return Factories.TagFactory.createTag(name, dataCompound.getObjectData(name, null));
    }

    @Override
    public void setTagValue(String name, Object value) {
        dataCompound.setObject(name, value);
    }

    @Override
    public List<Entity> getAttackEntities() {
        return attackEntities;
    }

    @Override
    public Entity getTarget() {
        return target;
    }

    @Override
    public void setTarget(Entity entity) {
        this.target = entity;
    }
}
