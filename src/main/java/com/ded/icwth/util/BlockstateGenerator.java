package com.ded.icwth.util;

import com.ded.icwth.Tags;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Утилита для генерации blockstate-файлов для солнечных панелей.
 */
public class BlockstateGenerator {

    private static final String BLOCKSTATE_TEMPLATE = 
            "{\n" +
            "  \"variants\": {\n" +
            "    \"normal\": { \"model\": \"icwth:solar_panel\" }\n" +
            "  }\n" +
            "}";

    /**
     * Создает blockstate-файл для блока солнечной панели.
     *
     * @param panelName Имя панели
     * @return true, если blockstate успешно создан
     */
    public static boolean createBlockstate(String panelName) {
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
            
            // Создаем директорию для blockstates
            File assetsDir = new File(modDir, "assets");
            if (!assetsDir.exists()) {
                assetsDir = new File(mcDir, "assets");
            }
            
            File modAssetsDir = new File(assetsDir, Tags.MODID);
            File blockstatesDir = new File(modAssetsDir, "blockstates");
            blockstatesDir.mkdirs();
            
            // Создаем blockstate-файл
            File outputFile = new File(blockstatesDir, panelName + ".json");
            
            try (FileWriter writer = new FileWriter(outputFile)) {
                writer.write(BLOCKSTATE_TEMPLATE);
            }
            
            System.out.println("Created blockstate for " + panelName + " at " + outputFile.getAbsolutePath());
            return true;
        } catch (IOException e) {
            System.err.println("Failed to create blockstate for " + panelName + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Проверяет, существует ли blockstate для указанной панели.
     *
     * @param panelName Имя панели
     * @return true, если blockstate существует
     */
    public static boolean blockstateExists(String panelName) {
        if (FMLCommonHandler.instance().getSide() != Side.CLIENT) {
            return false;
        }
        
        // Определяем путь к папке с модом
        String mcDir = System.getProperty("user.dir");
        File modDir = new File("mods");
        if (!modDir.exists()) {
            modDir = new File(mcDir, "mods");
        }
        
        // Проверяем наличие blockstate
        File assetsDir = new File(modDir, "assets");
        if (!assetsDir.exists()) {
            assetsDir = new File(mcDir, "assets");
        }
        
        File modAssetsDir = new File(assetsDir, Tags.MODID);
        File blockstateFile = new File(modAssetsDir, "blockstates/" + panelName + ".json");
        
        return blockstateFile.exists();
    }
}
