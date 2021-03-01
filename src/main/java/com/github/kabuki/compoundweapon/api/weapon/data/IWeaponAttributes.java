package com.github.kabuki.compoundweapon.api.weapon.data;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;

public interface IWeaponAttributes {

    @Nullable
    default IAttribute getAttribute(String name)
    {
        return toMap().get(name);
    }

    default void setAttribute(String name, IAttribute value)
    {
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(value, "value must not be null");
        toMap().put(name, value);
    }

    default boolean hasAttribute(String name) { return toMap().containsKey(name); }

    Map<String, IAttribute> toMap();
}
