package com.ded.icwth.blocks;


import com.ded.icwth.blocks.batbox.TileCompressedMFSU;
import ic2.core.block.wiring.ContainerElectricBlock;
import ic2.core.block.wiring.GuiElectricBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {
    public static final int COMPRESSED_MFSU_GUI = 0;

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == COMPRESSED_MFSU_GUI) {
            TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
            if (te instanceof TileCompressedMFSU) {
                return new ContainerElectricBlock(player, (TileCompressedMFSU) te);
            }
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == COMPRESSED_MFSU_GUI) {
            TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
            if (te instanceof TileCompressedMFSU) {
                return new GuiElectricBlock(new ContainerElectricBlock(player, (TileCompressedMFSU) te));
            }
        }
        return null;
    }
}