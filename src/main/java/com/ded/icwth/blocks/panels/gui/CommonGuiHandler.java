package com.ded.icwth.blocks.panels.gui;

import com.ded.icwth.blocks.panels.TileEntitySolarBase;
import com.ded.icwth.blocks.panels.gui.ContainerSolarBase;
import com.ded.icwth.blocks.panels.gui.GuiSolarBase;
import com.ded.icwth.blocks.batbox.TileMFSUBase;
import com.ded.icwth.blocks.batbox.gui.ContainerMFSUBase;
import com.ded.icwth.blocks.batbox.gui.GuiMFSUBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class CommonGuiHandler implements IGuiHandler {
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity te = world.getTileEntity(pos);
        System.out.println("[Common GUI Handler] Server side GUI requested for ID: " + ID + " at " + pos);

        if (te instanceof TileEntitySolarBase) {
            System.out.println("Creating ContainerSolarBase for SolarPanel");
            return new ContainerSolarBase(player.inventory, (TileEntitySolarBase) te);
        } else if (te instanceof TileMFSUBase) {
            System.out.println("Creating ContainerMFSUBase for MFSU");
            return new ContainerMFSUBase(player.inventory, (TileMFSUBase) te);
        }
        System.out.println("No valid TileEntity found for ID: " + ID);
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity te = world.getTileEntity(pos);
        System.out.println("[Common GUI Handler] Client side GUI requested for ID: " + ID + " at " + pos);

        if (te instanceof TileEntitySolarBase) {
            System.out.println("Creating GuiSolarBase for SolarPanel");
            TileEntitySolarBase tile = (TileEntitySolarBase) te;
            return new GuiSolarBase(new ContainerSolarBase(player.inventory, tile), tile);
        } else if (te instanceof TileMFSUBase) {
            System.out.println("Creating GuiMFSUBase for MFSU");
            TileMFSUBase tile = (TileMFSUBase) te;
            return new GuiMFSUBase(new ContainerMFSUBase(player.inventory, tile), tile);
        }
        System.out.println("No valid TileEntity found for ID: " + ID);
        return null;
    }
}