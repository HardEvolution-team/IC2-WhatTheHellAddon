package com.ded.icwth.blocks.mechanism.distiller.gui;

import com.ded.icwth.blocks.mechanism.distiller.TileElectricDistiller;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerElectricDistiller extends Container {

    private final TileElectricDistiller tileEntity;

    // Последние значения полей для синхронизации
    private int lastProgress = 0;
    private int lastMaxProgress = 0;
    private int lastEnergyStored = 0;
    private int lastMaxEnergy = 0;
    private int lastInputFluid = 0;
    private int lastOutputFluid = 0;

    public ContainerElectricDistiller(TileElectricDistiller tileEntity, EntityPlayer player) {
        this.tileEntity = tileEntity;
        InventoryPlayer playerInventory = player.inventory;

        // Добавляем слоты инвентаря игрока (3 ряда по 9 слотов)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlotToContainer(new Slot(
                        playerInventory,
                        col + row * 9 + 9,
                        8 + col * 18,
                        84 + row * 18
                ));
            }
        }

        // Добавляем слоты хотбара игрока (1 ряд из 9 слотов)
        for (int col = 0; col < 9; col++) {
            this.addSlotToContainer(new Slot(
                    playerInventory,
                    col,
                    8 + col * 18,
                    142
            ));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        // Синхронизируем данные с клиентом
        for (IContainerListener listener : this.listeners) {
            int progress = (int) (tileEntity.getProgress() * 100);
            int maxProgress = (int) (tileEntity.getMaxProgress() * 100);
            int energyStored = (int) tileEntity.getEnergyStored();
            int maxEnergy = (int) tileEntity.getMaxEnergy();
            int inputFluid = tileEntity.getInputTank().getFluidAmount();
            int outputFluid = tileEntity.getOutputTank().getFluidAmount();

            if (progress != lastProgress) {
                listener.sendWindowProperty(this, 0, progress);
                lastProgress = progress;
            }

            if (maxProgress != lastMaxProgress) {
                listener.sendWindowProperty(this, 1, maxProgress);
                lastMaxProgress = maxProgress;
            }

            if (energyStored != lastEnergyStored) {
                listener.sendWindowProperty(this, 2, energyStored);
                lastEnergyStored = energyStored;
            }

            if (maxEnergy != lastMaxEnergy) {
                listener.sendWindowProperty(this, 3, maxEnergy);
                lastMaxEnergy = maxEnergy;
            }

            if (inputFluid != lastInputFluid) {
                listener.sendWindowProperty(this, 4, inputFluid);
                lastInputFluid = inputFluid;
            }

            if (outputFluid != lastOutputFluid) {
                listener.sendWindowProperty(this, 5, outputFluid);
                lastOutputFluid = outputFluid;
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int data) {
        switch (id) {
            case 0:
                tileEntity.setProgress(data);
                break;
            case 1:
                tileEntity.setMaxProgress(data);
                break;
            case 2:
                tileEntity.setEnergyStored(data);
                break;
            case 3:
                tileEntity.setMaxEnergy(data);
                break;
            case 4:
                tileEntity.setInputFluidAmount(data);
                break;
            case 5:
                tileEntity.setOutputFluidAmount(data);
                break;
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        // Дистиллятор не имеет слотов для предметов, поэтому просто возвращаем пустой стек
        return ItemStack.EMPTY;
    }
}

