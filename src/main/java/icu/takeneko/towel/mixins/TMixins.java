package icu.takeneko.towel.mixins;

import java.util.List;

import com.google.common.collect.ImmutableList;

import icu.takeneko.towel.helpers.mixin.MixinPhase;
import icu.takeneko.towel.helpers.mixin.MixinSide;

public enum TMixins {

    MINECRAFT(ImmutableList.of("MinecraftServerMixin", "WorldServerMixin"), ImmutableList.of(), MixinPhase.EARLY,
        MixinSide.COMMON);

    private final List<String> mixinClasses;
    private final List<String> targets;
    private final MixinPhase phase;
    private final MixinSide side;

    TMixins(List<String> mixinClasses, List<String> targets, MixinPhase phase, MixinSide side) {
        this.mixinClasses = mixinClasses;
        this.targets = targets;
        this.phase = phase;
        this.side = side;
    }

    public List<String> getMixinClasses() {
        return mixinClasses;
    }

    public List<String> getTargets() {
        return targets;
    }

    public MixinPhase getPhase() {
        return phase;
    }

    public MixinSide getSide() {
        return side;
    }
}
