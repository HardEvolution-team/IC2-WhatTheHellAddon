package com.ded.icwth.blocks.moleculartransformer.advanced;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

/**
 * Менеджер рецептов для улучшенного молекулярного сборщика.
 * Хранит все доступные рецепты и предоставляет методы для их добавления и получения.
 */
public class AdvancedMolecularTransformerRecipeManager {
    private static final AdvancedMolecularTransformerRecipeManager INSTANCE = new AdvancedMolecularTransformerRecipeManager();
    private final List<AdvancedMolecularTransformerRecipe> recipes = new ArrayList<>();

    private AdvancedMolecularTransformerRecipeManager() {
        // Приватный конструктор для синглтона
    }

    /**
     * Получить экземпляр менеджера рецептов.
     *
     * @return Экземпляр менеджера рецептов
     */
    public static AdvancedMolecularTransformerRecipeManager getInstance() {
        return INSTANCE;
    }

    /**
     * Добавить новый рецепт в менеджер.
     *
     * @param input Входной предмет
     * @param output Выходной предмет
     * @param energyRequired Требуемая энергия в EU
     * @return Созданный рецепт
     */
    public AdvancedMolecularTransformerRecipe addRecipe(ItemStack input, ItemStack output, double energyRequired) {
        if (input.isEmpty() || output.isEmpty() || energyRequired <= 0) {
            return null;
        }

        AdvancedMolecularTransformerRecipe recipe = new AdvancedMolecularTransformerRecipe(input, output, energyRequired);
        recipes.add(recipe);
        return recipe;
    }

    /**
     * Найти рецепт для указанного входного предмета.
     *
     * @param input Входной предмет
     * @return Найденный рецепт или null, если рецепт не найден
     */
    public AdvancedMolecularTransformerRecipe findRecipe(ItemStack input) {
        if (input.isEmpty()) {
            return null;
        }

        for (AdvancedMolecularTransformerRecipe recipe : recipes) {
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
    public List<AdvancedMolecularTransformerRecipe> getRecipes() {
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
    public boolean removeRecipe(AdvancedMolecularTransformerRecipe recipe) {
        return recipes.remove(recipe);
    }
}
