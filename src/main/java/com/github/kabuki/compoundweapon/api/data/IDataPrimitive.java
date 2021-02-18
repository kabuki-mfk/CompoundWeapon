package com.github.kabuki.compoundweapon.api.data;

public interface IDataPrimitive extends IDataEntryBase{
    String getString();

    boolean getBoolean();

    long getLong();

    int getInt();

    short getShort();

    byte getByte();

    double getDouble();

    float getFloat();
}
