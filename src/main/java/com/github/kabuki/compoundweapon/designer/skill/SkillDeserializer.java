package com.github.kabuki.compoundweapon.designer.skill;

import com.github.kabuki.compoundweapon.CompoundWeapon;
import com.github.kabuki.compoundweapon.api.skill.DeviceType;
import com.github.kabuki.compoundweapon.api.skill.ISkillRelease;
import com.github.kabuki.compoundweapon.weapon.skill.Skill;
import com.github.kabuki.compoundweapon.weapon.skill.SkillExecutor;
import com.github.kabuki.compoundweapon.weapon.skill.SkillReleaseDevice;
import com.github.kabuki.compoundweapon.weapon.skill.task.SkillTask;
import com.google.common.collect.Lists;
import com.google.gson.*;
import net.minecraft.util.StringUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public enum SkillDeserializer {
    INSTANCE;

    private Gson gson;

    public void load(File f) throws IOException
    {
        File skillDir = new File(f, "mods/CompoundWeapon/Custom/Skill");
        File taskDir = new File(skillDir, "Task");

        if(!skillDir.exists())
        {
            skillDir.mkdir();
            return;
        }

        if(!taskDir.exists())
        {
            taskDir.mkdir();
        }
        else if(taskDir.isDirectory())
        {
            GsonBuilder builder = new GsonBuilder();
            for(Map.Entry<String, SkillTask> e : SkillTask.REGISTRY.getRegistry().entrySet())
            {
                SkillTask task = e.getValue();
                builder.registerTypeAdapter(task.getClass(), task.getDeserializer());
            }
            gson = builder.create();
            loadSkillTask(taskDir);
        }

        if(skillDir.isDirectory())
        {
            if(skillDir.listFiles() != null) {
                List<File> files = Lists.newArrayList(skillDir.listFiles());
                files.stream()
                        .filter(SkillDeserializer.INSTANCE::validJsonFile)
                        .forEach(f3 -> {
                            try {
                                loadSkillElement(f3);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
            }
        }
    }

    private void loadSkillTask(File f) {
        for (File f2 : f.listFiles()) {
            if (validJsonFile(f2)) {
                try {
                    loadSkillTaskElement(f2);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void loadSkillTaskElement(File f) throws IOException
    {
        JsonParser parser = new JsonParser();

        try(FileReader reader = new FileReader(f))
        {
            Object object = parser.parse(reader);

            if(object instanceof JsonObject)
            {
                JsonObject obj = (JsonObject) object;
                String name = getFileTrueName(f);
                String type = obj.get("type").getAsString();
                SkillTask task = SkillTask.REGISTRY.getTypeTask(type);
                if(StringUtils.isNullOrEmpty(type) || task.isMissing())
                {
                    CompoundWeapon.LOGGER.error("Not such type in File:{}", f.getPath());
                    return;
                }

                SkillTask element = gson.fromJson(obj.get("parameter"), task.getClass());
                element.setName(name);
                SkillTask.REGISTRY.register(name, element);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void loadSkillElement(File f) throws IOException
    {
        if(!f.exists())
        {
            return;
        }

        JsonParser parser = new JsonParser();

        try(FileReader reader = new FileReader(f))
        {
            Object object = parser.parse(reader);

            if(object instanceof JsonObject)
            {
                JsonObject obj = (JsonObject) object;
                if(obj.has("task"))
                {
                    JsonArray arr = obj.get("task").getAsJsonArray();
                    String name = getFileTrueName(f);
                    String device = obj.get("device").getAsString();
                    int index = device.indexOf("&");

                    ISkillRelease r;
                    if(index == -1)
                    {
                        r = new SkillReleaseDevice(DeviceType.fromString(device));
                    }
                    else
                    {
                        r = new SkillReleaseDevice(DeviceType.fromString(device.substring(0, index)))
                                .setCombination(SkillReleaseDevice.Combination.fromString(device.substring(index + 1)));
                    }

                    SkillExecutor skill = new SkillExecutor(r);
                    if(obj.has("delay"))
                    {
                        skill.setDelay(obj.get("delay").getAsLong());
                    }

                    if(obj.has("cooldown"))
                    {
                        skill.setCooldown(obj.get("cooldown").getAsLong());
                    }

                    for(int i = 0; i < arr.size(); i++)
                    {
                        String str = arr.get(i).getAsString();
                        index = str.indexOf(":");
                        if(index == -1)
                        {
                            CompoundWeapon.LOGGER.error("error on Skill:{} , task element:{} no setting priority", name, str);
                        }
                        else
                        {
                            String taskName = str.substring(0, index);
                            int p = Integer.parseInt(str.substring(index + 1));

                            SkillTask skilltask = SkillTask.REGISTRY.getTypeTask(taskName);

                            if(skilltask.isMissing())
                            {
                                CompoundWeapon.LOGGER.warn("missing skillTask:{} on {}, skip deserializer skill element", taskName, f.getPath());
                                return;
                            }
                            else
                            {
                                skilltask.setPriority(p);
                                skill.addTask(skilltask);
                            }
                        }
                    }

                    Skill.REGISTRY.register(name, skill);
                }
                else
                {
                    CompoundWeapon.LOGGER.error("cannot found \"task\" element");
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private boolean validJsonFile(File f)
    {
        if(f.isFile())
        {
            String s = f.getName();
            int i = s.indexOf(".");
            return i != -1 && s.substring(i + 1).equals("json");
        }
        return false;
    }

    private String getFileTrueName(File f)
    {
        String s = f.getName();
        int i = s.indexOf(".");
        if(i == -1)
        {
            return s;
        }
        else
        {
            return s.substring(0, i);
        }
    }
}
