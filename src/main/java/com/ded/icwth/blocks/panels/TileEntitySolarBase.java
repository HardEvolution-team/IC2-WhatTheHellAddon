package com.ded.icwth.blocks.panels;

import com.ded.icwth.TileEntityBase;
import ic2.api.energy.prefab.BasicSource;
import ic2.api.info.ILocatable;
import ic2.api.tile.IWrenchable;

import ic2.api.energy.EnergyNet;
import ic2.api.energy.event.*;
import ic2.api.energy.tile.*;
import ic2.core.*;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlotCharge;
import ic2.core.gui.dynamic.*;
import ic2.core.init.Localization;
import ic2.core.network.GuiSynced;




import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.storage.WorldInfo;

import java.util.Collections;
import java.util.List;
import java.util.Random;


public class TileEntitySolarBase extends TileEntityBase implements ITickable, IWrenchable, ILocatable, IMultiEnergySource {

    public BasicSource energy;
    private static final Random r = new Random();
    public double packetAmount;
    private double storage;
    protected int tier;
    protected int tick;
    public double output;

    // Конструктор с правильной инициализацией энергии
    public TileEntitySolarBase(double output, double capacity, int tier) {
        this.energy = this.energy;
        this.output = output;
        this.tick = r.nextInt(64);
        this.tier = tier;

    }
    public TileEntitySolarBase() {
        this(1.0, 1000.0, 1);
    }

    public void update() {
        if (this.world == null || this.world.isRemote) {
            return;
        }
        this.energy.update();
        this.checkConditions();
    }

    protected void checkConditions() {
        this.createEnergy();
    }

    protected void createEnergy() {
        if (this.canGenerate()) {
            this.energy.addEnergy(this.output);
        }
    }

    protected boolean canGenerate() {
        return true;
    }

    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.energy.readFromNBT(nbt);
        this.output = nbt.getDouble("output");
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        this.energy.writeToNBT(nbt);
        nbt.setDouble("output", this.output);
        return nbt;
    }

    public void onChunkUnload() {
        this.energy.onChunkUnload();
    }

    public void invalidate() {
        this.energy.invalidate();
        super.invalidate();
    }

    public World getWorld() {
        return super.getWorld();
    }

    public BlockPos getPosition() {
        return super.getPos();
    }

    public World getWorldObj() {
        return super.getWorld();
    }

    public EnumFacing getFacing(World world, BlockPos pos) {
        return EnumFacing.UP;
    }

    public boolean setFacing(World world, BlockPos pos, EnumFacing newDirection, EntityPlayer player) {
        return false;
    }

    public boolean wrenchCanRemove(World world, BlockPos pos, EntityPlayer player) {
        return true;
    }

    public List<ItemStack> getWrenchDrops(World world, BlockPos pos, IBlockState state, TileEntity te, EntityPlayer player, int fortune) {
        return Collections.singletonList(new ItemStack(state.getBlock()));
    }

    public boolean sendMultipleEnergyPackets() {
        return this.packetAmount > 0.0;
    }

    public int getMultipleEnergyPacketAmount() {
        return (int)this.packetAmount;
    }

    public double getOfferedEnergy() {
        return Math.min(this.output, this.storage);
    }

    public void drawEnergy(double amount) {
        this.storage -= amount;
    }

    public int getSourceTier() {
        return this.tier;
    }

    public boolean emitsEnergyTo(IEnergyAcceptor receiver, EnumFacing side) {
        return side != EnumFacing.UP;
    }
}