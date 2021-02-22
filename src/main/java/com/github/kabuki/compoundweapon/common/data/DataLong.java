package com.github.kabuki.compoundweapon.common.data;

import com.github.kabuki.compoundweapon.api.data.IDataObject;
import com.github.kabuki.compoundweapon.api.data.IDataPrimitive;

public class DataLong implements IDataPrimitive {

    private final long value;

    public DataLong(long valueIn)
    {
        this.value = valueIn;
    }

    @Override
    public Class<?> getDataType() {
        return DataLong.class;
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
        return (int) value;
    }

    @Override
    public short getShort() {
        return (short) value;
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
