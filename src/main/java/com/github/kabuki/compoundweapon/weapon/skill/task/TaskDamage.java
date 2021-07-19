package com.github.kabuki.compoundweapon.weapon.skill.task;

import com.github.kabuki.compoundweapon.api.skill.SkillAPI;
import com.github.kabuki.compoundweapon.api.skill.service.ISkillContext;
import com.github.kabuki.compoundweapon.weapon.skill.tag.SharedSkillTag;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;

import java.util.List;
import java.util.function.Function;

public class TaskDamage extends SkillTask{

    protected float knockBack_strength = 0.0F;

    public TaskDamage(float knockBack) {
        knockBack_strength = knockBack;
    }

    protected TaskDamage() {
        super("DAMAGE");
    }

    @Override
    public JsonDeserializer<? extends SkillTask> getDeserializer() {
        return (json, typeOfT, context) -> {
            if(json.isJsonObject())
            {
                JsonObject obj = (JsonObject) json;
                return new TaskDamage(obj.has("knockBack_strength") ? obj.get("knockBack_strength").getAsFloat() : 0.0F);
            }
            else
            {
                throw new JsonParseException("\"parameter\" is not a json object");
            }
        };
    }

    protected boolean canKnockBack()
    {
        return knockBack_strength > 0.0F;
    }

    protected void onDamage(Entity source, EntityLivingBase entityLivingBase, float damage)
    {
        if(canKnockBack()) entityLivingBase.knockBack(source, 0, MathHelper.sin(source.rotationYaw * 0.017453292F), -MathHelper.cos(source.rotationYaw * 0.017453292F));
        entityLivingBase.attackEntityFrom(SkillAPI.causeSkillDamageSource(source), damage);
    }

    @Override
    public void run(ISkillContext context) {
        List<Entity> list = context.getAttackEntities();
        if(list.isEmpty()) return;
        float damage = SharedSkillTag.arithmetic(SharedSkillTag.WEAPON_DAMAGE, context, Function.identity()).floatValue();

        list.stream()
                .filter(e -> e instanceof EntityLivingBase)
                .forEach(e -> onDamage(context.getSource(), (EntityLivingBase) e, damage));
    }
}
