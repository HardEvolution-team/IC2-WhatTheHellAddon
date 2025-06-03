package com.ded.icwth.client;

import com.ded.icwth.blocks.panels.SolarPanelManager;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.awt.*;

/**
 * Обработчик цвета для солнечных панелей.
 * Применяет тонирование к верхней грани блока на основе HEX-кода.
 */
@SideOnly(Side.CLIENT)
public class ColorHandler implements IBlockColor, IItemColor {

    /**
     * Получает цвет для блока на основе его состояния и позиции.
     * Тонирует только верхнюю грань (tintIndex = 1).
     */
    @Override
    public int colorMultiplier(IBlockState state, @Nullable IBlockAccess world, @Nullable BlockPos pos, int tintIndex) {
        // Тонируем только верхнюю грань (tintIndex = 1)
        if (tintIndex == 1 && state.getBlock() instanceof SolarPanelManager.SolarPanelBlock) {
            SolarPanelManager.SolarPanelBlock block = (SolarPanelManager.SolarPanelBlock) state.getBlock();
            String hexColor = block.getHexTopColor();
            
            if (hexColor != null && !hexColor.isEmpty()) {
                return parseHexColor(hexColor);
            }
        }
        
        // Для остальных граней или если цвет не задан, возвращаем белый (без тонирования)
        return 0xFFFFFF;
    }

    /**
     * Получает цвет для предмета на основе его ItemStack и tintIndex.
     * Тонирует только верхнюю грань (tintIndex = 1).
     */
    @Override
    public int colorMultiplier(ItemStack stack, int tintIndex) {
        // Тонируем только верхнюю грань (tintIndex = 1)
        if (tintIndex == 1 && Block.getBlockFromItem(stack.getItem()) instanceof SolarPanelManager.SolarPanelBlock) {
            SolarPanelManager.SolarPanelBlock block = (SolarPanelManager.SolarPanelBlock) Block.getBlockFromItem(stack.getItem());
            String hexColor = block.getHexTopColor();
            
            if (hexColor != null && !hexColor.isEmpty()) {
                return parseHexColor(hexColor);
            }
        }
        
        // Для остальных граней или если цвет не задан, возвращаем белый (без тонирования)
        return 0xFFFFFF;
    }
    
    /**
     * Преобразует HEX-код цвета в целочисленное представление RGB.
     */
    private int parseHexColor(String hexColor) {
        try {
            if (hexColor.startsWith("#")) {
                hexColor = hexColor.substring(1);
            }
            
            // Преобразуем HEX в цвет
            Color color = Color.decode("#" + hexColor);
            return color.getRGB() & 0xFFFFFF; // Убираем альфа-канал
        } catch (Exception e) {
            System.err.println("Failed to parse hex color: " + hexColor);
            e.printStackTrace();
            return 0xFFFFFF; // Белый цвет по умолчанию
        }
    }
}
