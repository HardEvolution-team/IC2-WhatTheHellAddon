package com.ded.icwth.items.singularity;

import com.ded.icwth.Tags;
import ic2.core.IC2;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemMFSUSingularity extends Item {


    public ItemMFSUSingularity() {
        setRegistryName("mfsu_singularity");        // The unique name (within your mod) that identifies this item
        setTranslationKey(Tags.MODID + ".mfsu_singularity");     // Used for localization (en_US.lang)
        setMaxStackSize(64);
        setCreativeTab(IC2.tabIC2);
    }
    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }
}
