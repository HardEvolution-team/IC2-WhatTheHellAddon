package com.ded.icwth.blocks.batbox;

import ic2.core.block.wiring.TileEntityElectricBlock;

public class TileCompressedMFSU extends TileEntityElectricBlock {
    public final static int maxStorageEnergy = 1000000;
    public TileCompressedMFSU()
    {
        super(3, 512, maxStorageEnergy);
    }
}
