package com.github.kabuki.compoundweapon.common.data;

import com.github.kabuki.compoundweapon.api.data.IDataObject;
import com.github.kabuki.compoundweapon.api.data.IDataPrimitive;

public class DataDouble implements IDataPrimitive {

    private final double value;

    public DataDouble(double valueIn)
    {
        this.value = valueIn;
    }

    @Override
    public Class<?> getDataType() {
        return DataDouble.class;
    }

    @Override
    public IDataObject toObject() {
        return new DataObject(value);
    }

    @Override
    public String getString() {
        return null;
    }

    @Override
    public boolean getBoolean() {
        return value == 0 ? false : true;
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
        return (float) value;
    }
}
