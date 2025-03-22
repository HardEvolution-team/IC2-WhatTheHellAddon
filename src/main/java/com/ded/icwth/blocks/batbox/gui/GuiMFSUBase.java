package com.ded.icwth.blocks.batbox.gui;

import com.ded.icwth.blocks.batbox.TileMFSUBase;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.core.GuiIC2;
import ic2.core.init.Localization;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Arrays;

@SideOnly(Side.CLIENT)
public class GuiMFSUBase extends GuiIC2<ContainerMFSUBase> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("ic2", "textures/gui/GUIElectricBlock.png");
    protected TileMFSUBase tile;

    private static final int PROGRESS_BAR_X = 84;
    private static final int PROGRESS_BAR_Y = 36;
    private static final int PROGRESS_BAR_WIDTH = 25;
    private static final int PROGRESS_BAR_HEIGHT = 14;

    public GuiMFSUBase(ContainerMFSUBase container, TileMFSUBase tile) {
        super(container, 196);
        this.tile = tile;
        System.out.println("GuiMFSUBase initialized with tile: " + tile);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(TEXTURE);
        this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

        if (tile instanceof IEnergySink) {
            double maxEnergy = tile.maxStorage;
            double energyStored = tile.energy; // Используем tile.energy напрямую
            System.out.println("Max Energy: " + maxEnergy + ", Stored: " + energyStored);

            if (maxEnergy > 0) {
                int energyBarWidth = calculateEnergyBarWidth(energyStored, maxEnergy);
                drawEnergyBar(energyBarWidth);
            }
        } else {
            System.out.println("Tile is not an instance of IEnergySink");
        }
    }

    private int calculateEnergyBarWidth(double energyStored, double maxEnergy) {
        double energyRatio = energyStored / maxEnergy;
        return (int) (PROGRESS_BAR_WIDTH * energyRatio);
    }

    private void drawEnergyBar(int width) {
        this.drawTexturedModalRect(335, guiTop + PROGRESS_BAR_Y - 2, 176, 14, width, PROGRESS_BAR_HEIGHT);
    }

    @Override
    protected void drawForegroundLayer(int mouseX, int mouseY) {
        System.out.println("Drawing foreground layer");
        super.drawForegroundLayer(mouseX, mouseY);

        drawDeviceName();
        drawArmourLabel();

        if (tile instanceof IEnergySink && tile instanceof IEnergySource) {
            double maxEnergy = tile.maxStorage;
            double energyStored = tile.energy; // Используем tile.energy напрямую

            drawPowerLevelLabel();
            drawEnergyValues(energyStored, maxEnergy);
            drawOutputValue(tile.getOutput());
        } else {
            drawNoEnergyData();
        }
    }

    private void drawDeviceName() {
        String name = I18n.format(getBlockNameKey());
        int nameX = (xSize - fontRenderer.getStringWidth(name)) / 2;
        fontRenderer.drawString(name, nameX, 6, 4210752);
    }

    private String getBlockNameKey() {
        return tile.storageName;
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
        String storedText;
        String maxText;

        if (stored >= 1e10) {
            storedText = String.format("%.2e", stored);
        } else {
            storedText = String.format("%.0f", stored);
        }

        if (max >= 1e10) {
            maxText = String.format("/%.2e", max);
        } else {
            maxText = String.format("/%.0f", max);
        }

        int textX = PROGRESS_BAR_X + PROGRESS_BAR_WIDTH + 5;
        fontRenderer.drawString(storedText, textX, 34, 4210752);
        fontRenderer.drawString(maxText, textX - 2, 44, 4210752);
    }

    private String formatEnergyText(double stored, double max) {
        String storedStr = (stored >= 1e10) ? String.format("%.2e", stored) : String.format("%,d", (int)stored);
        String maxStr = (max >= 1e10) ? String.format("%.2e", max) : String.format("%,d", (int)max);
        return String.format("Energy: %s / %s EU", storedStr, maxStr);
    }


    private void drawOutputValue(double offeredEnergy) {
        String text;
        if (offeredEnergy >= 1e10) {
            text = String.format("Out: %.2e EU/t", offeredEnergy);
        } else {
            text = String.format("Out: %.1f EU/t", offeredEnergy);
        }
        int textX = 85;
        fontRenderer.drawString(text, textX, 60, 4210752);
    }

    private String formatOutputText(double offeredEnergy) {
        if (offeredEnergy >= 1e10) {
            return String.format("Max Output: %.2e EU/t", offeredEnergy);
        } else {
            return String.format("Max Output: %.1f EU/t", offeredEnergy);
        }
    }
    private void drawNoEnergyData() {
        fontRenderer.drawString("No Energy Data", PROGRESS_BAR_X + PROGRESS_BAR_WIDTH + 5, PROGRESS_BAR_Y + 2, 4210752);
    }

    @Override
    protected void renderHoveredToolTip(int mouseX, int mouseY) {
        int relativeMouseX = mouseX - guiLeft;
        int relativeMouseY = mouseY - guiTop;

        if (isMouseOverProgressBar(relativeMouseX, relativeMouseY)) {
            if (tile instanceof IEnergySink && tile instanceof IEnergySource) {
                IEnergySource energySource = (IEnergySource) tile;
                double maxEnergy = tile.maxStorage;
                double energyStored = tile.energy; // Используем tile.energy напрямую

                String energyText = formatEnergyText(energyStored, maxEnergy);
                String outputText = formatOutputText(energySource.getOfferedEnergy());
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





    private String formatFillText(double stored, double max) {
        double fillPercent = (max > 0) ? (stored / max * 100) : 0;
        return String.format("Fill Level: %.1f%%", fillPercent);
    }

    @Override
    protected ResourceLocation getTexture() {
        return TEXTURE;
    }
}