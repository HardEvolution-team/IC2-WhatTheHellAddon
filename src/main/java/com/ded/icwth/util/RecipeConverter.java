// Placeholder for RecipeConverter.java
package com.ded.icwth.util;

import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.Recipes;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class RecipeConverter {

    // Target directory for generated MMCE recipes
    private static final String MMCE_RECIPE_DIR = "../src/main/resources/assets/modularmachinery/recipes/icwth_ultimate_macerator";
    private static final int BATCH_SIZE = 1000;
    private static final int TICKS = 1;
    // Placeholder energy cost per operation (adjust as needed)
    private static final int ENERGY_PER_TICK = 1000000;

    public static void generateMaceratorRecipes() {
        System.out.println("Starting IC2 Macerator recipe conversion for MMCE...");

        // Ensure the target directory exists
        Path recipePath = Paths.get(MMCE_RECIPE_DIR);
        try {
            Files.createDirectories(recipePath);
        } catch (IOException e) {
            System.err.println("Failed to create MMCE recipe directory: " + recipePath);
            e.printStackTrace();
            return;
        }

        // Check if IC2 is loaded
        if (!Loader.isModLoaded("ic2")) {
            System.err.println("IC2 mod not found. Cannot generate recipes.");
            return;
        }

        try {
            // Access IC2 Macerator recipes
            Map<IRecipeInput, List<ItemStack>> recipeMap = (Map<IRecipeInput, List<ItemStack>>) Recipes.macerator.getRecipes();

            if (recipeMap == null || recipeMap.isEmpty()) {
                System.err.println("Could not retrieve IC2 Macerator recipes or recipe map is empty.");
                return;
            }

            System.out.println("Found " + recipeMap.size() + " IC2 Macerator recipes to convert.");

            int recipeCounter = 0;
            for (Map.Entry<IRecipeInput, List<ItemStack>> entry : recipeMap.entrySet()) {
                IRecipeInput input = entry.getKey();
                List<ItemStack> outputs = entry.getValue();

                // We need the primary input ItemStack for naming and quantity
                // IRecipeInput can represent OreDict or specific ItemStacks
                // This simplistic approach takes the first matching ItemStack
                List<ItemStack> inputStacks = input.getInputs();
                if (inputStacks.isEmpty()) {
                    System.err.println("Skipping recipe with empty input list.");
                    continue;
                }
                ItemStack primaryInputStack = inputStacks.get(0); // Use first as representative
                int inputAmount = input.getAmount();

                if (outputs.isEmpty()) {
                    System.err.println("Skipping recipe with no outputs: Input = " + primaryInputStack.getDisplayName());
                    continue;
                }
                ItemStack primaryOutputStack = outputs.get(0); // Use first output for filename

                // Generate a somewhat unique filename
                String inputName = primaryInputStack.getItem().getRegistryName().getPath().replace(":", "_").replace("/", "_");
                String outputName = primaryOutputStack.getItem().getRegistryName().getPath().replace(":", "_").replace("/", "_");
                String recipeFileName = "macerator_" + inputName + "_to_" + outputName + "_" + recipeCounter + ".json";
                recipeCounter++;

                File recipeFile = recipePath.resolve(recipeFileName).toFile();

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(recipeFile))) {
                    writer.write("{\n");
                    writer.write("  \"machinename\": \"icwth_ultimate_macerator\",\n"); // Reference the machine structure name
                    writer.write("  \"name\": \"" + recipeFileName.replace(".json", "") + "\",\n");
                    writer.write("  \"tickrate\": " + TICKS + ",\n");
                    writer.write("  \"crafttime\": " + TICKS + ",\n"); // Redundant with tickrate=1?

                    // --- Inputs --- 
                    writer.write("  \"inputs\": [\n");
                    // Energy Input
                    writer.write("    {\n");
                    writer.write("      \"type\": \"energy\",\n");
                    writer.write("      \"amount\": " + (long)ENERGY_PER_TICK * TICKS + "\n");
                    writer.write("    }");

                    // Item Input(s)
                    // Assuming IRecipeInput maps to a single MMCE item input slot for simplicity
                    // If IRecipeInput uses OreDict, use the OreDict name
                    String inputType = "item";
                    String inputKey = primaryInputStack.getItem().getRegistryName().toString();
                    int inputMeta = primaryInputStack.getMetadata();
                    // Basic OreDict check (may need refinement)
                    if (input.getClass().getSimpleName().contains("RecipeInputOreDict")) {
                        // Need a way to get the OreDict name from IRecipeInput - This requires deeper API knowledge or reflection
                        // For now, fallback to item name, which might be wrong for OreDict inputs
                        System.out.println("Warning: OreDict input detected, using item name as fallback: " + inputKey);
                        // inputType = "ore"; 
                        // inputKey = "ore:<OreDictName>"; // Placeholder
                    }

                    writer.write(",\n    {\n");
                    writer.write("      \"type\": \"" + inputType + "\",\n");
                    writer.write("      \"io-type\": \"input\",\n");
                    if (inputType.equals("item")) {
                        writer.write("      \"item\": \"" + inputKey + "\",\n");
                        writer.write("      \"meta\": " + inputMeta + ",\n");
                    } else { // ore
                        writer.write("      \"ore\": \"" + inputKey + "\",\n"); // Assuming inputKey holds ore:<name>
                    }
                    writer.write("      \"amount\": " + (long)inputAmount * BATCH_SIZE + "\n");
                    writer.write("    }\n");

                    writer.write("  ],\n"); // End Inputs

                    // --- Outputs --- 
                    writer.write("  \"outputs\": [\n");
                    boolean firstOutput = true;
                    for (ItemStack outputStack : outputs) {
                        if (!firstOutput) {
                            writer.write(",\n");
                        }
                        writer.write("    {\n");
                        writer.write("      \"type\": \"item\",\n");
                        writer.write("      \"io-type\": \"output\",\n");
                        writer.write("      \"item\": \"" + outputStack.getItem().getRegistryName().toString() + "\",\n");
                        writer.write("      \"meta\": " + outputStack.getMetadata() + ",\n");
                        writer.write("      \"amount\": " + (long)outputStack.getCount() * BATCH_SIZE + "\n");
                        writer.write("    }");
                        firstOutput = false;
                    }
                    writer.write("\n  ]\n"); // End Outputs

                    writer.write("}\n"); // End Recipe JSON
                } catch (IOException e) {
                    System.err.println("Failed to write recipe file: " + recipeFileName);
                    e.printStackTrace();
                }
            }
            System.out.println("Successfully converted " + recipeCounter + " recipes.");

        } catch (Exception e) {
            System.err.println("An error occurred during IC2 Macerator recipe conversion:");
            e.printStackTrace();
        }
    }
}

