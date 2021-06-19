package com.github.kabuki.compoundweapon.weapon.skill.task;

import com.github.kabuki.compoundweapon.api.skill.service.ISkillContext;
import com.github.kabuki.compoundweapon.utils.Utils;
import com.github.kabuki.compoundweapon.weapon.skill.tag.SharedSkillTag;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class TaskRange extends SkillTask {
    protected float override_range = 0.0F;

    protected TaskRange(String subName) {
        super("RANGE" + (StringUtils.isNullOrEmpty(subName) ? "" : subName));
    }

    public TaskRange(float range) {
        this.override_range = range;
    }

    protected TaskRange createInstance(float range)
    {
        return new TaskRange(range);
    }

    @Override
    public void run(ISkillContext context) {
        searchEntities(context.getSource(), context);
    }

    public AxisAlignedBB getAABB(Entity entity, float reach) {
        Vec3d vec3d = entity.getLookVec();
        return entity.getEntityBoundingBox().expand(vec3d.x * reach, vec3d.y * reach, vec3d.z * reach);
    }

    protected float calcRange(ISkillContext context) {
        float range = override_range;

        if(range == 0.0F && SharedSkillTag.hasTagValue(SharedSkillTag.WEAPON_RANGE, context))
        {
            return SharedSkillTag.getTagNumber(SharedSkillTag.WEAPON_RANGE, context).floatValue();
        }
        return range;
    }

    protected void searchEntities(Entity entityIn, ISkillContext context)
    {
        float range = calcRange(context);
        if(range <= 0.0F) return;

        for (EntityLivingBase entitylivingbase : entityIn.world.getEntitiesWithinAABB(EntityLivingBase.class, getAABB(entityIn, range)))
        {
            if (entitylivingbase != entityIn && !entityIn.isOnSameTeam(entitylivingbase) && entityIn.getDistanceSq(entitylivingbase) < range * range)
            {
                context.getAttackEntities().add(entitylivingbase);
            }
        }
    }

    @Override
    public JsonDeserializer<? extends SkillTask> getDeserializer() {
        return (jsonElement, type, jsonDeserializationContext) -> {
            JsonObject obj = jsonElement.getAsJsonObject();
            float range = obj.has("range") ? obj.get("range").getAsFloat() : 0.0F;
            return createInstance(range);
        };
    }

    public static class Square extends TaskRange {
        protected Square() {
            super("_SQUARE");
        }

        public Square(float range) {
            super(range);
        }

        @Override
        public AxisAlignedBB getAABB(Entity entity, float reach) {
            return entity.getEntityBoundingBox().grow(reach);
        }

        @Override
        protected TaskRange createInstance(float range) {
            return new Square(range);
        }
    }

    public static class Round extends TaskRange {
        public Round() {
            super("_ROUND");
        }

        public Round(float range) {
            super(range);
        }

        @Override
        protected void searchEntities(Entity entityIn, ISkillContext context) {
            float range = calcRange(context);
            if(range == 0.0F) {
                return;
            }

            List<Entity> attack_entities = context.getAttackEntities();

            for(EntityLivingBase entitylivingbase : Utils.WorldHelper.getEntityLivingInRange(entityIn, range)) {
                if (entitylivingbase != entityIn && !entityIn.isOnSameTeam(entitylivingbase) && entityIn.getDistanceSq(entitylivingbase) < override_range * override_range) {
                    attack_entities.add(entitylivingbase);
                }
            }
        }

        @Override
        protected TaskRange createInstance(float range) {
            return new Round(range);
        }
    }
}
