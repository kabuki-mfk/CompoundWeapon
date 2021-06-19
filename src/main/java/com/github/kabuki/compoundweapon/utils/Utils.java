package com.github.kabuki.compoundweapon.utils;

import com.github.kabuki.compoundweapon.utils.math.Point;
import com.google.common.base.Predicates;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class Utils {
    public static class WorldHelper {
        protected static List<Entity> getEntitiesInRayAABBExcluding(Entity entityIn, float reach)
        {
            Vec3d vec3d = entityIn.getLookVec();
            return entityIn.world.getEntitiesInAABBexcluding(entityIn, entityIn.getEntityBoundingBox().expand(vec3d.x * reach, vec3d.y * reach, vec3d.z * reach).grow(1.0D, 1.0D, 1.0D),
                    Predicates.and(EntitySelectors.NOT_SPECTATING, (entity) -> entity != null && entity.canBeCollidedWith()));
        }

        public static List<EntityLivingBase> getEntityLivingInRange(Entity entityIn, float range)
        {
            return getEntitiesInRange(EntityLivingBase.class, entityIn, range);
        }

        public static List<EntityLivingBase> getEntityLivingInRange(World world, Vec3d pos, float range)
        {
            return getEntitiesInRange(EntityLivingBase.class, world, pos, range);
        }

        public static <T extends Entity> List<T> getEntitiesInRange(Class <? extends T > clazz, Entity entityIn, float range)
        {
            return getEntitiesInRange(clazz, entityIn.world, entityIn.getPositionVector(), range);
        }

        public static <T extends Entity> List<T> getEntitiesInRange(Class <? extends T > clazz, World world, Vec3d pos, float range)
        {
            AxisAlignedBB aabb = new AxisAlignedBB(pos.x - range, pos.y - range, pos.z - range, pos.x + range, pos.y + range, pos.z + range);
            return world.getEntitiesWithinAABB(clazz, aabb, entity -> pos.squareDistanceTo(entity.getPositionVector()) <= range * range);
        }
    }

    public static class SpaceHelper
    {
        public final static double PRECISION = 2E-10;

        @SideOnly(Side.CLIENT)
        @Nullable
        public static Entity getMouseOverEntity()
        {
            RayTraceResult ray = Minecraft.getMinecraft().objectMouseOver;
            return ray == null ? null : ray.entityHit;
        }

        public static boolean inPolygon(Point p, List<Point> pArr) {
            boolean flag = false;

            for(int i = 0, j = pArr.size() - 1; i < pArr.size(); j = i++) {
                Point pl = pArr.get(i);
                Point pr = pArr.get(j);

                if(acrossLine(p, pl, pr)) flag = !flag;
            }

            return flag;
        }

        public static boolean accuratelyInPolygon(Point p, List<Point> pArr, boolean boundOrVertex) {
            boolean flag = false;

            for(int i = 0, j = pArr.size() - 1; i < pArr.size(); j = i++) {
                Point pl = pArr.get(i);
                Point pr = pArr.get(j);

                if((pl.x > p.x) != (pr.x > p.x)) {
                    if(p.y <= Math.max(pl.y, pr.y)) {
                        if(pl.x == pr.x && p.y >= Math.min(pl.y, pr.y)) {
                            return boundOrVertex;
                        }

                        if(pl.y == pr.y) {
                            if(p.y == pl.y) {
                                return boundOrVertex;
                            }
                            else {
                                flag = !flag;
                            }
                        }
                        else {
                            double inters = (p.x - pl.x) * (pr.y - pl.y) / (pr.x - pl.x) + pl.y;
                            if(Math.abs(p.y - inters) < PRECISION) {
                                return boundOrVertex;
                            }
                            else if(p.y < inters) {
                                flag = !flag;
                            }
                        }
                    }
                }
                else {
                    if(p.x == pr.x && p.y <= pr.y) {
                        Point p2 = pArr.get((j + 1) % pArr.size());
                        if((pl.x > p.x) != (p2.x > p.x)) {
                            flag = !flag;
                        }
                    }
                }
            }

            return flag;
        }

        public static boolean acrossLine(Point p, Point pl, Point pr) {
            if((pl.y > p.y) != (pr.y > p.y)) {
                return p.x < (pr.x - pl.x) * (p.y - pl.y) / (pr.y - pl.y + pl.x);
            }
            return false;
        }

        @Nullable
        public static RayTraceResult reachRayTrace(Entity entity, float reach) {
            RayTraceResult rayTraceResult = rayTrace(entity, reach);
            double d1 = reach;
            Vec3d vec3d = entity.getLookVec();
            Vec3d vec3d2 = new Vec3d(entity.posX, entity.posY + (double)entity.getEyeHeight(), entity.posZ);
            Vec3d vec3d1 = vec3d2.add(vec3d.x * reach, vec3d.y * reach, vec3d.z * reach);

            if(rayTraceResult != null) {
                d1 = rayTraceResult.hitVec.distanceTo(vec3d2);
            }

            List<Entity> list = WorldHelper.getEntitiesInRayAABBExcluding(entity, (float) d1);
            Entity target = null;
            double d0 = 0.0D;
            for (Entity entityIn: list) {
                if (entityIn.canBeCollidedWith() && (entityIn != entity)) {
                    AxisAlignedBB axisalignedbb = entityIn.getEntityBoundingBox().expand(vec3d.x * reach, vec3d.y * reach, vec3d.z * reach);
                    RayTraceResult rayTraceResult1 = axisalignedbb.calculateIntercept(vec3d2, vec3d1);
                    if (rayTraceResult1 != null) {
                        double d2 = vec3d2.distanceTo(rayTraceResult1.hitVec);
                        if (d2 < d0 || d0 == 0.0D) {
                            target = entityIn;
                            d0 = d2;
                        }
                    }
                }
            }

            if (target != null) {
                rayTraceResult = new RayTraceResult(target);
            }

            if (rayTraceResult != null && rayTraceResult.entityHit instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) rayTraceResult.entityHit;
                if (player.capabilities.disableDamage || (entity instanceof EntityPlayer
                        && !((EntityPlayer) entity).canAttackPlayer(player)))
                {
                    rayTraceResult = null;
                }
            }
            return rayTraceResult;
        }

        public static RayTraceResult rayTrace(Entity entity, float reach) {
            Vec3d vec3d = new Vec3d(entity.posX, entity.posY + (double)entity.getEyeHeight(), entity.posZ);
            Vec3d vec3d1 = entity.getLookVec();
            Vec3d Vec3d2 = vec3d.add(vec3d1.x * reach, vec3d1.y * reach, vec3d.z * reach);
            return entity.world.rayTraceBlocks(vec3d, Vec3d2, false, false, true);
        }

        @Nullable
        public static Entity getReachEntity(EntityPlayer entityplayer, float reach) {
            RayTraceResult ray = rayTrace(entityplayer, reach);
            return ray == null ? null : ray.entityHit;
        }

    }

    public static class MotionHelper {
        public static void motion(@Nonnull Entity entity, double x, double y, double z, float velocity) {
            float f = (float) Math.sqrt(x * x + y * y + z * z);
            x += entity.world.rand.nextGaussian() * (entity.world.rand.nextBoolean() ? 1 : -1) * 0.007499999832361937D;
            y += entity.world.rand.nextGaussian() * (entity.world.rand.nextBoolean() ? 1 : -1) * 0.007499999832361937D;
            z += entity.world.rand.nextGaussian() * (entity.world.rand.nextBoolean() ? 1 : -1) * 0.007499999832361937D;
            x /= f;
            y /= f;
            z /= f;
            x *= velocity;
            y *= velocity;
            z *= velocity;
            entity.motionX = x;
            entity.motionY = y;
            entity.motionZ = z;
        }

        public static void motionFromVector(@Nonnull Entity entity, Vec3d vec3d, float velocity) {
            motion(entity, vec3d.x, vec3d.y, vec3d.z, velocity);
        }

        public static void rotateYawAndMotion(@Nonnull Entity entity, float yaw, Vec3d vec3d, float velocity) {
            Vec3d vec3d1 = vec3d.rotateYaw(yaw);
            motionFromVector(entity, vec3d1, velocity);
        }
    }
}
