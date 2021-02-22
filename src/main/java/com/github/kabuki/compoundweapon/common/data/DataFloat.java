package com.github.kabuki.compoundweapon.common.data;

import com.github.kabuki.compoundweapon.api.data.IDataObject;
import com.github.kabuki.compoundweapon.api.data.IDataPrimitive;

public class DataFloat implements IDataPrimitive {

    private final float value;

    public DataFloat(float valueIn)
    {
        this.value = valueIn;
    }

    @Override
    public Class<?> getDataType() {
        return DataFloat.class;
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
        return value != 0;
    }

    @Override
    public long getLong() {
        return (long) value;
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
