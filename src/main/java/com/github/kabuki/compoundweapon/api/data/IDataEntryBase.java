package com.github.kabuki.compoundweapon.api.data;

public interface IDataEntryBase {

    Class<?> getDataType();

    IDataObject toObject();
}
