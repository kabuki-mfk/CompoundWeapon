package com.github.kabuki.compoundweapon.api.weapon.data;

import com.github.kabuki.compoundweapon.api.data.IDataEntryBase;

import javax.annotation.Nullable;
import java.util.Map;

public interface IWeaponAttributes {

    @Nullable
    default IDataEntryBase getAttribute(String name)
    {
        return toMap().get(name);
    }

    void setAttribute(String name, Object value);

    default void setAttribute(String name, IDataEntryBase value)
    {
        toMap().put(name, value);
    }

    Map<String, IDataEntryBase> toMap();
}
