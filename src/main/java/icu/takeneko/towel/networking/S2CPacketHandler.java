package icu.takeneko.towel.networking;

import net.minecraft.client.Minecraft;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;

public class S2CPacketHandler implements IMessageHandler<S2CPacketBase, IMessage> {

    @Override
    public IMessage onMessage(S2CPacketBase message, MessageContext ctx) {
        if (ctx.side != Side.CLIENT) return null;
        Minecraft.getMinecraft()
            .func_152344_a(message::handle);
        return null;
    }
}
