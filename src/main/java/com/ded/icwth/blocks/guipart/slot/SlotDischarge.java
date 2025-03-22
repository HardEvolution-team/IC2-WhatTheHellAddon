package com.ded.icwth.blocks.guipart.slot;

import ic2.api.item.IElectricItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotDischarge extends Slot {
    public SlotDischarge(IInventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        // Разрешаем только электрические предметы
        return stack.getItem() instanceof IElectricItem;
    }
}