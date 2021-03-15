package com.github.kabuki.compoundweapon.weapon.skill.service;

import com.github.kabuki.compoundweapon.api.skill.service.ISkillTag;
import com.github.kabuki.compoundweapon.common.data.DataObject;

public class TagObject implements ISkillTag {
    private DataObject object;
    private final String name;

    public TagObject(String name)
    {
        this.name = name;
    }

    public TagObject(String name, Object object)
    {
        this.name = name;
        setValue(object);
    }

    @Override
    public <T> T getValue(Class<T> var_class) {
        return isDataEmpty() ? null : object.get(var_class);
    }

    @Override
    public void setValue(Object obj) {
        if(isDataEmpty())
        {
            this.object = new DataObject(null);
        }
        else
        {
            this.object.set(obj);
        }
    }

    @Override
    public boolean isDataEmpty() {
        return object == null || object.getDataType() == DataObject.class;
    }

    @Override
    public String getTagName() {
        return name;
    }
}
