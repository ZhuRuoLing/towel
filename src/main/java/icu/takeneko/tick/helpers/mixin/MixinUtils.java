package icu.takeneko.tick.helpers.mixin;

import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableList;

import cpw.mods.fml.relauncher.FMLLaunchHandler;
import cpw.mods.fml.relauncher.Side;
import icu.takeneko.tick.mixins.TMixins;

public class MixinUtils {

    private static final List<MixinSide> CLIENT_ACCEPTS = ImmutableList.of(MixinSide.CLIENT, MixinSide.COMMON);
    private static final List<MixinSide> SERVER_ACCEPTS = ImmutableList.of(MixinSide.COMMON);

    public static List<String> getEarlyMixins(Set<String> loadedCoreMods) {
        ImmutableList.Builder<String> allMixins = new ImmutableList.Builder<>();
        for (TMixins value : TMixins.values()) {
            if (value.getPhase() != MixinPhase.EARLY) continue;
            Side side = FMLLaunchHandler.side();
            if (!sideAccepts(side, value.getSide())) continue;
            allMixins.addAll(value.getMixinClasses());
        }
        return allMixins.build();
    }

    public static List<String> getLateMixins(Set<String> loadedMods) {
        ImmutableList.Builder<String> allMixins = new ImmutableList.Builder<>();
        for (TMixins value : TMixins.values()) {
            if (value.getPhase() != MixinPhase.LATE) continue;
            Side side = FMLLaunchHandler.side();
            if (!sideAccepts(side, value.getSide())) continue;
            if (!loadedMods.containsAll(value.getTargets())) continue;
            allMixins.addAll(value.getMixinClasses());
        }
        return allMixins.build();
    }

    private static boolean sideAccepts(Side side, MixinSide mixinSide) {
        List<MixinSide> accepts;
        if (side == Side.CLIENT) {
            accepts = CLIENT_ACCEPTS;
        } else {
            accepts = SERVER_ACCEPTS;
        }
        return accepts.contains(mixinSide);
    }
}
