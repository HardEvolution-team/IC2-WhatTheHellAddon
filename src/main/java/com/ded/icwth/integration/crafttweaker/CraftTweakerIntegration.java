package com.ded.icwth.integration.crafttweaker;



import com.ded.icwth.blocks.moleculartransformer.based.MolecularTransformerRecipeManager;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

/**
 * Интеграция с CraftTweaker для добавления рецептов молекулярного сборщика через скрипты.
 */
@ZenClass("mods.icwth.MolecularAssembler")
@ZenRegister
public class CraftTweakerIntegration {

    /**
     * Добавить рецепт в молекулярный сборщик.
     *
     * @param output Выходной предмет
     * @param input Входной предмет
     * @param energyRequired Требуемая энергия в EU
     */
    @ZenMethod
    public static void addRecipe(IItemStack output, IItemStack input, int energyRequired) {
        if (output == null || input == null || energyRequired <= 0) {
            CraftTweakerAPI.logError("Invalid recipe parameters for Molecular Assembler");
            return;
        }

        CraftTweakerAPI.apply(new AddRecipeAction(input, output, energyRequired));
    }

    /**
     * Удалить все рецепты из молекулярного сборщика.
     */
    @ZenMethod
    public static void removeAllRecipes() {
        CraftTweakerAPI.apply(new RemoveAllRecipesAction());
    }

    /**
     * Класс действия для добавления рецепта.
     */
    private static class AddRecipeAction implements IAction {
        private final IItemStack input;
        private final IItemStack output;
        private final int energyRequired;

        public AddRecipeAction(IItemStack input, IItemStack output, int energyRequired) {
            this.input = input;
            this.output = output;
            this.energyRequired = energyRequired;
        }

        @Override
        public void apply() {
            ItemStack inputStack = (ItemStack) input.getInternal();
            ItemStack outputStack = (ItemStack) output.getInternal();
            MolecularTransformerRecipeManager.getInstance().addRecipe(inputStack, outputStack, energyRequired);
        }

        @Override
        public String describe() {
            return "Adding Molecular Assembler recipe for " + input.getDisplayName() + " -> " + output.getDisplayName();
        }
    }

    /**
     * Класс действия для удаления всех рецептов.
     */
    private static class RemoveAllRecipesAction implements IAction {
        @Override
        public void apply() {
            MolecularTransformerRecipeManager.getInstance().clearRecipes();
        }

        @Override
        public String describe() {
            return "Removing all Molecular Assembler recipes";
        }
    }
}
