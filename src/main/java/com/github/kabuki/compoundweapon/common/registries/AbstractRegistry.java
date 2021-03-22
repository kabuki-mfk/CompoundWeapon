package com.github.kabuki.compoundweapon.common.registries;

import java.util.Map;

public abstract class AbstractRegistry<T> {

    public abstract Map<String, T> getRegistry();

    public void register(String name, T entry)
    {
        if(getRegistry().containsKey(name))
            throw new IllegalStateException("The entry is already registered");
        getRegistry().put(name, entry);
    }

    public T getRegistryElement(String key) {
        return getRegistry().get(key);
    }
}
