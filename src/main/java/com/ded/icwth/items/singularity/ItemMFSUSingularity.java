package com.ded.icwth.items.singularity;

import codechicken.lib.model.ModelRegistryHelper;
import codechicken.lib.util.TransformUtils;
import com.ded.icwth.Tags;
import ic2.core.IC2;
import morph.avaritia.api.IHaloRenderItem;
import morph.avaritia.client.render.item.HaloRenderItem;
import morph.avaritia.init.AvaritiaTextures;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.IRarity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public class ItemMFSUSingularity extends Item implements IHaloRenderItem {


    public ItemMFSUSingularity() {
        setRegistryName("mfsu_singularity");        // The unique name (within your mod) that identifies this item
        setTranslationKey(Tags.MODID + ".mfsu_singularity");     // Used for localization (en_US.lang)
        setCreativeTab(IC2.tabIC2);
    }

    @Nonnull
    @Override
    public IRarity getForgeRarity(@Nonnull ItemStack stack) {
        return EnumRarity.UNCOMMON;
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelResourceLocation location = new ModelResourceLocation(getRegistryName(), "inventory");
        ModelLoader.registerItemVariants(this, location);
        IBakedModel wrappedModel = new HaloRenderItem(TransformUtils.DEFAULT_ITEM, modelRegistry -> modelRegistry.getObject(location));
        ModelRegistryHelper.register(location, wrappedModel);
        ModelLoader.setCustomMeshDefinition(this, stack -> location);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean shouldDrawHalo(ItemStack itemStack) {
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public TextureAtlasSprite getHaloTexture(ItemStack itemStack) {
        return AvaritiaTextures.HALO;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getHaloColour(ItemStack itemStack) {
        return -16777216;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getHaloSize(ItemStack itemStack) {
        return 4;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean shouldDrawPulse(ItemStack itemStack) {
        return false;
    }
}
