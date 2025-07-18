package icu.takeneko.towel.mixins.early;

import net.minecraft.network.NetworkSystem;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import icu.takeneko.towel.helpers.TickSpeed;

@SuppressWarnings("UnusedMixin")
@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

    @Shadow
    protected abstract void tick();

    @Shadow
    protected abstract NetworkSystem func_147137_ag();

    @Shadow
    private boolean serverIsRunning;

    @Shadow
    private ServerConfigurationManager serverConfigManager;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    void onTick(CallbackInfo ci) {
        TickSpeed.tick((MinecraftServer) (Object) this);
        if (!TickSpeed.shouldTick()) {
            net.minecraftforge.common.chunkio.ChunkIOExecutor.tick();
            func_147137_ag().networkTick();
            this.serverConfigManager.sendPlayerInfoToAllPlayers();
            ci.cancel();
        }
    }

    @Unique
    private void bridge$setServerRunning() {
        this.serverIsRunning = true;
    }

    @Redirect(method = "run", at = @At(value = "INVOKE", target = "Ljava/lang/Thread;sleep(J)V"))
    private static void dismissSleep(long time) {}

    @Unique
    private void bridge$sleepOrNot(boolean fallingBehind, long l) throws InterruptedException {
        if (fallingBehind) {
            Thread.sleep(1L);
        } else {
            Thread.sleep(Math.max(1L, TickSpeed.mspt - l));
        }
    }

    @Unique
    private void bridge$tick() {
        this.tick();
    }
}
