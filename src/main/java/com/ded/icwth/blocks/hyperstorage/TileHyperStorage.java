package com.ded.icwth.blocks.hyperstorage;

import com.ded.icwth.blocks.hyperstorage.gui.ContainerHyperStorage;
import ic2.api.energy.EnergyNet;
import ic2.api.energy.tile.*;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
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
import net.minecraft.world.WorldServer;

public class TileHyperStorage extends TileEntity implements ITickable, IEnergySink, IEnergySource, IInventory {

    // Energy storage variables
    public double v1 = 0.0;
    public double v2 = 0.0;
    public double v3 = 0.0;
    private static final double OVERFLOW_STEP = 1e-323;
    private static final double MAX_V1 = Double.MAX_VALUE;

    // Other fields
    protected EnumFacing facing = EnumFacing.NORTH;
    protected NonNullList<ItemStack> inventory = NonNullList.withSize(6, ItemStack.EMPTY);
    protected int tier;
    protected double output;
    public double maxStorage;
    public String storageName;

    // Client sync tracking (server side)
    private double lastSentV1 = -1;
    private double lastSentV2 = -1;
    private double lastSentV3 = -1;

    public TileHyperStorage(int tier, double output, double maxStorage, String storageName) {
        this.tier = tier;
        this.output = output;
        this.maxStorage = maxStorage;
        this.storageName = storageName;
        System.out.println("[TileHyperStorage] Constructor (tier: " + tier + ", output: " + output + ", maxStorage: " + maxStorage + ", storageName: " + storageName + ")");
    }

    public TileHyperStorage() {
        System.out.println("[TileHyperStorage] Default Constructor");
    }



    private void logState(String prefix) {
        System.out.printf("[TileHyperStorage %s] %s - Pos: %s, v1: %.3e, v2: %.3e, v3: %.3e%n",
                (world != null && world.isRemote ? "Client" : "Server"), prefix, pos, v1, v2, v3);
    }

    private double addEnergyInternal(double amount) {
        if (amount <= 0) {
            System.out.println("[TileHyperStorage Server] addEnergyInternal: Amount <= 0 (" + amount + ")");
            return 0;
        }
        if (v3 >= MAX_V1) {
            System.out.println("[TileHyperStorage Server] addEnergyInternal: v3 >= MAX_V1, rejecting amount: " + amount);
            return amount;
        }

        logState("Add Start (Amount: " + String.format("%.3e", amount) + ")");

        double remaining = amount;

        // Добавляем в v1
        double spaceInV1 = MAX_V1 - v1;
        double addToV1 = Math.min(remaining, spaceInV1);
        v1 += addToV1;
        remaining -= addToV1;
        System.out.println("[TileHyperStorage Server] Added to v1: " + addToV1 + ", Remaining: " + remaining);

        // Обрабатываем переполнение v1 -> v2
        while (remaining > 0) {
            double overflowsToV2 = Math.floor(remaining / MAX_V1) * OVERFLOW_STEP;
            double remainder = remaining % MAX_V1;

            double spaceInV2 = MAX_V1 - v2;
            double addToV2 = Math.min(overflowsToV2, spaceInV2);
            v2 += addToV2;
            remaining = overflowsToV2 > addToV2 ? remainder : remainder + (overflowsToV2 - addToV2) * MAX_V1 / OVERFLOW_STEP;
            System.out.println("[TileHyperStorage Server] Added to v2: " + addToV2 + ", Remaining: " + remaining);

            // Обрабатываем переполнение v2 -> v3
            if (v2 >= MAX_V1) {
                double overflowsToV3 = Math.floor(v2 / MAX_V1) * OVERFLOW_STEP;
                v2 -= Math.floor(v2 / MAX_V1) * MAX_V1;
                double spaceInV3 = MAX_V1 - v3;
                double addToV3 = Math.min(overflowsToV3, spaceInV3);
                v3 += addToV3;
                System.out.println("[TileHyperStorage Server] Added to v3: " + addToV3);
                if (addToV3 < overflowsToV3) {
                    remaining = remaining + (overflowsToV3 - addToV3) * MAX_V1 / OVERFLOW_STEP;
                    System.out.println("[TileHyperStorage Server] v3 overflow, adjusted remaining: " + remaining);
                    break;
                }
            }

            spaceInV1 = MAX_V1 - v1;
            addToV1 = Math.min(remaining, spaceInV1);
            v1 += addToV1;
            remaining -= addToV1;
            System.out.println("[TileHyperStorage Server] Added to v1 (loop): " + addToV1 + ", Remaining: " + remaining);

            if (remaining <= 0 || v3 >= MAX_V1) break;
        }

        if (v1 > MAX_V1) v1 = MAX_V1;
        if (v2 > MAX_V1) v2 = MAX_V1;
        if (v3 > MAX_V1) v3 = MAX_V1;

        double rejected = Math.max(0, remaining);
        logState("Add End (Rejected: " + String.format("%.3e", rejected) + ")");
        return rejected;
    }

