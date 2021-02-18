package com.github.kabuki.compoundweapon.api.data;

public interface IDataObject extends IDataEntryBase{

    void set(Object value);

    <T> T get(Class<T> var_class);

    @Override
    default IDataObject toObject() { return this; }
}
