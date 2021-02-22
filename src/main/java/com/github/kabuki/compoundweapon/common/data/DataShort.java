package com.github.kabuki.compoundweapon.common.data;

import com.github.kabuki.compoundweapon.api.data.IDataObject;
import com.github.kabuki.compoundweapon.api.data.IDataPrimitive;

public class DataShort implements IDataPrimitive {

    private final short value;

    public DataShort(short valueIn)
    {
        this.value = valueIn;
    }

    @Override
    public Class<?> getDataType() {
        return DataShort.class;
    }

    @Override
    public IDataObject toObject() {
        return new DataObject(value);
    }

    @Override
    public String getString() {
        return String.valueOf(value);
    }

    @Override
    public boolean getBoolean() {
        return value == 0 ? false : true;
    }

    @Override
    public long getLong() {
        return value;
    }

    @Override
    public int getInt() {
        return value;
    }

    @Override
    public short getShort() {
        return value;
    }

    @Override
    public byte getByte() {
        return (byte) value;
    }

    @Override
    public double getDouble() {
        return value;
    }

    @Override
    public float getFloat() {
        return value;
    }
}
