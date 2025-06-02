package com.ded.icwth;

import com.ded.icwth.blocks.moleculartransformer.advanced.AdvancedMolecularTransformerRecipeManager;
import com.ded.icwth.blocks.moleculartransformer.based.MolecularTransformerRecipeManager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class RecipeRegistration {
    public static void registerRecipes() {
        MolecularTransformerRecipeManager baseManager = MolecularTransformerRecipeManager.getInstance();
        AdvancedMolecularTransformerRecipeManager advancedManager = AdvancedMolecularTransformerRecipeManager.getInstance();



        baseManager.addRecipe(
                new ItemStack(Blocks.COBBLESTONE),
                new ItemStack(Blocks.STONE),
                10000000
        );


    }
}