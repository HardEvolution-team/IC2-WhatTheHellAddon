package com.ded.icwth.blocks.mechanism.distiller.gui;

import com.ded.icwth.Tags;
import com.ded.icwth.blocks.mechanism.distiller.TileElectricDistiller;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiElectricDistiller extends GuiContainer {

    // Оригинальная текстура GUI
    private static final ResourceLocation TEXTURE = new ResourceLocation(Tags.MODID, "textures/gui/electric_distiller.png");

    // Текстура IC2 common.png для всех элементов
    private static final ResourceLocation IC2_COMMON = new ResourceLocation("ic2", "textures/gui/common.png");

    private final TileElectricDistiller tileEntity;

    public GuiElectricDistiller(TileElectricDistiller tileEntity, EntityPlayer player) {
        super(new ContainerElectricDistiller(tileEntity, player));
        this.tileEntity = tileEntity;

        // Стандартные размеры GUI
        this.xSize = 176;
        this.ySize = 166;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(TEXTURE);

        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;

        // Отрисовка фона GUI
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);

        // Отрисовка индикатора прогресса в стиле IC2 (стрелка)
        drawIC2ProgressArrow(x + 79, y + 34, tileEntity.getProgress(), tileEntity.getMaxProgress());

        // Отрисовка индикатора энергии в стиле IC2 (молния) - 16x16
        drawIC2EnergyGauge(x + 8, y + 28, tileEntity.getEnergyStored(), tileEntity.getMaxEnergy());

        // Отрисовка индикаторов жидкостей в стиле IC2
        drawIC2FluidTanks(x, y);
    }

    /**
     * Отрисовывает индикаторы жидкостей в стиле IC2.
     *
     * @param x Базовая X-координата GUI
     * @param y Базовая Y-координата GUI
     */
    private void drawIC2FluidTanks(int x, int y) {
        // Отрисовка входного резервуара
        FluidTank inputTank = tileEntity.getInputTank();
        if (inputTank != null) {
            drawIC2FluidTank(x + 36, y + 17, inputTank, 16, 52);
        }

        // Отрисовка выходного резервуара
        FluidTank outputTank = tileEntity.getOutputTank();
        if (outputTank != null) {
            drawIC2FluidTank(x + 124, y + 17, outputTank, 16, 52);
        }
    }

    /**
     * Отрисовывает индикатор жидкости в стиле IC2.
     *
     * @param x X-координата индикатора
     * @param y Y-координата индикатора
     * @param tank Резервуар с жидкостью
     * @param width Ширина индикатора
     * @param height Высота индикатора
     */
    private void drawIC2FluidTank(int x, int y, FluidTank tank, int width, int height) {
        // Отрисовка жидкости
        if (tank.getFluid() != null && tank.getFluidAmount() > 0) {
            int fluidAmount = tank.getFluidAmount();
            int fluidCapacity = tank.getCapacity();
            int fluidHeight = height * fluidAmount / fluidCapacity;

            if (fluidHeight > 0) {
                int fluidColor = tank.getFluid().getFluid().getColor(tank.getFluid());
                float red = (fluidColor >> 16 & 255) / 255.0F;
                float green = (fluidColor >> 8 & 255) / 255.0F;
                float blue = (fluidColor & 255) / 255.0F;

                GlStateManager.color(red, green, blue, 1.0F);

                // Привязываем текстуру для жидкости
                this.mc.getTextureManager().bindTexture(TEXTURE);

                // Отрисовка жидкости
                this.drawTexturedModalRect(x + 1, y + 1 + height - fluidHeight - 2, 176, 84, width - 2, fluidHeight);

                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            }
        }

        // Привязываем текстуру IC2 для рамки резервуара
        this.mc.getTextureManager().bindTexture(IC2_COMMON);

        // Отрисовка рамки резервуара в стиле IC2 (левая часть текстуры)
        // Используем координаты из левой части текстуры common.png
        this.drawTexturedModalRect(x, y, 0, 60, width, height);
    }

    /**
     * Отрисовывает прогресс-бар в стиле IC2 (стрелка).
     *
     * @param x X-координата прогресс-бара
     * @param y Y-координата прогресс-бара
     * @param progress Текущий прогресс
     * @param maxProgress Максимальный прогресс
     */
    private void drawIC2ProgressArrow(int x, int y, double progress, double maxProgress) {
        // Привязываем текстуру IC2
        this.mc.getTextureManager().bindTexture(IC2_COMMON);

        // Рассчитываем прогресс (0-24 пикселей)
        int progressPixels = maxProgress > 0 ? (int)(progress * 24 / maxProgress) : 0;

        // Отрисовываем фон прогресс-бара (стрелка)
        // Используем координаты стрелки из текстуры common.png
        this.drawTexturedModalRect(x, y, 78, 170, 24, 17);

        // Отрисовываем заполненную часть прогресс-бара
        if (progressPixels > 0) {
            this.drawTexturedModalRect(x, y, 78, 187, progressPixels, 17);
        }
    }

    /**
     * Отрисовывает индикатор энергии в стиле IC2 (молния) - 16x16.
     *
     * @param x X-координата индикатора
     * @param y Y-координата индикатора
     * @param energy Текущая энергия
     * @param maxEnergy Максимальная энергия
     */
    private void drawIC2EnergyGauge(int x, int y, double energy, double maxEnergy) {
        // Привязываем текстуру IC2
        this.mc.getTextureManager().bindTexture(IC2_COMMON);

        // Рассчитываем заполнение (0-16 пикселей для высоты 16x16)
        int energyHeight = maxEnergy > 0 ? (int)(energy * 16 / maxEnergy) : 0;

        // Отрисовываем фон индикатора энергии (разряженная молния) - 16x16
        // Используем координаты молнии из текстуры common.png
        this.drawTexturedModalRect(x, y, 240, 0, 16, 16);

        // Отрисовываем заполненную часть индикатора энергии (заряженная молния)
        if (energyHeight > 0) {
            // Отрисовываем заполненную молнию снизу вверх
            this.drawTexturedModalRect(x, y + 16 - energyHeight, 240, 16 + 16 - energyHeight, 16, energyHeight);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        // Заголовок GUI
        String title = I18n.format(tileEntity.getName());
        this.fontRenderer.drawString(title, (this.xSize - this.fontRenderer.getStringWidth(title)) / 2, 6, 4210752);

        // Дополнительная информация о дистилляторе
        this.fontRenderer.drawString("Water → Distilled Water", 50, 20, 4210752);

        // Инвентарь игрока
        this.fontRenderer.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);

        // Отрисовка всплывающих подсказок
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;

        // Подсказка для индикатора энергии (16x16)
        if (mouseX >= x + 8 && mouseX <= x + 24 && mouseY >= y + 28 && mouseY <= y + 44) {
            List<String> tooltip = new ArrayList<>();
            tooltip.add(String.format("%d / %d EU", (int)tileEntity.getEnergyStored(), (int)tileEntity.getMaxEnergy()));
            this.drawHoveringText(tooltip, mouseX, mouseY);
        }

        // Подсказка для индикатора прогресса
        if (mouseX >= x + 79 && mouseX <= x + 103 && mouseY >= y + 34 && mouseY <= y + 51) {
            List<String> tooltip = new ArrayList<>();
            tooltip.add(String.format("Progress: %d%%", (int)(tileEntity.getProgressPercent() * 100)));
            this.drawHoveringText(tooltip, mouseX, mouseY);
        }

        // Подсказки для резервуаров с жидкостями
        drawFluidTankTooltips(x, y, mouseX, mouseY);
    }

    /**
     * Отрисовывает всплывающие подсказки для резервуаров с жидкостями.
     *
     * @param x Базовая X-координата GUI
     * @param y Базовая Y-координата GUI
     * @param mouseX X-координата курсора
     * @param mouseY Y-координата курсора
     */
    private void drawFluidTankTooltips(int x, int y, int mouseX, int mouseY) {
        // Подсказка для входного резервуара
        if (mouseX >= x + 36 && mouseX <= x + 52 && mouseY >= y + 17 && mouseY <= y + 69) {
            FluidTank tank = tileEntity.getInputTank();
            if (tank != null) {
                List<String> tooltip = new ArrayList<>();
                if (tank.getFluid() != null) {
                    tooltip.add(tank.getFluid().getLocalizedName());
                    tooltip.add(String.format("%d / %d mB", tank.getFluidAmount(), tank.getCapacity()));
                } else {
                    tooltip.add("Empty");
                    tooltip.add(String.format("0 / %d mB", tank.getCapacity()));
                }
                this.drawHoveringText(tooltip, mouseX, mouseY);
            }
        }

        // Подсказка для выходного резервуара
        if (mouseX >= x + 124 && mouseX <= x + 140 && mouseY >= y + 17 && mouseY <= y + 69) {
            FluidTank tank = tileEntity.getOutputTank();
            if (tank != null) {
                List<String> tooltip = new ArrayList<>();
                if (tank.getFluid() != null) {
                    tooltip.add(tank.getFluid().getLocalizedName());
                    tooltip.add(String.format("%d / %d mB", tank.getFluidAmount(), tank.getCapacity()));
                } else {
                    tooltip.add("Empty");
                    tooltip.add(String.format("0 / %d mB", tank.getCapacity()));
                }
                this.drawHoveringText(tooltip, mouseX, mouseY);
            }
        }
    }
}

