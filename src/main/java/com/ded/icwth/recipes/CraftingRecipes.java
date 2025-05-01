package com.ded.icwth.recipes;

import com.ded.icwth.Tags;
import com.ded.icwth.items.ModItems;
import ic2.api.item.IC2Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CraftingRecipes {



    /** Регистрация крафтов для MFSU */


    /** Выбор схемы для панелей */
    private static ItemStack getCircuitForPanelTier(String name, ItemStack advancedCircuit, ItemStack lapotronCrystal, ItemStack ultimateLappack) {
        if (name.equals("intermediate") || name.equals("intermediate_high") || name.equals("spectral_solar")) {
            return advancedCircuit; // IC2 Exp Advanced Circuit
        } else if (name.equals("advanced_high") || name.equals("superior") || name.equals("what_the_hell_panel")) {
            return lapotronCrystal; // IC2 Exp Lapotron Crystal
        } else if (name.equals("magnetron") || name.equals("photon_resonance") || name.equals("extreme_photonic") ||
                name.equals("arcsinus_solar") || name.equals("diffraction_solar") || name.equals("dispersion_solar")) {
            return new ItemStack(ModItems.hybridCircut); // Твой Hybrid Circuit
        } else if (name.equals("graviton_solar") || name.equals("omega_solar") || name.equals("photonic_solar")) {
            return new ItemStack(ModItems.ultimateHybridCircuit); // Твой Ultimate Hybrid Circuit
        } else if (name.equals("vector_solar") || name.equals("brauthem_solar")) {
            return new ItemStack(ModItems.quantumCircuit); // Твой Quantum Circuit
        } else { // loli_solar
            return ultimateLappack; // GraviSuite Ultimate Lappack
        }
    }

    /** Выбор компонента для панелей */
    private static ItemStack getComponentForPanelTier(String name, ItemStack iridiumPlate, ItemStack gravitonCore) {
        if (name.equals("intermediate") || name.equals("intermediate_high") || name.equals("advanced_high") ||
                name.equals("superior") || name.equals("spectral_solar")) {
            return iridiumPlate; // IC2 Exp Iridium Plate
        } else if (name.equals("what_the_hell_panel") || name.equals("magnetron") || name.equals("photon_resonance") ||
                name.equals("extreme_photonic") || name.equals("arcsinus_solar") || name.equals("diffraction_solar") ||
                name.equals("dispersion_solar")) {
            return new ItemStack(ModItems.doubleDenseIridiumPlate); // Твой Double Dense Iridium Plate
        } else if (name.equals("graviton_solar") || name.equals("omega_solar") || name.equals("photonic_solar")) {
            return new ItemStack(ModItems.tripleDenseIridiumPlate); // Твой Triple Dense Iridium Plate
        } else if (name.equals("vector_solar") || name.equals("brauthem_solar")) {
            return new ItemStack(ModItems.quadrupleDenseIridiumPlate); // Твой Quadruple Dense Iridium Plate
        } else { // loli_solar
            return gravitonCore; // GraviSuite Graviton Core
        }
    }

    /** Выбор схемы для MFSU */
    private static ItemStack getCircuitForMFSUTier(String name, ItemStack advancedCircuit, ItemStack lapotronCrystal, ItemStack ultimateLappack) {
        if (name.equals("spectral_mfsu") || name.equals("intermediate_mfsu") || name.equals("intermediate_high_mfsu")) {
            return advancedCircuit; // IC2 Exp Advanced Circuit
        } else if (name.equals("advanced_high_mfsu") || name.equals("superior_mfsu") || name.equals("what_the_hell_mfsu")) {
            return lapotronCrystal; // IC2 Exp Lapotron Crystal
        } else if (name.equals("magnetron_mfsu") || name.equals("photon_resonance_mfsu") || name.equals("extreme_photonic_mfsu") ||
                name.equals("arcsinus_mfsu") || name.equals("diffraction_mfsu") || name.equals("dispersion_mfsu")) {
            return new ItemStack(ModItems.hybridCircut); // Твой Hybrid Circuit
        } else if (name.equals("graviton_mfsu") || name.equals("omega_mfsu") || name.equals("photonic_mfsu")) {
            return new ItemStack(ModItems.ultimateHybridCircuit); // Твой Ultimate Hybrid Circuit
        } else if (name.equals("vector_mfsu") || name.equals("brauthem_mfsu")) {
            return new ItemStack(ModItems.quantumCircuit); // Твой Quantum Circuit
        } else { // hell_yeah_mfsu
            return ultimateLappack; // GraviSuite Ultimate Lappack
        }
    }

    /** Выбор компонента для MFSU */
    private static ItemStack getComponentForMFSUTier(String name, ItemStack iridiumPlate, ItemStack gravitonCore) {
        if (name.equals("spectral_mfsu") || name.equals("intermediate_mfsu") || name.equals("intermediate_high_mfsu") ||
                name.equals("advanced_high_mfsu") || name.equals("superior_mfsu")) {
            return iridiumPlate; // IC2 Exp Iridium Plate
        } else if (name.equals("what_the_hell_mfsu") || name.equals("magnetron_mfsu") || name.equals("photon_resonance_mfsu") ||
                name.equals("extreme_photonic_mfsu") || name.equals("arcsinus_mfsu") || name.equals("diffraction_mfsu") ||
                name.equals("dispersion_mfsu")) {
            return new ItemStack(ModItems.doubleDenseIridiumPlate); // Твой Double Dense Iridium Plate
        } else if (name.equals("graviton_mfsu") || name.equals("omega_mfsu") || name.equals("photonic_mfsu")) {
            return new ItemStack(ModItems.tripleDenseIridiumPlate); // Твой Triple Dense Iridium Plate
        } else if (name.equals("vector_mfsu") || name.equals("brauthem_mfsu")) {
            return new ItemStack(ModItems.quadrupleDenseIridiumPlate); // Твой Quadruple Dense Iridium Plate
        } else { // hell_yeah_mfsu
            return gravitonCore; // GraviSuite Graviton Core
        }
    }
}