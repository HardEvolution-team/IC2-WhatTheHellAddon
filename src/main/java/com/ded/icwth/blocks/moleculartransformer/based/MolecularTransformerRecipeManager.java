package com.ded.icwth.blocks.moleculartransformer.based;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.ItemStack;

/**
 * Менеджер рецептов для молекулярного сборщика.
 * Хранит все доступные рецепты и предоставляет методы для их добавления и получения.
 */
public class MolecularTransformerRecipeManager {
    private static final MolecularTransformerRecipeManager INSTANCE = new MolecularTransformerRecipeManager();
    private final List<MolecularTransformerRecipe> recipes = new ArrayList<>();

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
     * Добавить новый рецепт в менеджер.
     * 
     * @param input Входной предмет
     * @param output Выходной предмет
     * @param energyRequired Требуемая энергия в EU
     * @return Созданный рецепт
     */
    public MolecularTransformerRecipe addRecipe(ItemStack input, ItemStack output, int energyRequired) {
        if (input.isEmpty() || output.isEmpty() || energyRequired <= 0) {
            return null;
        }
        
        MolecularTransformerRecipe recipe = new MolecularTransformerRecipe(input, output, energyRequired);
        recipes.add(recipe);
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
}
