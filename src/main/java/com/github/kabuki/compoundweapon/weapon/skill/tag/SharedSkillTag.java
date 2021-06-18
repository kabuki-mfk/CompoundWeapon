package com.github.kabuki.compoundweapon.weapon.skill.tag;

import com.github.kabuki.compoundweapon.api.skill.service.ISkillContext;
import com.github.kabuki.compoundweapon.api.skill.service.ISkillTag;

import java.util.function.Function;

public class SharedSkillTag {
    public final static String WEAPON_DAMAGE = "damage";
    public final static String WEAPON_SPEED = "speed";
    public final static String WEAPON_RANGE = "range";
    public final static String ENTITY_SOURCE = "EntitySource";

    public static <T extends Number> T arithmetic(String tagName, ISkillContext context, Function<Number, T> fun) {
        ISkillTag tag = context.getTagValue(tagName);
        Number num = tag != null ? tag.getValue(Number.class) : 0;
        return fun.apply(num);
    }
}
