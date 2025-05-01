package com.ded.icwth.blocks.panels.gui;

import com.ded.icwth.Tags;
import com.ded.icwth.blocks.panels.TileEntitySolarBase;
import ic2.core.GuiIC2;
import ic2.core.init.Localization;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

@SideOnly(Side.CLIENT)
public class GuiSolarBase extends GuiIC2<ContainerSolarBase> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("icwth", "textures/gui/gui_solarpanel.png");
    private static final Logger LOGGER = LogManager.getLogger(Tags.MODID);
    private final TileEntitySolarBase tile;

    private static final int PROGRESS_BAR_X = 84;
    private static final int PROGRESS_BAR_Y = 36;
    private static final int PROGRESS_BAR_WIDTH = 25;
    private static final int PROGRESS_BAR_HEIGHT = 14;

    public GuiSolarBase(ContainerSolarBase container, TileEntitySolarBase tile) {
        super(container, 196); // xSize = 196
        this.tile = tile;
        this.ySize = 196; // Увеличиваем высоту GUI до 196 пикселей
        LOGGER.info("GuiSolarBase initialized with tile: " + tile.getClass().getSimpleName());
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        LOGGER.debug("Binding texture: " + TEXTURE.toString());
        this.mc.getTextureManager().bindTexture(TEXTURE);
        LOGGER.debug("Drawing texture at " + guiLeft + ", " + guiTop + " with size " + xSize + "x" + ySize);
        this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

        double maxEnergy = tile.getMaxStorage();
        double energyStored = tile.getEnergyStored();
        LOGGER.debug("Max Energy: " + maxEnergy + ", Stored: " + energyStored);

        if (maxEnergy > 0) {
            int energyBarWidth = calculateEnergyBarWidth(energyStored, maxEnergy);
            drawEnergyBar(energyBarWidth);
        }
    }

    private int calculateEnergyBarWidth(double energyStored, double maxEnergy) {
        double energyRatio = energyStored / maxEnergy;
        return (int) (PROGRESS_BAR_WIDTH * energyRatio);
    }
    private void drawEnergyBar(int width) {
        // Используем те же координаты, что в GuiMFSUBase, включая ошибочную X-координату 335
        this.drawTexturedModalRect(
                335, guiTop + PROGRESS_BAR_Y - 2,
                176, 14,
                width, PROGRESS_BAR_HEIGHT
        );
    }

    @Override
    protected void drawForegroundLayer(int mouseX, int mouseY) {
        LOGGER.debug("Drawing foreground layer");
        super.drawForegroundLayer(mouseX, mouseY);

        drawDeviceName();
        drawArmourLabel();

        double maxEnergy = tile.getMaxStorage();
        double energyStored = tile.getEnergyStored();

        if (maxEnergy > 0 && energyStored >= 0) {
            drawPowerLevelLabel();
            drawEnergyValues(energyStored, maxEnergy);
            drawOutputValue(tile.getOutput());
        } else {
            drawNoEnergyData();
        }
    }

    private void drawDeviceName() {
        // Используем имя из tile, которое содержит ключ локализации
        String name = I18n.format(tile.getName());
        int nameX = (xSize - fontRenderer.getStringWidth(name)) / 2;
        fontRenderer.drawString(name, nameX, 6, 4210752);
    }

    private void drawArmourLabel() {
        fontRenderer.drawString(Localization.translate("ic2.EUStorage.gui.info.armor"), 8, ySize - 123, 4210752);
    }

    private void drawPowerLevelLabel() {
        String text = Localization.translate("ic2.EUStorage.gui.info.level");
        int textX = 80;
        fontRenderer.drawString(text, textX, PROGRESS_BAR_Y - 11, 4210752);
    }

    private void drawEnergyValues(double stored, double max) {
        String storedText = (stored >= 1e10) ? String.format("%.2e", stored) : String.format("%.0f", stored);
        String maxText = (max >= 1e10) ? String.format("/%.2e", max) : String.format("/%.0f", max);

        int textX = PROGRESS_BAR_X + PROGRESS_BAR_WIDTH + 5;
        fontRenderer.drawString(storedText, textX, 34, 4210752);
        fontRenderer.drawString(maxText, textX - 2, 44, 4210752);
    }

    private void drawOutputValue(double offeredEnergy) {
        String text = (offeredEnergy >= 1e10) ? String.format("Out: %.2e EU/t", offeredEnergy) : String.format("Out: %.1f EU/t", offeredEnergy);
        int textX = 85;
        fontRenderer.drawString(text, textX, 88, 4210752);
    }

    private void drawNoEnergyData() {
        fontRenderer.drawString("No Energy Data", PROGRESS_BAR_X + PROGRESS_BAR_WIDTH + 5, PROGRESS_BAR_Y + 2, 4210752);
    }

    @Override
    protected void renderHoveredToolTip(int mouseX, int mouseY) {
        int relativeMouseX = mouseX - guiLeft;
        int relativeMouseY = mouseY - guiTop;

        if (isMouseOverProgressBar(relativeMouseX, relativeMouseY)) {
            double maxEnergy = tile.getMaxStorage();
            double energyStored = tile.getEnergyStored();

            if (maxEnergy > 0 && energyStored >= 0) {
                String energyText = formatEnergyText(energyStored, maxEnergy);
                String outputText = formatOutputText(tile.getOutput());
                String fillText = formatFillText(energyStored, maxEnergy);

                drawHoveringText(Arrays.asList(energyText, outputText, fillText), mouseX, mouseY, fontRenderer);
            }
        } else {
            super.renderHoveredToolTip(mouseX, mouseY);
        }
    }

    private boolean isMouseOverProgressBar(int relativeX, int relativeY) {
        return isPointInRegion(PROGRESS_BAR_X, PROGRESS_BAR_Y, PROGRESS_BAR_WIDTH, PROGRESS_BAR_HEIGHT, relativeX, relativeY);
    }

    private String formatEnergyText(double stored, double max) {
        String storedStr = (stored >= 1e10) ? String.format("%.2e", stored) : String.format("%,d", (int)stored);
        String maxStr = (max >= 1e10) ? String.format("%.2e", max) : String.format("%,d", (int)max);
        return String.format("Energy: %s / %s EU", storedStr, maxStr);
    }

    private String formatOutputText(double offeredEnergy) {
        if (offeredEnergy >= 1e10) {
            return String.format("Max Output: %.2e EU/t", offeredEnergy);
        } else {
            return String.format("Max Output: %.1f EU/t", offeredEnergy);
        }
    }

    private String formatFillText(double stored, double max) {
        double fillPercent = (max > 0) ? (stored / max * 100) : 0;
        return String.format("Fill Level: %.1f%%", fillPercent);
    }

    @Override
    protected ResourceLocation getTexture() {
        return TEXTURE;
    }
}