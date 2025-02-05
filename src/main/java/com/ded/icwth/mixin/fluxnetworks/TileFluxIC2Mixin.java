package com.ded.icwth.mixin.fluxnetworks;


import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Mixin;

import org.spongepowered.asm.mixin.injection.At;
import sonar.fluxnetworks.common.tileentity.energy.TileIC2Energy;


@Mixin(
        value = TileIC2Energy.class,
        remap = false
)
public class TileFluxIC2Mixin {

    @ModifyReturnValue(
            method = "getSinkTier",
            at = @At("RETURN")
    )
    public int iC2_WhatTheHell$getSinkTier(int original) {
        return Integer.MAX_VALUE;
    }


    @ModifyReturnValue(
            method = "getSourceTier",
            at = @At("RETURN")
    )
    public int iC2_WhatTheHell$getSourceTier(int original) {
        return Integer.MAX_VALUE;
    }




    @ModifyReturnValue(
            method = "getDemandedEnergy",
            at = @At("RETURN")
    )
    public double iC2_WhatTheHell$getDemandedEnergy(double original) {
        return Double.MAX_VALUE;
    }



}
