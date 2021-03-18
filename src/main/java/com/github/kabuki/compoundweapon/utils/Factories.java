package com.github.kabuki.compoundweapon.utils;

import com.github.kabuki.compoundweapon.api.data.IDataEntryBase;
import com.github.kabuki.compoundweapon.api.skill.service.ISkillTag;
import com.github.kabuki.compoundweapon.common.data.*;
import com.github.kabuki.compoundweapon.weapon.skill.service.TagNumber;
import com.github.kabuki.compoundweapon.weapon.skill.service.TagObject;

import java.util.Objects;

public class Factories {
    public static class DataFactory {
        public static IDataEntryBase typeOf(Object obj)
        {
            Objects.requireNonNull(obj);
            switch (obj.getClass().getSimpleName()) {
                case "String":
                    return new DataString((String) obj);
                case "Boolean":
                    return new DataBoolean((boolean) obj);
                case "Integer":
                    return new DataInteger((int) obj);
                case "Double":
                    return new DataDouble((double) obj);
                case "Float":
                    return new DataFloat((float) obj);
                case "Byte":
                    return new DataByte((byte) obj);
                case "Long":
                    return new DataLong((long) obj);
                case "Short":
                    return new DataShort((short) obj);
                default:
                    return new DataObject(obj);
            }
        }
    }

    public static class TagFactory {
        public static ISkillTag createTag(String tagName, Object tagValue)
        {
            return (tagValue instanceof Number) ? new TagNumber(tagName, (Number) tagValue) : new TagObject(tagName, tagValue);
        }
    }
}
