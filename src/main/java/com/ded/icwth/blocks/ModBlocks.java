package com.ded.icwth.blocks;


import com.ded.icwth.Tags;

import com.ded.icwth.blocks.batbox.BlockCompressedMFSU;
import com.ded.icwth.blocks.panels.ArcsinusSolarPanel.BlockArcsinusSolarPanel;

import com.ded.icwth.blocks.panels.BrauthemSolarPanel.BlockBrauthemSolarPanel;
import com.ded.icwth.blocks.panels.ChinaSolar.BlockChinaSolarPanel;
import com.ded.icwth.blocks.panels.DiffractionPanel.BlockDiffractionSolarPanel;
import com.ded.icwth.blocks.panels.DispersionSolarPanel.BlockDispersionSolarPanel;
import com.ded.icwth.blocks.panels.GravitonSolarPanel.BlockGravitonSolarPanel;
import com.ded.icwth.blocks.panels.LoliSolarPanel.BlockLoliSolarPanel;
import com.ded.icwth.blocks.panels.OmegaSolarPanel.BlockOmegaSolarPanel;
import com.ded.icwth.blocks.panels.PhotonicSolarPanel.BlockPhotonicSolarPanel;

import com.ded.icwth.blocks.panels.SpectralSolarPanel.BlockSpectralSolarPanel;
import com.ded.icwth.blocks.panels.VectorSolarPanel.BlockVectorSolarPanel;
import com.ded.icwth.blocks.trash.BlockEnergyTrashCan;
import com.rumaruka.emt.emt;
import ic2.core.IC2;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import com.google.common.base.Strings;
import com.google.common.collect.ObjectArrays;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderException;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.logging.log4j.Level;

import java.lang.reflect.Constructor;

public class ModBlocks {
    public static Block SpectralSolar;
    public static Block DiffractionSolar;
    public static Block DispersionSolar;
    public static Block BrauthemSolar;
    public static Block LoliSolar;
    public static Block ArcsinusSolar;
    public static Block VectorSolar;
    public static Block PhotonicSolar;
    public static Block GravitonSolar;
    public static Block OmegaSolar;


    public static Block CompressedMFSU;


    public static Block EnergyTrash;

    public static Block ChinaSolar;




    public static void init() {
        SpectralSolar = new BlockSpectralSolarPanel(Material.IRON).setTranslationKey("spectral_solar").setCreativeTab(IC2.tabIC2);
        DiffractionSolar = new BlockDiffractionSolarPanel(Material.IRON).setTranslationKey("diffraction_solar").setCreativeTab(IC2.tabIC2);
        DispersionSolar = new BlockDispersionSolarPanel(Material.IRON).setTranslationKey("dispersion_solar").setCreativeTab(IC2.tabIC2);
        BrauthemSolar = new BlockBrauthemSolarPanel(Material.IRON).setTranslationKey("brauthem_solar").setCreativeTab(IC2.tabIC2);
        LoliSolar = new BlockLoliSolarPanel(Material.IRON).setTranslationKey("loli_solar").setCreativeTab(IC2.tabIC2);
        ArcsinusSolar = new BlockArcsinusSolarPanel(Material.IRON).setTranslationKey("arcsinus_solar").setCreativeTab(IC2.tabIC2);
        VectorSolar = new BlockVectorSolarPanel(Material.IRON).setTranslationKey("vector_solar").setCreativeTab(IC2.tabIC2);
        PhotonicSolar = new BlockPhotonicSolarPanel(Material.IRON).setTranslationKey("photonic_solar").setCreativeTab(IC2.tabIC2);
        GravitonSolar = new BlockGravitonSolarPanel(Material.IRON).setTranslationKey("graviton_solar").setCreativeTab(IC2.tabIC2);
        OmegaSolar = new BlockOmegaSolarPanel(Material.IRON).setTranslationKey("omega_solar").setCreativeTab(IC2.tabIC2);

        CompressedMFSU = new BlockCompressedMFSU(Material.IRON).setTranslationKey("compressed_mfsu").setCreativeTab(IC2.tabIC2);


        EnergyTrash = new BlockEnergyTrashCan(Material.IRON).setTranslationKey("energy_trash").setCreativeTab(IC2.tabIC2);


        ChinaSolar = new BlockChinaSolarPanel(Material.IRON).setTranslationKey("china_solar").setCreativeTab(IC2.tabIC2);


    }

