package com.github.kabuki.compoundweapon.weapon.skill.task;

import com.github.kabuki.compoundweapon.api.skill.service.ISkillContext;
import com.github.kabuki.compoundweapon.utils.Utils;
import com.github.kabuki.compoundweapon.utils.math.Point;
import com.github.kabuki.compoundweapon.utils.math.RelativePoint;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.List;

public class TaskPolygon extends TaskRange {
    protected final List<RelativePoint> polygon = Lists.newArrayList();

    protected TaskPolygon() {
        super("_POLYGON");
    }

    public TaskPolygon(float range) {
        super(range);
    }

    @Override
    protected void searchEntities(Entity entityIn, ISkillContext context) {
        if(polygon.isEmpty()) return;

        float range = calcRange(context);
        if(range == 0.0F) return;

        List<Point> list = Lists.newArrayList();
        List<Entity> attack_entities = context.getAttackEntities();
        polygon.forEach(p -> list.add(p.toPoint(entityIn)));
        Point p = new Point(entityIn);

        for(EntityLivingBase entitylivingbase : entityIn.world.getEntitiesWithinAABB(EntityLivingBase.class, getAABB(entityIn, range)))
        {
            if (entitylivingbase != entityIn && !entityIn.isOnSameTeam(entitylivingbase) && Utils.SpaceHelper.inPolygon(p, list))
            {
                attack_entities.add(entitylivingbase);
            }
        }
    }

    @Override
    public JsonDeserializer<? extends SkillTask> getDeserializer() {
        return (jsonElement, type, jsonDeserializationContext) -> {
            JsonObject obj = jsonElement.getAsJsonObject();
            if(!obj.has("points"))
            {
                throw new JsonParseException("On Deserializer Type:RANGE_POLYGON error, cannot found element 'points'");
            }

            JsonArray array = obj.get("points").getAsJsonArray();
            float range = obj.has("range") ? obj.get("range").getAsFloat() : 0.0F;

            TaskPolygon task = new TaskPolygon(range);
            for(int i = 0; i < array.size(); i++)
            {
                JsonObject e = array.get(i).getAsJsonObject();
                Point.PointParser pointParser = e.has("pitch") ? Point.PointParser.POLAR : Point.PointParser.CARTESIAN;
                task.polygon.add(pointParser.parser(e));
            }

            return task;
        };
    }

    @Override
    public AxisAlignedBB getAABB(Entity entity, float reach) {
        return entity.getEntityBoundingBox().grow(reach, 1.0, reach);
    }
}
