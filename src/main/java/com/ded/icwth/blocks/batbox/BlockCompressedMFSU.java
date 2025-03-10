package com.ded.icwth.blocks.batbox;

import com.ded.icwth.blocks.panels.BrauthemSolarPanel.TileBrauthemSolarPanel;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class BlockCompressedMFSU extends BlockContainer {
    public static final PropertyDirection FACING = BlockHorizontal.FACING;

    public BlockCompressedMFSU(Material iron) {
        super(iron);
    }


    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileCompressedMFSU(); // Создаем новый TileEntity
    }


}