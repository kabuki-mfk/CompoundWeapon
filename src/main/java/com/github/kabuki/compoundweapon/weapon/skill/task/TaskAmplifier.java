package com.github.kabuki.compoundweapon.weapon.skill.task;

import com.github.kabuki.compoundweapon.api.skill.service.ISkillContext;
import com.github.kabuki.compoundweapon.utils.AmplifierFunction;
import com.github.kabuki.compoundweapon.weapon.skill.tag.SharedSkillTag;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class TaskAmplifier extends SkillTask {

    private String amplifier_tag = "";
    protected AmplifierFunction fun;

    protected TaskAmplifier() {
        super("AMPLIFIER");
        this.fun = null;
    }

    public TaskAmplifier(String amplifier_tagName, AmplifierFunction function) {
        this.amplifier_tag = amplifier_tagName;
        this.fun = function;
    }

    @Override
    public void run(ISkillContext context) {
        amplifier(context);
    }

    protected void amplifier(ISkillContext context) {
        if (fun != null && SharedSkillTag.hasTagValue(amplifier_tag, context)) {
            double d = fun.calc(context);
            SharedSkillTag.changeTagNumber(amplifier_tag, context, num -> num.doubleValue() + d);
        }
    }

    @Override
    public JsonDeserializer<? extends SkillTask> getDeserializer() {
        return (json, type, context) -> {
            JsonObject obj = json.getAsJsonObject();
            AmplifierFunction f;
            if(obj.has("fun")) {
                String s = obj.get("fun").getAsString();
                String tag = "";
                int i = s.indexOf("=");

                if(i != -1)
                {
                    if(i == 0) throw new JsonParseException("Cannot found amplifier object");
                    tag = s.substring(0, i);
                    f = AmplifierFunction.create(s.substring(i + 1));
                }
                else
                {
                    throw new JsonParseException("Wrong Function format");
                }
                return new TaskAmplifier(tag, f);
            }
            else {
                throw new JsonParseException("AmplifierTask#JsonDeserializer cannot found function element");
            }
        };
    }
}

