package com.ded.icwth.items.materials;

import com.ded.icwth.Tags;
import ic2.core.IC2;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemPerfectMatter extends Item{


    public ItemPerfectMatter() {
        setRegistryName("perfect_matter");
        setTranslationKey(Tags.MODID + ".perfect_matter");
        setMaxStackSize(64);
        setCreativeTab(IC2.tabIC2);
    }
    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }
}
