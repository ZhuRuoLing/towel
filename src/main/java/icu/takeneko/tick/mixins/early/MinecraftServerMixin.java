package icu.takeneko.tick.mixins.early;

import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import icu.takeneko.tick.helpers.TickSpeed;
import net.minecraft.server.MinecraftServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("UnusedMixin")
@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

    @Shadow
    protected abstract void tick();

    @Shadow
    private boolean serverIsRunning;

    @Inject(method = "tick", at = @At("HEAD"))
    void onTick(CallbackInfo ci) {
        TickSpeed.tick((MinecraftServer) (Object) this);
    }

    @Unique
    private void bridge$setServerRunning() {
        this.serverIsRunning = true;
    }

    @Redirect(
        method = "run",
        at = @At(value = "INVOKE", target = "Ljava/lang/Thread;sleep(J)V")
    )
    private static void dismissSleep(long time) {
    }

    @Unique
    private void bridge$sleepOrNot(boolean fallingBehind, long l) throws InterruptedException{
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
