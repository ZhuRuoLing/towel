package icu.takeneko.tick.coremod;

import com.gtnewhorizon.gtnhlib.mixin.IMixins;
import com.gtnewhorizon.gtnhmixins.ILateMixinLoader;
import com.gtnewhorizon.gtnhmixins.LateMixin;
import icu.takeneko.tick.mixins.TMixins;

import java.util.List;
import java.util.Set;

@LateMixin
public class TLateMixins implements ILateMixinLoader {
    @Override
    public String getMixinConfig() {
        return "mixins.command-tick.late.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedMods) {
        return IMixins.getLateMixins(TMixins.class, loadedMods);
    }
}
