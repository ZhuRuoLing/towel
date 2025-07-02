package icu.takeneko.tick.networking;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import icu.takeneko.tick.TickMod;

public class TNetworking {

    private static SimpleNetworkWrapper networkWrapper;
    private static final Logger logger = LogManager.getLogger();

    public static void preInit() {
        networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(TickMod.MODID);
        int id = 0;
        networkWrapper.registerMessage(new S2CPacketHandler(), S2CTickRateStatusPacket.class, id++, Side.CLIENT);
        logger.info("Registered {} packets.", id);
    }

    public static void broadcastTickRateChanges(float now) {
        networkWrapper.sendToAll(new S2CTickRateStatusPacket(now));
    }
}
