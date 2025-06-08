package com.ded.icwth.blocks.moleculartransformer.based;

import ic2.api.energy.EnergyNet;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
import net.minecraft.client.particle.ParticleManager;
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
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import java.util.Arrays;
import java.util.List;


/**
 * TileEntity для молекулярного трансформера.
 * Обрабатывает логику преобразования предметов, потребление энергии и взаимодействие с GUI.
 */
public class TileEntityMolecularTransformer extends TileEntity implements ITickable, IInventory, IEnergySink {
    protected static final List<AxisAlignedBB> AABBs = Arrays.asList(new AxisAlignedBB(0.25, 0.0, 0.25, 0.75, 1.0, 0.75), new AxisAlignedBB(0.05, 0.0, 0.2, 0.6, 1.0, 0.8));
    // Инвентарь: слот 0 - вход, слот 1 - выход
    private NonNullList<ItemStack> inventory = NonNullList.withSize(2, ItemStack.EMPTY);
    private InvWrapper itemHandler = new InvWrapper(this);

    // Энергия и прогресс
    private double energyUsed = 0.0;
    private double lastEnergyInput = 0.0;
    private double energyInput = 0.0;
    private int lastEnergyRequired = 0;

    // Текущий рецепт и его состояние
    private MolecularTransformerRecipe currentRecipe = null;
    private ItemStack currentInput = ItemStack.EMPTY;
    private ItemStack currentOutput = ItemStack.EMPTY;

    // Состояние активности
    private boolean isActive = false;
    private byte waitTime = 0;
    private static final byte MAX_WAIT_TIME = 40;

    // Флаг для отслеживания регистрации в EnergyNet
    private boolean addedToEnet = false;

    public TileEntityMolecularTransformer() {
        // Конструктор без инициализации BasicSink
    }

