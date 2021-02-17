package com.github.kabuki.compoundweapon.api.skill.service;

public interface ISkillTag {

    <T> T getValue(Class<?> var_class);

    void setValue(Object obj);

    boolean isDataEmpty();

    String getTagName();
}