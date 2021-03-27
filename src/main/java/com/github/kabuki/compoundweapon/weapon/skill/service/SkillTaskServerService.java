package com.github.kabuki.compoundweapon.weapon.skill.service;

import com.github.kabuki.compoundweapon.api.skill.service.DelayedTask;
import com.github.kabuki.compoundweapon.api.skill.service.ISideTaskService;
import com.github.kabuki.compoundweapon.common.network.SimpleNetworkHandler;
import com.github.kabuki.compoundweapon.common.network.SkillInterruptPacket;
import com.google.common.collect.Maps;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Map;

public class SkillTaskServerService implements ISideTaskService {

    public static final SkillTaskServerService INSTANCE = new SkillTaskServerService();
    private final Map<Integer, DelayedTask> orders = Maps.newConcurrentMap();

    private SkillTaskServerService()
    {
    }

    @Override
    public Side getSide() {
        return Side.SERVER;
    }

    public static SkillTaskServerService getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean scheduledRelease(int id, long unix, Runnable callback_fun) {
        if(hasTask(id)) return false;
        if(unix == 0)
        {
            callback_fun.run();
            return true;
        }
        DelayedTask task = new DelayedTask(unix, callback_fun);
        return orders.put(id, task) != null;
    }

    @Override
    public boolean interruptTask(Entity interruptTarget) {
        int id = interruptTarget.getEntityId();
        DelayedTask task = getTask(id);
        if(task == null) return false;
        SimpleNetworkHandler.INSTANCE.sendToPosition(new SkillInterruptPacket(id), interruptTarget.dimension, interruptTarget.getPosition());
        return !task.isCancelled() && !task.isDone() && task.cancel(true);
    }

    @Override
    public DelayedTask getTask(int id) {
        return orders.get(id);
    }

    public boolean hasTask(int id)
    {
        return orders.containsKey(id);
    }

    public void update() {
        if(orders.isEmpty()) return;
        orders.forEach((i, t) -> {
            if(t.isCancelled() || t.isDone())
            {
                orders.remove(i);
            }
            else
            {
                t.update();
            }
        });
    }
}
