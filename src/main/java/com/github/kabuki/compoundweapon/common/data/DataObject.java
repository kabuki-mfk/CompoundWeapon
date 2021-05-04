package com.github.kabuki.compoundweapon.common.data;

import com.github.kabuki.compoundweapon.api.data.IDataObject;

public class DataObject implements IDataObject {

    private Object value;

    public DataObject(Object objIn)
    {
        checkType(objIn);
        this.value = objIn;
    }

    private void checkType(Object object)
    {
        if(object instanceof DataObject)
            throw new IllegalArgumentException("object class cannot be DataObject");
    }

    @Override
    public Class<?> getDataType() {
        return value == null ? DataObject.class : value.getClass();
    }

    @Override
    public void set(Object value) {
        checkType(value);
        this.value = value;
    }

    @Override
    public <T> T get(Class<T> var_class) {
        return getDataType() == DataObject.class ? null : (T) value;
    }
}
