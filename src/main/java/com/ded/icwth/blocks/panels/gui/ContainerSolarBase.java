package com.ded.icwth.blocks.panels.gui;

import com.ded.icwth.blocks.guipart.slot.SlotCharge;
import com.ded.icwth.blocks.panels.TileEntitySolarBase;
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

public class ContainerSolarBase extends ContainerBase<TileEntitySolarBase> {
    protected TileEntitySolarBase tile;
    protected EntityPlayer player;

    public ContainerSolarBase(InventoryPlayer playerInventory, TileEntitySolarBase tile) {
        super(tile);
        this.tile = tile;
        this.player = playerInventory.player;

        this.addSlotToContainer(new SlotCharge(tile, 0, 56, 17));
        this.addSlotToContainer(new SlotCharge(tile, 1, 56, 53));

        for (int i = 0; i < 4; i++) {
            final EntityEquipmentSlot slotType = EntityEquipmentSlot.values()[i + 2];
            this.addSlotToContainer(new Slot(playerInventory, 39 - i, 8 + i * 18, 84) {
                @Override
                public int getSlotStackLimit() {
                    return 1;
                }

                @Override
                public boolean isItemValid(ItemStack stack) {
                    if (stack.getItem() instanceof ItemArmor) {
                        ItemArmor armor = (ItemArmor) stack.getItem();
                        switch (armor.armorType) {
                            case FEET: return slotType == EntityEquipmentSlot.HEAD;
                            case LEGS: return slotType == EntityEquipmentSlot.CHEST;
                            case CHEST: return slotType == EntityEquipmentSlot.LEGS;
                            case HEAD: return slotType == EntityEquipmentSlot.FEET;
                            default: return false;
                        }
                    }
                    return false;
                }

                @SideOnly(Side.CLIENT)
                @Override
                public String getSlotTexture() {
                    return ItemArmor.EMPTY_SLOT_NAMES[3 - slotType.getIndex()];
                }
            });
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlotToContainer(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 114 + row * 18));
            }
        }

        for (int col = 0; col < 9; col++) {
            this.addSlotToContainer(new Slot(playerInventory, col, 8 + col * 18, 172));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return tile.isUsableByPlayer(player);
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickType, EntityPlayer player) {
        if (clickType != ClickType.PICKUP && clickType != ClickType.QUICK_MOVE) {
            return super.slotClick(slotId, dragType, clickType, player);
        }

        ItemStack heldStack = player.inventory.getItemStack();
        ItemStack result = ItemStack.EMPTY;

        if (slotId < 0 || slotId >= this.inventorySlots.size()) {
            return super.slotClick(slotId, dragType, clickType, player);
        }

        Slot slot = this.inventorySlots.get(slotId);
        ItemStack stackInSlot = slot.getStack();

        if (heldStack.isEmpty() && !stackInSlot.isEmpty()) {
            if (clickType == ClickType.PICKUP) {
                player.inventory.setItemStack(stackInSlot.copy());
                slot.putStack(ItemStack.EMPTY);
                slot.onSlotChanged();
                return stackInSlot;
            } else if (clickType == ClickType.QUICK_MOVE) {
                if (slotId < 6) {
                    ItemStack originalStack = stackInSlot.copy();
                    for (int i = 6; i < this.inventorySlots.size(); i++) {
                        Slot targetSlot = this.inventorySlots.get(i);
                        if (!targetSlot.getHasStack() && targetSlot.isItemValid(stackInSlot)) {
                            targetSlot.putStack(stackInSlot.copy());
                            slot.putStack(ItemStack.EMPTY);
                            slot.onSlotChanged();
                            targetSlot.onSlotChanged();
                            return originalStack;
                        }
                    }
                    return ItemStack.EMPTY;
                }
            }
        }

        if (!heldStack.isEmpty()) {
            if (slotId == 0 && tile.isItemValidForSlot(0, heldStack)) {
                mergeItemToSlot(heldStack, slot);
            } else if (slotId == 1 && tile.isItemValidForSlot(1, heldStack)) {
                mergeItemToSlot(heldStack, slot);
            } else if (slotId >= 2 && slotId < 6 && heldStack.getItem() instanceof ItemArmor && slot.isItemValid(heldStack)) {
                mergeItemToSlot(heldStack, slot);
            } else if (slotId >= 6) {
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