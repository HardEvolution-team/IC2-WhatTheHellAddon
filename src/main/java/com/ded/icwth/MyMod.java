package com.ded.icwth;





//import com.ded.icwth.blocks.BlockEnergyCoreUltra;
import com.ded.icwth.blocks.ModBlocks;


import com.ded.icwth.blocks.batbox.EnergyStorageManager;
import com.ded.icwth.blocks.hyperstorage.HyperStorageManager;
import com.ded.icwth.blocks.hyperstorage.TileHyperStorage;
import com.ded.icwth.blocks.moleculartransformer.advanced.TileEntityAdvancedMolecularTransformer;
import com.ded.icwth.blocks.moleculartransformer.advanced.renders.AdvancedMolecularTransformerTESR;
import com.ded.icwth.blocks.moleculartransformer.recipes.RecipeInitializer;
import com.ded.icwth.blocks.moleculartransformer.recipes.RecipeSynchronizer;
import com.ded.icwth.blocks.moleculartransformer.based.TileEntityMolecularTransformer;
import com.ded.icwth.blocks.moleculartransformer.based.renders.MolecularTransformerTESR;
import com.ded.icwth.blocks.panels.ChinaSolar.TileChinaSolarPanel;
import com.ded.icwth.blocks.panels.SolarPanelManager;

import com.ded.icwth.blocks.CommonGuiHandler;
import com.ded.icwth.blocks.trash.EnergyTrashCanTile;
import com.ded.icwth.items.upgrades.UpgradeItems;
import ic2.api.event.TeBlockFinalCallEvent;
import ic2.api.recipe.Recipes;
import ic2.core.block.BlockTileEntity;
import ic2.core.ref.TeBlock;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.ForgeRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;
import java.util.Iterator;

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


