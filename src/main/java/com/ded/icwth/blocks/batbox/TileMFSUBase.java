package com.ded.icwth.blocks.batbox;

import ic2.api.energy.EnergyNet;
import ic2.api.energy.tile.*;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
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
import net.minecraftforge.fml.common.FMLCommonHandler;

public abstract class TileMFSUBase extends TileEntity implements ITickable, IEnergySink, IEnergySource, IInventory {
    public double energy;
    protected EnumFacing facing = EnumFacing.NORTH;
    protected NonNullList<ItemStack> inventory = NonNullList.withSize(6, ItemStack.EMPTY);
    protected int tier;
    protected double output;
    public double maxStorage;
    public String storageName;

    public TileMFSUBase(int tier, double output, double maxStorage, String storageName) {
        this.tier = tier;
        this.output = output;
        this.maxStorage = maxStorage;
        this.storageName = storageName;
    }

    // Конструктор без аргументов для загрузки из NBT
    public TileMFSUBase() {
    }

    @Override
    public void update() {
        if (!world.isRemote) {
            chargeItems();
            transferEnergy();
            markDirty();
            if (this.energy != this.getLastEnergy()) {
                sendUpdateToClient();
            }
        }
    }

    private double lastEnergy = -1;

    protected double getLastEnergy() {
        return lastEnergy;
    }

    protected void sendUpdateToClient() {
        if (!world.isRemote) {
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
            lastEnergy = energy;
        }
    }

    protected void chargeItems() {
        ItemStack chargeStack = inventory.get(0);
        if (!chargeStack.isEmpty() && chargeStack.getItem() instanceof IElectricItem) {
            double transfer = Math.min(output, energy);
            double charged = ElectricItem.manager.charge(chargeStack, transfer, tier, false, false);
            energy -= charged;
        }
    }

    protected void transferEnergy() {
        if (facing != null) {
            TileEntity tileEntity = world.getTileEntity(pos.offset(facing));
            if (tileEntity instanceof IEnergySink) {
                IEnergySink sink = (IEnergySink) tileEntity;
                if (sink.acceptsEnergyFrom(this, facing.getOpposite())) {
                    double transfer = Math.min(output, energy);
                    double sent = sink.injectEnergy(facing.getOpposite(), transfer, tier);
                    energy -= sent;
                }
            }
        }
    }

    @Override
    public double getDemandedEnergy() {
        return Math.min(maxStorage - energy, output);
    }

    @Override
    public int getSinkTier() {
        return tier;
    }

    @Override
    public double injectEnergy(EnumFacing direction, double amount, double voltage) {
        if (direction != facing) {
            double added = Math.min(maxStorage - energy, amount);
            energy += added;
            return amount - added;
        }
        return amount;
    }

    @Override
    public double getOfferedEnergy() {
        return Math.min(energy, output);
    }

    @Override
    public void drawEnergy(double amount) {
        energy = Math.max(0, energy - amount);
    }

    @Override
    public int getSourceTier() {
        return tier;
    }

    @Override
    public boolean emitsEnergyTo(IEnergyAcceptor receiver, EnumFacing side) {
        return side == facing;
    }

    @Override
    public boolean acceptsEnergyFrom(IEnergyEmitter emitter, EnumFacing side) {
        return side != facing;
    }

    public void setFacing(EnumFacing facing) {
        this.facing = facing;
        markDirty();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setDouble("Energy", energy);
        compound.setInteger("Facing", facing.getIndex());
        compound.setInteger("Tier", tier);
        compound.setDouble("Output", output);
        compound.setDouble("MaxStorage", maxStorage);
        compound.setString("StorageName", storageName);
        ItemStackHelper.saveAllItems(compound, inventory);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        energy = compound.getDouble("Energy");
        facing = EnumFacing.byIndex(compound.getInteger("Facing"));
        tier = compound.getInteger("Tier");
        output = compound.getDouble("Output");
        maxStorage = compound.getDouble("MaxStorage");
        storageName = compound.getString("StorageName");
        ItemStackHelper.loadAllItems(compound, inventory);
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
    public String getName() {
        return "container.mfsu";
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (!world.isRemote) {
            EnergyNet.instance.addTile(this);
        }
    }

    public double getOutput() {
        return output;
    }

    @Override
    public void invalidate() {
        super.invalidate();
        if (!world.isRemote) {
            EnergyNet.instance.removeTile(this);
        }
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }
}