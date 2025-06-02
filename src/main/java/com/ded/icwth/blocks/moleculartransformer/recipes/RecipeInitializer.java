package com.ded.icwth.blocks.moleculartransformer.recipes;

import com.ded.icwth.blocks.moleculartransformer.based.MolecularTransformerRecipeManager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Класс для инициализации рецептов молекулярного сборщика.
 * Регистрирует стандартные рецепты при загрузке мода.
 */
@Mod.EventBusSubscriber
public class RecipeInitializer {

    /**
     * Инициализирует рецепты на этапе инициализации мода.
     *
     * @param event Событие инициализации
     */
    public static void init(FMLInitializationEvent event) {
        registerRecipes();
    }

    /**
     * Обработчик события входа игрока в игру.
     * Гарантирует, что рецепты инициализированы даже если основная инициализация не сработала.
     */
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!MolecularTransformerRecipeManager.getInstance().isInitialized()) {
            registerRecipes();
        }
    }

    /**
     * Регистрирует стандартные рецепты.
     */
    private static void registerRecipes() {
        MolecularTransformerRecipeManager manager = MolecularTransformerRecipeManager.getInstance();

        // Проверяем, не инициализированы ли уже рецепты
        if (manager.isInitialized()) {
            return;
        }


        try {
            // Портированные рецепты из пользовательского списка

            // minecraft:skull@1; minecraft:nether_star; 250000000
            addRecipe(manager, "minecraft:skull", 1, "minecraft:nether_star", 0, 1, 250000000);

            // minecraft:iron_ingot@*; ic2:misc_resource#iridium_ore; 9000000
            addRecipe(manager, "minecraft:iron_ingot", OreDictionary.WILDCARD_VALUE, "ic2:misc_resource", getMetaFromNBT("iridium_ore"), 1, 9000000);

            // minecraft:netherrack@*; minecraft:gunpowder*2; 70000
            addRecipe(manager, "minecraft:netherrack", OreDictionary.WILDCARD_VALUE, "minecraft:gunpowder", 0, 2, 70000);

            // minecraft:sand@*; minecraft:gravel; 50000
            addRecipe(manager, "minecraft:sand", OreDictionary.WILDCARD_VALUE, "minecraft:gravel", 0, 1, 50000);

            // minecraft:dirt@*; minecraft:clay; 50000
            addRecipe(manager, "minecraft:dirt", OreDictionary.WILDCARD_VALUE, "minecraft:clay", 0, 1, 50000);

            // minecraft:coal@1; minecraft:coal@0; 60000
            addRecipe(manager, "minecraft:coal", 1, "minecraft:coal", 0, 1, 60000);

            // minecraft:glowstone_dust@*; advanced_solar_panels:crafting@1; 1000000
            addRecipe(manager, "minecraft:glowstone_dust", OreDictionary.WILDCARD_VALUE, "advanced_solar_panels:crafting", 1, 1, 1000000);

            // minecraft:glowstone@*; advanced_solar_panels:crafting@0; 9000000
            addRecipe(manager, "minecraft:glowstone", OreDictionary.WILDCARD_VALUE, "advanced_solar_panels:crafting", 0, 1, 9000000);

            // minecraft:wool@4; minecraft:glowstone; 500000
            addRecipe(manager, "minecraft:wool", 4, "minecraft:glowstone", 0, 1, 500000);

            // minecraft:wool@11; minecraft:lapis_block; 500000
            addRecipe(manager, "minecraft:wool", 11, "minecraft:lapis_block", 0, 1, 500000);

            // minecraft:wool@14; minecraft:redstone_block; 500000
            addRecipe(manager, "minecraft:wool", 14, "minecraft:redstone_block", 0, 1, 500000);

            // minecraft:dye@4; OreDict:gemSapphire; 5000000
            addOreDictRecipe(manager, "minecraft:dye", 4, "gemSapphire", 1, 5000000);

            // minecraft:redstone@*; OreDict:gemRuby; 5000000
            addOreDictRecipe(manager, "minecraft:redstone", OreDictionary.WILDCARD_VALUE, "gemRuby", 1, 5000000);

            // minecraft:coal@0; ic2:crafting:19 (industrial_diamond); 9000000
            addRecipe(manager, "minecraft:coal", 0, "ic2:crafting", 19, 1, 9000000);

            // ic2:crafting:19 (industrial_diamond); minecraft:diamond; 1000000
            addRecipe(manager, "ic2:crafting", 19, "minecraft:diamond", 0, 1, 1000000);

            // OreDict:dustTitanium; OreDict:dustChrome; 500000
            addOreDictToOreDictRecipe(manager, "dustTitanium", "dustChrome", 1, 500000);

            // OreDict:ingotTitanium; OreDict:ingotChrome; 500000
            addOreDictToOreDictRecipe(manager, "ingotTitanium", "ingotChrome", 1, 500000);

            // OreDict:gemNetherQuartz; OreDict:gemCertusQuartz; 500000
            addOreDictToOreDictRecipe(manager, "gemNetherQuartz", "gemCertusQuartz", 1, 500000);

            // OreDict:ingotCopper; OreDict:ingotNickel; 300000
            addOreDictToOreDictRecipe(manager, "ingotCopper", "ingotNickel", 1, 300000);

            // OreDict:ingotTin; OreDict:ingotSilver; 500000
            addOreDictToOreDictRecipe(manager, "ingotTin", "ingotSilver", 1, 500000);

            // OreDict:ingotSilver; OreDict:ingotGold; 500000
            addOreDictToOreDictRecipe(manager, "ingotSilver", "ingotGold", 1, 500000);

            // OreDict:ingotGold; OreDict:ingotPlatinum; 9000000
            addOreDictToOreDictRecipe(manager, "ingotGold", "ingotPlatinum", 1, 9000000);

        } catch (Exception e) {
            System.err.println("Ошибка при инициализации рецептов: " + e.getMessage());
            e.printStackTrace();
        }

        // Отмечаем, что рецепты инициализированы
        manager.initializeRecipes();


    }

    /**
     * Добавляет рецепт с обычными предметами.
     *
     * @param manager Менеджер рецептов
     * @param inputId ID входного предмета
     * @param inputMeta Метаданные входного предмета
     * @param outputId ID выходного предмета
     * @param outputMeta Метаданные выходного предмета
     * @param outputCount Количество выходных предметов
     * @param energy Требуемая энергия
     */
    private static void addRecipe(MolecularTransformerRecipeManager manager, String inputId, int inputMeta,
                                  String outputId, int outputMeta, int outputCount, double energy) {
        try {
            Item inputItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(inputId));
            Item outputItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(outputId));

            if (inputItem == null || outputItem == null) {
                System.err.println("Ошибка: не найден предмет " +
                        (inputItem == null ? inputId : outputId));
                return;
            }

            ItemStack input = new ItemStack(inputItem, 1, inputMeta);
            ItemStack output = new ItemStack(outputItem, outputCount, outputMeta);

            manager.addRecipe(input, output, energy);
            System.out.println("Добавлен рецепт: " + inputId + "@" + inputMeta +
                    " -> " + outputId + "@" + outputMeta + "*" + outputCount +
                    " (" + energy + " EU)");
        } catch (Exception e) {
            System.err.println("Ошибка при добавлении рецепта " + inputId + " -> " + outputId + ": " + e.getMessage());
        }
    }

    /**
     * Добавляет рецепт с входным предметом и выходным предметом из OreDictionary.
     *
     * @param manager Менеджер рецептов
     * @param inputId ID входного предмета
     * @param inputMeta Метаданные входного предмета
     * @param outputOreDict Название выходного предмета в OreDictionary
     * @param outputCount Количество выходных предметов
     * @param energy Требуемая энергия
     */
    private static void addOreDictRecipe(MolecularTransformerRecipeManager manager, String inputId, int inputMeta,
                                         String outputOreDict, int outputCount, double energy) {
        try {
            Item inputItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(inputId));

            if (inputItem == null) {
                System.err.println("Ошибка: не найден предмет " + inputId);
                return;
            }

            ItemStack input = new ItemStack(inputItem, 1, inputMeta);

            // Получаем первый предмет из OreDictionary
            ItemStack output = getFirstItemFromOreDict(outputOreDict);

            if (output.isEmpty()) {
                System.err.println("Ошибка: не найден предмет в OreDictionary " + outputOreDict);
                return;
            }

            output.setCount(outputCount);

            manager.addRecipe(input, output, energy);
            System.out.println("Добавлен рецепт: " + inputId + "@" + inputMeta +
                    " -> OreDict:" + outputOreDict + "*" + outputCount +
                    " (" + energy + " EU)");
        } catch (Exception e) {
            System.err.println("Ошибка при добавлении рецепта " + inputId + " -> OreDict:" + outputOreDict + ": " + e.getMessage());
        }
    }

    /**
     * Добавляет рецепт с входным и выходным предметами из OreDictionary.
     *
     * @param manager Менеджер рецептов
     * @param inputOreDict Название входного предмета в OreDictionary
     * @param outputOreDict Название выходного предмета в OreDictionary
     * @param outputCount Количество выходных предметов
     * @param energy Требуемая энергия
     */
    private static void addOreDictToOreDictRecipe(MolecularTransformerRecipeManager manager, String inputOreDict,
                                                  String outputOreDict, int outputCount, double energy) {
        try {
            // Получаем первые предметы из OreDictionary
            ItemStack input = getFirstItemFromOreDict(inputOreDict);
            ItemStack output = getFirstItemFromOreDict(outputOreDict);

            if (input.isEmpty() || output.isEmpty()) {
                System.err.println("Ошибка: не найден предмет в OreDictionary " +
                        (input.isEmpty() ? inputOreDict : outputOreDict));
                return;
            }

            output.setCount(outputCount);

            manager.addRecipe(input, output, energy);
            System.out.println("Добавлен рецепт: OreDict:" + inputOreDict +
                    " -> OreDict:" + outputOreDict + "*" + outputCount +
                    " (" + energy + " EU)");
        } catch (Exception e) {
            System.err.println("Ошибка при добавлении рецепта OreDict:" + inputOreDict +
                    " -> OreDict:" + outputOreDict + ": " + e.getMessage());
        }
    }

    /**
     * Получает первый предмет из OreDictionary по названию.
     *
     * @param oreDictName Название в OreDictionary
     * @return Первый предмет из OreDictionary или пустой ItemStack, если предмет не найден
     */
    private static ItemStack getFirstItemFromOreDict(String oreDictName) {
        if (!OreDictionary.doesOreNameExist(oreDictName)) {
            return ItemStack.EMPTY;
        }

        java.util.List<ItemStack> ores = OreDictionary.getOres(oreDictName);
        if (ores.isEmpty()) {
            return ItemStack.EMPTY;
        }

        return ores.get(0).copy();
    }

    /**
     * Получает метаданные из NBT-тега.
     * Это упрощенная реализация, в реальном коде нужно использовать NBT-теги.
     *
     * @param nbtName Название NBT-тега
     * @return Метаданные (по умолчанию 0)
     */
    private static int getMetaFromNBT(String nbtName) {

        switch (nbtName) {
            case "iridium_ore":
                return 1;
            case "industrial_diamond":
                return 2;
            default:
                return 0;
        }
    }

    /**
     * Метод для добавления пользовательских рецептов.
     * Может быть вызван из других модов или через API.
     *
     * @param input Входной предмет
     * @param output Выходной предмет
     * @param energyRequired Требуемая энергия в EU
     * @return true, если рецепт успешно добавлен, иначе false
     */
    public static boolean addCustomRecipe(ItemStack input, ItemStack output, double energyRequired) {
        if (input.isEmpty() || output.isEmpty() || energyRequired <= 0) {
            return false;
        }

        return MolecularTransformerRecipeManager.getInstance().addRecipe(input, output, energyRequired) != null;
    }
}