//tier,out,storage
// Energy Storage

        EnergyStorageManager.registerStorage("compressed_mfsu", 7, 18432, 360000000, "tile.icwth.intermediate_mfsu.name");
        EnergyStorageManager.registerStorage("double_compressed_mfsu", 9, 165888, 3240000000.0, "tile.icwth.intermediate_mfsu.name");
        EnergyStorageManager.registerStorage("triple_compressed_mfsu", 11, 1492992, 29160000000.0, "tile.icwth.intermediate_mfsu.name");
        EnergyStorageManager.registerStorage("quadruple_compressed_mfsu", 13, 13436928, 262440000000.0, "tile.icwth.intermediate_mfsu.name");
        EnergyStorageManager.registerStorage("fifth_compressed_mfsu", 15, 120932352, 2361960000000.0, "tile.icwth.intermediate_mfsu.name");

        EnergyStorageManager.registerStorage("intermediate_mfsu", 5, 8192, 160000000, "tile.icwth.intermediate_mfsu.name");
        EnergyStorageManager.registerStorage("intermediate_high_mfsu", 6, 32768, 640000000, "tile.icwth.intermediate_high_mfsu.name");
        EnergyStorageManager.registerStorage("advanced_high_mfsu", 7, 131072, 2560000000.0, "tile.icwth.advanced_high_mfsu.name");
        EnergyStorageManager.registerStorage("superior_mfsu", 8, 524288, 10240000000.0, "tile.icwth.superior_mfsu.name");
        EnergyStorageManager.registerStorage("what_the_hell_mfsu", 9, 2097152, 40960000000.0, "tile.icwth.what_the_hell_mfsu.name");
        EnergyStorageManager.registerStorage("magnetron_mfsu", 10, 8388608, 163840000000.0, "tile.icwth.magnetron_mfsu.name");
        EnergyStorageManager.registerStorage("photon_resonance_mfsu", 11, 33554432, 655360000000.0, "tile.icwth.photon_resonance_mfsu.name");
        EnergyStorageManager.registerStorage("extreme_mfsu", 12, 134217728, 2621440000000.0, "tile.icwth.extreme_photonic_mfsu.name");
        EnergyStorageManager.registerStorage("spectral_mfsu", 13, 536870912, 10485760000000.0, "tile.icwth.spectral_mfsu.name");
        EnergyStorageManager.registerStorage("arcsinus_mfsu", 14, 2147483648.0, 41943040000000.0, "tile.icwth.arcsinus_mfsu.name");
        EnergyStorageManager.registerStorage("diffraction_mfsu", 15, 8589934592.0, 167772160000000.0, "tile.icwth.diffraction_mfsu.name");
        EnergyStorageManager.registerStorage("dispersion_mfsu", 16, 34359738368.0, 671088640000000.0, "tile.icwth.dispersion_mfsu.name");
        EnergyStorageManager.registerStorage("graviton_mfsu", 17, 137438953472.0, 2684354560000000.0, "tile.icwth.graviton_mfsu.name");
        EnergyStorageManager.registerStorage("omega_mfsu", 18, 549755813888.0, 10737418240000000.0, "tile.icwth.omega_mfsu.name");
        EnergyStorageManager.registerStorage("photonic_mfsu", 19, 2199023255552.0, 42949672960000000.0, "tile.icwth.photonic_mfsu.name");
        EnergyStorageManager.registerStorage("vector_mfsu", 20, 8796093022208.0, 171798691840000000.0, "tile.icwth.vector fio_mfsu.name");
        EnergyStorageManager.registerStorage("brauthem_mfsu", 21, 35184372088832.0, 687194767360000000.0, "tile.icwth.brauthem_mfsu.name");
        EnergyStorageManager.registerStorage("loli_mfsu", 22, 140737488355328.0, 2748779069440000000.0, "tile.icwth.loli_mfsu.name");
        EnergyStorageManager.registerStorage("hell_yeah_mfsu", 22, Long.MAX_VALUE, Long.MAX_VALUE, "tile.icwth.hell_yeah_mfsu.name");


        SolarPanelManager.registerPanel("intermediate_solar", 6, 16384, 40000000, "tile.icwth.intermediate.name");
        SolarPanelManager.registerPanel("superior_solar", 7, 65536, 160000000, "tile.icwth.superior.name");
        SolarPanelManager.registerPanel("what_the_hell_panel", 8, 262144, 640000000, "tile.icwth.what_the_hell_panel.name");
        SolarPanelManager.registerPanel("photon_resonance_solar", 9, 1048576, 2560000000.0, "tile.icwth.photon_resonance.name");
        SolarPanelManager.registerPanel("extreme_solar", 10, 4194304, 10240000000.0, "tile.icwth.extreme.name");

        SolarPanelManager.registerPanel("spectral_solar", 11, 16777216, 40960000000.0, "tile.icwth.spectral_solar.name");
        SolarPanelManager.registerPanel("arcsinus_solar", 12, 67108864, 163840000000.0, "tile.icwth.arcsinus_solar_panel.name");
        SolarPanelManager.registerPanel("diffraction_solar", 13, 268435456, 655360000000.0, "tile.icwth.diffraction_panel.name");
        SolarPanelManager.registerPanel("dispersion_solar", 14, 1073741824, 2621440000000.0, "tile.icwth.dispersion_solar_panel.name");
        SolarPanelManager.registerPanel("graviton_solar", 15, 4294967296.0, 10485760000000.0, "tile.icwth.graviton_solar_panel.name");
        SolarPanelManager.registerPanel("omega_solar", 16, 17179869184.0, 41943040000000.0, "tile.icwth.omega_solar_panel.name");
        SolarPanelManager.registerPanel("photonic_solar", 17, 68719476736.0, 167772160000000.0, "tile.icwth.photonic_solar_panel.name");
        SolarPanelManager.registerPanel("vector_solar", 18, 274877906944.0, 671088640000000.0, "tile.icwth.vector_solar_panel.name");
        SolarPanelManager.registerPanel("brauthem_solar", 19, 1099511627776.0, 2684354560000000.0, "tile.icwth.brauthem_solar_panel.name");
        SolarPanelManager.registerPanel("loli_solar", 20, 4398046511104.0, 10737418240000000.0, "tile.icwth.loli_solar_panel.name");
        SolarPanelManager.registerPanel("hell_yeah_solar", 21, 17592186044416.0, 42949672960000000.0, "tile.icwth.hell_yeah_solar.name");

        MinecraftForge.EVENT_BUS.register(RecipeInitializer.class);
        MinecraftForge.EVENT_BUS.register(RecipeSynchronizer.class);

        HyperStorageManager.registerHyperStorage("hyper_storage_unit", Integer.MAX_VALUE, Double.MAX_VALUE, "tile.icwth.hyper_storage_unit.name");

    }








    @SideOnly(Side.CLIENT)
    public static void initModels() {
        //   BlockLoliSolarPanel.initModel();
        RecipeRegistration.registerRecipes();


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

    @EventHandler
    public void init(FMLInitializationEvent event) {
        GameRegistry.registerTileEntity(SolarPanelManager.SolarPanelTile.class,
                new ResourceLocation(MODID, "solar_panel_tile"));
        GameRegistry.registerTileEntity(EnergyTrashCanTile.class, "tileEntityEnergyTrash");
        GameRegistry.registerTileEntity(TileChinaSolarPanel.class, "tileEntityChinaSolar");
        GameRegistry.registerTileEntity(TileHyperStorage.class, new ResourceLocation(MODID, "hyper_storage_tile"));
        GameRegistry.registerTileEntity(TileEntityMolecularTransformer.class, new ResourceLocation(MODID, "molecular_transformer_tile"));
        GameRegistry.registerTileEntity(TileEntityAdvancedMolecularTransformer.class, new ResourceLocation(MODID, "advanced_molecular_transformer_tile"));

        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMolecularTransformer.class, new MolecularTransformerTESR());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAdvancedMolecularTransformer.class, new AdvancedMolecularTransformerTESR());

        RecipeInitializer.init(event);
        RecipeSynchronizer.init();

