package com.ded.icwth.blocks.panels;

import com.ded.icwth.TileEntityBase;
import ic2.api.energy.prefab.BasicSource;
import ic2.api.info.ILocatable;
import ic2.api.item.IElectricItem;
import ic2.api.tile.IWrenchable;
import ic2.api.energy.tile.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public abstract class TileEntitySolarBase extends TileEntityBase implements ITickable, IWrenchable, ILocatable, IMultiEnergySource, IInventory {
    public BasicSource energy;
    private static final Random r = new Random();
    public double packetAmount;
    protected int tier;
    protected int tick;
    public double output;
    protected double capacity;
    protected String localizedName;
    protected NonNullList<ItemStack> inventory = NonNullList.withSize(6, ItemStack.EMPTY);
    private double lastEnergy = -1;

    public TileEntitySolarBase(double output, double capacity, int tier) {
        this.energy = new BasicSource((TileEntity) this, capacity, tier);
        this.output = output;
        this.capacity = capacity;
        this.tick = r.nextInt(64);
        this.tier = tier;
        this.localizedName = "tile.default_solar.name";
    }

    public TileEntitySolarBase() {
    }

    @Override
    public void invalidate() {
        super.invalidate();
        if (!world.isRemote) {
            ic2.api.energy.EnergyNet.instance.removeTile((IEnergySource) this);
        }
        if (this.energy != null) {
            this.energy.invalidate();
        }
    }

    @Override
    public void update() {
        if (this.world == null || this.world.isRemote) {
            return;
        }
        this.energy.update();
        this.checkConditions();

        if (this.energy.getEnergyStored() != this.lastEnergy) {
            sendUpdateToClient();
        }
    }

    protected void checkConditions() {
        this.createEnergy();
        this.chargeItems();
    }

    protected void createEnergy() {
        if (this.canGenerate()) {
            this.energy.addEnergy(this.output);
        }
    }

    protected boolean canGenerate() {
        return world.canSeeSky(pos.up()) && world.isDaytime() && !world.isRaining() && !world.isThundering();
    }

    protected void chargeItems() {
        ItemStack chargeStack = inventory.get(0);
        if (!chargeStack.isEmpty() && chargeStack.getItem() instanceof IElectricItem) {
            double transfer = Math.min(output, energy.getEnergyStored());
            double charged = ic2.api.item.ElectricItem.manager.charge(chargeStack, transfer, tier, false, false);
            energy.useEnergy(charged);
        }
    }

    protected void sendUpdateToClient() {
        if (!world.isRemote) {
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
            lastEnergy = energy.getEnergyStored();
        }
    }

    public double getEnergyStored() {
        return this.energy != null ? this.energy.getEnergyStored() : 0;
    }

    public double getMaxStorage() {
        return this.energy != null ? this.energy.getCapacity() : 0;
    }

    public double getOutput() {
        return this.output;
    }

    @Override
    public double getOfferedEnergy() {
        return Math.min(this.output, this.energy != null ? this.energy.getEnergyStored() : 0);
    }

    @Override
    public void drawEnergy(double amount) {
        if (this.energy != null) {
            this.energy.useEnergy(amount);
        }
    }

    @Override
    public int getSourceTier() {
        return this.tier;
    }

    @Override
    public boolean emitsEnergyTo(IEnergyAcceptor receiver, EnumFacing side) {
        return side != EnumFacing.UP;
    }

    @Override
    public boolean sendMultipleEnergyPackets() {
        return this.packetAmount > 0.0;
    }

    @Override
    public int getMultipleEnergyPacketAmount() {
        return (int) this.packetAmount;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.output = nbt.getDouble("output");
        this.capacity = nbt.getDouble("capacity");
        this.tier = nbt.getInteger("tier");
        this.localizedName = nbt.getString("localizedName");
        if (this.energy == null) {
            this.energy = new BasicSource((TileEntity) this, this.capacity, this.tier);
        }
        this.energy.readFromNBT(nbt);
        ItemStackHelper.loadAllItems(nbt, this.inventory);
        System.out.println("Loaded energy: " + this.energy.getEnergyStored() + ", capacity: " + this.capacity + ", tier: " + this.tier + ", localizedName: " + this.localizedName);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        if (this.energy != null) {
            this.energy.writeToNBT(nbt);
            nbt.setDouble("capacity", this.capacity);
            nbt.setInteger("tier", this.tier);
            nbt.setString("localizedName", this.localizedName);
        }
        nbt.setDouble("output", this.output);
        ItemStackHelper.saveAllItems(nbt, this.inventory);
        return nbt;
    }

    @Override
    public void onChunkUnload() {
        if (this.energy != null) {
            this.energy.onChunkUnload();
        }
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound nbt = new NBTTagCompound();
        this.writeToNBT(nbt);
        return new SPacketUpdateTileEntity(this.pos, 1, nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        this.readFromNBT(pkt.getNbtCompound());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return this.writeToNBT(new NBTTagCompound());
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        this.readFromNBT(tag);
    }

    @Override
    public World getWorld() {
        return super.getWorld();
    }

    @Override
    public BlockPos getPosition() {
        return super.getPos();
    }

    @Override
    public World getWorldObj() {
        return super.getWorld();
    }

    @Override
    public EnumFacing getFacing(World world, BlockPos pos) {
        return EnumFacing.UP;
    }

    @Override
    public boolean setFacing(World world, BlockPos pos, EnumFacing newDirection, EntityPlayer player) {
        return false;
    }

    @Override
    public boolean wrenchCanRemove(World world, BlockPos pos, EntityPlayer player) {
        return true;
    }

    @Override
    public List<ItemStack> getWrenchDrops(World world, BlockPos blockPos, IBlockState iBlockState, TileEntity tileEntity, EntityPlayer entityPlayer, int i) {
        return Collections.emptyList();
    }

    @Override
    public int getSizeInventory() {
        return inventory.size();
    }

    @Override
    public boolean isEmpty() {
        return inventory.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return inventory.get(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        return ItemStackHelper.getAndSplit(inventory, index, count);
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        return ItemStackHelper.getAndRemove(inventory, index);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        inventory.set(index, stack);
        markDirty();
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return world.getTileEntity(pos) == this && player.getDistanceSq(pos) <= 64;
    }

    @Override
    public void openInventory(EntityPlayer player) {}

    @Override
    public void closeInventory(EntityPlayer player) {}

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return index == 0 && stack.getItem() instanceof IElectricItem;
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {}

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {
        inventory.clear();
    }

    @Override
    public abstract String getName();

    @Override
    public boolean hasCustomName() {
        return false;
    }
}