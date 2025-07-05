package icu.takeneko.tick.coremod;

import java.util.List;
import java.util.Set;

import com.gtnewhorizon.gtnhmixins.ILateMixinLoader;
import com.gtnewhorizon.gtnhmixins.LateMixin;

import icu.takeneko.tick.helpers.mixin.MixinUtils;

@LateMixin
public class TLateMixins implements ILateMixinLoader {

    @Override
    public String getMixinConfig() {
        return "mixins.command-tick.late.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedMods) {
        return MixinUtils.getLateMixins(loadedMods);
    }
}
