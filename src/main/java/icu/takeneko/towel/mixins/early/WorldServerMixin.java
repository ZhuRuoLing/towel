package icu.takeneko.towel.mixins.early;

import icu.takeneko.towel.helpers.mixin.WorldServerExtension;
import net.minecraft.server.management.PlayerManager;
import net.minecraft.world.WorldServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import icu.takeneko.towel.helpers.TickSpeed;

@Mixin(WorldServer.class)
public abstract class WorldServerMixin implements WorldServerExtension {

    @Unique
    private boolean loginMinecartFix = false;

    @Shadow
    private PlayerManager thePlayerManager;

    @Override
    public void setupMinecartFix() {
        loginMinecartFix = true;
    }

    @Override
    public void teardownMinecartFix() {
        loginMinecartFix = false;
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    void onTick(CallbackInfo ci) {
        if (!TickSpeed.shouldTick()) {
            this.thePlayerManager.updatePlayerInstances();
            ci.cancel();
        }
    }
}
