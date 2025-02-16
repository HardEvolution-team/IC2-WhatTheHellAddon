package com.ded.icwth;


import com.ded.icwth.blocks.ModBlocks;


import com.ded.icwth.blocks.batbox.TileCompressedMFSU;
import com.ded.icwth.blocks.panels.ArcsinusSolarPanel.TileArcsinusSolarPanel;
import com.ded.icwth.blocks.panels.BrauthemSolarPanel.TileBrauthemSolarPanel;
import com.ded.icwth.blocks.panels.DiffractionPanel.TileDiffractionSolarPanel;
import com.ded.icwth.blocks.panels.DispersionSolarPanel.TileDispersionSolarPanel;
import com.ded.icwth.blocks.panels.GravitonSolarPanel.TileGravitonSolarPanel;
import com.ded.icwth.blocks.panels.LoliSolarPanel.BlockLoliSolarPanel;
import com.ded.icwth.blocks.panels.LoliSolarPanel.TileLoliSolarPanel;
import com.ded.icwth.blocks.panels.OmegaSolarPanel.TileOmegaSolarPanel;
import com.ded.icwth.blocks.panels.PhotonicSolarPanel.TilePhotonicSolarPanel;
import com.ded.icwth.blocks.panels.SpectralSolarPanel.TileSpectralSolarPanel;

import com.ded.icwth.blocks.panels.VectorSolarPanel.TileVectorSolarPanel;
import ic2.api.event.TeBlockFinalCallEvent;
import ic2.core.IC2;
import ic2.core.block.BlockTileEntity;
import ic2.core.block.TeBlockRegistry;
import ic2.core.block.generator.tileentity.TileEntitySolarGenerator;
import ic2.core.item.upgrade.ItemUpgradeModule;
import ic2.core.ref.IC2Material;
import ic2.core.ref.TeBlock;
import ic2.core.util.Util;
import info.u_team.hycrafthds_wtf_ic2_addon.config.CommonConfig;
import net.minecraft.block.Block;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import static com.ded.icwth.Tags.MODID;

@Mod(modid = MODID, version = Tags.VERSION, name = Tags.MODNAME, acceptedMinecraftVersions = "[1.12.2]")
public class MyMod {
  //  public static final ItemUpgradeModule.UpgradeType MATTER_UPGRADE_TYPE = new ItemUpgradeModule.UpgradeType("matter_upgrade");
    public static BlockTileEntity machines;
    public static CommonProxy proxy;

    @Mod.Instance
    public static MyMod instance;
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    @EventHandler
    // preInit "Run before anything else. Read your config, create blocks, items, etc. (Remove if not needed)
    public void preInit(FMLPreInitializationEvent event) {
        // register to the event bus so that we can listen to events
        MinecraftForge.EVENT_BUS.register(this);
        LOGGER.info("I am " + Tags.MODNAME + " + at version " + Tags.VERSION);


        ModBlocks.init();
        ModBlocks.InGameRegister();


      //  CommonConfig.energystorage.extrememfsu.outputTier = Integer.MAX_VALUE;



    }


    @SideOnly(Side.CLIENT)
    public static void initModels() {
     //   BlockLoliSolarPanel.initModel();


    }
    @SubscribeEvent
    public void register(TeBlockFinalCallEvent event) {
    }
    @SubscribeEvent
    // Register recipes here (Remove if not needed)
    public void registerRecipes(RegistryEvent.Register<IRecipe> event) {

    }

    @SubscribeEvent
    // Register items here (Remove if not needed)
    public void registerItems(RegistryEvent.Register<Item> event) {

    }

    @SubscribeEvent
    // Register blocks here (Remove if not needed)
    public void registerBlocks(RegistryEvent.Register<Block> event) {

    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        //GameRegistry.registerTileEntity(TileSpectralSolarPanel.class, "tileEntitySpectralSolar");
        GameRegistry.registerTileEntity(TileDiffractionSolarPanel.class, "tileEntityDiffractionSolar");
        GameRegistry.registerTileEntity(TileDispersionSolarPanel.class, "tileEntityDispersionSolar");
        GameRegistry.registerTileEntity(TileBrauthemSolarPanel.class, "tileEntityBrauthemSolar");
        GameRegistry.registerTileEntity(TileLoliSolarPanel.class, "tileEntityLoliSolar");
        GameRegistry.registerTileEntity(TileArcsinusSolarPanel.class, "tileEntityArcsinusSolar");
        GameRegistry.registerTileEntity(TileVectorSolarPanel.class, "tileEntityVectorSolar");
        GameRegistry.registerTileEntity(TilePhotonicSolarPanel.class, "tileEntityPhotonicSolar");
        GameRegistry.registerTileEntity(TileGravitonSolarPanel.class, "tileEntityGravitonSolar");
        GameRegistry.registerTileEntity(TileOmegaSolarPanel.class, "tileEntityOmegaSolar");

        GameRegistry.registerTileEntity(TileCompressedMFSU.class, "tileEntityCompressedMFSU");

    }

    @EventHandler
    // postInit "Handle interaction with other mods, complete your setup based on this." (Remove if not needed)
    public void postInit(FMLPostInitializationEvent event) {
        TeBlock.registerTeMappings();
    }

    @EventHandler
    // register server commands in this event handler (Remove if not needed)
    public void serverStarting(FMLServerStartingEvent event) {
    }
}
