package com.ded.icwth.blocks.moleculartransformer.advanced;

import com.ded.icwth.MyMod;
import com.ded.icwth.Tags;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Блок улучшенного молекулярного сборщика.
 * Отвечает за создание TileEntity и обработку взаимодействия с игроком.
 */
public class BlockAdvancedMolecularTransformer extends BlockContainer {

    public BlockAdvancedMolecularTransformer(Material iron) {
        super(Material.IRON);
        this.setHardness(3.0F);
        this.setResistance(10.0F);
        this.setHarvestLevel("pickaxe", 2);
    }

    @Override
    public TileEntityAdvancedMolecularTransformer createNewTileEntity(World world, int meta) {
        return new TileEntityAdvancedMolecularTransformer();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        }

        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityAdvancedMolecularTransformer) {
            player.openGui(MyMod.instance, 1, world, pos.getX(), pos.getY(), pos.getZ());
            return true;
        }

        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @SideOnly(Side.CLIENT)
    private static void initModel(Block block, String name) {
        ModelLoader.setCustomModelResourceLocation(
                Item.getItemFromBlock(block), 0,
                new ModelResourceLocation(Tags.MODID + ":" + name, "inventory")
        );
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

}
