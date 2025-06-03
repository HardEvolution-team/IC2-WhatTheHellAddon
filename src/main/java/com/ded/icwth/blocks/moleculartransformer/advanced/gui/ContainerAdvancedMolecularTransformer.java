package com.ded.icwth.blocks.moleculartransformer.advanced.gui;

import com.ded.icwth.blocks.moleculartransformer.advanced.TileEntityAdvancedMolecularTransformer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Контейнер для улучшенного молекулярного сборщика.
 * Управляет слотами и взаимодействием с инвентарем игрока.
 */
public class ContainerAdvancedMolecularTransformer extends Container {
    // Расстояние между слотами (18 пикселей слот + 2 пикселя промежуток)
    public static final int SLOT_SPACING = 21;

    private final TileEntityAdvancedMolecularTransformer tileEntity;
    private int lastEnergyUsed = 0;
    private int lastEnergyRequired = 0;
    private int lastActiveState = 0;
    private int lastEnergyInput = 0;
    private int lastProgressPercent = 0; // Добавляем переменную для отслеживания изменений прогресса

    public ContainerAdvancedMolecularTransformer(TileEntityAdvancedMolecularTransformer tileEntity, EntityPlayer player) {
        this.tileEntity = tileEntity;
        InventoryPlayer playerInventory = player.inventory;

        // Константы для инвентаря игрока
        final int PLAYER_INV_X = 18;  // Начало инвентаря игрока по X
        final int PLAYER_INV_Y = 98; // Начало основного инвентаря по Y
        final int HOTBAR_Y = 165;    // Y координата хотбара

        // Константы для расположения слотов
        final int FIRST_SLOT_X = 17; // X координата первого слота (левый верхний)
        final int FIRST_SLOT_Y = 26; // Y координата первого слота (левый верхний)
        final int SLOT_SIZE = 16;    // Размер слота
        final int SLOT_SPACING = 4;  // Расстояние между слотами
        final int SLOT_SPACING_INVENTORY = 5;  // Расстояние между слотами


        // Добавляем матрицу 4x3 входных слотов
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 4; col++) {
                final int slotIndex = col + row * 4;
                this.addSlotToContainer(new Slot(tileEntity, slotIndex,
                        FIRST_SLOT_X + col * (SLOT_SIZE + SLOT_SPACING),
                        FIRST_SLOT_Y + row * (SLOT_SIZE + SLOT_SPACING)) {
                    @Override
                    public boolean isItemValid(ItemStack stack) {
                        return tileEntity.isItemValidForSlot(slotIndex, stack);
                    }
                });
            }
        }

        // Добавляем матрицу 4x3 выходных слотов (смещение 80 пикселей вправо от первого слота)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 4; col++) {
                final int slotIndex = 12 + col + row * 4; // 12 - смещение для выходных слотов
                this.addSlotToContainer(new Slot(tileEntity, slotIndex,
                        FIRST_SLOT_X + 110 + col * (SLOT_SIZE + SLOT_SPACING),
                        FIRST_SLOT_Y + row * (SLOT_SIZE + SLOT_SPACING)) {
                    @Override
                    public boolean isItemValid(ItemStack stack) {
                        return false; // Выходные слоты только для чтения
                    }
                });
            }
        }

        // Инвентарь игрока (3 ряда по 9 слотов) - стандартное расположение
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlotToContainer(new Slot(playerInventory, col + row * 9 + 9,
                        PLAYER_INV_X + col * (SLOT_SIZE + SLOT_SPACING_INVENTORY),
                        PLAYER_INV_Y + row * (SLOT_SIZE + SLOT_SPACING_INVENTORY)));
            }
        }

        // Хотбар игрока (1 ряд из 9 слотов)
        for (int col = 0; col < 9; col++) {
            this.addSlotToContainer(new Slot(playerInventory, col,
                    PLAYER_INV_X + col * (SLOT_SIZE + SLOT_SPACING_INVENTORY),
                    HOTBAR_Y));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return this.tileEntity.isUsableByPlayer(playerIn);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        for (IContainerListener listener : this.listeners) {
            int currentEnergyUsed = (int) this.tileEntity.getField(1);
            int currentEnergyRequired = this.tileEntity.getField(2);
            int currentActiveState = this.tileEntity.getField(0);
            int currentEnergyInput = (int) this.tileEntity.getField(3);
            int currentProgressPercent = this.tileEntity.getField(4); // Получаем текущий прогресс в процентах

            // Принудительно отправляем обновления каждый тик
            listener.sendWindowProperty(this, 0, currentActiveState);
            listener.sendWindowProperty(this, 1, currentEnergyUsed);
            listener.sendWindowProperty(this, 2, currentEnergyRequired);
            listener.sendWindowProperty(this, 3, currentEnergyInput);
            listener.sendWindowProperty(this, 4, currentProgressPercent); // Отправляем прогресс клиенту

            this.lastActiveState = currentActiveState;
            this.lastEnergyUsed = currentEnergyUsed;
            this.lastEnergyRequired = currentEnergyRequired;
            this.lastEnergyInput = currentEnergyInput;
            this.lastProgressPercent = currentProgressPercent; // Обновляем последнее значение прогресса
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int data) {
        this.tileEntity.setField(id, data);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            // Если клик по слотам устройства (0-23)
            if (index < 24) {
                // Пытаемся переместить в инвентарь игрока
                if (!this.mergeItemStack(itemstack1, 24, this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            }
            // Если клик по инвентарю игрока, пытаемся переместить во входные слоты (0-11)
            else if (!this.mergeItemStack(itemstack1, 0, 12, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }

        return itemstack;
    }
}
