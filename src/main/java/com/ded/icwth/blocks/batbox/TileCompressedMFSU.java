package com.ded.icwth.blocks.batbox;






import ic2.core.block.wiring.TileEntityElectricBlock;
import net.minecraft.util.EnumFacing;

import java.util.EnumSet;


public class TileCompressedMFSU extends TileEntityElectricBlock {
    public TileCompressedMFSU() {
        super(3, 512, 40000000); // Tier 3, Output 512 EU/t, Capacity 40M EU
    }


}