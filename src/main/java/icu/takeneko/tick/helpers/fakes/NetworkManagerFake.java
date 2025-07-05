package icu.takeneko.tick.helpers.fakes;

import net.minecraft.network.NetworkManager;

public class NetworkManagerFake extends NetworkManager {
    public NetworkManagerFake(boolean isClient) {
        super(isClient);
    }

    public void disableAutoRead() {
    }

    public void checkDisconnected() {
    }
}
