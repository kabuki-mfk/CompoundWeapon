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

    public static Number getTagNumber(String tagName, ISkillContext context) {
        return getTagValue(tagName, context, Number.class);
    }

    public static <T> T getTagValue(String tagName, ISkillContext context, Class<T> tagType) {
        return context.getTagValue(tagName).getValue(tagType);
    }

    public static void changeTagNumber(String tagName, ISkillContext context, Function<Number, Number> fun) {
        setTagValue(tagName, context, fun.apply(getTagValue(tagName, context, Number.class)));
    }

    public static void setTagValue(String tagName, ISkillContext context, Object tagValue) {
        context.setTagValue(tagName, tagValue);
    }

    public static boolean hasTagValue(String tagName, ISkillContext context) {
        return context.getTagValue(tagName).getValue(Object.class) != null;
    }
}
