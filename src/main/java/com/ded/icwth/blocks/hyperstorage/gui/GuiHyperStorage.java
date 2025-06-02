package com.ded.icwth.blocks.hyperstorage.gui;

import com.ded.icwth.blocks.hyperstorage.TileHyperStorage;
import ic2.core.GuiIC2;
import ic2.core.init.Localization;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Locale;

@SideOnly(Side.CLIENT)
public class GuiHyperStorage extends GuiIC2<ContainerHyperStorage> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("ic2", "textures/gui/GUIElectricBlock.png");

    private static final int PROGRESS_BAR_X = 84;
    private static final int PROGRESS_BAR_Y = 36;
    private static final int PROGRESS_BAR_WIDTH = 25;
    private static final int PROGRESS_BAR_HEIGHT = 14;

    // Constants for energy calculation
    private static final BigDecimal OVERFLOW_STEP = new BigDecimal("1e-323");
    private static final BigDecimal MAX_V1 = new BigDecimal(Double.MAX_VALUE);
    private static final BigDecimal V2_MULTIPLIER = MAX_V1; // v2 * MAX_V1
    private static final BigDecimal V3_MULTIPLIER = MAX_V1.multiply(MAX_V1).divide(OVERFLOW_STEP, MathContext.DECIMAL128); // v3 * MAX_V1^2 / 1E-323

    public GuiHyperStorage(ContainerHyperStorage container, TileHyperStorage tile) {
        super(container, 196);
        System.out.println("[GuiHyperStorage] Initialized with tile: " + tile + ", v1: " + (tile != null ? tile.v1 : "null") +
                ", v2: " + (tile != null ? tile.v2 : "null") + ", v3: " + (tile != null ? tile.v3 : "null"));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(TEXTURE);
        this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

        int energyBarWidth = 0;
        TileHyperStorage tile = container.base;
        if (tile != null && (tile.v1 > 0 || tile.v2 > 0 || tile.v3 > 0)) {
            energyBarWidth = PROGRESS_BAR_WIDTH;
        }
        drawEnergyBar(energyBarWidth);
    }


    private void drawEnergyBar(int width) {
        this.drawTexturedModalRect(335, guiTop + PROGRESS_BAR_Y - 2, 176, 14, width, PROGRESS_BAR_HEIGHT);
    }
    @Override
    protected void drawForegroundLayer(int mouseX, int mouseY) {
        super.drawForegroundLayer(mouseX, mouseY);
        drawDeviceName();
        drawArmourLabel();
        drawPowerLevelLabel();
        drawEnergyValues();
        drawOutputValue(container.base != null ? container.base.getOutput() : 0);
    }

    private void drawDeviceName() {
        String name = I18n.format(getBlockNameKey());
        int nameX = (xSize - fontRenderer.getStringWidth(name)) / 2;
        fontRenderer.drawString(name, nameX, 6, 4210752);
    }

    private String getBlockNameKey() {
        return container.base != null && container.base.storageName != null ? container.base.storageName : "container.hyperstorage";
    }

    private void drawArmourLabel() {
        fontRenderer.drawString(Localization.translate("ic2.EUStorage.gui.info.armor"), 8, ySize - 123, 4210752);
    }

    private void drawPowerLevelLabel() {
        String text = Localization.translate("ic2.EUStorage.gui.info.level");
        int textX = 80;
        fontRenderer.drawString(text, textX, PROGRESS_BAR_Y - 11, 4210752);
    }

    private String calculateTotalEnergy() {
        TileHyperStorage tile = container.base;
        if (tile == null) {
            System.out.println("[GuiHyperStorage] Tile is null");
            return "0 EU";
        }

        try {
            BigDecimal total = BigDecimal.ZERO;

            // Логирование для отладки
            System.out.println("[GuiHyperStorage] v1: " + tile.v1 + ", v2: " + tile.v2 + ", v3: " + tile.v3);

            // Добавляем v1
            if (tile.v1 > 0) {
                total = total.add(new BigDecimal(tile.v1, MathContext.DECIMAL128));
            }

            // Добавляем v2
            if (tile.v2 > 0) {
                BigDecimal v2Value = new BigDecimal(tile.v2, MathContext.DECIMAL128).multiply(V2_MULTIPLIER);
                total = total.add(v2Value);
            }

            // Добавляем v3
            if (tile.v3 > 0) {
                BigDecimal v3Value = new BigDecimal(tile.v3, MathContext.DECIMAL128).multiply(V3_MULTIPLIER);
                total = total.add(v3Value);
            }

            if (total.compareTo(BigDecimal.ZERO) <= 0) {
                System.out.println("[GuiHyperStorage] Total energy is zero");
                return "0 EU";
            }

            String formatted = formatBigNumber(total);
            System.out.println("[GuiHyperStorage] Total Energy: " + formatted);
            return formatted + " EU";
        } catch (Exception e) {
            System.err.println("[GuiHyperStorage] Error calculating energy: " + e.getMessage());
            return "Error";
        }
    }

    private String formatBigNumber(BigDecimal number) {
        if (number.compareTo(BigDecimal.ZERO) <= 0) {
            return "0";
        }

        try {
            DecimalFormat formatter = new DecimalFormat("0.###E0", DecimalFormatSymbols.getInstance(Locale.ROOT));
            formatter.setMaximumFractionDigits(3);
            formatter.setMinimumFractionDigits(3);
            String result = formatter.format(number);
            System.out.println("[GuiHyperStorage] Formatted Number: " + result);
            return result;
        } catch (Exception e) {
            System.err.println("[GuiHyperStorage] Error formatting number: " + e.getMessage());
            return "Very Large";
        }
    }

    private void drawEnergyValues() {
        String storedText = calculateTotalEnergy();
        int textX = PROGRESS_BAR_X + PROGRESS_BAR_WIDTH + 5;
        fontRenderer.drawString(storedText, textX, 40, 4210752);
    }

    private void drawOutputValue(double outputRate) {
        String text;
        if (outputRate >= 1e10) {
            text = String.format(Locale.ROOT, "Out: %.2e EU/t", outputRate);
        } else {
            text = String.format(Locale.ROOT, "Out: %.1f EU/t", outputRate);
        }
        int textX = 85;
        fontRenderer.drawString(text, textX, 60, 4210752);
    }

    @Override
    protected void renderHoveredToolTip(int mouseX, int mouseY) {
        int relativeMouseX = mouseX - guiLeft;
        int relativeMouseY = mouseY - guiTop;

        if (isMouseOverProgressBar(relativeMouseX, relativeMouseY)) {
            TileHyperStorage tile = container.base;
            if (tile == null) {
                System.out.println("[GuiHyperStorage] Tile is null in tooltip");
                return;
            }

            String energyText = "Energy: " + calculateTotalEnergy();
            String outputText = formatOutputText(tile.getOutput());
            String v1Text = String.format(Locale.ROOT, "V1: %.3e", tile.v1);
            String v2Text = String.format(Locale.ROOT, "V2: %.3e", tile.v2);
            String v3Text = String.format(Locale.ROOT, "V3: %.3e", tile.v3);

            drawHoveringText(Arrays.asList(energyText, outputText, "---", v1Text, v2Text, v3Text), mouseX, mouseY, fontRenderer);
        } else {
            super.renderHoveredToolTip(mouseX, mouseY);
        }
    }

    private String formatOutputText(double outputRate) {
        if (outputRate >= 1e10) {
            return String.format(Locale.ROOT, "Max Output: %.2e EU/t", outputRate);
        } else {
            return String.format(Locale.ROOT, "Max Output: %.1f EU/t", outputRate);
        }
    }

    private boolean isMouseOverProgressBar(int relativeX, int relativeY) {
        return isPointInRegion(PROGRESS_BAR_X, PROGRESS_BAR_Y, PROGRESS_BAR_WIDTH, PROGRESS_BAR_HEIGHT, relativeX, relativeY);
    }

    @Override
    protected ResourceLocation getTexture() {
        return TEXTURE;
    }
}