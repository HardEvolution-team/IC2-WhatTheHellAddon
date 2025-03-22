package com.ded.icwth.blocks.panels.OmegaSolarPanel;

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

public class TileOmegaSolarPanel extends TileEntitySolarBase {

    public static double capacity = CommonConfig.solarpanel.extremewtf.capacity * 32768;
    public static double generate = (double)CommonConfig.solarpanel.extremewtf.generateDay * 16384;

    public TileOmegaSolarPanel(){
        super();
        energy = new BasicSource((TileEntity) this, capacity, 14);
        output = generate;

    }


    @Override
    public List<ItemStack> getWrenchDrops(World world, BlockPos blockPos, IBlockState iBlockState, TileEntity tileEntity, EntityPlayer entityPlayer, int i) {
        return Arrays.asList(new ItemStack[]{new ItemStack(ModBlocks.OmegaSolar)});
    }

    @Override
    public String getName() {
        return null;
    }
}
