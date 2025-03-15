package com.ded.icwth.blocks.batbox;

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergyAcceptor;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;

import ic2.core.block.wiring.TileEntityElectricBlock;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.EnumSet;

public class TileCompressedMFSU extends TileEntityElectricBlock implements IEnergySink, IEnergySource {

    private static final int tier = 3;

    public TileCompressedMFSU() {
        super(tier, 512, 40000000);
        this.energy.setDirections(EnumSet.allOf(EnumFacing.class), EnumSet.allOf(EnumFacing.class));
        this.redstoneMode = 0;
        MinecraftForge.EVENT_BUS.register(this); // Регистрируем обработчик событий
    }

    @Override
    public void onLoaded() {
        super.onLoad();
        if (!world.isRemote) {
            MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
            world.notifyNeighborsOfStateChange(pos, getBlockType(), true);
        }
    }

    // Обработчик события выгрузки чанка
    @SubscribeEvent
    public void onChunkUnload(ChunkEvent.Unload event) {
        if (event.getChunk().getTileEntityMap().containsValue(this)) {
            if (!world.isRemote) {
                MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
            }
        }
    }

    @Override
    public boolean acceptsEnergyFrom(IEnergyEmitter emitter, EnumFacing side) {
        return true;
    }

    @Override
    public boolean emitsEnergyTo(IEnergyAcceptor receiver, EnumFacing side) {
        return true;
    }

    @Override
    public double getDemandedEnergy() {
        return Math.max(0, energy.getCapacity() - energy.getEnergy());
    }

    @Override
    public double injectEnergy(EnumFacing direction, double amount, double voltage) {
        return energy.addEnergy(amount);
    }

    @Override
    public double getOfferedEnergy() {
        return energy.getEnergy();
    }

    @Override
    public void drawEnergy(double amount) {
        energy.useEnergy(amount);
    }

    @Override
    public int getSourceTier() {
        return tier;
    }

    @Override
    public int getSinkTier() {
        return tier;
    }

    @Override
    public void setFacing(EnumFacing facing) {
        super.setFacing(facing);
        energy.setDirections(EnumSet.allOf(EnumFacing.class), EnumSet.allOf(EnumFacing.class));
    }

    // Удаляем TileEntity из энергосети при разрушении блока

}