package com.ded.icwth.blocks.hyperstorage.gui;

import com.ded.icwth.blocks.guipart.slot.SlotCharge;
import com.ded.icwth.blocks.guipart.slot.SlotDischarge;
import com.ded.icwth.blocks.hyperstorage.TileHyperStorage;
import ic2.core.ContainerBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerHyperStorage extends ContainerBase<TileHyperStorage> {
    protected TileHyperStorage tile;
    protected EntityPlayer player;

    public ContainerHyperStorage(InventoryPlayer playerInventory, TileHyperStorage tile) {
        super(tile);
        this.tile = tile;
        this.player = playerInventory.player;

        // Слоты для зарядки и разрядки (сильно сдвинуты вправо)
        this.addSlotToContainer(new SlotCharge(tile, 0, 56, 17)); // Зарядка
        this.addSlotToContainer(new SlotDischarge(tile, 1, 56, 53)); // Разрядка

        // Слоты для брони (как в AFSU)
// Слоты для брони (корректное сопоставление)
        for (int i = 0; i < 4; i++) {
            final EntityEquipmentSlot slot = EntityEquipmentSlot.values()[i + 2]; // HEAD, CHEST, LEGS, FEET
            this.addSlotToContainer(new Slot(playerInventory, 39 - i, 8 + i * 18, 84) {
                @Override
                public int getSlotStackLimit() {
                    return 1;
                }

                @Override
                public boolean isItemValid(ItemStack stack) {
                    if (stack.getItem() instanceof ItemArmor) {
                        ItemArmor armor = (ItemArmor) stack.getItem();
                        // Сравниваем armorType с правильным EntityEquipmentSlot
                        switch (armor.armorType) {
                            case FEET: // HEAD
                                return slot == EntityEquipmentSlot.HEAD;
                            case LEGS: // CHEST
                                return slot == EntityEquipmentSlot.CHEST;
                            case CHEST: // LEGS
                                return slot == EntityEquipmentSlot.LEGS;
                            case HEAD: // FEET
                                return slot == EntityEquipmentSlot.FEET;
                            default:
                                return false;
                        }
                    }
                    return false;
                }
                @SideOnly(Side.CLIENT)
                @Override
                public String getSlotTexture() {
                    return ItemArmor.EMPTY_SLOT_NAMES[3 - slot.getIndex()]; // HEAD(3) -> 0, FEET(0) -> 3
                }
            });
        }
// Инвентарь игрока (опущен на 1 пиксель)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlotToContainer(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 114 + row * 18)); // Основной инвентарь
            }
        }

// Хотбар (опущен на 1 пиксель)
        for (int col = 0; col < 9; col++) {
            this.addSlotToContainer(new Slot(playerInventory, col, 8 + col * 18, 172)); // Хотбар
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return tile.isUsableByPlayer(player);
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickType, EntityPlayer player) {
        // Если клик нестандартный (например, выброс предмета с Q), используем стандартную обработку
        if (clickType != ClickType.PICKUP && clickType != ClickType.QUICK_MOVE) {
            return super.slotClick(slotId, dragType, clickType, player);
        }

        ItemStack heldStack = player.inventory.getItemStack(); // Предмет в руке игрока
        ItemStack result = ItemStack.EMPTY;

        // Если слот недоступен или вне диапазона, возвращаем стандартную обработку
        if (slotId < 0 || slotId >= this.inventorySlots.size()) {
            return super.slotClick(slotId, dragType, clickType, player);
        }

        Slot slot = this.inventorySlots.get(slotId);
        ItemStack stackInSlot = slot.getStack();

        // Клик по слоту с пустой рукой (извлечение предмета)
        if (heldStack.isEmpty() && !stackInSlot.isEmpty()) {
            if (clickType == ClickType.PICKUP) {
                // Обычный клик: берем весь стак
                player.inventory.setItemStack(stackInSlot.copy());
                slot.putStack(ItemStack.EMPTY);
                slot.onSlotChanged();
                return stackInSlot;
            } else if (clickType == ClickType.QUICK_MOVE) {
                // Shift-клик: перемещаем в инвентарь
                if (slotId < 6) {
                    // Перемещение из слотов зарядки/разрядки/брони в инвентарь
                    ItemStack originalStack = stackInSlot.copy(); // Создаем копию стака
                    for (int i = 6; i < this.inventorySlots.size(); i++) {
                        Slot targetSlot = this.inventorySlots.get(i);
                        if (!targetSlot.getHasStack() && targetSlot.isItemValid(stackInSlot)) {
                            targetSlot.putStack(stackInSlot.copy());
                            slot.putStack(ItemStack.EMPTY);
                            slot.onSlotChanged();
                            targetSlot.onSlotChanged();
                            return originalStack; // Возвращаем копию перемещенного стака
                        }
                    }
                    return ItemStack.EMPTY; // Если не удалось переместить, возвращаем пустой стак
                }
            }
        }

        // Клик по слоту с предметом в руке (вставка предмета)
        if (!heldStack.isEmpty()) {
            if (slotId == 0 && tile.isItemValidForSlot(0, heldStack)) {
                mergeItemToSlot(heldStack, slot);
            } else if (slotId == 1 && tile.isItemValidForSlot(1, heldStack)) {
                mergeItemToSlot(heldStack, slot);
            } else if (slotId >= 2 && slotId < 6 && heldStack.getItem() instanceof ItemArmor && slot.isItemValid(heldStack)) {
                mergeItemToSlot(heldStack, slot);
            } else if (slotId >= 6) {
                // Если клик по инвентарю, используем стандартное поведение
                return super.slotClick(slotId, dragType, clickType, player);
            }
        }

        return result.isEmpty() ? heldStack : result;
    }

    private void mergeItemToSlot(ItemStack stack, Slot slot) {
        if (!slot.getHasStack()) {
            slot.putStack(stack.copy());
            stack.setCount(0);
        } else {
            ItemStack slotStack = slot.getStack();
            if (ItemStack.areItemsEqual(stack, slotStack) && ItemStack.areItemStackTagsEqual(stack, slotStack)) {
                int space = slotStack.getMaxStackSize() - slotStack.getCount();
                if (space > 0) {
                    int toAdd = Math.min(space, stack.getCount());
                    slotStack.grow(toAdd);
                    stack.shrink(toAdd);
                }
            }
        }
        slot.onSlotChanged();
    }

}