    private double removeEnergyInternal(double amount) {
        if (amount <= 0) {
            System.out.println("[TileHyperStorage Server] removeEnergyInternal: Amount <= 0 (" + amount + ")");
            return 0;
        }
        logState("Remove Start (Amount: " + String.format("%.3e", amount) + ")");

        double totalRemoved = 0;
        double remainingToRemove = amount;

        // Remove from v1
        double availableInV1 = v1;
        double removeFromV1 = Math.min(remainingToRemove, availableInV1);
        v1 -= removeFromV1;
        totalRemoved += removeFromV1;
        remainingToRemove -= removeFromV1;
        System.out.println("[TileHyperStorage Server] Removed from v1: " + removeFromV1 + ", Total Removed: " + totalRemoved);

        // Borrow from v2 if needed
        while (remainingToRemove > 0 && v2 > 0) {
            v2 -= OVERFLOW_STEP;
            v1 = MAX_V1;
            availableInV1 = v1;
            removeFromV1 = Math.min(remainingToRemove, availableInV1);
            v1 -= removeFromV1;
            totalRemoved += removeFromV1;
            remainingToRemove -= removeFromV1;
            System.out.println("[TileHyperStorage Server] Borrowed from v2, Removed from v1: " + removeFromV1 + ", Total Removed: " + totalRemoved);
            if (v1 > 0 || remainingToRemove <= 0) break;
        }

        // Borrow from v3 if needed
        while (remainingToRemove > 0 && v3 > 0) {
            v3 -= OVERFLOW_STEP;
            v2 = MAX_V1;
            while (remainingToRemove > 0 && v2 > 0) {
                v2 -= OVERFLOW_STEP;
                v1 = MAX_V1;
                availableInV1 = v1;
                removeFromV1 = Math.min(remainingToRemove, availableInV1);
                v1 -= removeFromV1;
                totalRemoved += removeFromV1;
                remainingToRemove -= removeFromV1;
                System.out.println("[TileHyperStorage Server] Borrowed from v3, Removed from v1: " + removeFromV1 + ", Total Removed: " + totalRemoved);
                if (v1 > 0 || remainingToRemove <= 0) break;
            }
            if (remainingToRemove <= 0) break;
        }

        if (v1 < 0) v1 = 0;
        if (v2 < 0) v2 = 0;
        if (v3 < 0) v3 = 0;

        logState("Remove End (Actual Removed: " + String.format("%.3e", totalRemoved) + ")");
        return totalRemoved;
    }

