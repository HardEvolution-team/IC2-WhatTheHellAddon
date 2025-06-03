package com.ded.icwth;



import com.ded.icwth.blocks.ModBlocks;


import com.ded.icwth.items.circuits.ItemHybridCircuit;
import com.ded.icwth.items.circuits.ItemQuantumCircuit;
import com.ded.icwth.items.circuits.ItemUltimateHybridCircuit;
import com.ded.icwth.items.materials.*;
import com.ded.icwth.items.rotors.IridiumRotor;
import com.ded.icwth.items.singularity.ItemMFSUSingularity;
import com.ded.icwth.items.upgrades.matter.ItemMatterUpgrade;
import com.ded.icwth.items.upgrades.overclocker.ItemAdvancedOverclocker;
import com.ded.icwth.items.upgrades.overclocker.ItemCreativeOverclocker;
import com.ded.icwth.items.upgrades.overclocker.ItemImprovedOverclocker;
import com.ded.icwth.items.upgrades.overclocker.ItemQuantumOverclocker;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.Objects;

@Mod.EventBusSubscriber
public class CommonProxy {

    @SubscribeEvent
    public static void init(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(new ItemImprovedOverclocker());
        event.getRegistry().register(new ItemAdvancedOverclocker());
        event.getRegistry().register(new ItemQuantumOverclocker());
        event.getRegistry().register(new ItemCreativeOverclocker());
        event.getRegistry().register(new ItemHybridCircuit());
        event.getRegistry().register(new ItemUltimateHybridCircuit());
        event.getRegistry().register(new ItemQuantumCircuit());
        event.getRegistry().register(new ItemMFSUSingularity());

        event.getRegistry().register(new ItemMatterUpgrade());
        event.getRegistry().register(new IridiumRotor());

        //event.getRegistry().register(new ItemBlock(CommonProxy.LoliSolar).setRegistryName(CommonProxy.LoliSolar.getTranslationKey()));





        event.getRegistry().register(new ItemQuadrupleDenseIridiumPlate());
        event.getRegistry().register(new ItemTripleDenseIridiumPlate());
        event.getRegistry().register(new ItemDoubleDenseIridiumPlate());
        event.getRegistry().register(new ItemFiveCompressedDenseIridiumPlate());

        event.getRegistry().register(new ItemDenseIridiumPlate());
        event.getRegistry().register(new ItemPerfectMatter());

        // event.getRegistry().register(new ItemBlock(ModBlocks.lol).setRegistryName(Objects.requireNonNull(ModBlocks.SpectralSolar.getRegistryName())));

    }


    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        //  event.getRegistry().register(new BlockLoliSolarPanel());
    }



}