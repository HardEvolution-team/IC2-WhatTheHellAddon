package com.ded.icwth.blocks.hyperstorage;

import com.ded.icwth.MyMod;
import com.ded.icwth.Tags;
import com.ded.icwth.blocks.hyperstorage.gui.ContainerHyperStorage;
import com.ded.icwth.blocks.hyperstorage.gui.GuiHyperStorage;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

// Manages registration for HyperStorage blocks
public class HyperStorageManager {

    private static final Map<String, Block> registeredHyperStorages = new HashMap<>();
    private static int nextGuiId = 100; // Start GUI IDs for hyperstorage from 100 to avoid conflict
    public static final Map<Integer, GuiData> guiRegistry = new HashMap<>();

    public static void registerHyperStorage(String name, int tier, double output, String storageName) {
        int currentGuiId = nextGuiId++;
        // MaxStorage is effectively infinite, pass a placeholder (e.g., Double.MAX_VALUE)
        HyperStorageBlock block = new HyperStorageBlock(name, tier, output, Double.MAX_VALUE, storageName, currentGuiId);

        block.setRegistryName(new ResourceLocation(Tags.MODID, name));
        block.setTranslationKey(Tags.MODID + "." + name);
        ForgeRegistries.BLOCKS.register(block);

        ItemBlock itemBlock = new ItemBlock(block);
        itemBlock.setRegistryName(block.getRegistryName());
        ForgeRegistries.ITEMS.register(itemBlock);

        if (net.minecraftforge.fml.common.FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            initModel(block, name);
        }

        registeredHyperStorages.put(name, block);

        // Register GUI data
        guiRegistry.put(currentGuiId, new GuiData(ContainerHyperStorage.class, GuiHyperStorage.class, TileHyperStorage.class));
    }

    @SideOnly(Side.CLIENT)
    private static void initModel(Block block, String name) {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0,
                new ModelResourceLocation(Tags.MODID + ":" + name, "inventory"));
        // Assuming model files exist or are generated elsewhere
    }

    // Helper class to store GUI registration info
    public static class GuiData {
        public final Class<? extends net.minecraft.inventory.Container> containerClass;
        public final Class<?> guiClass; // Client-side GUI class
        public final Class<? extends TileEntity> tileClass;

        public GuiData(Class<? extends net.minecraft.inventory.Container> containerClass, Class<?> guiClass, Class<? extends TileEntity> tileClass) {
            this.containerClass = containerClass;
            this.guiClass = guiClass;
            this.tileClass = tileClass;
        }
    }
}

