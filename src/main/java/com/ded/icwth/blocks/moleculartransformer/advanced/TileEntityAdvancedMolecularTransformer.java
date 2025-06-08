package com.ded.icwth.blocks.moleculartransformer.advanced;

import ic2.api.energy.EnergyNet;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
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
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * TileEntity для улучшенного молекулярного трансформера.
 * Обрабатывает параллельную обработку входных слотов (4x3) и размещение результатов в выходных слотах (4x3).
 */
public class TileEntityAdvancedMolecularTransformer extends TileEntity implements ITickable, IInventory, IEnergySink {

    // Инвентарь: слоты 0-11 - входные (4x3), слоты 12-23 - выходные (4x3)
    private NonNullList<ItemStack> inventory = NonNullList.withSize(24, ItemStack.EMPTY);
    private InvWrapper itemHandler = new InvWrapper(this);

    // Энергия и прогресс
    private double energyUsed = 0.0; // Общая накопленная энергия
    private double lastEnergyInput = 0.0;
    private double energyInput = 0.0;
    private int lastEnergyRequired = 0;

    // Добавляем переменную для хранения общего прогресса в процентах (0-100)
    private int totalProgressPercent = 0;

    // Список активных рецептов для каждого входного слота
    private List<RecipeProcess> activeRecipes = new ArrayList<>();
    private boolean isActive = false;
    private byte waitTime = 0;
    private static final byte MAX_WAIT_TIME = 40;

    // Флаг для отслеживания регистрации в EnergyNet
    private boolean addedToEnet = false;

    // Внутренний класс для отслеживания процесса рецепта
    private static class RecipeProcess {
        AdvancedMolecularTransformerRecipe recipe;
        ItemStack input;
        ItemStack output;
        double energyUsed; // Энергия, накопленная для этого рецепта
        int inputSlotIndex; // Индекс входного слота

        RecipeProcess(AdvancedMolecularTransformerRecipe recipe, ItemStack input, ItemStack output, int inputSlotIndex) {
            this.recipe = recipe;
            this.input = input.copy();
            this.output = output.copy();
            this.energyUsed = 0.0;
            this.inputSlotIndex = inputSlotIndex;
        }
    }

    public TileEntityAdvancedMolecularTransformer() {
        // Конструктор
    }

    @Override
    public void update() {
        if (world.isRemote) {
            return;
        }

        // Регистрация в EnergyNet
        if (!addedToEnet && world != null) {
            EnergyNet.instance.addTile(this);
            this.addedToEnet = true;
        }

        boolean updateInventory = false;
        boolean nextActive = false;

        // Обновление энергии
        if (energyInput > 0.0) {
            lastEnergyInput = energyInput;
            energyUsed += energyInput;
        } else {
            // Если нет энергии в этом тике, сбрасываем lastEnergyInput
            lastEnergyInput = 0.0;
        }
        energyInput = 0.0;

        // Проверка и запуск новых рецептов
        if (!activeRecipes.isEmpty() || checkNewRecipes()) {
            nextActive = true;
        }

        // Обработка активных рецептов
        if (!activeRecipes.isEmpty() && lastEnergyInput > 0.0) {
            // Не используем waitTime, так как не нужна задержка между крафтами
            double energyPerRecipe = lastEnergyInput / activeRecipes.size(); // Распределяем энергию между рецептами

            List<RecipeProcess> completedRecipes = new ArrayList<>();
            for (RecipeProcess process : activeRecipes) {
                process.energyUsed += energyPerRecipe;
                if (process.energyUsed >= process.recipe.getEnergyRequired()) {
                    // Рецепт завершен, пытаемся выдать результат
                    if (tryOutputItem(process.output, process.inputSlotIndex)) {
                        // Предмет уже был уменьшен при старте крафта в checkNewRecipes,
                        // поэтому здесь не нужно уменьшать входной предмет
                        completedRecipes.add(process);
                        updateInventory = true;
                    } else {
                        nextActive = false; // Нет места для вывода, останавливаем
                    }
                }
            }

            // Удаляем завершенные рецепты
            activeRecipes.removeAll(completedRecipes);
        } else if (lastEnergyInput <= 0.0) {
            // Если нет энергии, просто устанавливаем nextActive в false
            // Не используем waitTime и не очищаем activeRecipes
            nextActive = false;
        }

        // Обновляем общий прогресс в процентах для синхронизации с клиентом
        updateTotalProgressPercent();

        // Обновление состояния активности
        if (isActive != nextActive) {
            isActive = nextActive;
            markDirty();
            if (world.getBlockState(pos).getBlock() instanceof BlockAdvancedMolecularTransformer) {
                world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
            }
        }

        if (updateInventory) {
            markDirty();
        }
    }

