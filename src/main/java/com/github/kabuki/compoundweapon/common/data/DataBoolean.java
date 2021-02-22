package com.github.kabuki.compoundweapon.common.data;

import com.github.kabuki.compoundweapon.api.data.IDataObject;
import com.github.kabuki.compoundweapon.api.data.IDataPrimitive;

public class DataBoolean implements IDataPrimitive {

    private final boolean value;

    public DataBoolean(boolean valueIn)
    {
        this.value = valueIn;
    }

    @Override
    public Class<?> getDataType() {
        return DataBoolean.class;
    }

    @Override
    public IDataObject toObject() {
        return new DataObject(value);
    }

    @Override
    public String getString() {
        return String.valueOf(value);
    }

    private Number parseNumber()
    {
        return value ? 1 : 0;
    }

    @Override
    public long getLong() {
        return (long) parseNumber();
    }

    @Override
    public int getInt() {
        return (int) parseNumber();
    }

    @Override
    public short getShort() {
        return (short) parseNumber();
    }

    @Override
    public byte getByte() {
        return (byte) parseNumber();
    }

    @Override
    public double getDouble() {
        return (double) parseNumber();
    }

    @Override
    public float getFloat() {
        return (float) parseNumber();
    }

    @Override
    public boolean getBoolean() {
        return value;
    }
}
