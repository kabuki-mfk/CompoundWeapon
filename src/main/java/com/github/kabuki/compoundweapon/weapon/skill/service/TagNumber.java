package com.github.kabuki.compoundweapon.weapon.skill.service;

import com.github.kabuki.compoundweapon.api.data.IDataPrimitive;
import com.github.kabuki.compoundweapon.api.skill.service.ISkillTag;
import com.github.kabuki.compoundweapon.utils.Factories;

public class TagNumber implements ISkillTag {
    private IDataPrimitive dataPrimitive;
    private String name;

    public TagNumber(String name)
    {
        this.name = name;
    }

    public TagNumber(String name, Number num)
    {
        this.name = name;
        this.dataPrimitive = (IDataPrimitive) Factories.DataFactory.typeOf(num);
    }

    @Override
    public <T> T getValue(Class<T> var_class) {
        T value = null;
        if (var_class.isAssignableFrom(Number.class)) {
            value = dataPrimitive.toObject().get(var_class);
        }
        return value;
    }

    @Override
    public void setValue(Object obj) {
        if(obj instanceof Number)
        {
            this.dataPrimitive = (IDataPrimitive) Factories.DataFactory.typeOf(obj);
        }
    }

    @Override
    public boolean isDataEmpty() {
        return dataPrimitive == null;
    }

    @Override
    public String getTagName() {
        return name;
    }
}