    /**
     * Обновляет общий прогресс в процентах для всех активных рецептов
     */
    private void updateTotalProgressPercent() {
        if (activeRecipes.isEmpty()) {
            totalProgressPercent = 0;
            return;
        }

        double totalProgress = 0.0;
        for (RecipeProcess process : activeRecipes) {
            totalProgress += process.energyUsed / process.recipe.getEnergyRequired();
        }

        // Рассчитываем средний прогресс и преобразуем в проценты (0-100)
        totalProgressPercent = (int)((totalProgress / activeRecipes.size()) * 100.0);

        // Отправляем обновление клиенту для синхронизации прогресса
        if (!this.world.isRemote) {
            this.world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
        }
    }

    /**
     * Проверяет все входные слоты и запускает новые рецепты.
     * Забирает предмет из слота сразу при старте крафта для предотвращения дюпа.
     *
     * @return true, если найдены новые рецепты
     */
    private boolean checkNewRecipes() {
        boolean foundRecipe = false;
        for (int slot = 0; slot < 12; slot++) { // Проверяем входные слоты (0-11)
            ItemStack input = getStackInSlot(slot);
            if (!input.isEmpty() && !isSlotInUse(slot)) {
                AdvancedMolecularTransformerRecipe recipe = AdvancedMolecularTransformerRecipeManager.getInstance().findRecipe(input);
                if (recipe != null && canOutputItem(recipe.getOutput())) {
                    // Создаем копию входного предмета до его уменьшения
                    ItemStack inputCopy = input.copy();

                    // Уменьшаем входной предмет ПЕРЕД добавлением в активные рецепты
                    // Это предотвращает дюп, когда игрок забирает предмет после начала крафта
                    input.shrink(1);
                    if (input.isEmpty()) {
                        setInventorySlotContents(slot, ItemStack.EMPTY);
                    }

                    // Добавляем процесс в активные рецепты с копией входного предмета
                    activeRecipes.add(new RecipeProcess(recipe, inputCopy, recipe.getOutput(), slot));
                    foundRecipe = true;
                    markDirty(); // Отмечаем, что инвентарь изменился
                }
            }
        }
        return foundRecipe;
    }

    /**
     * Проверяет, используется ли слот в активных рецептах.
     */
    private boolean isSlotInUse(int slotIndex) {
        for (RecipeProcess process : activeRecipes) {
            if (process.inputSlotIndex == slotIndex) {
                return true;
            }
        }
        return false;
    }

