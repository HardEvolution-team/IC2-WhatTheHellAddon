package com.ded.icwth.integration.jei;

import com.ded.icwth.blocks.ModBlocks;


import com.ded.icwth.blocks.moleculartransformer.advanced.gui.GuiAdvancedMolecularTransformer;
import com.ded.icwth.blocks.moleculartransformer.based.MolecularTransformerRecipe;
import com.ded.icwth.blocks.moleculartransformer.based.MolecularTransformerRecipeManager;

import com.ded.icwth.blocks.moleculartransformer.based.gui.GuiMolecularTransformer;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс для интеграции с JEI (Just Enough Items).
 * Регистрирует рецепты молекулярного сборщика в JEI.
 */
@JEIPlugin
public class JEICompat implements IModPlugin {

    public static final String MOLECULAR_ASSEMBLER_UID = "icwth.molecular_assembler";

    @Override
    public void register(IModRegistry registry) {
        // Регистрируем область клика в GUI для перехода к рецептам
        registry.addRecipeClickArea(GuiMolecularTransformer.class, 22, 40, 11, 32, MOLECULAR_ASSEMBLER_UID);

        // Создаем категорию рецептов
        IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
        MolecularTransformerCategory category = new MolecularTransformerCategory(guiHelper);

        // Регистрируем категорию
        registry.addRecipeCategories(category);

        // Получаем список рецептов и создаем обертки
        List<MolecularTransformerRecipeWrapper> recipeWrappers = new ArrayList<>();
        for (MolecularTransformerRecipe recipe : MolecularTransformerRecipeManager.getInstance().getRecipes()) {
            recipeWrappers.add(new MolecularTransformerRecipeWrapper(recipe));
        }

        // Регистрируем рецепты
        registry.addRecipes(recipeWrappers, MOLECULAR_ASSEMBLER_UID);

        // Регистрируем блок как катализатор для рецептов
        registry.addRecipeCatalyst(new ItemStack(ModBlocks.MolecularTransformer), MOLECULAR_ASSEMBLER_UID);
        registry.addRecipeCatalyst(new ItemStack(ModBlocks.AdvancedMolecularTransformer), MOLECULAR_ASSEMBLER_UID);

    }
}