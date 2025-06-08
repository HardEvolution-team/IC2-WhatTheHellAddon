package com.ded.icwth.blocks;


import com.ded.icwth.Tags;


import com.ded.icwth.blocks.mechanism.distiller.BlockElectricDistiller;
import com.ded.icwth.blocks.moleculartransformer.advanced.BlockAdvancedMolecularTransformer;

import com.ded.icwth.blocks.moleculartransformer.based.BlockMolecularTransformer;
import com.ded.icwth.blocks.panels.ChinaSolar.BlockChinaSolarPanel;




import com.ded.icwth.blocks.trash.BlockEnergyTrashCan;
import ic2.core.IC2;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
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







    public static Block EnergyTrash;

    public static Block ChinaSolar;

    public static Block MolecularTransformer;
    
    public static Block AdvancedMolecularTransformer;

    public static Block Distiller;

  //  public static Block TestRenderBlock;
    public static void init() {






        EnergyTrash = new BlockEnergyTrashCan().setTranslationKey("energy_trash").setCreativeTab(IC2.tabIC2);

        ChinaSolar = new BlockChinaSolarPanel(Material.IRON).setTranslationKey("china_solar").setCreativeTab(IC2.tabIC2);

        MolecularTransformer = new BlockMolecularTransformer(Material.IRON).setTranslationKey("molecular_transformer").setCreativeTab(IC2.tabIC2);
        
        AdvancedMolecularTransformer = new BlockAdvancedMolecularTransformer(Material.IRON).setTranslationKey("advanced_molecular_transformer").setCreativeTab(IC2.tabIC2);

        // TestRenderBlock = new BlockEnergyCoreProcedural(Material.IRON).setTranslationKey("test_render_block").setCreativeTab(IC2.tabIC2);

        Distiller = new BlockElectricDistiller(Material.IRON).setTranslationKey("electric_distiller").setCreativeTab(IC2.tabIC2);
    }

    public static void InGameRegister() {





        registerBlock(EnergyTrash, EnergyTrash.getTranslationKey().substring(5));

        registerBlock(ChinaSolar, ChinaSolar.getTranslationKey().substring(5));

        registerBlock(MolecularTransformer, MolecularTransformer.getTranslationKey().substring(5));
        
        registerBlock(AdvancedMolecularTransformer, AdvancedMolecularTransformer.getTranslationKey().substring(5));

        registerBlock(Distiller, Distiller.getTranslationKey().substring(5));
    }

    public static void Render() {





        registerRender(EnergyTrash);

        registerRender(ChinaSolar);

        registerRender(MolecularTransformer);
        
        registerRender(AdvancedMolecularTransformer);

        registerRender(Distiller);
    }




    @SideOnly(Side.CLIENT)
    public static void initModels() {


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