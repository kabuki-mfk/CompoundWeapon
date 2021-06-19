package com.github.kabuki.compoundweapon.utils.math;

import com.github.kabuki.compoundweapon.utils.ConvertHelper;
import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.Objects;

public class Point {
    public static final Point ZERO = new Point(0, 0);
    public double x;
    public double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point(Vec3d vec3d)
    {
        this(vec3d.x, vec3d.z);
    }

    public Point(Entity entity) {
        this(entity.posX, entity.posZ);
    }

    public Point(BlockPos blockpos)
    {
        this(blockpos.getX(), blockpos.getZ());
    }

    public enum PointParser
    {
        CARTESIAN {
            public RelativePoint parser(JsonObject obj) {
                Objects.requireNonNull(obj, "parser parameter");
                double x = parserField(obj.get("x").getAsString());
                double z = parserField(obj.get("z").getAsString());
                return RelativePoint.createCartesianPoint(x, z);
            }

            @Override
            public Point toReal(RelativePoint relative, Entity entity) {
                return new Point(relative.field_0 + entity.posX, relative.field_1 + entity.posZ);
            }
        },
        POLAR {
            public RelativePoint parser(JsonObject obj) {
                Objects.requireNonNull(obj, "parser parameter");
                double theta = parserField(obj.get("yaw").getAsString());
                double r = parserField(obj.get("r").getAsString());
                return RelativePoint.createPolarPoint(theta, r);
            }

            @Override
            public Point toReal(RelativePoint relative, Entity entity) {
                float rotateYaw = entity.rotationYaw;
                double z = MathHelper.cos(-(rotateYaw + (float)relative.field_0) * 0.017453292F - (float)Math.PI);
                double x = MathHelper.sin(-(rotateYaw + (float)relative.field_0) * 0.017453292F - (float)Math.PI);
                return new Point(new Vec3d(x + relative.field_1, entity.posY, z + relative.field_1));
            }
        };

        public abstract RelativePoint parser(JsonObject obj);
        public abstract Point toReal(RelativePoint relative, Entity entity);

        private static double parserField(String string)
        {
            if(string.startsWith("~"))
            {
                return ConvertHelper.pasteDouble(string.substring(1));
            }
            else
            {
                throw new IllegalArgumentException("cannot found specific prefix");
            }
        }
    }
}
