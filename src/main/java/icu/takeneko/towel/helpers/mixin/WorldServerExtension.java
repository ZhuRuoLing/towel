package icu.takeneko.towel.helpers.mixin;

import net.minecraft.world.WorldServer;

public interface WorldServerExtension {

    void setupMinecartFix();

    void teardownMinecartFix();

    static WorldServerExtension of(WorldServer world) {
        return (WorldServerExtension) world;
    }
}
