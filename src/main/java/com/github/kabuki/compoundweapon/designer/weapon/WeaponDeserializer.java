package com.github.kabuki.compoundweapon.designer.weapon;

import com.github.kabuki.compoundweapon.CompoundWeapon;
import com.github.kabuki.compoundweapon.api.skill.ISkill;
import com.github.kabuki.compoundweapon.api.weapon.IWeapon;
import com.github.kabuki.compoundweapon.api.weapon.WeaponType;
import com.github.kabuki.compoundweapon.common.registries.WeaponRegistry;
import com.github.kabuki.compoundweapon.weapon.material.WeaponMaterial;
import com.github.kabuki.compoundweapon.weapon.skill.Skill;
import com.google.gson.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.function.Consumer;

public enum WeaponDeserializer {
    INSTANCE;

    public void load(File f) throws IOException
    {
        File parent = new File(f, "mods/CompoundWeapon/Custom");
        if(!parent.exists())
        {
            parent.mkdirs();
        }

        loadWeaponMaterial(parent);
        loadWeapon(parent);
    }

    private void readIsFileLoad(File dir, Consumer<File> consumer) {
        if(dir.isDirectory()) {
            if(dir.listFiles() == null) return;
            for(File f1 : dir.listFiles())
            {
                if(f1.isDirectory()) {
                    readIsFileLoad(f1, consumer);
                }
                else {
                    consumer.accept(f1);
                }
            }
        }
        else {
            consumer.accept(dir);
        }
    }

    private boolean isJsonFile(File f)
    {
        String s = f.getName();
        int i = s.indexOf(".");
        if(i == -1)
        {
            return false;
        }
        else
        {
            return s.substring(i).equals(".json");
        }
    }

    private void loadWeaponMaterial(File parent) throws IOException
    {
        File f = new File(parent, "materials.json");

        if(!f.exists())
        {
            f.createNewFile();
            return;
        }

        JsonParser jsonParser = new JsonParser();
        CompoundWeapon.LOGGER.debug("onLoad materials...");

        try (FileReader reader = new FileReader(f)) {
            Object obj = jsonParser.parse(reader);

            if(obj instanceof JsonObject)
            {
                JsonObject jsonObj = (JsonObject) obj;
                JsonObject elements = jsonObj.get("materials").getAsJsonObject();

                for(Map.Entry<String, JsonElement> e : elements.entrySet())
                {
                    String name = e.getKey();
                    if(e.getValue() instanceof JsonObject)
                    {
                        JsonObject o = (JsonObject) e.getValue();
                        int durability = o.get("durability").getAsInt();
                        float damage = o.get("damage").getAsFloat();
                        float speed = o.get("speed").getAsFloat();
                        WeaponMaterial.REGISTRY.register(name, new WeaponMaterial(name, damage, speed, durability));
                    }
                }
            }
        }
        catch (NullPointerException e)
        {
            CompoundWeapon.LOGGER.error("missing material property");
        }
        catch (ClassCastException e)
        {
            CompoundWeapon.LOGGER.error("wrong material property type");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private void loadWeapon(File parent) throws IOException
    {
        File f = new File(parent, "Weapon");

        if(!f.exists())
        {
            f.mkdir();
            return;
        }

        CompoundWeapon.LOGGER.debug("onLoad weapon...");

        readIsFileLoad(f, WeaponDeserializer.INSTANCE::loadWeaponElement);
    }

    private void loadWeaponElement(File file)
    {
        if(!isJsonFile(file)) return;

        JsonParser jsonParser = new JsonParser();
        try(FileReader reader = new FileReader(file))
        {
            Object obj = jsonParser.parse(reader);
            if(obj instanceof JsonObject)
            {
                JsonObject jsonObj = (JsonObject) obj;
                JsonObject property = jsonObj.get("property").getAsJsonObject();

                String name = jsonObj.get("name").getAsString();
                WeaponType type = WeaponType.valueOf(jsonObj.get("type").getAsString());
                IWeapon weapon = deserializeWeapon(property, type, name);
                if(weapon == null)
                {
                    CompoundWeapon.LOGGER.error(String.format("on LoadWeapon %s error", name));
                    return;
                }
                WeaponRegistry.getInstance().register(name, weapon);
            }
        }
        catch (ClassCastException e)
        {
            CompoundWeapon.LOGGER.error(String.format("wrong property on create weapon, fileName:%s", file.getName()));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private IWeapon deserializeWeapon(JsonObject obj, WeaponType type, String name)
    {
        String material$nameIn = null;
        if(obj.has("material")) {
            material$nameIn = obj.get("material").getAsString();
        }
        else {
            CompoundWeapon.LOGGER.warn(String.format("Weapon %s missing Property \"material\"", name));
            return null;
        }

        WeaponMaterial material = WeaponMaterial.REGISTRY.getMaterial(material$nameIn);

        if(material == null)
        {
            CompoundWeapon.LOGGER.error(String.format("cannot found material \"%s\"", material$nameIn));
            return null;
        }

        WeaponType.AbstractWeaponBuilder<? extends IWeapon> builder = type.builder().name(name).material(material);

        if(obj.has("skill"))
        {
            JsonArray skillArr = obj.get("skill").getAsJsonArray();
            for(JsonElement skillName : skillArr)
            {
                ISkill skill = Skill.REGISTRY.getRegistryElement(skillName.getAsString());
                builder.skill(skill);
            }

        }

        if(obj.has("override")) {
            if (!(obj.get("override") instanceof JsonObject)) {
                CompoundWeapon.LOGGER.warn("Syntax Error on Override material property");
            }
            else {
                JsonObject override = obj.get("override").getAsJsonObject();
                if(override.has("damage"))
                {
                    builder.damage(override.get("damage").getAsFloat());
                }
                if(override.has("durability"))
                {
                    builder.durability(override.get("durability").getAsInt());
                }
                if(override.has("speed"))
                {
                    builder.speed(override.get("speed").getAsFloat());
                }
            }
        }

        return builder.build();
    }
}
