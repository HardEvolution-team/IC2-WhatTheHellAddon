package com.ded.icwth.blocks.mechanism.distiller;

import ic2.api.energy.EnergyNet;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.tile.IWrenchable;
import ic2.core.ref.FluidName;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class TileElectricDistiller extends TileEntity implements ITickable, IEnergySink, IWrenchable, IFluidHandler {

    // Константы для дистиллятора
    private static final int FLUID_PER_PROCESS = 100; // мБ воды на один процесс
    private static final int PROCESS_TIME = 200; // тики на один процесс
    private static final double ENERGY_PER_TICK = 5.0; // EU/тик
    private static final int TIER = 1; // Уровень энергии (LV)
    private static final int INPUT_TANK_CAPACITY = 10000; // мБ
    private static final int OUTPUT_TANK_CAPACITY = 10000; // мБ

    // Резервуары для жидкостей
    private final FluidTank inputTank;
    private final FluidTank outputTank;

    // Энергия
    private double energy = 0.0;
    private double maxEnergy = 10000.0;

    // Прогресс обработки
    private int progress = 0;
    private int maxProgress = PROCESS_TIME;

    // Флаг активности
    private boolean isActive = false;

    // Флаг для отслеживания регистрации в EnergyNet
    private boolean addedToEnet = false;

    public TileElectricDistiller() {
        // Инициализация резервуаров для жидкостей
        this.inputTank = new FluidTank(INPUT_TANK_CAPACITY) {
            @Override
            public boolean canFill() {
                return true;
            }

            @Override
            public boolean canDrain() {
                return true;
            }

            @Override
            public boolean canFillFluidType(FluidStack fluidStack) {
                return fluidStack != null && fluidStack.getFluid() == FluidRegistry.WATER;
            }
        };

        this.outputTank = new FluidTank(OUTPUT_TANK_CAPACITY) {
            @Override
            public boolean canFill() {
                return true; // Разрешаем заполнение выходного бака
            }

            @Override
            public boolean canDrain() {
                return true;
            }

            @Override
            public boolean canFillFluidType(FluidStack fluidStack) {
                return fluidStack != null && fluidStack.getFluid() == FluidName.distilled_water.getInstance();
            }
        };
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

        boolean wasActive = this.isActive;
        this.isActive = false;

        // Проверяем условия для работы
        if (canProcess()) {
            // Потребляем энергию
            if (energy >= ENERGY_PER_TICK) {
                energy -= ENERGY_PER_TICK;
                this.isActive = true;
                progress++;

                // Если процесс завершен
                if (progress >= maxProgress) {
                    processComplete();
                    progress = 0;
                }
            }
        } else {
            progress = 0;
        }

        // Если состояние активности изменилось, обновляем клиент
        if (wasActive != this.isActive) {
            markDirty();
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
        }
    }

    /**
     * Проверяет, может ли механизм обрабатывать в данный момент.
     *
     * @return true, если механизм может обрабатывать
     */
    private boolean canProcess() {
        // Проверяем, есть ли достаточно входной жидкости
        if (inputTank.getFluidAmount() < FLUID_PER_PROCESS) {
            return false;
        }

        // Проверяем, есть ли место для выходной жидкости
        if (outputTank.getFluidAmount() + FLUID_PER_PROCESS > outputTank.getCapacity()) {
            return false;
        }

        return true;
    }

    /**
     * Вызывается, когда процесс обработки завершен.
     */
    private void processComplete() {
        // Потребляем входную жидкость
        FluidStack drained = inputTank.drain(FLUID_PER_PROCESS, true);
        if (drained != null && drained.amount > 0) {
            // Создаем выходную жидкость
            FluidStack outputFluid = new FluidStack(FluidName.distilled_water.getInstance(), drained.amount);

            // Добавляем выходную жидкость в выходной бак
            int filled = outputTank.fill(outputFluid, true);

            // Логирование для отладки
            System.out.println("Distiller processed: Drained " + drained.amount + " mB of water, filled " + filled + " mB of distilled water");

            // Обновляем клиент
            markDirty();
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
        }
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
    public void onLoad() {
        super.onLoad();
        if (!world.isRemote) {
            EnergyNet.instance.addTile(this);
            this.addedToEnet = true;
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        // Чтение энергии
        this.energy = compound.getDouble("Energy");

        // Чтение прогресса
        this.progress = compound.getInteger("Progress");

        // Чтение состояния активности
        this.isActive = compound.getBoolean("IsActive");

        // Чтение жидкостей
        if (compound.hasKey("InputTank")) {
            NBTTagCompound inputTankNBT = compound.getCompoundTag("InputTank");
            this.inputTank.readFromNBT(inputTankNBT);
        }

        if (compound.hasKey("OutputTank")) {
            NBTTagCompound outputTankNBT = compound.getCompoundTag("OutputTank");
            this.outputTank.readFromNBT(outputTankNBT);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);

        // Запись энергии
        compound.setDouble("Energy", this.energy);

        // Запись прогресса
        compound.setInteger("Progress", this.progress);

        // Запись состояния активности
        compound.setBoolean("IsActive", this.isActive);

        // Запись жидкостей
        NBTTagCompound inputTankNBT = new NBTTagCompound();
        this.inputTank.writeToNBT(inputTankNBT);
        compound.setTag("InputTank", inputTankNBT);

        NBTTagCompound outputTankNBT = new NBTTagCompound();
        this.outputTank.writeToNBT(outputTankNBT);
        compound.setTag("OutputTank", outputTankNBT);

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
        return Math.max(0, maxEnergy - energy);
    }

    @Override
    public int getSinkTier() {
        return TIER;
    }

    @Override
    public double injectEnergy(EnumFacing directionFrom, double amount, double voltage) {
        double energyNeeded = maxEnergy - energy;
        double energyToAdd = Math.min(amount, energyNeeded);
        energy += energyToAdd;
        return amount - energyToAdd; // Возвращаем неиспользованную энергию
    }

    // Методы для IWrenchable

    @Override
    public EnumFacing getFacing(World world, BlockPos pos) {
        return EnumFacing.UP;
    }

    @Override
    public boolean setFacing(World world, BlockPos pos, EnumFacing newDirection, EntityPlayer player) {
        return false;
    }

    @Override
    public boolean wrenchCanRemove(World world, BlockPos pos, EntityPlayer player) {
        return true;
    }

    @Override
    public List<ItemStack> getWrenchDrops(World world, BlockPos pos, IBlockState state, TileEntity te, EntityPlayer player, int fortune) {
        return Collections.emptyList();
    }

    // Методы для IFluidHandler

    @Override
    public IFluidTankProperties[] getTankProperties() {
        return new IFluidTankProperties[] { inputTank.getTankProperties()[0], outputTank.getTankProperties()[0] };
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        if (resource == null) {
            return 0;
        }

        // Если это вода, заполняем входной бак
        if (resource.getFluid() == FluidRegistry.WATER) {
            return inputTank.fill(resource, doFill);
        }

        // Если это дистиллированная вода, заполняем выходной бак (для тестирования)
        if (resource.getFluid() == FluidName.distilled_water.getInstance()) {
            return outputTank.fill(resource, doFill);
        }

        return 0;
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        if (resource == null) {
            return null;
        }

        // Если запрашивается дистиллированная вода, берем из выходного резервуара
        if (resource.getFluid() == FluidName.distilled_water.getInstance()) {
            return outputTank.drain(resource, doDrain);
        }

        // Если запрашивается вода, берем из входного резервуара (для тестирования)
        if (resource.getFluid() == FluidRegistry.WATER) {
            return inputTank.drain(resource, doDrain);
        }

        return null;
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        // Сначала пытаемся слить из выходного резервуара
        FluidStack drained = outputTank.drain(maxDrain, doDrain);
        if (drained != null && drained.amount > 0) {
            return drained;
        }

        // Если выходной резервуар пуст, не сливаем из входного
        return null;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Override
    @Nullable
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(this);
        }
        return super.getCapability(capability, facing);
    }

    // Геттеры для GUI

    public int getProgress() {
        return progress;
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    public double getProgressPercent() {
        return (double) progress / maxProgress;
    }

    public boolean isActive() {
        return isActive;
    }

    public double getEnergyStored() {
        return energy;
    }

    public double getMaxEnergy() {
        return maxEnergy;
    }

    public FluidTank getInputTank() {
        return inputTank;
    }

    public FluidTank getOutputTank() {
        return outputTank;
    }

    public String getName() {
        return "container.electric_distiller";
    }

    // Методы для установки значений (используются в ContainerElectricDistiller)
    public void setProgress(int progress) {
        this.progress = progress;
    }

    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
    }

    public void setEnergyStored(int energyStored) {
        // Этот метод используется только на клиенте для синхронизации
        this.energy = energyStored;
    }

    public void setMaxEnergy(int maxEnergy) {
        // Этот метод используется только на клиенте для синхронизации
        this.maxEnergy = maxEnergy;
    }

    public void setInputFluidAmount(int amount) {
        // Этот метод используется только на клиенте для синхронизации
        if (this.inputTank.getFluid() != null) {
            this.inputTank.getFluid().amount = amount;
        } else if (amount > 0) {
            this.inputTank.setFluid(new FluidStack(FluidRegistry.WATER, amount));
        }
    }

    public void setOutputFluidAmount(int amount) {
        // Этот метод используется только на клиенте для синхронизации
        if (this.outputTank.getFluid() != null) {
            this.outputTank.getFluid().amount = amount;
        } else if (amount > 0) {
            this.outputTank.setFluid(new FluidStack(FluidName.distilled_water.getInstance(), amount));
        }
    }
}

