package icu.takeneko.tick.mixins.early;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("UnusedMixin")
@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @Inject(method = "run", at = @At("HEAD"))
    void onTick(CallbackInfo ci){

    }
}
