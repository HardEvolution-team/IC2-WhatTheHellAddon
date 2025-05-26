package com.ded.icwth;

import com.ded.icwth.blocks.ModBlocks;

import com.ded.icwth.blocks.moleculartransformer.advanced.TileEntityAdvancedMolecularTransformer;
import com.ded.icwth.blocks.moleculartransformer.advanced.renders.AdvancedMolecularTransformerTESR;
import com.ded.icwth.blocks.moleculartransformer.based.TileEntityMolecularTransformer;
import com.ded.icwth.blocks.moleculartransformer.based.renders.MolecularTransformerTESR;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Класс для регистрации рендеров на стороне клиента
 */
@SideOnly(Side.CLIENT)
public class ClientSetup {

    /**
     * Регистрирует специальные рендеры для TileEntity и предметов
     */
    public static void registerRenderers() {
        // Регистрируем TESR для блока
        ClientRegistry.bindTileEntitySpecialRenderer(
                TileEntityMolecularTransformer.class,
                new MolecularTransformerTESR()
        );
        ClientRegistry.bindTileEntitySpecialRenderer(
                TileEntityAdvancedMolecularTransformer.class,
                new AdvancedMolecularTransformerTESR()
        );
        // Связываем TESR с предметом блока
        Block molecularTransformer = ModBlocks.MolecularTransformer;
        ForgeHooksClient.registerTESRItemStack(
                Item.getItemFromBlock(molecularTransformer), // Правильный способ получения Item из Block
                0, // Метаданные предмета (обычно 0)
                TileEntityMolecularTransformer.class // Класс TileEntity
        );
        Block advancedMolecularTransformer = ModBlocks.AdvancedMolecularTransformer;
        ForgeHooksClient.registerTESRItemStack(
                Item.getItemFromBlock(advancedMolecularTransformer), // Правильный способ получения Item из Block
                0, // Метаданные предмета (обычно 0)
                TileEntityAdvancedMolecularTransformer.class // Класс TileEntity
        );
    }
}
