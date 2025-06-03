package com.ded.icwth.mixin;

import com.ded.icwth.Tags;
import com.google.common.collect.ImmutableMap;
import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class IC2WTHLateMixinLoader implements ILateMixinLoader {
    public static final Map<String, Boolean> modMixinsConfig = new ImmutableMap.Builder<String, Boolean>()
            .build();

    @Override
    public List<String> getMixinConfigs() {
        return modMixinsConfig.keySet().stream().map(mod -> "mixins." + Tags.MODID + "." + mod + ".json")
                .collect(Collectors.toList());
    }
}
