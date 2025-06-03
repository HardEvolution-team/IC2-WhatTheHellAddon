package com.ded.icwth.util;

import com.ded.icwth.Tags;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Утилита для генерации текстур с градиентом для солнечных панелей.
 */
public class TextureGenerator {

    private static final int TEXTURE_SIZE = 16; // Стандартный размер текстуры Minecraft

    /**
     * Создает текстуру с градиентом для верхней грани солнечной панели.
     *
     * @param hexColor HEX-цвет для градиента
     * @param panelName Имя панели (используется для имени файла)
     * @return true, если текстура успешно создана
     */
    public static boolean createGradientTexture(String hexColor, String panelName) {
        // Проверяем, что мы на клиентской стороне
        if (FMLCommonHandler.instance().getSide() != Side.CLIENT) {
            return false;
        }
        
        try {
            // Преобразуем HEX в цвет
            if (hexColor == null || hexColor.isEmpty()) {
                return false;
            }
            
            if (hexColor.startsWith("#")) {
                hexColor = hexColor.substring(1);
            }
            Color color = Color.decode("#" + hexColor);
            
            // Создаем изображение
            BufferedImage image = new BufferedImage(TEXTURE_SIZE, TEXTURE_SIZE, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = image.createGraphics();
            
            // Создаем градиент от центра к краям
            RadialGradientPaint gradient = new RadialGradientPaint(
                    TEXTURE_SIZE / 2f, TEXTURE_SIZE / 2f, TEXTURE_SIZE / 1.5f,
                    new float[] { 0.0f, 1.0f },
                    new Color[] { color, color.darker().darker() }
            );
            
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, TEXTURE_SIZE, TEXTURE_SIZE);
            
            // Добавляем небольшую сетку для эффекта солнечной панели
            g2d.setColor(new Color(0, 0, 0, 40)); // Полупрозрачный черный
            for (int i = 0; i < TEXTURE_SIZE; i += 4) {
                g2d.drawLine(0, i, TEXTURE_SIZE, i);
                g2d.drawLine(i, 0, i, TEXTURE_SIZE);
            }
            
            g2d.dispose();
            
            // Определяем путь к папке с модом
            String mcDir = System.getProperty("user.dir");
            File modDir = new File("mods");
            if (!modDir.exists()) {
                modDir = new File(mcDir, "mods");
            }
            
            // Создаем директорию для текстур
            File assetsDir = new File(modDir, "assets");
            if (!assetsDir.exists()) {
                assetsDir = new File(mcDir, "assets");
            }
            
            File modAssetsDir = new File(assetsDir, Tags.MODID);
            File texturesDir = new File(modAssetsDir, "textures/blocks");
            texturesDir.mkdirs();
            
            // Сохраняем текстуру
            File outputFile = new File(texturesDir, panelName + "_top.png");
            ImageIO.write(image, "PNG", outputFile);
            
            System.out.println("Created gradient texture for " + panelName + " at " + outputFile.getAbsolutePath());
            return true;
        } catch (Exception e) {
            System.err.println("Failed to create gradient texture for " + panelName + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Проверяет, существует ли текстура для указанной панели.
     *
     * @param panelName Имя панели
     * @return true, если текстура существует
     */
    public static boolean textureExists(String panelName) {
        if (FMLCommonHandler.instance().getSide() != Side.CLIENT) {
            return false;
        }
        
        // Определяем путь к папке с модом
        String mcDir = System.getProperty("user.dir");
        File modDir = new File("mods");
        if (!modDir.exists()) {
            modDir = new File(mcDir, "mods");
        }
        
        // Проверяем наличие текстуры
        File assetsDir = new File(modDir, "assets");
        if (!assetsDir.exists()) {
            assetsDir = new File(mcDir, "assets");
        }
        
        File modAssetsDir = new File(assetsDir, Tags.MODID);
        File texturesDir = new File(modAssetsDir, "textures/blocks");
        File textureFile = new File(texturesDir, panelName + "_top.png");
        
        return textureFile.exists();
    }
    
    /**
     * Получает ResourceLocation для текстуры панели.
     *
     * @param panelName Имя панели
     * @return ResourceLocation текстуры
     */
    public static ResourceLocation getTextureLocation(String panelName) {
        return new ResourceLocation(Tags.MODID, "textures/blocks/" + panelName + "_top.png");
    }
}
