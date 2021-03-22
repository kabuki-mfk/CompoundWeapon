package com.github.kabuki.compoundweapon.weapon.attribute;

import com.github.kabuki.compoundweapon.api.weapon.data.IAttribute;
import com.github.kabuki.compoundweapon.api.weapon.data.IWeaponAttributes;
import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public class WeaponAttributeHelper {

    public static Map<String, IAttribute.IDataAttribute> getDataAttributes(IWeaponAttributes attributes)
    {
        Objects.requireNonNull(attributes);
        Map<String, IAttribute.IDataAttribute> map = Maps.newHashMap();
        for(Map.Entry<String, IAttribute> e : attributes.toMap().entrySet())
        {
            if(e.getValue() instanceof IAttribute.IDataAttribute)
                map.put(e.getKey(), (IAttribute.IDataAttribute) e.getValue());
        }
        return map;
    }

    public static Map<String, IAttribute.IDynamicAttribute> getDynamicAttributes(IWeaponAttributes attributes)
    {
        Objects.requireNonNull(attributes);
        Map<String, IAttribute.IDynamicAttribute> map = Maps.newHashMap();
        for(Map.Entry<String, IAttribute> e : attributes.toMap().entrySet())
        {
            if(e.getValue() instanceof IAttribute.IDynamicAttribute)
                map.put(e.getKey(), (IAttribute.IDynamicAttribute) e.getValue());
        }
        return map;
    }

    public static <T> Optional<T> getDataAttributeValue(IWeaponAttributes attributes, String attributeName, Class<T> typeClass)
    {
        IAttribute.IDataAttribute dataAttribute = getDataAttributes(attributes).get(attributeName);
        return Optional.ofNullable(dataAttribute.getValue().toObject().get(typeClass));
    }

    public static void handleDynamicAttribute(IWeaponAttributes attributes, Consumer<IAttribute.IDynamicAttribute> handleMethod)
    {
        for(IAttribute.IDynamicAttribute attribute : getDynamicAttributes(attributes).values())
            handleMethod.accept(attribute);
    }
}
