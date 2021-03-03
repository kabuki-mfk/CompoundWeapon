package com.github.kabuki.compoundweapon.common.data;

import com.github.kabuki.compoundweapon.api.data.IDataObject;
import com.github.kabuki.compoundweapon.api.data.IDataPrimitive;
import com.github.kabuki.compoundweapon.utils.ConvertHelper;

public class DataString implements IDataPrimitive {
    private final String value;

    public DataString(String strIn)
    {
        this.value = strIn;
    }

    @Override
    public Class<?> getDataType() {
        return DataString.class;
    }

    @Override
    public IDataObject toObject() {
        return new DataObject(value);
    }

    @Override
    public String getString() {
        return value;
    }

    @Override
    public boolean getBoolean() {
        return Boolean.parseBoolean(value);
    }

    @Override
    public long getLong() {
        return ConvertHelper.isNumber(value) ? Long.parseLong(value) : 0;
    }

    @Override
    public int getInt() {
        return (int) ConvertHelper.pasteDouble(value);
    }

    @Override
    public short getShort() {
        if(ConvertHelper.isNumber(value))
        {
            double d = Double.parseDouble(value);
            return d > Short.MAX_VALUE ? Short.MAX_VALUE : d < Short.MIN_VALUE ? Short.MIN_VALUE : Short.parseShort(value);
        }

        return 0;
    }

    @Override
    public byte getByte() {
        if(ConvertHelper.isNumber(value))
        {
            double d = Double.parseDouble(value);
            return d > Byte.MAX_VALUE ? Byte.MAX_VALUE : d < Byte.MIN_VALUE ? Byte.MIN_VALUE : Byte.parseByte(value);
        }

        return 0;
    }

    @Override
    public double getDouble() {
        return ConvertHelper.pasteDouble(value);
    }

    @Override
    public float getFloat() {
        return (float) ConvertHelper.pasteDouble(value);
    }
}
