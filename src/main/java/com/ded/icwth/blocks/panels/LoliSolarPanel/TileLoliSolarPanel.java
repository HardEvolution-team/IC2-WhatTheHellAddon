package com.ded.icwth.blocks.panels.LoliSolarPanel;

import com.ded.icwth.blocks.ModBlocks;
import com.ded.icwth.blocks.panels.TileEntitySolarBase;
import ic2.api.energy.prefab.BasicSource;
import info.u_team.hycrafthds_wtf_ic2_addon.config.CommonConfig;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;

public class TileLoliSolarPanel
        extends TileEntitySolarBase {
    public static double capacity;
    public static double generate;

    public TileLoliSolarPanel() {
        this.capacity = (double) Integer.MAX_VALUE * Long.MAX_VALUE;
        this.generate = (double)CommonConfig.solarpanel.extremewtf.generateDay * 1.073741824E9;
        this.energy = new BasicSource((TileEntity)this, this.capacity, Integer.MAX_VALUE);
        this.output = this.generate;
    }

    @Override
    public List<ItemStack> getWrenchDrops(World world, BlockPos blockPos, IBlockState iBlockState, TileEntity tileEntity, EntityPlayer entityPlayer, int i) {
        return Arrays.asList(new ItemStack(ModBlocks.SpectralSolar));
    }
}