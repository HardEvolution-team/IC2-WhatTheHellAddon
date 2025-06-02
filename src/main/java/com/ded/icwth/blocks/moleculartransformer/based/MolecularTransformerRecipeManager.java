package com.ded.icwth.blocks.moleculartransformer.based;

import java.util.ArrayList;
import java.util.List;

import com.ded.icwth.blocks.moleculartransformer.advanced.AdvancedMolecularTransformerRecipeManager;
import net.minecraft.item.ItemStack;

/**
 * Менеджер рецептов для молекулярного сборщика.
 * Хранит все доступные рецепты и предоставляет методы для их добавления и получения.
 */
public class MolecularTransformerRecipeManager {
    private static final MolecularTransformerRecipeManager INSTANCE = new MolecularTransformerRecipeManager();
    private final List<MolecularTransformerRecipe> recipes = new ArrayList<>();
    private boolean initialized = false;

    private MolecularTransformerRecipeManager() {
        // Приватный конструктор для синглтона
    }

    /**
     * Получить экземпляр менеджера рецептов.
     *
     * @return Экземпляр менеджера рецептов
     */
    public static MolecularTransformerRecipeManager getInstance() {
        return INSTANCE;
    }

    /**
     * Инициализирует стандартные рецепты.
     * Вызывается один раз при загрузке мода.
     */
    public void initializeRecipes() {
        if (initialized) {
            return;
        }

        // Здесь можно добавить стандартные рецепты
        // Например:
        // addRecipe(new ItemStack(Items.IRON_INGOT), new ItemStack(Items.GOLD_INGOT), 5000);

        initialized = true;
    }

    /**
     * Добавить новый рецепт в менеджер.
     *
     * @param input Входной предмет
     * @param output Выходной предмет
     * @param energyRequired Требуемая энергия в EU
     * @return Созданный рецепт
     */
    public MolecularTransformerRecipe addRecipe(ItemStack input, ItemStack output, double energyRequired) {
        if (input.isEmpty() || output.isEmpty() || energyRequired <= 0) {
            return null;
        }

        // Проверяем, не существует ли уже такой рецепт
        for (MolecularTransformerRecipe recipe : recipes) {
            if (recipe.matches(input) && ItemStack.areItemsEqual(recipe.getOutput(), output)) {
                return recipe; // Рецепт уже существует
            }
        }

        MolecularTransformerRecipe recipe = new MolecularTransformerRecipe(input, output, energyRequired);
        recipes.add(recipe);

        // Автоматически добавляем рецепт в улучшенную версию
        try {
            AdvancedMolecularTransformerRecipeManager.getInstance()
                    .addRecipe(input, output, energyRequired);
        } catch (Exception e) {
            System.err.println("Ошибка при добавлении рецепта в AdvancedMolecularTransformerRecipeManager: " + e.getMessage());
            // Продолжаем выполнение, даже если улучшенный менеджер недоступен
        }

        return recipe;
    }

    /**
     * Найти рецепт для указанного входного предмета.
     *
     * @param input Входной предмет
     * @return Найденный рецепт или null, если рецепт не найден
     */
    public MolecularTransformerRecipe findRecipe(ItemStack input) {
        if (input.isEmpty()) {
            return null;
        }

        for (MolecularTransformerRecipe recipe : recipes) {
            if (recipe.matches(input)) {
                return recipe;
            }
        }

        return null;
    }

    /**
     * Получить список всех рецептов.
     *
     * @return Список всех рецептов
     */
    public List<MolecularTransformerRecipe> getRecipes() {
        return new ArrayList<>(recipes);
    }

    /**
     * Удалить все рецепты.
     */
    public void clearRecipes() {
        recipes.clear();
    }

    /**
     * Удалить рецепт.
     *
     * @param recipe Рецепт для удаления
     * @return true, если рецепт был удален, иначе false
     */
    public boolean removeRecipe(MolecularTransformerRecipe recipe) {
        return recipes.remove(recipe);
    }

    /**
     * Проверяет, инициализирован ли менеджер рецептов.
     *
     * @return true, если менеджер инициализирован, иначе false
     */
    public boolean isInitialized() {
        return initialized;
    }
}
