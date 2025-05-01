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

        // Charging slots (0-4)
        this.addSlotToContainer(new SlotCharge(tile, 0, 47, 53));  // Slot 0
        this.addSlotToContainer(new SlotCharge(tile, 1, 65, 53));  // Slot 1
        this.addSlotToContainer(new SlotCharge(tile, 2, 83, 53));  // Slot 2
        this.addSlotToContainer(new SlotCharge(tile, 3, 101, 53)); // Slot 3
        this.addSlotToContainer(new SlotCharge(tile, 4, 119, 53)); // Slot 4

        // Upgrade slots (5-9)
        for (int i = 0; i < 5; i++) {
            this.addSlotToContainer(new Slot(tile, 5 + i, 47 + i * 18, 75) {
                @Override
                public int getSlotStackLimit() {
                    return 1; // Limit upgrade slots to 1 item
                }
            });
        }

        // Armor slots (10-13)
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

        // Main player inventory (14-40)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlotToContainer(new Slot(playerInventory, col + row * 9 + 9,
                        8 + col * 18, 114 + row * 18));
            }
        }

        // Player hotbar (41-49)
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

        // Handle shift-click (QUICK_MOVE) since we can't override transferStackInSlot
        if (clickType == ClickType.QUICK_MOVE) {
            if (!stackInSlot.isEmpty()) {
                if (slotId < 10) { // Charging (0-4) or Upgrade slots (5-9)
                    if (mergeItemToPlayerInventory(stackInSlot)) {
                        slot.putStack(ItemStack.EMPTY);
                        slot.onSlotChanged();
                        return stackInSlot;
                    }
                } else if (slotId >= 14) { // Player inventory to tile
                    if (tryTransferToTile(stackInSlot)) {
                        slot.putStack(stackInSlot.isEmpty() ? ItemStack.EMPTY : stackInSlot);
                        slot.onSlotChanged();
                        return stackInSlot;
                    }
                }
            }
            return ItemStack.EMPTY;
        }

        // Handle regular click (PICKUP)
        if (heldStack.isEmpty() && !stackInSlot.isEmpty()) {
            player.inventory.setItemStack(stackInSlot.copy());
            slot.putStack(ItemStack.EMPTY);
            slot.onSlotChanged();
            return stackInSlot;
        }

        // If holding an item
        if (!heldStack.isEmpty()) {
            if (slotId >= 0 && slotId < 5 && tile.isItemValidForSlot(slotId, heldStack)) { // Charging slots
                mergeItemToSlot(heldStack, slot);
            } else if (slotId >= 5 && slotId < 10 && tile.isItemValidForSlot(slotId, heldStack)) { // Upgrade slots
                mergeItemToSlot(heldStack, slot);
            } else if (slotId >= 10 && slotId < 14 && heldStack.getItem() instanceof ItemArmor && slot.isItemValid(heldStack)) { // Armor slots
                mergeItemToSlot(heldStack, slot);
            } else if (slotId >= 14) { // Player inventory
                return super.slotClick(slotId, dragType, clickType, player);
            }
        }

        return result.isEmpty() ? heldStack : result;
    }

    private boolean mergeItemToPlayerInventory(ItemStack stack) {
        for (int i = 14; i < this.inventorySlots.size(); i++) {
            Slot targetSlot = this.inventorySlots.get(i);
            if (!targetSlot.getHasStack()) {
                targetSlot.putStack(stack.copy());
                targetSlot.onSlotChanged();
                return true;
            } else {
                ItemStack targetStack = targetSlot.getStack();
                if (ItemStack.areItemsEqual(stack, targetStack) &&
                        ItemStack.areItemStackTagsEqual(stack, targetStack)) {
                    int space = targetStack.getMaxStackSize() - targetStack.getCount();
                    if (space >= stack.getCount()) {
                        targetStack.grow(stack.getCount());
                        targetSlot.onSlotChanged();
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean tryTransferToTile(ItemStack stack) {
        // Try charging slots first
        for (int i = 0; i < 5; i++) {
            if (tile.isItemValidForSlot(i, stack)) {
                Slot slot = this.inventorySlots.get(i);
                if (!slot.getHasStack()) {
                    slot.putStack(stack.copy());
                    stack.setCount(0);
                    slot.onSlotChanged();
                    return true;
                }
            }
        }

        // Then upgrade slots
        for (int i = 5; i < 10; i++) {
            if (tile.isItemValidForSlot(i, stack)) {
                Slot slot = this.inventorySlots.get(i);
                if (!slot.getHasStack()) {
                    ItemStack singleStack = stack.copy();
                    singleStack.setCount(1);
                    slot.putStack(singleStack);
                    stack.shrink(1);
                    slot.onSlotChanged();
                    return true;
                }
            }
        }
        return false;
    }

    private void mergeItemToSlot(ItemStack stack, Slot slot) {
        if (!slot.getHasStack()) {
            ItemStack toPlace = stack.copy();
            toPlace.setCount(1); // Limit to 1 item for charging, upgrade, and armor slots
            slot.putStack(toPlace);
            stack.shrink(1);
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

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        // Sync all tile slots (0-9)
        for (int i = 0; i < 10; i++) {
            ItemStack stack = tile.getStackInSlot(i);
            if (!ItemStack.areItemStacksEqual(((Slot) inventorySlots.get(i)).getStack(), stack)) {
                ((Slot) inventorySlots.get(i)).putStack(stack);
            }
        }
    }
}