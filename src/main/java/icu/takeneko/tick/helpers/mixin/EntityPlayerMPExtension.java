package icu.takeneko.tick.helpers.mixin;

import icu.takeneko.tick.helpers.fakes.EntityPlayerActionPack;
import icu.takeneko.tick.helpers.fakes.EntityPlayerMPFake;
import net.minecraft.entity.player.EntityPlayerMP;

public interface EntityPlayerMPExtension {

    static EntityPlayerMPExtension of(EntityPlayerMP playerShadow) {
        return (EntityPlayerMPExtension) playerShadow;
    }

    static EntityPlayerActionPack getActionPack(EntityPlayerMP playerShadow) {
        return ((EntityPlayerMPExtension) playerShadow).cm$getActionPack();
    }

    EntityPlayerActionPack cm$getActionPack();
    void cm$setActionPack(EntityPlayerActionPack pack);
}
