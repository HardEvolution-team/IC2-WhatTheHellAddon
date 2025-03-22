package com.ded.icwth.blocks.panels.LoliSolarPanel;

import ic2.core.init.Localization;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class BlockLoliSolarPanel extends BlockContainer implements ITileEntityProvider{
    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    public BlockLoliSolarPanel(Material iron) {
        super(iron);

    }
    public static final int GUI_ID = 1;



    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        super.onBlockAdded(world, pos, state);
        if (!world.isRemote) {
            world.notifyNeighborsOfStateChange(pos, this, true);
        }
    }



    @Override
    public void addInformation(ItemStack stack, @org.jetbrains.annotations.Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(Localization.translate("icwth.mfsu.capacity ") + " " + String.format("%.0f", TileLoliSolarPanel.capacity) + (" eu"));
        tooltip.add(Localization.translate("icwth.mfsu.generate ") + " " + String.format("%.0f", TileLoliSolarPanel.generate) + (" eu/t"));
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return true;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileLoliSolarPanel();
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }


}
