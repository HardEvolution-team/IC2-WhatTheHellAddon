package com.ded.icwth.blocks.trash;

import ic2.core.IC2;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockEnergyTrashCan extends BlockContainer {

    public BlockEnergyTrashCan() {
        super(Material.IRON);
        setHardness(3.0F);
        setResistance(10.0F);
        setHarvestLevel("pickaxe", 1);
        setCreativeTab(IC2.tabIC2); // Укажите ваш креативный таб
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new EnergyTrashCanTile();
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return true;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return true;
    }
}
