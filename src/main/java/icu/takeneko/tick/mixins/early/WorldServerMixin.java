package icu.takeneko.tick.mixins.early;

import icu.takeneko.tick.helpers.TickSpeed;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerManager;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldServer.class)
public abstract class WorldServerMixin {

    @Shadow
    private PlayerManager thePlayerManager;


    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    void onTick(CallbackInfo ci) {
        if (!TickSpeed.shouldTick()) {
            this.thePlayerManager.updatePlayerInstances();
            ci.cancel();
        }
    }
}
