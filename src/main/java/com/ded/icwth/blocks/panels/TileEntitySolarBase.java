package com.ded.icwth.blocks.panels;

import com.ded.icwth.TileEntityBase;
import com.ded.icwth.items.upgrades.UpgradeItems;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public abstract class TileEntitySolarBase extends TileEntityBase implements ITickable, IWrenchable, ILocatable, IMultiEnergySource, IInventory {
    private static final Logger LOGGER = LogManager.getLogger();
    public BasicSource energy;
    private static final Random r = new Random();
    public double packetAmount;
    protected int tier;
    protected int tick;
    public double output;
    protected double capacity;
    protected String localizedName;
    protected NonNullList<ItemStack> inventory = NonNullList.withSize(10, ItemStack.EMPTY); // Фиксированный размер 10 слотов
    private double lastEnergy = -1;
    private double baseOutput;  // Add this to store original output
    private double baseCapacity; // Add this to store original capacity


    public TileEntitySolarBase(double output, double capacity, int tier) {
        this.energy = new BasicSource((TileEntity) this, capacity, tier);
        this.baseOutput = output;    // Store base values
        this.baseCapacity = capacity;
        this.output = output;
        this.capacity = capacity;
        this.tick = r.nextInt(64);
        this.tier = tier;
        this.localizedName = "tile.default_solar.name";
        this.inventory = NonNullList.withSize(10, ItemStack.EMPTY); // Всегда 10 слотов (5 зарядных + 5 апгрейдов)
    }

    public TileEntitySolarBase() {
        // Убедимся, что инвентарь всегда инициализирован с правильным размером
        this.inventory = NonNullList.withSize(10, ItemStack.EMPTY);
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

    protected void createEnergy() {
        if (this.canGenerate()) {
            this.energy.addEnergy(this.output);
        }
    }

    protected boolean canGenerate() {
        return world.canSeeSky(pos.up()) && world.isDaytime() && !world.isRaining() && !world.isThundering();
    }

    protected void chargeItems() {
        try {
            // Проверка на null и правильный размер
            if (inventory == null) {
                LOGGER.error("TileEntitySolarBase.chargeItems: inventory is null");
                inventory = NonNullList.withSize(10, ItemStack.EMPTY);
                return;
            }

            if (inventory.size() < 5) {
                LOGGER.warn("TileEntitySolarBase.chargeItems: inventory size is {}, expected at least 5", inventory.size());
                NonNullList<ItemStack> newList = NonNullList.withSize(10, ItemStack.EMPTY);
                for (int i = 0; i < inventory.size(); i++) {
                    newList.set(i, inventory.get(i));
                }
                inventory = newList;
            }

            double totalTransfer = Math.min(output, energy.getEnergyStored());
            int chargeableSlots = 0;

            // Подсчитываем количество слотов с заряжаемыми предметами
            for (int i = 0; i < 5; i++) {
                ItemStack stack = inventory.get(i);
                if (!stack.isEmpty() && stack.getItem() instanceof IElectricItem) {
                    chargeableSlots++;
                }
            }

            if (chargeableSlots > 0) {
                double transferPerSlot = totalTransfer / chargeableSlots;
                for (int i = 0; i < 5; i++) {
                    ItemStack chargeStack = inventory.get(i);
                    if (!chargeStack.isEmpty() && chargeStack.getItem() instanceof IElectricItem) {
                        double charged = ic2.api.item.ElectricItem.manager.charge(chargeStack, transferPerSlot, tier, false, false);
                        energy.useEnergy(charged);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error in TileEntitySolarBase.chargeItems", e);
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
    public int getSizeInventory() {
        // Убедимся, что инвентарь инициализирован
        if (inventory == null) {
            inventory = NonNullList.withSize(10, ItemStack.EMPTY);
        }
        return inventory.size();
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
        this.baseOutput = nbt.getDouble("baseOutput");
        this.capacity = nbt.getDouble("capacity");
        this.baseCapacity = nbt.getDouble("baseCapacity");
        this.tier = nbt.getInteger("tier");
        this.localizedName = nbt.getString("localizedName");
        if (this.energy == null) {
            this.energy = new BasicSource((TileEntity) this, this.capacity, this.tier);
        }
        this.energy.readFromNBT(nbt);

        try {
            // Убедимся, что инвентарь инициализирован с правильным размером
            if (inventory == null || inventory.size() != 10) {
                LOGGER.warn("TileEntitySolarBase.readFromNBT: Reinitializing inventory with size 10");
                inventory = NonNullList.withSize(10, ItemStack.EMPTY);
            }

            // Загружаем предметы из NBT
            ItemStackHelper.loadAllItems(nbt, this.inventory);

            // Проверка размера после загрузки
            if (inventory.size() != 10) {
                LOGGER.warn("TileEntitySolarBase.readFromNBT: Incorrect inventory size after loading: {}", inventory.size());
                NonNullList<ItemStack> newList = NonNullList.withSize(10, ItemStack.EMPTY);
                for (int i = 0; i < Math.min(inventory.size(), 10); i++) {
                    newList.set(i, inventory.get(i));
                }
                inventory = newList;
            }
        } catch (Exception e) {
            LOGGER.error("Error reading inventory from NBT", e);
            inventory = NonNullList.withSize(10, ItemStack.EMPTY);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        if (this.energy != null) {
            this.energy.writeToNBT(nbt);
            nbt.setDouble("capacity", this.capacity);
            nbt.setDouble("baseCapacity", this.baseCapacity);
            nbt.setInteger("tier", this.tier);
            nbt.setString("localizedName", this.localizedName);
        }
        nbt.setDouble("output", this.output);
        nbt.setDouble("baseOutput", this.baseOutput);

        try {
            // Проверка на null и правильный размер
            if (inventory == null) {
                LOGGER.warn("TileEntitySolarBase.writeToNBT: inventory is null, creating new one");
                inventory = NonNullList.withSize(10, ItemStack.EMPTY);
            } else if (inventory.size() != 10) {
                LOGGER.warn("TileEntitySolarBase.writeToNBT: Incorrect inventory size: {}, resizing to 10", inventory.size());
                NonNullList<ItemStack> newList = NonNullList.withSize(10, ItemStack.EMPTY);
                for (int i = 0; i < Math.min(inventory.size(), 10); i++) {
                    newList.set(i, inventory.get(i));
                }
                inventory = newList;
            }

            ItemStackHelper.saveAllItems(nbt, this.inventory);
        } catch (Exception e) {
            LOGGER.error("Error writing inventory to NBT", e);
        }

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

    protected void checkConditions() {
        this.applyUpgrades();  // Add this call
        this.createEnergy();
        this.chargeItems();
    }

    private void applyUpgrades() {
        try {
            // Проверка на null
            if (inventory == null) {
                LOGGER.error("TileEntitySolarBase.applyUpgrades: inventory is null");
                inventory = NonNullList.withSize(10, ItemStack.EMPTY);
                return;
            }

            // Проверка размера списка
            if (inventory.size() < 10) {
                LOGGER.warn("TileEntitySolarBase.applyUpgrades: inventory size is {}, expected 10", inventory.size());
                NonNullList<ItemStack> newList = NonNullList.withSize(10, ItemStack.EMPTY);
                for (int i = 0; i < inventory.size(); i++) {
                    newList.set(i, inventory.get(i));
                }
                inventory = newList;
            }

            // Reset to base values
            this.output = this.baseOutput;
            this.capacity = this.baseCapacity;

            double nightMultiplier = 1.0;
            double dayMultiplier = 1.0;
            double efficiencyMultiplier = 1.0;

            // Check upgrade slots (5-9)
            for (int i = 5; i < 10; i++) {
                // Безопасный доступ к элементам списка
                if (i >= inventory.size()) {
                    LOGGER.warn("TileEntitySolarBase.applyUpgrades: trying to access index {} but inventory size is {}", i, inventory.size());
                    continue;
                }

                ItemStack stack = inventory.get(i);
                if (!stack.isEmpty()) {
                    if (stack.getItem() == UpgradeItems.nightGenerationUpgrade) {
                        nightMultiplier = 2.0;
                    } else if (stack.getItem() == UpgradeItems.dayGenerationUpgrade) {
                        dayMultiplier = 1.5;
                    } else if (stack.getItem() == UpgradeItems.capacityUpgrade) {
                        this.capacity *= 1.5;
                    } else if (stack.getItem() == UpgradeItems.efficiencyUpgrade) {
                        efficiencyMultiplier = 1.25;
                    }
                }
            }

            // Apply multipliers
            if (world.isDaytime()) {
                this.output *= dayMultiplier * efficiencyMultiplier;
            } else {
                this.output *= nightMultiplier * efficiencyMultiplier;
            }

            // Update energy capacity if changed
            if (this.energy != null && this.capacity != this.energy.getCapacity()) {
                this.energy.setCapacity(this.capacity);
            }
        } catch (Exception e) {
            LOGGER.error("Error applying solar panel upgrades", e);
        }
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        if (index < 5) {  // Charging slots
            return stack.getItem() instanceof IElectricItem;
        } else if (index < 10) {  // Upgrade slots
            return stack.getItem() == UpgradeItems.nightGenerationUpgrade ||
                    stack.getItem() == UpgradeItems.dayGenerationUpgrade ||
                    stack.getItem() == UpgradeItems.capacityUpgrade ||
                    stack.getItem() == UpgradeItems.efficiencyUpgrade;
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        // Проверка на null
        if (inventory == null) {
            return true;
        }
        return inventory.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        // Безопасный доступ к элементам списка
        if (inventory == null) {
            LOGGER.error("TileEntitySolarBase.getStackInSlot: inventory is null");
            inventory = NonNullList.withSize(10, ItemStack.EMPTY);
            return ItemStack.EMPTY;
        }

        if (index < 0 || index >= inventory.size()) {
            LOGGER.warn("TileEntitySolarBase.getStackInSlot: index {} out of bounds (size: {})", index, inventory.size());
            return ItemStack.EMPTY;
        }

        return inventory.get(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        // Безопасный доступ к элементам списка
        if (inventory == null) {
            LOGGER.error("TileEntitySolarBase.decrStackSize: inventory is null");
            inventory = NonNullList.withSize(10, ItemStack.EMPTY);
            return ItemStack.EMPTY;
        }

        if (index < 0 || index >= inventory.size()) {
            LOGGER.warn("TileEntitySolarBase.decrStackSize: index {} out of bounds (size: {})", index, inventory.size());
            return ItemStack.EMPTY;
        }

        return ItemStackHelper.getAndSplit(inventory, index, count);
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        // Безопасный доступ к элементам списка
        if (inventory == null) {
            LOGGER.error("TileEntitySolarBase.removeStackFromSlot: inventory is null");
            inventory = NonNullList.withSize(10, ItemStack.EMPTY);
            return ItemStack.EMPTY;
        }

        if (index < 0 || index >= inventory.size()) {
            LOGGER.warn("TileEntitySolarBase.removeStackFromSlot: index {} out of bounds (size: {})", index, inventory.size());
            return ItemStack.EMPTY;
        }

        return ItemStackHelper.getAndRemove(inventory, index);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        // Безопасный доступ к элементам списка
        if (inventory == null) {
            LOGGER.error("TileEntitySolarBase.setInventorySlotContents: inventory is null");
            inventory = NonNullList.withSize(10, ItemStack.EMPTY);
        }

        if (index < 0 || index >= inventory.size()) {
            LOGGER.warn("TileEntitySolarBase.setInventorySlotContents: index {} out of bounds (size: {})", index, inventory.size());
            return;
        }

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
        if (inventory == null) {
            inventory = NonNullList.withSize(10, ItemStack.EMPTY);
        } else {
            inventory.clear();
        }
    }

    @Override
    public abstract String getName();

    @Override
    public boolean hasCustomName() {
        return false;
    }
}
