package com.github.kabuki.compoundweapon.common.data;

import com.github.kabuki.compoundweapon.api.data.IDataObject;
import com.github.kabuki.compoundweapon.api.data.IDataPrimitive;

public class DataByte implements IDataPrimitive {

    private final byte value;

    public DataByte(byte valueIn)
    {
        this.value = valueIn;
    }

    @Override
    public Class<?> getDataType() {
        return DataByte.class;
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
        return value;
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
