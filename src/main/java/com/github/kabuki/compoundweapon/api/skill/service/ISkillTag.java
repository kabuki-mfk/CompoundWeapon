package com.github.kabuki.compoundweapon.api.skill.service;

public interface ISkillTag {

    <T> T getValue(Class<T> var_class);

    void setValue(Object obj);

    boolean isDataEmpty();

    String getTagName();
}