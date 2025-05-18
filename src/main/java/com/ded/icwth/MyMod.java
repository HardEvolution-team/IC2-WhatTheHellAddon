package com.ded.icwth;





import com.ded.icwth.blocks.ModBlocks;


import com.ded.icwth.blocks.batbox.EnergyStorageManager;
import com.ded.icwth.blocks.hyperstorage.HyperStorageManager;
import com.ded.icwth.blocks.hyperstorage.TileHyperStorage;
import com.ded.icwth.blocks.molecularassembler.based.TileEntityMolecularAssembler;
import com.ded.icwth.blocks.molecularassembler.based.renders.MolecularTransformerTESR;
import com.ded.icwth.blocks.panels.ChinaSolar.TileChinaSolarPanel;
import com.ded.icwth.blocks.panels.SolarPanelManager;

import com.ded.icwth.blocks.CommonGuiHandler;

import com.ded.icwth.blocks.trash.EnergyTrashCanTile;
import com.ded.icwth.items.upgrades.UpgradeItems;
import ic2.api.event.TeBlockFinalCallEvent;
import ic2.core.block.BlockTileEntity;
import ic2.core.ref.TeBlock;
import mcjty.theoneprobe.api.ITheOneProbe;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Loader;
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
@Mod.EventBusSubscriber(Side.CLIENT)
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
        UpgradeItems.init();
        ModBlocks.init();
        ModBlocks.InGameRegister();

        //  CommonConfig.energystorage.extrememfsu.outputTier = Integer.MAX_VALUE;



        EnergyStorageManager.registerStorage("intermediate_mfsu", 5, 8192, 160000000, "tile.icwth.intermediate_mfsu.name");
        EnergyStorageManager.registerStorage("intermediate_high_mfsu", 6, 20480, 400000000, "tile.icwth.intermediate_high_mfsu.name");
        EnergyStorageManager.registerStorage("advanced_high_mfsu", 8, 61440, 1200000000, "tile.icwth.advanced_high_mfsu.name");
        EnergyStorageManager.registerStorage("superior_mfsu", 8, 122880, 2400000000.0, "tile.icwth.superior_mfsu.name");
        EnergyStorageManager.registerStorage("what_the_hell_mfsu", 14, 307200, 6000000000.0, "tile.icwth.what_the_hell_mfsu.name");
        EnergyStorageManager.registerStorage("magnetron_mfsu", 14, 921600, 18000000000.0, "tile.icwth.magnetron_mfsu.name");
        EnergyStorageManager.registerStorage("photon_resonance_mfsu", 14, 1843200, 36000000000.0, "tile.icwth.photon_resonance_mfsu.name");
        EnergyStorageManager.registerStorage("extreme_photonic_mfsu", 14, 4608000, 90000000000.0, "tile.icwth.extreme_photonic_mfsu.name");
        EnergyStorageManager.registerStorage("spectral_mfsu", 14, 4608000*2, 800000000, "tile.icwth.spectral_mfsu.name");
        EnergyStorageManager.registerStorage("arcsinus_mfsu", 14, 13824000, 270000000000.0, "tile.icwth.arcsinus_mfsu.name");
        EnergyStorageManager.registerStorage("diffraction_mfsu", 15, 27648000, 540000000000.0, "tile.icwth.diffraction_mfsu.name");
        EnergyStorageManager.registerStorage("dispersion_mfsu", 16, 69120000, 1350000000000.0, "tile.icwth.dispersion_mfsu.name");
        EnergyStorageManager.registerStorage("graviton_mfsu", 17, 207360000, 4050000000000.0, "tile.icwth.graviton_mfsu.name");
        EnergyStorageManager.registerStorage("omega_mfsu", 18, 414720000, 8100000000000.0, "tile.icwth.omega_mfsu.name");
        EnergyStorageManager.registerStorage("photonic_mfsu", Integer.MAX_VALUE, 1036800000, 20250000000000.0, "tile.icwth.photonic_mfsu.name");
        EnergyStorageManager.registerStorage("vector_mfsu", Integer.MAX_VALUE, 3110400000.0, 60750000000000.0, "tile.icwth.vector_mfsu.name");

