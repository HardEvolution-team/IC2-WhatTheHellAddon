package com.ded.icwth;

import com.ded.icwth.blocks.ModBlocks;
import com.ded.icwth.blocks.panels.SolarPanelManager;
import com.ded.icwth.client.ColorHandler;
import com.ded.icwth.items.ModItems;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;


@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        ModBlocks.initModels();
        ModItems.initModels();

        // Вызываем регистрацию специальных рендеров
        ClientSetup.registerRenderers();
    }

    @SubscribeEvent
    public void registerRenders() {
        ModBlocks.Render();
    }

    @SubscribeEvent
    public static void registerBlockColors(ColorHandlerEvent.Block event) {
        BlockColors blockColors = event.getBlockColors();
        ColorHandler colorHandler = new ColorHandler();

        // Регистрируем обработчик цвета для всех блоков солнечных панелей
        for (Block block : SolarPanelManager.registeredPanels.values()) {
            if (block instanceof SolarPanelManager.SolarPanelBlock) {
                blockColors.registerBlockColorHandler(colorHandler, block);
                System.out.println("Registered block color handler for: " + block.getRegistryName());
            }
        }
    }
    @SubscribeEvent
    public static void registerItemColors(ColorHandlerEvent.Item event) {
        ItemColors itemColors = event.getItemColors();
        ColorHandler colorHandler = new ColorHandler();

        // Регистрируем обработчик цвета для всех предметов солнечных панелей
        for (Block block : SolarPanelManager.registeredPanels.values()) {
            if (block instanceof SolarPanelManager.SolarPanelBlock) {
                Item item = Item.getItemFromBlock(block);
                itemColors.registerItemColorHandler(colorHandler, item);
                System.out.println("Registered item color handler for: " + item.getRegistryName());
            }
        }
    }








}
