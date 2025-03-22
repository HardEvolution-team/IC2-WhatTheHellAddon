package com.ded.icwth;




import com.ded.icwth.blocks.ModBlocks;


import com.ded.icwth.blocks.batbox.EnergyStorageManager;
import com.ded.icwth.blocks.panels.ArcsinusSolarPanel.TileArcsinusSolarPanel;
import com.ded.icwth.blocks.panels.BrauthemSolarPanel.TileBrauthemSolarPanel;
import com.ded.icwth.blocks.panels.ChinaSolar.TileChinaSolarPanel;
import com.ded.icwth.blocks.panels.DiffractionPanel.TileDiffractionSolarPanel;
import com.ded.icwth.blocks.panels.DispersionSolarPanel.TileDispersionSolarPanel;
import com.ded.icwth.blocks.panels.GravitonSolarPanel.TileGravitonSolarPanel;
import com.ded.icwth.blocks.panels.LoliSolarPanel.TileLoliSolarPanel;
import com.ded.icwth.blocks.panels.OmegaSolarPanel.TileOmegaSolarPanel;
import com.ded.icwth.blocks.panels.PhotonicSolarPanel.TilePhotonicSolarPanel;
import com.ded.icwth.blocks.panels.SolarPanelManager;
import com.ded.icwth.blocks.panels.VectorSolarPanel.TileVectorSolarPanel;
import com.ded.icwth.blocks.panels.gui.CommonGuiHandler;

import com.ded.icwth.blocks.trash.EnergyTrashCanTile;
import ic2.api.event.TeBlockFinalCallEvent;
import ic2.core.block.BlockTileEntity;
import ic2.core.ref.TeBlock;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
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
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new CommonGuiHandler());

        ModBlocks.init();
        ModBlocks.InGameRegister();

      //  CommonConfig.energystorage.extrememfsu.outputTier = Integer.MAX_VALUE;


        EnergyStorageManager.registerStorage("arcsinus_mfsu", 6, 1024, 1000000, "tile.arcsinus_mfsu.name");
        EnergyStorageManager.registerStorage("hell_yeah_mfsu", Integer.MAX_VALUE, Long.MAX_VALUE, Double.MAX_VALUE, "tile.brauthem_mfsu.name");
        EnergyStorageManager.registerStorage("china_mfsu", 5, 512, 500000, "tile.china_mfsu.name");
        EnergyStorageManager.registerStorage("diffraction_mfsu", 6, 1024, 1500000, "tile.diffraction_mfsu.name");
        EnergyStorageManager.registerStorage("dispersion_mfsu", 7, 2048, 2500000, "tile.dispersion_mfsu.name");
        EnergyStorageManager.registerStorage("graviton_mfsu", 8, 4096, 5000000, "tile.graviton_mfsu.name");
        EnergyStorageManager.registerStorage("omega_mfsu", 9, 8192, 10000000, "tile.omega_mfsu.name");
        EnergyStorageManager.registerStorage("photonic_mfsu", 8, 4096, 4000000, "tile.photonic_mfsu.name");
        EnergyStorageManager.registerStorage("spectral_mfsu", 7, 2048, 3000000, "tile.spectral_mfsu.name");
        EnergyStorageManager.registerStorage("vector_mfsu", 6, 1024, 1200000, "tile.vector_mfsu.name");




        SolarPanelManager.registerPanel("spectral_solar", 3, 512.0, 1000000.0, "tile.icwth.spectral_solar.name");

    //    SolarPanelManager.registerSolarPanel("solar_panel_basic", Integer.MAX_VALUE, Long.MAX_VALUE, Double.MAX_VALUE, "Solar Panel", 1.0);



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

        GameRegistry.registerTileEntity(SolarPanelManager.SolarPanelTile.class,
                new ResourceLocation(Tags.MODID, "solar_panel_tile"));


        GameRegistry.registerTileEntity(EnergyTrashCanTile.class, "tileEntityEnergyTrash");


        GameRegistry.registerTileEntity(TileChinaSolarPanel.class, "tileEntityChinaSolar");

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