    /**
     * Проверяет, можно ли разместить выходной предмет в выходных слотах.
     */
    private boolean canOutputItem(ItemStack output) {
        for (int slot = 12; slot < 24; slot++) { // Выходные слоты (12-23)
            ItemStack stackInSlot = getStackInSlot(slot);
            if (stackInSlot.isEmpty() ||
                    (ItemStack.areItemsEqual(stackInSlot, output) &&
                            stackInSlot.getCount() + output.getCount() <= stackInSlot.getMaxStackSize())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Пытается разместить выходной предмет в выходных слотах.
     */
    private boolean tryOutputItem(ItemStack output, int inputSlotIndex) {
        for (int slot = 12; slot < 24; slot++) { // Выходные слоты (12-23)
            ItemStack stackInSlot = getStackInSlot(slot);
            if (stackInSlot.isEmpty()) {
                setInventorySlotContents(slot, output.copy());
                return true;
            } else if (ItemStack.areItemsEqual(stackInSlot, output) &&
                    stackInSlot.getCount() + output.getCount() <= stackInSlot.getMaxStackSize()) {
                stackInSlot.grow(output.getCount());
                setInventorySlotContents(slot, stackInSlot);
                return true;
            }
        }
        return false;
    }

    /**
     * Возвращает процент прогресса в виде строки
     */
    public String getProgressPercent() {
        if (activeRecipes.isEmpty()) {
            return "0%";
        }
        return String.format("%d%%", totalProgressPercent);
    }

    /**
     * Возвращает прогресс в диапазоне 0.0-1.0 для GUI
     */
    public double getProgress() {
        return totalProgressPercent / 100.0;
    }

    /**
     * Возвращает состояние активности машины
     */
    public boolean isActive() {
        return isActive;
    }

    @Override
    public void invalidate() {
        super.invalidate();
        if (!world.isRemote && addedToEnet) {
            EnergyNet.instance.removeTile(this);
            addedToEnet = false;
        }
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        if (!world.isRemote && addedToEnet) {
            EnergyNet.instance.removeTile(this);
            addedToEnet = false;
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        ItemStackHelper.loadAllItems(compound, inventory);
        energyUsed = compound.getDouble("EnergyUsed");
        lastEnergyInput = compound.getDouble("LastEnergyInput");
        lastEnergyRequired = compound.getInteger("LastEnergyRequired");
        isActive = compound.getBoolean("IsActive");
        totalProgressPercent = compound.getInteger("TotalProgressPercent");

        // Восстановление активных рецептов
        activeRecipes.clear();
        int recipeCount = compound.getInteger("RecipeCount");
        for (int i = 0; i < recipeCount; i++) {
            NBTTagCompound recipeTag = compound.getCompoundTag("Recipe" + i);
            ItemStack input = new ItemStack(recipeTag.getCompoundTag("Input"));
            int slotIndex = recipeTag.getInteger("SlotIndex");
            double recipeEnergyUsed = recipeTag.getDouble("RecipeEnergyUsed");
            AdvancedMolecularTransformerRecipe recipe = AdvancedMolecularTransformerRecipeManager.getInstance().findRecipe(input);
            if (recipe != null) {
                RecipeProcess process = new RecipeProcess(recipe, input, recipe.getOutput(), slotIndex);
                process.energyUsed = recipeEnergyUsed;
                activeRecipes.add(process);
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        ItemStackHelper.saveAllItems(compound, inventory);
        compound.setDouble("EnergyUsed", energyUsed);
        compound.setDouble("LastEnergyInput", lastEnergyInput);
        compound.setInteger("LastEnergyRequired", lastEnergyRequired);
        compound.setBoolean("IsActive", isActive);
        compound.setInteger("TotalProgressPercent", totalProgressPercent);

        // Сохранение активных рецептов
        compound.setInteger("RecipeCount", activeRecipes.size());
        for (int i = 0; i < activeRecipes.size(); i++) {
            RecipeProcess process = activeRecipes.get(i);
            NBTTagCompound recipeTag = new NBTTagCompound();
            recipeTag.setTag("Input", process.input.writeToNBT(new NBTTagCompound()));
            recipeTag.setInteger("SlotIndex", process.inputSlotIndex);
            recipeTag.setDouble("RecipeEnergyUsed", process.energyUsed);
            compound.setTag("Recipe" + i, recipeTag);
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

    // Методы IEnergySink
    @Override
    public boolean acceptsEnergyFrom(IEnergyEmitter emitter, EnumFacing side) {
        return true;
    }

    @Override
    public double getDemandedEnergy() {
        double totalDemanded = 0.0;
        for (RecipeProcess process : activeRecipes) {
            totalDemanded += process.recipe.getEnergyRequired() - process.energyUsed;
        }
        return totalDemanded;
    }

    @Override
    public int getSinkTier() {
        return Integer.MAX_VALUE;
    }

    @Override
    public double injectEnergy(EnumFacing directionFrom, double amount, double voltage) {
        if (activeRecipes.isEmpty()) {
            return amount; // Возвращаем энергию, если нет активных рецептов
        }
        energyInput += amount;
        return 0.0; // Принимаем всю энергию
    }

    // Методы IInventory
    @Override
    public int getSizeInventory() {
        return inventory.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : inventory) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
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
        if (stack.getCount() > getInventoryStackLimit()) {
            stack.setCount(getInventoryStackLimit());
        }
        markDirty();
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return world.getTileEntity(pos) == this &&
                player.getDistanceSq(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64.0;
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
        if (index < 12) { // Входные слоты (0-11)
            return true;
        }
        return false; // Выходные слоты (12-23) только для чтения
    }

    @Override
    public int getField(int id) {
        switch (id) {
            case 0: return isActive ? 1 : 0;
            case 1: return (int) energyUsed;
            case 2: return lastEnergyRequired;
            case 3: return (int) lastEnergyInput;
            case 4: return totalProgressPercent; // Добавляем новое поле для синхронизации прогресса
            default: return 0;
        }
    }

    @Override
    public void setField(int id, int value) {
        switch (id) {
            case 0: isActive = value != 0; break;
            case 1: energyUsed = value; break;
            case 2: lastEnergyRequired = value; break;
            case 3: lastEnergyInput = value; break;
            case 4: totalProgressPercent = value; break;
        }
    }

    @Override
    public int getFieldCount() {
        return 5; // Увеличиваем количество полей до 5
    }

    @Override
    public void clear() {
        this.inventory.clear();
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }
    @Override
    public boolean canRenderBreaking() {
        return true; // Было true
    }
    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(itemHandler);
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public String getName() {
        return "container.advanced_molecular_transformer";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }
}
