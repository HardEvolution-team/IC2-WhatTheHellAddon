package com.ded.icwth.blocks.neutgen;

import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.items.ItemStackHandler;

public class TileNeutroniumGenerator extends TileEntity implements ITickable, IEnergySink {
    public static final int GUI_ID = 0;
    ItemStackHandler inventory = new ItemStackHandler(1);
    int progress;
    int maxProgressTicks = 100;
    private double energyPerTick = 10.0;

    @Override
    public void update() {
        if(world.isRemote) return;

        if(canProduce() && energy >= energyPerTick) {
            progress++;
            energy -= energyPerTick;

            if(progress >= maxProgressTicks) {
                produceItem();
                progress = 0;
            }
        } else {
            progress = 0;
        }
        markDirty();
    }

    private boolean canProduce() {
        ItemStack output = inventory.getStackInSlot(0);
        return output.isEmpty() ||
                (output.getItem() == Items.APPLE && output.getCount() < output.getMaxStackSize());
    }

    private void produceItem() {
        ItemStack output = inventory.getStackInSlot(0);
        if(output.isEmpty()) {
            inventory.setStackInSlot(0, new ItemStack(Items.APPLE));
        } else {
            output.grow(1);
        }
    }

    // IEnergySink implementation
    double energy;

    @Override
    public double getDemandedEnergy() {
        return energyPerTick - energy;
    }

    @Override
    public int getSinkTier() {
        return 14;
    }

    @Override
    public double injectEnergy(EnumFacing enumFacing, double v, double v1) {
        return 0;
    }



    // NBT methods
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setTag("inventory", inventory.serializeNBT());
        compound.setInteger("progress", progress);
        compound.setDouble("energy", energy);
        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        inventory.deserializeNBT(compound.getCompoundTag("inventory"));
        progress = compound.getInteger("progress");
        energy = compound.getDouble("energy");
        super.readFromNBT(compound);
    }

    @Override
    public boolean acceptsEnergyFrom(IEnergyEmitter iEnergyEmitter, EnumFacing enumFacing) {
        return false;
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(pos, 0, getUpdateTag());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
    }



    // Метод для доступа к инвентарю
    public IInventory getInventory() {
        return (IInventory) this.inventory;
    }
}