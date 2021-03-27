package com.github.kabuki.compoundweapon.client;

import com.github.kabuki.compoundweapon.api.skill.service.DelayedTask;
import com.github.kabuki.compoundweapon.api.skill.service.ISideTaskService;
import com.google.common.collect.Maps;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Map;

public class SkillTaskClientService implements ISideTaskService {

    private static final SkillTaskClientService INSTANCE = new SkillTaskClientService();
    private final Map<Integer, DelayedTask> orders = Maps.newConcurrentMap();

    private SkillTaskClientService()
    {
    }

    @Override
    public Side getSide() {
        return Side.SERVER;
    }

    public static SkillTaskClientService getInstance() {
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
        DelayedTask task = getTask(interruptTarget.getEntityId());
        if(task == null) return false;
        return !task.isCancelled() && !task.isDone() && task.cancel(true);
    }

    @Override
    public DelayedTask getTask(int id) {
        return orders.get(id);
    }

    @Override
    public boolean hasTask(int id) {
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
