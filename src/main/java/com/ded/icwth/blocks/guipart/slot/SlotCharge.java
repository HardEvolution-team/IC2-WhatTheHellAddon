package com.ded.icwth.blocks.guipart.slot;
import ic2.api.item.IElectricItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotCharge extends Slot {
    public SlotCharge(IInventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        // Разрешаем только электрические предметы
        return stack.getItem() instanceof IElectricItem;
    }
    @Override
    public int getSlotStackLimit() {
        return 1; // Ограничиваем слот зарядки до 1 предмета
    }
}