    // --- ITickable ---
    @Override
    public void update() {
        if (world == null) {
            System.out.println("[TileHyperStorage] update: world is null");
            return;
        }
        if (world.isRemote) {
            System.out.println("[TileHyperStorage Client] update called, skipping");
            return;
        }

        System.out.println("[TileHyperStorage Server] update start");
        boolean stateChanged = false;

        // Проверяем изменения энергии с последнего синка
        if (v1 != lastSentV1 || v2 != lastSentV2 || v3 != lastSentV3) {
            stateChanged = true;
        }

        // Сохраняем начальные значения для проверки chargeItems()
        double initialV1 = v1, initialV2 = v2, initialV3 = v3;

        chargeItems();

        // Проверяем, изменил ли chargeItems() энергию
        if (v1 != initialV1 || v2 != initialV2 || v3 != initialV3) {
            stateChanged = true;
        }

        // Если были изменения, обновляем клиент
        if (stateChanged) {
            lastSentV1 = v1;
            lastSentV2 = v2;
            lastSentV3 = v3;
            markDirty();
            forceSyncToClient();
            System.out.println("[TileHyperStorage Server] Energy changed, syncing to client: v1=" + v1 + ", v2=" + v2 + ", v3=" + v3);
        }

        System.out.println("[TileHyperStorage Server] update end");
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        NBTTagCompound nbt = pkt.getNbtCompound();
        handleUpdateTag(nbt);
        // Log after updating data
        System.out.println("[TileHyperStorage Client] Updated energy: v1=" + v1 + ", v2=" + v2 + ", v3=" + v3);
        // Force block rendering update on the client
        if (world.isRemote) {
            world.markBlockRangeForRenderUpdate(pos, pos);
        }
    }

    // --- Item Charging ---
    protected void chargeItems() {
        System.out.println("[TileHyperStorage Server] chargeItems start");
        ItemStack chargeStack = inventory.get(0);
        if (chargeStack.isEmpty()) {
            System.out.println("[TileHyperStorage Server] chargeItems: Slot 0 is empty");
            return;
        }
        if (!(chargeStack.getItem() instanceof IElectricItem)) {
            System.out.println("[TileHyperStorage Server] chargeItems: Item in slot 0 is not IElectricItem");
            return;
        }

        double maxOffer = Math.min(this.output, getTotalEnergyForDisplay());
        if (maxOffer <= 0) {
            System.out.println("[TileHyperStorage Server] chargeItems: maxOffer <= 0 (" + maxOffer + ")");
            return;
        }

        double chargeNeeded = ElectricItem.manager.charge(chargeStack, Double.POSITIVE_INFINITY, tier, true, true);
        double offer = Math.min(maxOffer, chargeNeeded);
        System.out.println("[TileHyperStorage Server] chargeItems: Charge needed: " + chargeNeeded + ", Offering: " + offer);

        if (offer > 0) {
            double removed = removeEnergyInternal(offer);
            System.out.println("[TileHyperStorage Server] chargeItems: Removed energy: " + removed);
            if (removed > 0) {
                double charged = ElectricItem.manager.charge(chargeStack, removed, tier, false, false);
                double refund = removed - charged;
                System.out.println("[TileHyperStorage Server] chargeItems: Charged: " + charged + ", Refund: " + refund);
                if (refund > 0) {
                    addEnergyInternal(refund);
                    System.out.println("[TileHyperStorage Server] chargeItems: Refunded " + refund + " energy");
                }
            }
        }
        System.out.println("[TileHyperStorage Server] chargeItems end");
    }

    // --- IEnergySink ---
    @Override
    public double getDemandedEnergy() {
        double demanded = (v3 >= MAX_V1) ? 0 : Double.MAX_VALUE;
        System.out.println("[TileHyperStorage Server] getDemandedEnergy: " + demanded);
        return demanded;
    }

    @Override
    public int getSinkTier() {
        System.out.println("[TileHyperStorage Server] getSinkTier: " + tier);
        return tier;
    }

