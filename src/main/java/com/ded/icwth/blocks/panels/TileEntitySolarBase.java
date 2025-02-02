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
    protected boolean isSunVisible;
    protected int tier;
    protected int tick;
    public double output;

    // Конструктор с правильной инициализацией энергии
    public TileEntitySolarBase(double output, double capacity, int tier) {
        this.energy = energy; // capacity, tier, provideEnergy
        this.output = output;
        this.tick = r.nextInt(64);
        packetAmount = 2;
        this.tier = tier;

    }

    // Пример конструктора для наследников
    public TileEntitySolarBase() {
        this(1.0D, 1000, 1); // Пример значений по умолчанию
    }

    @Override
    public void update() {
        if (world == null || world.isRemote) return; // Работаем только на сервере

        energy.update();
        checkConditions();
    }

    protected void checkConditions() {
        // Убрана периодическая проверка солнца
        createEnergy();
    }

    protected void createEnergy() {
        if (canGenerate()) { // Теперь зависит только от canGenerate()
            energy.addEnergy(output);
        }
    }

    // Упрощенная проверка условий (можно переопределить в наследниках)
    protected boolean canGenerate() {
        return true; // Всегда разрешено по умолчанию
    }

    // Сохранение и загрузка
    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        energy.readFromNBT(nbt);
        output = nbt.getDouble("output");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        energy.writeToNBT(nbt);
        nbt.setDouble("output", output);
        return nbt;
    }

    @Override
    public void onChunkUnload() {
        energy.onChunkUnload();
    }

    @Override
    public void invalidate() {
        energy.invalidate();
        super.invalidate();
    }

    // Реализация ILocatable для BasicSource
    @Override
    public World getWorld() {
        return super.getWorld(); // Возвращаем мир через метод суперкласса
    }

    @Override
    public BlockPos getPosition() {
        return super.getPos(); // Используем позицию из TileEntity
    }

    @Override
    public World getWorldObj() {
        return super.getWorld();
    }


    // Реализация IWrenchable
    @Override
    public EnumFacing getFacing(World world, BlockPos pos) {
        return EnumFacing.UP; // Пример направления
    }

    @Override
    public boolean setFacing(World world, BlockPos pos, EnumFacing newDirection, EntityPlayer player) {
        return false; // Нельзя менять направление
    }

    @Override
    public boolean wrenchCanRemove(World world, BlockPos pos, EntityPlayer player) {
        return true; // Разрешить удаление гаечным ключом
    }

    @Override
    public List<ItemStack> getWrenchDrops(World world, BlockPos pos, IBlockState state, TileEntity te, EntityPlayer player, int fortune) {
        return Collections.singletonList(new ItemStack(state.getBlock()));
    }

    @Override
    public boolean sendMultipleEnergyPackets() {
        return packetAmount > 0;
    }

    @Override
    public int getMultipleEnergyPacketAmount() {
        return (int) packetAmount;
    }

    @Override
    public double getOfferedEnergy() {
        return Math.min(output, storage);
    }

    @Override
    public void drawEnergy(double amount) {
        storage -= amount;
    }

    @Override
    public int getSourceTier() {
        return tier;
    }


    @Override
    public boolean emitsEnergyTo(IEnergyAcceptor receiver, EnumFacing side) {
        return side != EnumFacing.UP;
    }


}
