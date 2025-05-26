package com.ded.icwth.blocks.moleculartransformer.based.gui;


import com.ded.icwth.blocks.moleculartransformer.based.TileEntityMolecularTransformer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * GUI для молекулярного сборщика.
 * Отображает слоты для входного и выходного предметов, а также информацию о процессе трансформации.
 */
@SideOnly(Side.CLIENT)
public class GuiMolecularTransformer extends GuiContainer {
    // Путь к текстуре соответствует структуре ресурсов мода
    private static final ResourceLocation TEXTURE = new ResourceLocation("icwth:textures/gui/moleculartransformer.png");

    private final TileEntityMolecularTransformer tileEntity;

    public GuiMolecularTransformer(TileEntityMolecularTransformer tileEntity, EntityPlayer player) {
        super(new ContainerMolecularTransformer(tileEntity, player));
        this.tileEntity = tileEntity;
        // Размеры GUI: xSize зависит от расстояния между слотами
        this.xSize = 220; // 220 + доп. промежутки для 8 слотов
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

        // Отрисовка вертикального индикатора прогресса
        double progress = this.tileEntity.getProgress();
        if (progress > 0) {
            // Координаты и размеры прогресс-бара
            int progressBarX = 22;
            int progressBarY = 40;
            int progressBarWidth = 11;
            int progressBarHeight = 32;

            // Координаты текстуры прогресс-бара в файле текстуры
            int progressBarTextureX = 220;
            int progressBarTextureY = 0;

            // Расчет высоты заполнения прогресс-бара (сверху вниз)
            int progressPixels = (int)(progressBarHeight * progress);

            // Отладка: выводим значения для проверки
            System.out.println("Progress: " + progress + ", Progress Pixels: " + progressPixels);

            // Отрисовка прогресс-бара (заполнение сверху вниз)
            this.drawTexturedModalRect(
                    x + progressBarX,
                    y + progressBarY,
                    progressBarTextureX,
                    progressBarTextureY,
                    progressBarWidth,
                    progressPixels
            );
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        // Заголовок GUI (по центру, y=9 из XML)
        String title = I18n.format("tile.molecular_assembler.name");
        this.fontRenderer.drawString(title, (this.xSize - this.fontRenderer.getStringWidth(title)) / 2, 9, 16777215);

        // Информация о текущем рецепте
        // Метки (справа, x=107, выравнивание справа из XML)
        this.fontRenderer.drawString("Input:", 107 - this.fontRenderer.getStringWidth("Input:"), 26, 16777215);
        this.fontRenderer.drawString("Output:", 107 - this.fontRenderer.getStringWidth("Output:"), 38, 16777215);
        this.fontRenderer.drawString("Energy:", 107 - this.fontRenderer.getStringWidth("Energy:"), 50, 16777215);
        this.fontRenderer.drawString("EU in:", 107 - this.fontRenderer.getStringWidth("EU in:"), 62, 16777215);
        this.fontRenderer.drawString("Progress:", 107 - this.fontRenderer.getStringWidth("Progress:"), 74, 16777215);

        // Значения (слева от меток, x=112 из XML)
        String energyNeeded = this.tileEntity.getField(2) == 0 ? "0 EU" : String.format("%,d EU", this.tileEntity.getField(2));
        String energyInput = this.tileEntity.getField(3) == 0 ? "0 EU/t" : String.format("%,d EU/t", this.tileEntity.getField(3));
        this.fontRenderer.drawString(this.tileEntity.getInputName(), 112, 26, 16777215);
        this.fontRenderer.drawString(this.tileEntity.getOutputName(), 112, 38, 16777215);
        this.fontRenderer.drawString(energyNeeded, 112, 50, 16777215);
        this.fontRenderer.drawString(energyInput, 112, 62, 16777215);
        this.fontRenderer.drawString(this.tileEntity.getProgressPercent().isEmpty() ? "0%" : this.tileEntity.getProgressPercent(), 112, 74, 16777215);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);

        // Добавляем всплывающую подсказку для индикатора прогресса
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        if (mouseX >= x + 22 && mouseX <= x + 33 && mouseY >= y + 40 && mouseY <= y + 72) {
            if (this.tileEntity.getProgress() > 0) {
                this.drawHoveringText(this.tileEntity.getProgressPercent(), mouseX, mouseY);
            }
        }
    }
}