    @Override
    public void update() {
        if (world.isRemote) {
            return;
        }

        // Проверяем регистрацию в EnergyNet
        if (!addedToEnet && world != null) {
            EnergyNet.instance.addTile(this);
            this.addedToEnet = true;
        }

        // Сбрасываем рецепт только если нет активного процесса и слоты пусты
        if (this.currentRecipe != null && !this.isActive && this.getStackInSlot(0).isEmpty() && this.getStackInSlot(1).isEmpty()) {
            this.currentRecipe = null;
            this.currentInput = ItemStack.EMPTY;
            this.currentOutput = ItemStack.EMPTY;
            this.energyUsed = 0.0;
            this.lastEnergyInput = 0.0;
            this.lastEnergyRequired = 0;
            this.markDirty();
            this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos),
                    this.world.getBlockState(this.pos), 3);
        }

        boolean nextActive = this.isActive;
        boolean updateInventory = false;

        // Проверка текущего рецепта
        if (this.currentRecipe == null) {
            if (!this.getStackInSlot(0).isEmpty()) {
                nextActive = updateInventory = this.checkRecipe();
            } else {
                nextActive = false;
            }
        } else {
            nextActive = true;
        }

        // Обновление энергии
        if (this.energyInput > 0.0) {
            this.lastEnergyInput = this.energyInput;
        }
        this.energyInput = 0.0;

        // Обработка активного состояния
        if (nextActive && this.currentRecipe != null) {
            if (this.lastEnergyInput <= 0.0) {
                // Если энергия не поступает, увеличиваем счетчик ожидания
                this.waitTime++;
                if (this.waitTime >= MAX_WAIT_TIME) {
                    nextActive = false;
                }
            } else {
                // Сбрасываем счетчик ожидания, если энергия поступает
                this.waitTime = 0;

                // Проверяем, достаточно ли энергии для завершения рецепта
                if (this.energyUsed >= this.currentRecipe.getEnergyRequired()) {
                    // Рецепт завершен, выдаем результат
                    ItemStack output = this.currentOutput.copy();
                    if (this.getStackInSlot(1).isEmpty()) {
                        this.setInventorySlotContents(1, output);
                    } else if (ItemStack.areItemsEqual(this.getStackInSlot(1), output) &&
                            this.getStackInSlot(1).getCount() + output.getCount() <= output.getMaxStackSize()) {
                        this.getStackInSlot(1).grow(output.getCount());
                    } else {
                        // Если нет места для выходного предмета, останавливаем процесс
                        nextActive = false;
                    }

                    if (nextActive) {
                        // Сбрасываем состояние рецепта
                        this.currentRecipe = null;
                        this.currentInput = ItemStack.EMPTY;
                        this.currentOutput = ItemStack.EMPTY;
                        this.energyUsed = 0.0;
                        this.lastEnergyInput = 0.0;
                        this.lastEnergyRequired = 0;
                        updateInventory = true;
                        this.markDirty();
                        this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos),
                                this.world.getBlockState(this.pos), 3);
                    }
                }
            }
        }

        // Обновление активного состояния
        if (this.isActive != nextActive) {
            this.isActive = nextActive;
            this.markDirty();
            if (this.world.getBlockState(this.pos).getBlock() instanceof BlockMolecularTransformer) {
                this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos),
                        this.world.getBlockState(this.pos), 3);
            }
        }

        // Обновление инвентаря при необходимости
        if (updateInventory) {
            this.markDirty();
        }
    }

    @Override
    public boolean canRenderBreaking() {
        return true; // Было true
    }

    protected List<AxisAlignedBB> getAabbs(boolean forCollision) {
        return AABBs;
    }
    /**
     * Проверяет, существует ли рецепт для текущего входного предмета.
     *
     * @return true, если рецепт найден и может быть обработан
     */
    protected boolean checkRecipe() {
        ItemStack input = this.getStackInSlot(0);
        if (input.isEmpty()) {
            return false;
        }

        MolecularTransformerRecipe recipe = MolecularTransformerRecipeManager.getInstance().findRecipe(input);
        if (recipe != null) {
            // Проверяем, можно ли добавить выходной предмет
            ItemStack output = recipe.getOutput();
            if (this.getStackInSlot(1).isEmpty() ||
                    (ItemStack.areItemsEqual(this.getStackInSlot(1), output) &&
                            this.getStackInSlot(1).getCount() + output.getCount() <= output.getMaxStackSize())) {

                this.currentRecipe = recipe;
                this.currentInput = input.copy();
                this.currentOutput = output;
                this.lastEnergyRequired = (int) recipe.getEnergyRequired();

                // Уменьшаем количество предметов в слоте на 1
                ItemStack newStack = input.copy();
                newStack.shrink(1);
                this.setInventorySlotContents(0, newStack.isEmpty() ? ItemStack.EMPTY : newStack);

                return true;
            }
        }
        return false;
    }

    @Override
    public void invalidate() {
        super.invalidate();
        if (!this.world.isRemote && this.addedToEnet) {
            EnergyNet.instance.removeTile(this);
            this.addedToEnet = false;
        }
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        if (!this.world.isRemote && this.addedToEnet) {
            EnergyNet.instance.removeTile(this);
            this.addedToEnet = false;
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        // Чтение инвентаря
        ItemStackHelper.loadAllItems(compound, this.inventory);

        // Чтение энергии
        this.energyUsed = compound.getDouble("EnergyUsed");
        this.lastEnergyInput = compound.getDouble("LastEnergyInput");
        this.lastEnergyRequired = compound.getInteger("LastEnergyRequired");

        // Чтение состояния активности
        this.isActive = compound.getBoolean("IsActive");

        // Восстановление текущего рецепта
        if (compound.hasKey("CurrentInput")) {
            ItemStack input = new ItemStack(compound.getCompoundTag("CurrentInput"));
            if (!input.isEmpty()) {
                MolecularTransformerRecipe recipe = MolecularTransformerRecipeManager.getInstance().findRecipe(input);
                if (recipe != null) {
                    this.currentRecipe = recipe;
                    this.currentInput = input;
                    this.currentOutput = recipe.getOutput();
                    this.lastEnergyRequired = (int) recipe.getEnergyRequired();
                }
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);

        // Запись инвентаря
        ItemStackHelper.saveAllItems(compound, this.inventory);

        // Запись энергии
        compound.setDouble("EnergyUsed", this.energyUsed);
        compound.setDouble("LastEnergyInput", this.lastEnergyInput);
        compound.setInteger("LastEnergyRequired", this.lastEnergyRequired);

        // Запись состояния активности
        compound.setBoolean("IsActive", this.isActive);

        // Сохранение текущего рецепта
        if (this.currentRecipe != null && !this.currentInput.isEmpty()) {
            compound.setTag("CurrentInput", this.currentInput.writeToNBT(new NBTTagCompound()));
        }

        return compound;
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

    // Методы для IEnergySink

    @Override
    public boolean acceptsEnergyFrom(IEnergyEmitter emitter, EnumFacing side) {
        return true; // Принимаем энергию со всех сторон
    }

    @Override
    public double getDemandedEnergy() {
        if (this.currentRecipe == null) {
            return 0.0;
        }
        return this.currentRecipe.getEnergyRequired() - this.energyUsed;
    }

    @Override
    public int getSinkTier() {
        return Integer.MAX_VALUE;
    }

    @Override
    public double injectEnergy(EnumFacing directionFrom, double amount, double voltage) {
        if (this.currentRecipe == null) {
            return amount; // Возвращаем всю энергию, если нет активного рецепта
        }

        double needed = this.currentRecipe.getEnergyRequired() - this.energyUsed;
        double used = Math.min(amount, needed);

        this.energyUsed += used;
        this.energyInput += used;

        // Отправляем обновление клиенту для синхронизации прогресса
        if (!this.world.isRemote) {
            this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos),
                    this.world.getBlockState(this.pos), 3);
        }

        return amount - used; // Возвращаем неиспользованную энергию
    }

    // Методы для IInventory

    @Override
    public int getSizeInventory() {
        return this.inventory.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.inventory) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return this.inventory.get(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        return ItemStackHelper.getAndSplit(this.inventory, index, count);
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        return ItemStackHelper.getAndRemove(this.inventory, index);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        this.inventory.set(index, stack);
        if (stack.getCount() > this.getInventoryStackLimit()) {
            stack.setCount(this.getInventoryStackLimit());
        }
        this.markDirty();
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return this.world.getTileEntity(this.pos) == this &&
                player.getDistanceSq(this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5) <= 64.0;
    }

    @Override
    public void openInventory(EntityPlayer player) {
        // Не требуется реализация
    }

    @Override
    public void closeInventory(EntityPlayer player) {
        // Не требуется реализация
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        if (index == 0) {
            // Входной слот принимает любые предметы
            return true;
        } else if (index == 1) {
            // Выходной слот только для чтения
            return false;
        }
        return false;
    }

    @Override
    public int getField(int id) {
        switch (id) {
            case 0:
                return this.isActive ? 1 : 0;
            case 1:
                return (int) this.energyUsed;
            case 2:
                return this.lastEnergyRequired;
            case 3:
                return (int) this.lastEnergyInput;
            default:
                return 0;
        }
    }

    @Override
    public void setField(int id, int value) {
        switch (id) {
            case 0:
                this.isActive = value != 0;
                break;
            case 1:
                this.energyUsed = value;
                break;
            case 2:
                this.lastEnergyRequired = value;
                break;
            case 3:
                this.lastEnergyInput = value;
                break;
        }
    }

    @Override
    public int getFieldCount() {
        return 4;
    }

    @Override
    public void clear() {
        this.inventory.clear();
    }

    @Override
    public String getName() {
        return "container.molecular_assembler";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this.itemHandler);
        }
        return super.getCapability(capability, facing);
    }

    // Дополнительные методы для GUI

    /**
     * Возвращает состояние активности машины
     */
    public boolean isActive() {
        return this.isActive;
    }

    /**
     * Возвращает прогресс в диапазоне 0.0-1.0 для GUI
     */
    public double getProgress() {
        if (this.currentRecipe == null) {
            return 0.0;
        }
        return this.energyUsed / this.currentRecipe.getEnergyRequired();
    }

    /**
     * Возвращает процент прогресса в виде строки
     */
    public String getProgressPercent() {
        if (this.currentRecipe == null) {
            return "0%";
        }
        int percent = (int) (this.getProgress() * 100);
        return percent + "%";
    }

    /**
     * Возвращает имя входного предмета
     */
    public String getInputName() {
        return this.currentInput.isEmpty() ? "" : this.currentInput.getDisplayName();
    }

    /**
     * Возвращает имя выходного предмета
     */
    public String getOutputName() {
        return this.currentOutput.isEmpty() ? "" : this.currentOutput.getDisplayName();
    }
}
