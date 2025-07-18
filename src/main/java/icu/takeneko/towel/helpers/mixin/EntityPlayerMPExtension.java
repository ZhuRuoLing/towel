package icu.takeneko.towel.helpers.mixin;

import icu.takeneko.towel.helpers.fakes.EntityPlayerActionPack;
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