    @Override
    public double injectEnergy(EnumFacing direction, double amount, double voltage) {
        System.out.println("[TileHyperStorage Server] injectEnergy: Direction: " + direction + ", Amount: " + amount + ", Voltage: " + voltage);
        if (direction == facing) {
            System.out.println("[TileHyperStorage Server] injectEnergy: Direction matches facing, rejecting amount: " + amount);
            return amount;
        }
        if (v3 >= MAX_V1) {
            System.out.println("[TileHyperStorage Server] injectEnergy: v3 >= MAX_V1, rejecting amount: " + amount);
            return amount;
        }

        logState("Inject Start (Amount: " + String.format("%.3e", amount) + ")");
        double amountRejected = addEnergyInternal(amount);
        logState("Inject End (Rejected: " + String.format("%.3e", amountRejected) + ")");
        return amountRejected;
    }

    @Override
    public boolean acceptsEnergyFrom(IEnergyEmitter emitter, EnumFacing side) {
        boolean accepts = side != facing;
        System.out.println("[TileHyperStorage Server] acceptsEnergyFrom: Emitter: " + emitter + ", Side: " + side + ", Accepts: " + accepts);
        return accepts;
    }

    // --- IEnergySource ---
    @Override
    public double getOfferedEnergy() {
        double available = 0;
        if (v1 > 0) available = v1;
        else if (v2 > 0) available = MAX_V1;
        else if (v3 > 0) available = MAX_V1;

        available = Math.max(0, available);
        if (Double.isNaN(available)) available = 0;

        double offer = Math.min(available, this.output);
        System.out.println("[TileHyperStorage Server] getOfferedEnergy: Available: " + available + ", Offer: " + offer);
        return Math.max(0, offer);
    }

    @Override
    public void drawEnergy(double amount) {
        System.out.println("[TileHyperStorage Server] drawEnergy: Amount: " + amount);
        if (amount <= 0) {
            System.out.println("[TileHyperStorage Server] drawEnergy: Amount <= 0, skipping");
            return;
        }
        logState("Draw Start (Amount: " + String.format("%.3e", amount) + ")");
        removeEnergyInternal(amount);
        logState("Draw End");
    }

    @Override
    public int getSourceTier() {
        System.out.println("[TileHyperStorage Server] getSourceTier: " + tier);
        return tier;
    }

    @Override
    public boolean emitsEnergyTo(IEnergyAcceptor receiver, EnumFacing side) {
        boolean emits = side == facing;
        System.out.println("[TileHyperStorage Server] emitsEnergyTo: Receiver: " + receiver + ", Side: " + side + ", Emits: " + emits);
        return emits;
    }

    // --- Facing ---
    public void setFacing(EnumFacing facing) {
        System.out.println("[TileHyperStorage Server] setFacing: New facing: " + facing);
        if (this.facing != facing) {
            this.facing = facing;
            markDirty();
            forceSyncToClient();
        }
    }

