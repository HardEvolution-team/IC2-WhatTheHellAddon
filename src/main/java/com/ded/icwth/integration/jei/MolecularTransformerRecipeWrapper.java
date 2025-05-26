package com.ded.icwth.integration.jei;


import com.ded.icwth.blocks.moleculartransformer.based.MolecularTransformerRecipe;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;

import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;

/**
 * Обертка рецепта молекулярного сборщика для JEI.
 * Предоставляет данные о рецепте для отображения в JEI.
 */
public class MolecularTransformerRecipeWrapper implements IRecipeWrapper {

    private final MolecularTransformerRecipe recipe;
    private final String inputName;
    private final String outputName;
    private final String energyText;

    public MolecularTransformerRecipeWrapper(MolecularTransformerRecipe recipe) {
        this.recipe = recipe;

        // Получаем имена предметов и энергию для отображения
        this.inputName = I18n.format("jei.icwth.input") + " " + recipe.getInput().getDisplayName();
        this.outputName = I18n.format("jei.icwth.output") + " " + recipe.getOutput().getDisplayName();

        // Форматируем энергию с разделителями тысяч
        NumberFormat format = NumberFormat.getIntegerInstance();
        this.energyText = I18n.format("jei.icwth.energy_required") + " " + format.format(recipe.getEnergyRequired()) + " EU";
    }
    @Override
    public void getIngredients(IIngredients ingredients) {
        // Устанавливаем входные и выходные предметы
        ingredients.setInput(ItemStack.class, recipe.getInput());
        ingredients.setOutput(ItemStack.class, recipe.getOutput());
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        // Рисуем информацию о рецепте, соответствуя XML
        minecraft.fontRenderer.drawString(inputName, 50, 16, 0xFFFFFF, true); // Right-aligned
        minecraft.fontRenderer.drawString(outputName, 50, 28, 0xFFFFFF, true); // Right-aligned
        minecraft.fontRenderer.drawString(energyText, 50, 40, 0xFFFFFF, true); // Right-aligned
    }

    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        return Collections.emptyList();
    }

    @Override
    public boolean handleClick(Minecraft minecraft, int mouseX, int mouseY, int mouseButton) {
        return false;
    }
}