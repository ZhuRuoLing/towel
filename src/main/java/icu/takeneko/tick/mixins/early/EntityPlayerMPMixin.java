package icu.takeneko.tick.mixins.early;

import icu.takeneko.tick.helpers.fakes.EntityPlayerActionPack;
import icu.takeneko.tick.helpers.mixin.EntityPlayerMPExtension;
import net.minecraft.entity.player.EntityPlayerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EntityPlayerMP.class)
public class EntityPlayerMPMixin implements EntityPlayerMPExtension {
    @Unique
    private EntityPlayerActionPack cm$actionPack = new EntityPlayerActionPack((EntityPlayerMP) (Object) this);

    @Override
    public EntityPlayerActionPack cm$getActionPack() {
        return cm$actionPack;
    }

    @Override
    public void cm$setActionPack(EntityPlayerActionPack pack) {
        this.cm$actionPack = pack;
    }
}
