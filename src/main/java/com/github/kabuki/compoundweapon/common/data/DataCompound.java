package com.github.kabuki.compoundweapon.common.data;

import com.github.kabuki.compoundweapon.api.data.IDataEntryBase;
import com.github.kabuki.compoundweapon.api.data.IDataObject;
import com.github.kabuki.compoundweapon.api.data.IDataPrimitive;
import com.github.kabuki.compoundweapon.utils.Factories;
import com.google.common.collect.Maps;

import javax.annotation.Nullable;
import java.util.Map;

public class DataCompound implements IDataEntryBase {
    private final Map<String, IDataEntryBase> valueMap = Maps.newHashMap();

    @Override
    public Class<?> getDataType() {
        return DataCompound.class;
    }

    private Class<?> getEntryType(String key)
    {
        IDataEntryBase entry = valueMap.get(key);
        return entry == null ? null : entry.getDataType();
    }

    public boolean isEmpty()
    {
        return valueMap.isEmpty();
    }

    public boolean hasKey(String key)
    {
        return hasKey(key, null);
    }

    public boolean hasKey(String key, @Nullable Class<?> type)
    {
        Class<?> clazz = getEntryType(key);
        return clazz != null && (type == null || type.isAssignableFrom(clazz));
    }

    public void setDataEntry(String key, IDataEntryBase value)
    {
        this.valueMap.put(key, value);
    }

    public void setObject(String key, Object value)
    {
        this.valueMap.put(key, Factories.DataFactory.typeOf(value));
    }

    public void setString(String key, String s)
    {
        this.valueMap.put(key, new DataString(s));
    }

    public void setInt(String key, int i)
    {
        this.valueMap.put(key, new DataInteger(i));
    }

    public void setLong(String key, long l)
    {
        this.valueMap.put(key, new DataLong(l));
    }

    public void setDouble(String key, double d)
    {
        this.valueMap.put(key, new DataDouble(d));
    }

    public void setFloat(String key, float f)
    {
        this.valueMap.put(key, new DataFloat(f));
    }

    public void setBoolean(String key, boolean b)
    {
        this.valueMap.put(key, new DataBoolean(b));
    }

    public void setByte(String key, byte bt)
    {
        this.valueMap.put(key, new DataByte(bt));
    }

    public String getString(String key)
    {
        return hasKey(key, String.class) ? ((IDataPrimitive)valueMap.get(key)).getString() : "";
    }

    public boolean getBoolean(String key)
    {
        return hasKey(key, Boolean.class) && ((IDataPrimitive) valueMap.get(key)).getBoolean();
    }

    public long getLong(String key)
    {
        return hasKey(key, Long.class) ? ((IDataPrimitive)valueMap.get(key)).getLong() : 0;
    }

    public int getInt(String key)
    {
        return hasKey(key, Integer.class) ? ((IDataPrimitive)valueMap.get(key)).getInt() : 0;
    }

    public short getShort(String key)
    {
        return hasKey(key, Short.class) ? ((IDataPrimitive)valueMap.get(key)).getShort() : 0;
    }

    public double getDouble(String key)
    {
        return hasKey(key, Double.class) ? ((IDataPrimitive)valueMap.get(key)).getDouble() : 0;
    }

    public byte getByte(String key)
    {
        return hasKey(key, Byte.class) ? ((IDataPrimitive)valueMap.get(key)).getByte() : 0;
    }

    public float getFloat(String key)
    {
        return hasKey(key, Float.class) ? ((IDataPrimitive)valueMap.get(key)).getFloat() : 0;
    }

    public <T> T getObjectData(String key, Class<T> type)
    {
        return hasKey(key, type) ? valueMap.get(key).toObject().get(type) : null;
    }

    @Override
    public IDataObject toObject() { return new DataObject(this); }

    public Map<String, IDataEntryBase> toMap() { return this.valueMap; }
}
