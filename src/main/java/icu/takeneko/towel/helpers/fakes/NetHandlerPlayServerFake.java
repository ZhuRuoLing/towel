package icu.takeneko.towel.helpers.fakes;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IChatComponent;

public class NetHandlerPlayServerFake extends NetHandlerPlayServer {
    public NetHandlerPlayServerFake(MinecraftServer server, NetworkManager nm, EntityPlayerMP playerIn) {
        super(server, nm, playerIn);
    }

    public void sendPacket(final Packet packetIn) {
    }

    @Override
    public void onDisconnect(IChatComponent reason) {
        super.onDisconnect(reason);
    }
}