    // --- NBT Saving/Loading ---
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        System.out.println("[TileHyperStorage] writeToNBT start");
        super.writeToNBT(compound);
        compound.setDouble("v1", v1);
        compound.setDouble("v2", v2);
        compound.setDouble("v3", v3);
        compound.setInteger("Facing", facing.getIndex());
        compound.setInteger("Tier", tier);
        compound.setDouble("Output", output);
        compound.setString("StorageName", storageName);
        ItemStackHelper.saveAllItems(compound, inventory);
        System.out.println("[TileHyperStorage] writeToNBT end");
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        System.out.println("[TileHyperStorage] readFromNBT start");
        super.readFromNBT(compound);
        v1 = compound.getDouble("v1");
        v2 = compound.getDouble("v2");
        v3 = compound.getDouble("v3");
        facing = EnumFacing.byIndex(compound.getInteger("Facing"));
        tier = compound.getInteger("Tier");
        output = compound.getDouble("Output");
        storageName = compound.getString("StorageName");
        inventory = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(compound, inventory);
        if (world != null && !world.isRemote) {
            lastSentV1 = -1;
            lastSentV2 = -1;
            lastSentV3 = -1;
            forceSyncToClient();
        }
        logState("NBT Load");
        System.out.println("[TileHyperStorage] readFromNBT end");
    }

    // --- Network Updates ---
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        logState("Sending update packet");
        SPacketUpdateTileEntity packet = new SPacketUpdateTileEntity(this.pos, 1, this.getUpdateTag());
        System.out.println("[TileHyperStorage Server] getUpdatePacket: Created packet for pos " + pos);
        return packet;
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        System.out.println("[TileHyperStorage] getUpdateTag");
        return this.writeToNBT(new NBTTagCompound());
    }


    public void forceSyncToClient() {
        if (!world.isRemote) {
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
        }
    }
    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        System.out.println("[TileHyperStorage] handleUpdateTag");
        this.readFromNBT(tag);
    }

    // --- IInventory ---
    @Override
    public int getSizeInventory() {
        System.out.println("[TileHyperStorage] getSizeInventory: 6");
        return 6;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : inventory) {
            if (!stack.isEmpty()) {
                System.out.println("[TileHyperStorage] isEmpty: Found non-empty stack");
                return false;
            }
        }
        System.out.println("[TileHyperStorage] isEmpty: Inventory is empty");
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        ItemStack stack = (index < 0 || index >= inventory.size()) ? ItemStack.EMPTY : inventory.get(index);
        System.out.println("[TileHyperStorage] getStackInSlot: Index: " + index + ", Stack: " + stack);
        return stack;
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack stack = ItemStackHelper.getAndSplit(inventory, index, count);
        System.out.println("[TileHyperStorage] decrStackSize: Index: " + index + ", Count: " + count + ", Result: " + stack);
        return stack;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        ItemStack stack = ItemStackHelper.getAndRemove(inventory, index);
        System.out.println("[TileHyperStorage] removeStackFromSlot: Index: " + index + ", Result: " + stack);
        return stack;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        System.out.println("[TileHyperStorage] setInventorySlotContents: Index: " + index + ", Stack: " + stack);
        if (index >= 0 && index < inventory.size()) {
            inventory.set(index, stack);
            if (!stack.isEmpty() && stack.getCount() > getInventoryStackLimit()) stack.setCount(getInventoryStackLimit());
            markDirty();
        }
    }

    @Override
    public int getInventoryStackLimit() {
        System.out.println("[TileHyperStorage] getInventoryStackLimit: 64");
        return 64;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        boolean usable = world != null && world.getTileEntity(pos) == this &&
                player.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
        System.out.println("[TileHyperStorage] isUsableByPlayer: Player: " + player.getName() + ", Usable: " + usable);
        return usable;
    }

    @Override
    public void openInventory(EntityPlayer player) {
        System.out.println("[TileHyperStorage Server] openInventory: Player: " + player.getName());
        forceSyncToClient();
    }

    @Override
    public void closeInventory(EntityPlayer player) {
        System.out.println("[TileHyperStorage Server] closeInventory: Player: " + player.getName());
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        boolean valid = (index == 0 || index == 1) && !stack.isEmpty() && stack.getItem() instanceof IElectricItem;
        System.out.println("[TileHyperStorage] isItemValidForSlot: Index: " + index + ", Stack: " + stack + ", Valid: " + valid);
        return valid;
    }

    @Override
    public void clear() {
        System.out.println("[TileHyperStorage] clear inventory");
        inventory.clear();
    }

    @Override
    public String getName() {
        String name = this.hasWorld() ? this.getBlockType().getTranslationKey() + ".name" : "container.hyperstorage";
        System.out.println("[TileHyperStorage] getName: " + name);
        return name;
    }

    @Override
    public boolean hasCustomName() {
        System.out.println("[TileHyperStorage] hasCustomName: false");
        return false;
    }

    // --- Container Fields ---
    @Override
    public int getField(int id) {
        int value;
        switch (id) {
            case 0: value = (v1 > 0) ? 1 : 0; break;
            case 1: value = (v2 > 0) ? 1 : 0; break;
            case 2: value = (v3 > 0) ? 1 : 0; break;
            default: value = 0; break;
        }
        System.out.println("[TileHyperStorage] getField: ID: " + id + ", Value: " + value);
        return value;
    }

    @Override
    public void setField(int id, int value) {
        System.out.println("[TileHyperStorage] setField: ID: " + id + ", Value: " + value + " (not used)");
    }

    @Override
    public int getFieldCount() {
        System.out.println("[TileHyperStorage] getFieldCount: 3");
        return 3;
    }

    // --- EnergyNet Registration ---
    @Override
    public void onLoad() {
        super.onLoad();
        System.out.println("[TileHyperStorage] onLoad start");
        if (world != null && !world.isRemote) {
            System.out.println("[TileHyperStorage Server] onLoad: Adding to EnergyNet");
            EnergyNet.instance.addTile(this);
            lastSentV1 = -1;
            lastSentV2 = -1;
            lastSentV3 = -1;
            forceSyncToClient();
        }
        logState("onLoad");
        System.out.println("[TileHyperStorage] onLoad end");
    }

    @Override
    public void invalidate() {
        System.out.println("[TileHyperStorage] invalidate start");
        super.invalidate();
        if (world != null && !world.isRemote) {
            System.out.println("[TileHyperStorage Server] invalidate: Removing from EnergyNet");
            EnergyNet.instance.removeTile(this);
        }
        logState("invalidate");
        System.out.println("[TileHyperStorage] invalidate end");
    }

    @Override
    public void onChunkUnload() {
        System.out.println("[TileHyperStorage] onChunkUnload start");
        super.onChunkUnload();
        if (world != null && !world.isRemote) {
            System.out.println("[TileHyperStorage Server] onChunkUnload: Removing from EnergyNet");
            EnergyNet.instance.removeTile(this);
        }
        logState("onChunkUnload");
        System.out.println("[TileHyperStorage] onChunkUnload end");
    }

    // --- Getters ---
    public double getOutput() {
        System.out.println("[TileHyperStorage] getOutput: " + output);
        return output;
    }

    public int getTier() {
        System.out.println("[TileHyperStorage] getTier: " + tier);
        return tier;
    }

    public double getTotalEnergyForDisplay() {
        System.out.println("[TileHyperStorage] getTotalEnergyForDisplay start");
        if (v3 > 0) {
            System.out.println("[TileHyperStorage] getTotalEnergyForDisplay: v3 > 0, returning POSITIVE_INFINITY");
            return Double.POSITIVE_INFINITY;
        }
        if (v2 > 0) {
            try {
                double v2Contribution = (v2 / OVERFLOW_STEP) * MAX_V1;
                if (Double.isInfinite(v2Contribution) || Double.isNaN(v2Contribution)) {
                    System.out.println("[TileHyperStorage] getTotalEnergyForDisplay: v2 contribution infinite/NaN");
                    return Double.POSITIVE_INFINITY;
                }
                if (Double.MAX_VALUE - v2Contribution < v1) {
                    System.out.println("[TileHyperStorage] getTotalEnergyForDisplay: v2 contribution too large");
                    return Double.POSITIVE_INFINITY;
                }
                double total = v1 + v2Contribution;
                System.out.println("[TileHyperStorage] getTotalEnergyForDisplay: Total with v2: " + total);
                return total;
            } catch (ArithmeticException e) {
                System.out.println("[TileHyperStorage] getTotalEnergyForDisplay: ArithmeticException: " + e.getMessage());
                return Double.POSITIVE_INFINITY;
            }
        }
        System.out.println("[TileHyperStorage] getTotalEnergyForDisplay: Returning v1: " + v1);
        return v1;
    }
}