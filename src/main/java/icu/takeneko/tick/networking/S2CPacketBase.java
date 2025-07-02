package icu.takeneko.tick.networking;

import cpw.mods.fml.common.network.simpleimpl.IMessage;

public interface S2CPacketBase extends IMessage {
    void handle();
}
