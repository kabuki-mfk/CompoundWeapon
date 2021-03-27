package com.github.kabuki.compoundweapon.common.network;

import com.github.kabuki.compoundweapon.api.skill.SkillAPI;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class SkillInterruptPacket implements IMessage {
    public int id;

    public SkillInterruptPacket() {
    }

    public SkillInterruptPacket(int entityId) {
        this.id = entityId;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.id = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(id);
    }

    public static class Handler implements IMessageHandler<SkillInterruptPacket, IMessage>
    {

        @Override
        public IMessage onMessage(SkillInterruptPacket message, MessageContext ctx) {
            int entityId = message.id;
            if(ctx.side.isClient())
            {
                Minecraft.getMinecraft().addScheduledTask(() -> {
                    Entity entity = Minecraft.getMinecraft().player.world.getEntityByID(entityId);
                    SkillAPI.getTimerService(Side.CLIENT).interruptTask(entity);
                });
            }
            return null;
        }

    }
}
