package com.github.kabuki.compoundweapon.weapon.attribute;

import com.github.kabuki.compoundweapon.api.data.IDataEntryBase;
import com.github.kabuki.compoundweapon.api.weapon.data.IAttribute;
import com.github.kabuki.compoundweapon.common.data.DataObject;

public class Attribute implements IAttribute.IDataAttribute {
    private IDataEntryBase data;

    public Attribute(IDataEntryBase data)
    {
        this.data = data;
    }

    public Attribute(Object object)
    {
        this.data = new DataObject(object);
    }

    @Override
    public IDataEntryBase getValue() {
        return data;
    }

    @Override
    public void setValue(IDataEntryBase data) {
        this.data = data;
    }
}
