package com.github.kabuki.compoundweapon.api.skill.service;

import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;

public interface ISideTaskService {
    Side getSide();

    boolean scheduledRelease(int id, long unix, Runnable callback_fun);

    boolean interruptTask(Entity interruptTarget);

    DelayedTask getTask(int id);

    boolean hasTask(int id);
}
