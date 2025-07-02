package icu.takeneko.tick.networking;

import io.netty.buffer.ByteBuf;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class S2CTickRateStatusPacket implements S2CPacketBase {
    private static final Logger logger = LogManager.getLogger();
    private float value;

    @SuppressWarnings("unused")
    public S2CTickRateStatusPacket() {
    }

    public S2CTickRateStatusPacket(float value) {
        this.value = value;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        value = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeFloat(value);
    }

    @Override
    public void handle() {
        logger.info("Server tps changed to {}", value);
        // original carpet mod (1.12) received but does not handle this packet
        // confused
    }
}
