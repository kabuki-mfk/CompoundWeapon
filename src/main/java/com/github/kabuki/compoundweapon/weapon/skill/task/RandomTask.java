package com.github.kabuki.compoundweapon.weapon.skill.task;

import com.github.kabuki.compoundweapon.api.skill.service.ISkillContext;
import com.github.kabuki.compoundweapon.common.registries.SkillTaskRegistry;
import com.google.gson.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class RandomTask extends SkillTask{

    private final List<SkillTask> taskList;
    private Random rand;

    public RandomTask(List<SkillTask> tasks) {
        Objects.requireNonNull(tasks);
        this.taskList = tasks;
    }

    protected RandomTask() {
        super("RANDOM");
        this.taskList = new ArrayList<>();
    }

    public List<SkillTask> getTaskList() {
        return taskList;
    }

    @Override
    public JsonDeserializer<? extends SkillTask> getDeserializer() {
        return (json, typeOfT, context) -> {
            if(json.isJsonObject())
            {
                JsonObject obj = (JsonObject) json;
                RandomTask task = new RandomTask();
                if(obj.has("tasks"))
                {
                    JsonArray jsonArray = obj.get("tasks").getAsJsonArray();
                    for(JsonElement element : jsonArray)
                    {
                        getTaskList().add(SkillTaskRegistry.getInstance().getTypeTask(element.getAsString()));
                    }
                }
                return task;
            }
            throw new JsonParseException("\"parameter\" is not a json object");
        };
    }

    @Override
    public void run(ISkillContext context) {
        taskList.get(rand.nextInt(taskList.size())).run(context);
    }
}
