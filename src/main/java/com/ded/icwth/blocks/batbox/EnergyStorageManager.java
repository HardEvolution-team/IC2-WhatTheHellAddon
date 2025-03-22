package com.ded.icwth.blocks.batbox;

import com.ded.icwth.MyMod;
import com.ded.icwth.Tags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.Map;

public class EnergyStorageManager {

    private static final Map<String, Block> registeredStorages = new HashMap<>();
    private static int guiIdCounter = 0;

    static {
        GameRegistry.registerTileEntity(EnergyStorageTile.class, new ResourceLocation(Tags.MODID, "energy_storage_tile"));
    }

    public static void registerStorage(String name, int tier, double output, double maxStorage, String storageName) {
        EnergyStorageBlock block = new EnergyStorageBlock(name, tier, output, maxStorage, storageName, guiIdCounter++);

        block.setRegistryName(new ResourceLocation(Tags.MODID, name));
        block.setTranslationKey(Tags.MODID + "." + name);
        ForgeRegistries.BLOCKS.register(block);

        ItemBlock itemBlock = new ItemBlock(block);
        itemBlock.setRegistryName(block.getRegistryName());
        ForgeRegistries.ITEMS.register(itemBlock);

        if (net.minecraftforge.fml.common.FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            initModel(block, name);
        }

        registeredStorages.put(name, block);
    }

    @SideOnly(Side.CLIENT)
    private static void initModel(Block block, String name) {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0,
                new ModelResourceLocation(Tags.MODID + ":" + name, "inventory"));
    }

    public static class EnergyStorageBlock extends BlockContainer {
        private final int tier;
        private final double output;
        private final double maxStorage;
        private final String storageName;
        private final int guiId;

        public EnergyStorageBlock(String name, int tier, double output, double maxStorage, String storageName, int guiId) {
            super(Material.IRON);
            this.setCreativeTab(ic2.core.IC2.tabIC2);
            this.setTranslationKey(Tags.MODID + "." + name);
            this.tier = tier;
            this.output = output;
            this.maxStorage = maxStorage;
            this.storageName = storageName;
            this.guiId = guiId;
        }

        @Override
        public TileEntity createNewTileEntity(World world, int meta) {
            return new EnergyStorageTile(tier, output, maxStorage, storageName);
        }

        @Override
        public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
                                        EnumFacing facing, float hitX, float hitY, float hitZ) {
            if (!world.isRemote) {
                player.openGui(MyMod.instance, guiId, world, pos.getX(), pos.getY(), pos.getZ());
            }
            return true;
        }

        @Override
        public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
            super.onBlockAdded(world, pos, state);
            if (!world.isRemote) {
                world.notifyNeighborsOfStateChange(pos, this, true);
            }
        }
    }

    public static class EnergyStorageTile extends TileMFSUBase {
        public EnergyStorageTile(int tier, double output, double maxStorage, String storageName) {
            super(tier, output, maxStorage, storageName);
        }

        // Конструктор без аргументов для загрузки из NBT
        public EnergyStorageTile() {
            super();
        }
    }
}