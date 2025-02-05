package com.ded.icwth.items.upgrades.matter;

import com.ded.icwth.Tags;
import ic2.api.upgrade.IUpgradeItem;
import ic2.core.IC2;
import ic2.core.block.machine.tileentity.TileEntityMatter;
import ic2.core.item.upgrade.ItemUpgradeModule;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemMatterUpgrade extends Item {
    public ItemMatterUpgrade() {
        super();
        setRegistryName("matter_upgrade");        // The unique name (within your mod) that identifies this item
        setTranslationKey(Tags.MODID + ".matter_upgrade");     // Used for localization (en_US.lang)
        setCreativeTab(IC2.tabIC2);
    }


    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }
}
