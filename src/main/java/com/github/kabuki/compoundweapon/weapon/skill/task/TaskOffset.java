package com.github.kabuki.compoundweapon.weapon.skill.task;

import com.github.kabuki.compoundweapon.api.skill.service.ISkillContext;
import com.github.kabuki.compoundweapon.utils.Utils;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class TaskOffset extends SkillTask {
    protected double forward;
    protected double strafe;
    protected double vertical;

    protected TaskOffset(String subName) {
        super("OFFSET" + (StringUtils.isNullOrEmpty(subName) ? "" : subName), 1);
    }

    public TaskOffset(double forward, double strafe, double vertical) {
        this("");
        this.forward = forward;
        this.strafe = strafe;
        this.vertical = vertical;
    }

    public TaskOffset(int priority, double forward, double strafe, double vertical) {
        this(priority, "", forward, strafe, vertical);
    }

    public TaskOffset(int priority, String subName, double forward, double strafe, double vertical) {
        this(subName);
        setPriority(priority);
        this.forward = forward;
        this.strafe = strafe;
        this.vertical = vertical;
    }

    @Override
    public void run(ISkillContext context) {
        addMotion(context.getSource(), context.getSource().getLookVec());
    }

    protected void addMotion(Entity entityIn, Vec3d normalized)
    {
        Utils.MotionHelper.motion(entityIn, forward, vertical, strafe, 1.0F);
    }

    @Override
    public JsonDeserializer<? extends SkillTask> getDeserializer() {
        return (json, type, context) -> {
            JsonObject obj = json.getAsJsonObject();
            double forward = obj.has("forward") ? obj.get("forward").getAsDouble() : 0.0D;
            double strafe = obj.has("strafe") ? obj.get("strafe").getAsDouble() : 0.0D;
            double vertical = obj.has("vertical") ? obj.get("vertical").getAsDouble() : 0.0D;

            return new TaskOffset(forward, strafe, vertical);
        };
    }

    public static class OffsetForward extends TaskOffset {
        protected OffsetForward() {
            super("_FORWARD");
        }

        public OffsetForward(int priority, double forward) {
            super(priority, forward, 0.0D, 0.0D);
        }

        @Override
        protected void addMotion(Entity entityIn, Vec3d normalized) {
            Utils.MotionHelper.motionFromVector(entityIn, normalized, (float) forward);
        }

        @Override
        public JsonDeserializer<? extends SkillTask> getDeserializer() {
            return (json, type, context) -> {
                JsonObject obj = json.getAsJsonObject();
                return new OffsetForward(1, obj.has("forward") ? obj.get("forward").getAsDouble() : 0.0D);
            };
        }
    }

    public static class OffsetStrafe extends TaskOffset {
        protected OffsetStrafe() {
            super("_STRAFE");
        }

        public OffsetStrafe(int priority, double strafe) {
            super(priority, 0.0D, strafe, 0.0D);
            this.strafe = strafe;
        }

        @Override
        public JsonDeserializer<? extends SkillTask> getDeserializer() {
            return (json, type, context) -> {
                JsonObject obj = json.getAsJsonObject();
                return new OffsetStrafe(1, obj.has("strafe") ? obj.get("strafe").getAsDouble() : 0.0D);
            };
        }

        @Override
        protected void addMotion(Entity playerIn, Vec3d normalized) {
            float yaw = strafe != 0.0D ? strafe > 0 ? -90.0F : 90.0F : 0.0F;
            if(yaw != 0)
            {
                float rotateYaw = playerIn.rotationYaw;
                double z = MathHelper.cos(-(rotateYaw + yaw) * 0.017453292F - (float)Math.PI);
                double x = MathHelper.sin(-(rotateYaw + yaw) * 0.017453292F - (float)Math.PI);
                Utils.MotionHelper.motionFromVector(playerIn, new Vec3d(x, 0, z), (float) strafe);
            }
        }
    }

    public static class OffsetVertical extends TaskOffset {
        protected OffsetVertical() {
            super("_VERTICAL");
        }
        public OffsetVertical(int priority, double vertical) {
            super(priority, 0.0D, 0.0D,  vertical);
        }

        @Override
        public JsonDeserializer<? extends SkillTask> getDeserializer() {
            return (json, type, context) -> {
                JsonObject obj = json.getAsJsonObject();
                return new OffsetVertical(1, obj.has("vertical") ? obj.get("vertical").getAsDouble() : 0.0D);
            };
        }

        @Override
        protected void addMotion(Entity entityIn, Vec3d normalized) {
            entityIn.motionY = vertical;
        }
    }
}
