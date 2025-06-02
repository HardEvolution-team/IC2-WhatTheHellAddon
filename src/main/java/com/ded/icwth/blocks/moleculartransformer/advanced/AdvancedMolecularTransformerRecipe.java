package com.ded.icwth.blocks.moleculartransformer.advanced;

import net.minecraft.item.ItemStack;

/**
 * Класс, представляющий рецепт для улучшенного молекулярного сборщика.
 * Содержит входной предмет, выходной предмет и количество энергии, необходимое для трансформации.
 */
public class AdvancedMolecularTransformerRecipe {
    private final ItemStack input;
    private final ItemStack output;
    private final double energyRequired;

    public AdvancedMolecularTransformerRecipe(ItemStack input, ItemStack output, double energyRequired) {
        this.input = input.copy();
        this.output = output.copy();
        this.energyRequired = energyRequired;
    }

    /**
     * Проверяет, соответствует ли входной предмет данному рецепту.
     *
     * @param stack Проверяемый предмет
     * @return true, если предмет соответствует рецепту, иначе false
     */
    public boolean matches(ItemStack stack) {
        if (stack.isEmpty() || input.isEmpty()) {
            return false;
        }

        return stack.getItem() == input.getItem() &&
                (input.getMetadata() == 32767 || stack.getMetadata() == input.getMetadata()) &&
                (!input.hasTagCompound() || ItemStack.areItemStackTagsEqual(stack, input));
    }

    /**
     * Получить копию входного предмета.
     *
     * @return Копия входного предмета
     */
    public ItemStack getInput() {
        return input.copy();
    }

    /**
     * Получить копию выходного предмета.
     *
     * @return Копия выходного предмета
     */
    public ItemStack getOutput() {
        return output.copy();
    }

    /**
     * Получить количество энергии, необходимое для трансформации.
     *
     * @return Количество энергии в EU
     */
    public double getEnergyRequired() {
        return energyRequired;
    }
}