//        GameRegistry.registerTileEntity(
//                BlockEnergyCoreUltra.TileEntityEnergyCoreExtreme.class,
//                MODID + ":energy_core_extreme"
//        );


    }

    @EventHandler
    // postInit "Handle interaction with other mods, complete your setup based on this." (Remove if not needed)
    public void postInit(FMLPostInitializationEvent event) {
        TeBlock.registerTeMappings();
        removeMolecularTransformerRecipe();


    }



    @EventHandler
    // register server commands in this event handler (Remove if not needed)
    public void serverStarting(FMLServerStartingEvent event) {
    }
    private void removeMolecularTransformerRecipe() {
        System.out.println("[YourMod] Начинаю процесс удаления рецепта молекулярного трансформера...");

        // Удаление через Forge Registry - самый надежный способ
        try {
            boolean removed = removeViaForgeRegistry();
            if (removed) {
                System.out.println("[YourMod] Рецепт молекулярного трансформера успешно удален через Forge Registry!");
                return;
            }
        } catch (Exception e) {
            System.out.println("[YourMod] Не удалось удалить рецепт через Forge Registry: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("[YourMod] Не удалось найти и удалить рецепт молекулярного трансформера!");
    }
    private boolean removeViaForgeRegistry() {
        // Получаем реестр рецептов
        ForgeRegistry<IRecipe> recipeRegistry = (ForgeRegistry<IRecipe>) ForgeRegistries.RECIPES;
        boolean removed = false;

        System.out.println("[YourMod] Поиск рецепта молекулярного трансформера в реестре Forge...");

        // Перебираем все рецепты и ищем молекулярный трансформер
        for (IRecipe recipe : recipeRegistry.getValuesCollection()) {
            ItemStack output = recipe.getRecipeOutput();

            // Пропускаем пустые результаты
            if (output.isEmpty() || output.getItem() == null || output.getItem().getRegistryName() == null) {
                continue;
            }

            String registryName = output.getItem().getRegistryName().toString();
            String unlocalizedName = output.getTranslationKey();
            String displayName = output.getDisplayName();

            System.out.println("[YourMod] Проверяю рецепт: " + registryName + " / " + unlocalizedName + " / " + displayName);

            // Проверяем, является ли результат молекулярным трансформером
            if (registryName.contains("advancedsolarpanels") &&
                    (unlocalizedName.contains("molecular_transformer") ||
                            displayName.toLowerCase().contains("molecular transformer") ||
                            displayName.toLowerCase().contains("молекулярный трансформер"))) {

                System.out.println("[YourMod] Найден рецепт молекулярного трансформера: " + recipe.getRegistryName());

                // Удаляем рецепт
                recipeRegistry.remove(recipe.getRegistryName());
                removed = true;
                break;
            }
        }

        return removed;
    }

}
