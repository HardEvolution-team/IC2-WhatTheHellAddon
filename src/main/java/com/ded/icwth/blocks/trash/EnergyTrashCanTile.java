package com.ded.icwth.blocks.trash;

import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

public class EnergyTrashCanTile extends TileEntity implements IEnergySink {

    @Override
    public double getDemandedEnergy() {
        // Всегда готов принять любое количество энергии
        return Double.MAX_VALUE;
    }

    @Override
    public double injectEnergy(EnumFacing directionFrom, double amount, double voltage) {
        // Немедленно уничтожаем полученную энергию
        return 0; // 0 = вся энергия поглощена
    }

    @Override
    public boolean acceptsEnergyFrom(IEnergyEmitter emitter, EnumFacing direction) {
        // Принимаем энергию с любых сторон
        return true;
    }

    @Override
    public int getSinkTier() {
        // Максимальный уровень для приёма любого напряжения
        return Integer.MAX_VALUE; // Tier 6 = 8192 EU/p (Ultimate Hybrid Solar Panel)
    }
}
