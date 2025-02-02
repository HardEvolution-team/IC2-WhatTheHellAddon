package com.ded.icwth.items;

import com.ded.icwth.blocks.panels.ArcsinusSolarPanel.BlockArcsinusSolarPanel;
import com.ded.icwth.blocks.panels.BrauthemSolarPanel.BlockBrauthemSolarPanel;
import com.ded.icwth.blocks.panels.DiffractionPanel.BlockDiffractionSolarPanel;
import com.ded.icwth.blocks.panels.DispersionSolarPanel.BlockDispersionSolarPanel;
import com.ded.icwth.blocks.panels.GravitonSolarPanel.BlockGravitonSolarPanel;
import com.ded.icwth.blocks.panels.LoliSolarPanel.BlockLoliSolarPanel;
import com.ded.icwth.blocks.panels.OmegaSolarPanel.BlockOmegaSolarPanel;
import com.ded.icwth.blocks.panels.PhotonicSolarPanel.BlockPhotonicSolarPanel;
import com.ded.icwth.blocks.panels.SpectralSolarPanel.BlockSpectralSolarPanel;
import com.ded.icwth.blocks.panels.VectorSolarPanel.BlockVectorSolarPanel;
import com.ded.icwth.items.circuits.ItemHybridCircuit;
import com.ded.icwth.items.circuits.ItemQuantumCircuit;
import com.ded.icwth.items.circuits.ItemUltimateHybridCircuit;
import com.ded.icwth.items.materials.ItemDoubleDenseIridiumPlate;
import com.ded.icwth.items.materials.ItemFiveCompressedDenseIridiumPlate;
import com.ded.icwth.items.materials.ItemQuadrupleDenseIridiumPlate;
import com.ded.icwth.items.materials.ItemTripleDenseIridiumPlate;
import com.ded.icwth.items.singularity.ItemMFSUSingularity;
import com.ded.icwth.items.upgrades.overclocker.ItemAdvancedOverclocker;
import com.ded.icwth.items.upgrades.overclocker.ItemCreativeOverclocker;
import com.ded.icwth.items.upgrades.overclocker.ItemImprovedOverclocker;
import com.ded.icwth.items.upgrades.overclocker.ItemQuantumOverclocker;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModItems {


    //overclocker
    @GameRegistry.ObjectHolder("icwth:improved_overclocker")
    public static ItemImprovedOverclocker improvedOverclocker;

    @GameRegistry.ObjectHolder("icwth:advanced_overclocker")
    public static ItemAdvancedOverclocker advancedOverclocker;

    @GameRegistry.ObjectHolder("icwth:quantum_overclocker")
    public static ItemQuantumOverclocker quantumOverclocker;

    @GameRegistry.ObjectHolder("icwth:creative_overclocker")
    public static ItemCreativeOverclocker creativeOverclocker;

    @GameRegistry.ObjectHolder("icwth:mfsu_singularity")
    public static ItemMFSUSingularity mfsuSingularity;


    @GameRegistry.ObjectHolder("icwth:double_dense_iridium_plate")
    public static ItemDoubleDenseIridiumPlate doubleDenseIridiumPlate;

    @GameRegistry.ObjectHolder("icwth:five_compressed_dense_iridium_plate")
    public static ItemFiveCompressedDenseIridiumPlate fiveCompressedDenseIridiumPlate;

    @GameRegistry.ObjectHolder("icwth:triple_dense_iridium_plate")
    public static ItemTripleDenseIridiumPlate tripleDenseIridiumPlate;

    @GameRegistry.ObjectHolder("icwth:quadruple_dense_iridium_plate")
    public static ItemQuadrupleDenseIridiumPlate quadrupleDenseIridiumPlate;






    @GameRegistry.ObjectHolder("icwth:hybrid_circuit")
    public static ItemHybridCircuit hybridCircut;

    @GameRegistry.ObjectHolder("icwth:ultimate_hybrid_circuit")
    public static ItemUltimateHybridCircuit ultimateHybridCircuit;

    @GameRegistry.ObjectHolder("icwth:quantum_circuit")
    public static ItemQuantumCircuit quantumCircuit;

    @GameRegistry.ObjectHolder("icwth:spectral_solar")
    public static BlockSpectralSolarPanel spectralSolar;

    @GameRegistry.ObjectHolder("icwth:diffraction_solar")
    public static BlockDiffractionSolarPanel diffractionSolar;

    @GameRegistry.ObjectHolder("icwth:dispersion_solar")
    public static BlockDispersionSolarPanel dispersionSolar;

    @GameRegistry.ObjectHolder("icwth:brauthem_solar")
    public static BlockBrauthemSolarPanel brauthemSolar;

    @GameRegistry.ObjectHolder("icwth:loli_solar")
    public static BlockLoliSolarPanel loliSolar;

    @GameRegistry.ObjectHolder("icwth:arcsinus_solar")
    public static BlockArcsinusSolarPanel arcsinusSolar;

    @GameRegistry.ObjectHolder("icwth:vector_solar")
    public static BlockVectorSolarPanel vectorSolar;

    @GameRegistry.ObjectHolder("icwth:photonic_solar")
    public static BlockPhotonicSolarPanel photonicSolar;

    @GameRegistry.ObjectHolder("icwth:graviton_solar")
    public static BlockGravitonSolarPanel gravitonSolar;

    @GameRegistry.ObjectHolder("icwth:omega_solar")
    public static BlockOmegaSolarPanel omegaSolar;





















    @SideOnly(Side.CLIENT)
    public static void initModels() {

        //items
        improvedOverclocker.initModel();
        advancedOverclocker.initModel();
        quantumOverclocker.initModel();
        creativeOverclocker.registerModels();
        hybridCircut.initModel();
        ultimateHybridCircuit.initModel();
        quantumCircuit.initModel();


        mfsuSingularity.initModel();




        doubleDenseIridiumPlate.initModel();
        fiveCompressedDenseIridiumPlate.initModel();
        tripleDenseIridiumPlate.initModel();
        quadrupleDenseIridiumPlate.initModel();






        //blocks
        spectralSolar.initModel();
        diffractionSolar.initModel();
        dispersionSolar.initModel();
        brauthemSolar.initModel();
        loliSolar.initModel();
        arcsinusSolar.initModel();
        vectorSolar.initModel();
        photonicSolar.initModel();
        gravitonSolar.initModel();
        omegaSolar.initModel();




    }







}
