package com.ded.icwth.blocks.moleculartransformer.based.gui;


import com.ded.icwth.blocks.moleculartransformer.based.TileEntityMolecularTransformer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Контейнер для молекулярного сборщика.
 * Управляет слотами и взаимодействием с инвентарем игрока.
 */
public class ContainerMolecularTransformer extends Container {
    // Расстояние между слотами (18 пикселей слот + 2 пикселя промежуток)
    public static final int SLOT_SPACING = 21;

    private final TileEntityMolecularTransformer tileEntity;
    private int lastEnergyUsed = 0;
    private int lastEnergyRequired = 0;
    private int lastActiveState = 0;
    private int lastEnergyInput = 0;

    public ContainerMolecularTransformer(TileEntityMolecularTransformer tileEntity, EntityPlayer player) {
        this.tileEntity = tileEntity;
        InventoryPlayer playerInventory = player.inventory;

        // Добавляем слот для входного предмета (x=20, y=27 из XML)
        this.addSlotToContainer(new Slot(tileEntity, 0, 20, 27) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return tileEntity.isItemValidForSlot(0, stack);
            }
        });

        // Добавляем слот для выходного предмета (x=20, y=68 из XML)
        this.addSlotToContainer(new Slot(tileEntity, 1, 20, 68) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return false; // Выходной слот только для чтения
            }
        });

        // Добавляем слоты инвентаря игрока с координатами из XML (x=18, y=98)
        // Инвентарь игрока (3 ряда по 9 слотов) с заданным расстоянием
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, 18 + j * SLOT_SPACING, 98 + i * 21));
            }
        }

        // Хотбар игрока (1 ряд из 9 слотов) с заданным расстоянием
        for (int i = 0; i < 9; i++) {
            this.addSlotToContainer(new Slot(playerInventory, i, 18 + i * SLOT_SPACING, 98 + 67));
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

            // Принудительно отправляем обновления каждый тик
            listener.sendWindowProperty(this, 0, currentActiveState);
            listener.sendWindowProperty(this, 1, currentEnergyUsed);
            listener.sendWindowProperty(this, 2, currentEnergyRequired);
            listener.sendWindowProperty(this, 3, currentEnergyInput);

            this.lastActiveState = currentActiveState;
            this.lastEnergyUsed = currentEnergyUsed;
            this.lastEnergyRequired = currentEnergyRequired;
            this.lastEnergyInput = currentEnergyInput;
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

            if (index < 2) {
                // Из слотов молекулярного сборщика в инвентарь игрока
                if (!this.mergeItemStack(itemstack1, 2, this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 0, 1, false)) {
                // Из инвентаря игрока во входной слот молекулярного сборщика
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