    public static void InGameRegister() {
        registerBlock(SpectralSolar, SpectralSolar.getTranslationKey().substring(5));
        registerBlock(DiffractionSolar, DiffractionSolar.getTranslationKey().substring(5));
        registerBlock(DispersionSolar, DispersionSolar.getTranslationKey().substring(5));
        registerBlock(BrauthemSolar, BrauthemSolar.getTranslationKey().substring(5));
        registerBlock(LoliSolar, LoliSolar.getTranslationKey().substring(5));
        registerBlock(ArcsinusSolar, ArcsinusSolar.getTranslationKey().substring(5));
        registerBlock(VectorSolar, VectorSolar.getTranslationKey().substring(5));
        registerBlock(PhotonicSolar, PhotonicSolar.getTranslationKey().substring(5));
        registerBlock(GravitonSolar, GravitonSolar.getTranslationKey().substring(5));
        registerBlock(OmegaSolar, OmegaSolar.getTranslationKey().substring(5));

        registerBlock(CompressedMFSU, CompressedMFSU.getTranslationKey().substring(5));

        registerBlock(EnergyTrash, EnergyTrash.getTranslationKey().substring(5));

        registerBlock(ChinaSolar, ChinaSolar.getTranslationKey().substring(5));

    }

    public static void Render() {
        registerRender(SpectralSolar);
        registerRender(DiffractionSolar);
        registerRender(DispersionSolar);
        registerRender(BrauthemSolar);
        registerRender(LoliSolar);
        registerRender(ArcsinusSolar);
        registerRender(VectorSolar);
        registerRender(PhotonicSolar);
        registerRender(GravitonSolar);
        registerRender(OmegaSolar);

        registerRender(CompressedMFSU);

        registerRender(EnergyTrash);

        registerRender(ChinaSolar);
    }




    @SideOnly(Side.CLIENT)
    public static void initModels() {
       // BlockLoliSolarPanel.initModel();


    }












    @Deprecated
    public static Block registerBlock(Block block)
    {
        ForgeRegistries.BLOCKS.register(block);
        ForgeRegistries.ITEMS.register(new ItemBlock(block).setRegistryName(block.getRegistryName()));
        return block;
    }
    @Deprecated
    public static Block registerBlock(Block block, String name)
    {
        if (block.getRegistryName() == null && Strings.isNullOrEmpty(name))
            throw new IllegalArgumentException("Attempted to register a Block with no name: " + block);
        if (block.getRegistryName() != null && !block.getRegistryName().toString().equals(name))
            throw new IllegalArgumentException("Attempted to register a Block with conflicting names. Old: " + block.getRegistryName() + " New: " + name);
        return registerBlock(block.getRegistryName() != null ? block : block.setRegistryName(name));
    }
    @Deprecated
    public static Block registerBlock(Block block, Class<? extends ItemBlock> itemclass, String name, Object... itemCtorArgs)
    {
        if (Strings.isNullOrEmpty(name))
        {
            throw new IllegalArgumentException("Attempted to register a block with no name: " + block);
        }
        if (Loader.instance().isInState(LoaderState.CONSTRUCTING))
        {
            FMLLog.warning("The mod %s is attempting to register a block whilst it it being constructed. This is bad modding practice - please use a proper mod lifecycle event.", Loader.instance().activeModContainer());
        }
        try
        {
            assert block != null : "registerBlock: block cannot be null";
            if (block.getRegistryName() != null && !block.getRegistryName().toString().equals(name))
                throw new IllegalArgumentException("Attempted to register a Block with conflicting names. Old: " + block.getRegistryName() + " New: " + name);
            ItemBlock i = null;
            if (itemclass != null)
            {
                Class<?>[] ctorArgClasses = new Class<?>[itemCtorArgs.length + 1];
                ctorArgClasses[0] = Block.class;
                for (int idx = 1; idx < ctorArgClasses.length; idx++)
                {
                    ctorArgClasses[idx] = itemCtorArgs[idx - 1].getClass();
                }
                Constructor<? extends ItemBlock> itemCtor = itemclass.getConstructor(ctorArgClasses);
                i = itemCtor.newInstance(ObjectArrays.concat(block, itemCtorArgs));
            }
            // block registration has to happen first
            ForgeRegistries.BLOCKS.register(block.getRegistryName() == null ? block.setRegistryName(name) : block);
            if (i != null)
                ForgeRegistries.ITEMS.register(i.setRegistryName(name));
            return block;
        } catch (Exception e)
        {
            FMLLog.log(Level.ERROR, e, "Caught an exception during block registration");
            throw new LoaderException(e);
        }
    }

    public static void registerRender(Block block) {
        Item item = Item.getItemFromBlock(block);
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, new ModelResourceLocation(Tags.MODID + ":" + item.getTranslationKey().substring(5), "inventory"));

    }
}



