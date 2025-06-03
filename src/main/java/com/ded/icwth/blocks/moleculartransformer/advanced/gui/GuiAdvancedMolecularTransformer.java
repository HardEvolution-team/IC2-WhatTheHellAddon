package com.ded.icwth.blocks.moleculartransformer.advanced.gui;

import com.ded.icwth.blocks.moleculartransformer.advanced.TileEntityAdvancedMolecularTransformer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * GUI для улучшенного молекулярного сборщика.
 * Отображает слоты 4x3 для входных и выходных предметов и центральный горизонтальный прогресс-бар.
 */
@SideOnly(Side.CLIENT)
public class GuiAdvancedMolecularTransformer extends GuiContainer {
    private static final ResourceLocation TEXTURE = new ResourceLocation("icwth:textures/gui/advanced_molecular_transformer_gui.png");
    private final TileEntityAdvancedMolecularTransformer tileEntity;

    public GuiAdvancedMolecularTransformer(TileEntityAdvancedMolecularTransformer tileEntity, EntityPlayer player) {
        super(new ContainerAdvancedMolecularTransformer(tileEntity, player));
        this.tileEntity = tileEntity;
        this.xSize = 220;
        this.ySize = 193;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(TEXTURE);

        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;

        // Отрисовка фона GUI
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);

        // Отрисовка горизонтального прогресс-бара
        double progress = this.tileEntity.getProgress();
        if (progress > 0) {
            // Координаты и размеры прогресс-бара на экране (поднимаем на 3 пикселя вверх)
            int progressBarX = 102;
            int progressBarY = 49; // Было 49, поднимаем на 3 пикселя вверх
            int progressBarWidth = 16;
            int progressBarHeight = 10;

            // Координаты текстуры прогресс-бара
            int progressBarTextureX = 221;
            int progressBarTextureY = 12;

            // Расчет ширины заполнения прогресс-бара
            int progressPixels = (int) (progressBarWidth * progress);

            // Отладка: выводим значения для проверки
            System.out.println("Progress: " + progress + ", Progress Pixels: " + progressPixels);

            // Отрисовка прогресс-бара
            this.drawTexturedModalRect(
                    x + progressBarX,
                    y + progressBarY,
                    progressBarTextureX,
                    progressBarTextureY,
                    progressPixels,
                    progressBarHeight
            );
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        // Заголовок GUI (по центру)
        String title = I18n.format("tile.advanced_molecular_transformer.name");
        this.fontRenderer.drawString(title, (this.xSize - this.fontRenderer.getStringWidth(title)) / 2, 6, 0x404040);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);

        // Всплывающая подсказка для прогресс-бара (увеличиваем область захвата вниз на 3 пикселя)
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        if (mouseX >= x + 102 && mouseX <= x + 117 && mouseY >= y + 46 && mouseY <= y + 61) { // Было y + 49 и y + 58, теперь y + 46 и y + 61
            if (this.tileEntity.getProgress() > 0) {
                String progressPercent = this.tileEntity.getProgressPercent();
                if (progressPercent.isEmpty()) progressPercent = "0%";
                this.drawHoveringText(progressPercent, mouseX, mouseY);
            }
        }
    }
}
