package com.ded.icwth.blocks.panels;

import com.ded.icwth.TileEntityBase;
import com.ded.icwth.blocks.panels.SpectralSolarPanel.BlockSpectralSolarPanel;
import ic2.api.energy.EnergyNet;
import ic2.api.energy.prefab.BasicSource;
import ic2.api.energy.tile.IEnergyAcceptor;
import ic2.api.energy.tile.IMultiEnergySource;
import ic2.api.info.ILocatable;
import ic2.api.tile.IWrenchable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.List;
import java.util.Random;


public class TileEntitySolarBase1 extends TileEntityBase implements ITickable, IWrenchable, ILocatable, IMultiEnergySource {

    public BasicSource energy;
    private static final Random r = new Random();
    private int packetAmount;
    protected boolean isSunVisible;
    protected int tier;
    protected int tick;
    protected double output;

    public TileEntitySolarBase1(double output, double capacity, int tier) {
        // Инициализируем BasicSource с правильными параметрами
        this.energy = energy;
        this.output = output;
        this.tick = r.nextInt(64);
        this.tier = tier;
        updatePacketAmount();
    }
    protected void checkConditions() {
        if (tick-- <= 0) {
            updatePacketAmount();
            tick = 64;
        }
        createEnergy();
    }
    private void updatePacketAmount() {
        // Используем EnergyNet для получения размера пакета по тиру
        this.packetAmount = (int) (output / EnergyNet.instance.getPowerFromTier(tier));
    }
    protected boolean canGenerate() {
        return true;
    }
    @Override
    public void update() {
        if (world == null || world.isRemote) return;

        checkConditions();
        energy.update(); // Обновляем состояние энергии
    }



    protected void createEnergy() {
        if (isSunVisible && canGenerate()) {
            energy.addEnergy(output); // Добавляем энергию напрямую в BasicSource
        }
    }

    // Переписываем методы интерфейса IMultiEnergySource
    @Override
    public boolean sendMultipleEnergyPackets() {
        return packetAmount > 1;
    }

    @Override
    public int getMultipleEnergyPacketAmount() {
        return packetAmount;
    }

    // Реализуем методы IEnergySource через BasicSource
    @Override
    public double getOfferedEnergy() {
        return energy.getOfferedEnergy();
    }

    @Override
    public void drawEnergy(double amount) {
        energy.drawEnergy(amount);
    }

    @Override
    public int getSourceTier() {
        return energy.getSourceTier();
    }

    @Override
    public boolean emitsEnergyTo(IEnergyAcceptor receiver, EnumFacing side) {
        return side != EnumFacing.UP;
    }

    // Обновляем методы работы с NBT
    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        energy.readFromNBT(nbt);
        output = nbt.getDouble("output");
        updatePacketAmount();
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
    public EnumFacing getFacing() {
        if (world != null && world.getBlockState(pos).getBlock() instanceof BlockSpectralSolarPanel) {
            // Корректное получение направления
            return world.getBlockState(pos).getValue(BlockSpectralSolarPanel.FACING);
        }
        return EnumFacing.NORTH; // Значение по умолчанию
    }




}
