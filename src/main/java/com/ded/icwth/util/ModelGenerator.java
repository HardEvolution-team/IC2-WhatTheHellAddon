package com.ded.icwth.util;

import com.ded.icwth.Tags;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Утилита для генерации JSON-моделей для солнечных панелей.
 */
public class ModelGenerator {

    private static final String MODEL_TEMPLATE = 
            "{\n" +
            "  \"parent\": \"block/cube\",\n" +
            "  \"textures\": {\n" +
            "    \"particle\": \"icwth:blocks/solar_panel_sides\",\n" +
            "    \"down\": \"icwth:blocks/solar_panel_sides\",\n" +
            "    \"up\": \"icwth:blocks/%s_top\",\n" +
            "    \"north\": \"icwth:blocks/solar_panel_sides\",\n" +
            "    \"east\": \"icwth:blocks/solar_panel_sides\",\n" +
            "    \"south\": \"icwth:blocks/solar_panel_sides\",\n" +
            "    \"west\": \"icwth:blocks/solar_panel_sides\"\n" +
            "  }\n" +
            "}";
    
    private static final String ITEM_MODEL_TEMPLATE = 
            "{\n" +
            "  \"parent\": \"icwth:block/%s\"\n" +
            "}";

    /**
     * Создает JSON-модель для блока солнечной панели.
     *
     * @param panelName Имя панели
     * @return true, если модель успешно создана
     */
    public static boolean createBlockModel(String panelName) {
        // Проверяем, что мы на клиентской стороне
        if (FMLCommonHandler.instance().getSide() != Side.CLIENT) {
            return false;
        }
        
        try {
            // Определяем путь к папке с модом
            String mcDir = System.getProperty("user.dir");
            File modDir = new File("mods");
            if (!modDir.exists()) {
                modDir = new File(mcDir, "mods");
            }
            
            // Создаем директорию для моделей
            File assetsDir = new File(modDir, "assets");
            if (!assetsDir.exists()) {
                assetsDir = new File(mcDir, "assets");
            }
            
            File modAssetsDir = new File(assetsDir, Tags.MODID);
            File modelsDir = new File(modAssetsDir, "models/block");
            modelsDir.mkdirs();
            
            // Создаем JSON-модель для блока
            String modelJson = String.format(MODEL_TEMPLATE, panelName);
            File outputFile = new File(modelsDir, panelName + ".json");
            
            try (FileWriter writer = new FileWriter(outputFile)) {
                writer.write(modelJson);
            }
            
            System.out.println("Created block model for " + panelName + " at " + outputFile.getAbsolutePath());
            return true;
        } catch (IOException e) {
            System.err.println("Failed to create block model for " + panelName + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Создает JSON-модель для предмета солнечной панели.
     *
     * @param panelName Имя панели
     * @return true, если модель успешно создана
     */
    public static boolean createItemModel(String panelName) {
        // Проверяем, что мы на клиентской стороне
        if (FMLCommonHandler.instance().getSide() != Side.CLIENT) {
            return false;
        }
        
        try {
            // Определяем путь к папке с модом
            String mcDir = System.getProperty("user.dir");
            File modDir = new File("mods");
            if (!modDir.exists()) {
                modDir = new File(mcDir, "mods");
            }
            
            // Создаем директорию для моделей предметов
            File assetsDir = new File(modDir, "assets");
            if (!assetsDir.exists()) {
                assetsDir = new File(mcDir, "assets");
            }
            
            File modAssetsDir = new File(assetsDir, Tags.MODID);
            File modelsDir = new File(modAssetsDir, "models/item");
            modelsDir.mkdirs();
            
            // Создаем JSON-модель для предмета
            String modelJson = String.format(ITEM_MODEL_TEMPLATE, panelName);
            File outputFile = new File(modelsDir, panelName + ".json");
            
            try (FileWriter writer = new FileWriter(outputFile)) {
                writer.write(modelJson);
            }
            
            System.out.println("Created item model for " + panelName + " at " + outputFile.getAbsolutePath());
            return true;
        } catch (IOException e) {
            System.err.println("Failed to create item model for " + panelName + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Проверяет, существуют ли модели для указанной панели.
     *
     * @param panelName Имя панели
     * @return true, если модели существуют
     */
    public static boolean modelsExist(String panelName) {
        if (FMLCommonHandler.instance().getSide() != Side.CLIENT) {
            return false;
        }
        
        // Определяем путь к папке с модом
        String mcDir = System.getProperty("user.dir");
        File modDir = new File("mods");
        if (!modDir.exists()) {
            modDir = new File(mcDir, "mods");
        }
        
        // Проверяем наличие моделей
        File assetsDir = new File(modDir, "assets");
        if (!assetsDir.exists()) {
            assetsDir = new File(mcDir, "assets");
        }
        
        File modAssetsDir = new File(assetsDir, Tags.MODID);
        File blockModelFile = new File(modAssetsDir, "models/block/" + panelName + ".json");
        File itemModelFile = new File(modAssetsDir, "models/item/" + panelName + ".json");
        
        return blockModelFile.exists() && itemModelFile.exists();
    }
}
