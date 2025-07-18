package icu.takeneko.towel.coremod;

import java.util.List;
import java.util.Set;

import com.gtnewhorizon.gtnhmixins.ILateMixinLoader;
import com.gtnewhorizon.gtnhmixins.LateMixin;

import icu.takeneko.towel.helpers.mixin.MixinUtils;

@LateMixin
public class TLateMixins implements ILateMixinLoader {

    @Override
    public String getMixinConfig() {
        return "mixins.towel.late.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedMods) {
        return MixinUtils.getLateMixins(loadedMods);
    }
}
