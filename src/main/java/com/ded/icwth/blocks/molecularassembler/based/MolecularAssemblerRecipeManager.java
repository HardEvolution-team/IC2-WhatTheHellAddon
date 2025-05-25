package com.ded.icwth.blocks.molecularassembler.based;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.ItemStack;

/**
 * Менеджер рецептов для молекулярного сборщика.
 * Хранит все доступные рецепты и предоставляет методы для их добавления и получения.
 */
public class MolecularAssemblerRecipeManager {
    private static final MolecularAssemblerRecipeManager INSTANCE = new MolecularAssemblerRecipeManager();
    private final List<MolecularAssemblerRecipe> recipes = new ArrayList<>();

    private MolecularAssemblerRecipeManager() {
        // Приватный конструктор для синглтона
    }

    /**
     * Получить экземпляр менеджера рецептов.
     * 
     * @return Экземпляр менеджера рецептов
     */
    public static MolecularAssemblerRecipeManager getInstance() {
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
    public MolecularAssemblerRecipe addRecipe(ItemStack input, ItemStack output, int energyRequired) {
        if (input.isEmpty() || output.isEmpty() || energyRequired <= 0) {
            return null;
        }
        
        MolecularAssemblerRecipe recipe = new MolecularAssemblerRecipe(input, output, energyRequired);
        recipes.add(recipe);
        return recipe;
    }

    /**
     * Найти рецепт для указанного входного предмета.
     * 
     * @param input Входной предмет
     * @return Найденный рецепт или null, если рецепт не найден
     */
    public MolecularAssemblerRecipe findRecipe(ItemStack input) {
        if (input.isEmpty()) {
            return null;
        }
        
        for (MolecularAssemblerRecipe recipe : recipes) {
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
    public List<MolecularAssemblerRecipe> getRecipes() {
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
    public boolean removeRecipe(MolecularAssemblerRecipe recipe) {
        return recipes.remove(recipe);
    }
}
