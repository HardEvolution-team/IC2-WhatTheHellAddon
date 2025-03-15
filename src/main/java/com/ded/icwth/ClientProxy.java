package com.ded.icwth;

import com.ded.icwth.blocks.ModBlocks;
import com.ded.icwth.items.ModItems;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy {



    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {

        ModItems.initModels();
    }

    @SubscribeEvent
    public void registerRenders() {
        ModBlocks.Render();

        ModelLoader.setCustomModelResourceLocation(
                Item.getItemFromBlock(ModBlocks.CompressedMFSU),
                0,
                new ModelResourceLocation("icwth:compressed_mfsu", "inventory")
        );
    }
}