// Финальные MFSU
        EnergyStorageManager.registerStorage("brauthem_mfsu", Integer.MAX_VALUE, 31104000000.0, 607500000000000.0, "tile.icwth.brauthem_mfsu.name");
        EnergyStorageManager.registerStorage("hell_yeah_mfsu", Integer.MAX_VALUE, Float.MAX_VALUE, Double.MAX_VALUE, "tile.icwth.hell_yeah_mfsu.name");

        // Register the new Hyper Storage Unit
        HyperStorageManager.registerHyperStorage("hyper_storage_unit", Integer.MAX_VALUE, Double.MAX_VALUE, "tile.icwth.hyper_storage_unit.name");


// новые панели
        SolarPanelManager.registerPanel("intermediate", 6, 8192.0, 20000000.0, "tile.icwth.intermediate.name");
        SolarPanelManager.registerPanel("intermediate_high", 6, 16384.0, 40000000.0, "tile.icwth.intermediate_high.name");
        SolarPanelManager.registerPanel("advanced_high", 6, 40960.0, 100000000.0, "tile.icwth.advanced_high.name");
        SolarPanelManager.registerPanel("superior", 7, 122880.0, 300000000.0, "tile.icwth.superior.name");
        SolarPanelManager.registerPanel("what_the_hell_panel", 7, 245760.0, 600000000.0, "tile.icwth.what_the_hell_panel.name");
        SolarPanelManager.registerPanel("magnetron", 7, 614400.0, 1500000000.0, "tile.icwth.magnetron.name");
        SolarPanelManager.registerPanel("photon_resonance", 8, 1843200.0, 4500000000.0, "tile.icwth.photon_resonance.name");
        SolarPanelManager.registerPanel("extreme_photonic", 9, 3686400.0, 9000000000.0, "tile.icwth.extreme_photonic.name");
        SolarPanelManager.registerPanel("spectral_solar", 10, 3686400.0*2, 9000000000.0*2, "tile.icwth.spectral_solar.name");

// старые панели
        SolarPanelManager.registerPanel("arcsinus_solar", Integer.MAX_VALUE, 9216000.0, 22500000000.0, "tile.icwth.arcsinus_solar_panel.name");
        SolarPanelManager.registerPanel("diffraction_solar", Integer.MAX_VALUE, 27648000.0, 67500000000.0, "tile.icwth.diffraction_panel.name");
        SolarPanelManager.registerPanel("dispersion_solar", Integer.MAX_VALUE, 55296000.0, 135000000000.0, "tile.icwth.dispersion_solar_panel.name");
        SolarPanelManager.registerPanel("graviton_solar", Integer.MAX_VALUE, 138240000.0, 337500000000.0, "tile.icwth.graviton_solar_panel.name");
        SolarPanelManager.registerPanel("omega_solar", Integer.MAX_VALUE, 414720000.0, 1012500000000.0, "tile.icwth.omega_solar_panel.name");
        SolarPanelManager.registerPanel("photonic_solar", Integer.MAX_VALUE, 829440000.0, 2025000000000.0, "tile.icwth.photonic_solar_panel.name");
        SolarPanelManager.registerPanel("vector_solar", Integer.MAX_VALUE, 2073600000.0, 5062500000000.0, "tile.icwth.vector_solar_panel.name");

// Финальные панели
        SolarPanelManager.registerPanel("brauthem_solar", Integer.MAX_VALUE, 20736000000.0, 50625000000000.0, "tile.icwth.brauthem_solar_panel.name");
        SolarPanelManager.registerPanel("loli_solar", Integer.MAX_VALUE, 414720000000.0, 1012500000000000.0, "tile.icwth.loli_solar_panel.name");
        SolarPanelManager.registerPanel("hell_yeah_solar", Integer.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, "tile.icwth.hell_yeah_solar.name");


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
        GameRegistry.registerTileEntity(SolarPanelManager.SolarPanelTile.class,
                new ResourceLocation(Tags.MODID, "solar_panel_tile"));
        GameRegistry.registerTileEntity(EnergyTrashCanTile.class, "tileEntityEnergyTrash");
        GameRegistry.registerTileEntity(TileChinaSolarPanel.class, "tileEntityChinaSolar");
        GameRegistry.registerTileEntity(TileHyperStorage.class, new ResourceLocation(Tags.MODID, "hyper_storage_tile"));
        GameRegistry.registerTileEntity(TileEntityMolecularAssembler.class, new ResourceLocation(Tags.MODID, "molecular_assembler_tile"));

        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMolecularAssembler.class, new MolecularTransformerTESR());

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
