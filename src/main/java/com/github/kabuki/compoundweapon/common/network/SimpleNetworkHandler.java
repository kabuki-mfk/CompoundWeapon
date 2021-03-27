package com.github.kabuki.compoundweapon.common.network;

import com.github.kabuki.compoundweapon.CompoundWeapon;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public enum SimpleNetworkHandler {
    INSTANCE;

    private final SimpleNetworkWrapper channel = NetworkRegistry.INSTANCE.newSimpleChannel(CompoundWeapon.MOD_ID);

    SimpleNetworkHandler() {
        int discriminator = 0;
        channel.registerMessage(SkillInterruptPacket.Handler.class, SkillInterruptPacket.class, discriminator++, Side.CLIENT);
    }

    public void sendToPlayer(IMessage message, EntityPlayerMP player)
    {
        channel.sendTo(message, player);
    }

    public void sendToPosition(IMessage message, int dimension, BlockPos pos)
    {
        channel.sendToAllAround(message, new NetworkRegistry.TargetPoint(dimension, pos.getX(), pos.getY(), pos.getZ(), 5.0D));
    }

    public void sendToServer(IMessage message)
    {
        channel.sendToServer(message);
    }

    public void sendToAllTracking(IMessage message, Entity tracker)
    {
        channel.sendToAllTracking(message, tracker);
    }
}
