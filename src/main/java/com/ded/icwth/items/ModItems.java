package com.ded.icwth.items;




import com.ded.icwth.blocks.moleculartransformer.advanced.BlockAdvancedMolecularTransformer;
import com.ded.icwth.blocks.moleculartransformer.based.BlockMolecularTransformer;
import com.ded.icwth.items.circuits.ItemHybridCircuit;
import com.ded.icwth.items.circuits.ItemQuantumCircuit;
import com.ded.icwth.items.circuits.ItemUltimateHybridCircuit;
import com.ded.icwth.items.materials.ItemDoubleDenseIridiumPlate;
import com.ded.icwth.items.materials.ItemFiveCompressedDenseIridiumPlate;
import com.ded.icwth.items.materials.ItemQuadrupleDenseIridiumPlate;
import com.ded.icwth.items.materials.ItemTripleDenseIridiumPlate;
import com.ded.icwth.items.singularity.ItemMFSUSingularity;
import com.ded.icwth.items.upgrades.matter.ItemMatterUpgrade;
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


    @GameRegistry.ObjectHolder("icwth:matter_upgrade")
    public static ItemMatterUpgrade matterUpgrade;




    @GameRegistry.ObjectHolder("icwth:hybrid_circuit")
    public static ItemHybridCircuit hybridCircut;

    @GameRegistry.ObjectHolder("icwth:ultimate_hybrid_circuit")
    public static ItemUltimateHybridCircuit ultimateHybridCircuit;

    @GameRegistry.ObjectHolder("icwth:quantum_circuit")
    public static ItemQuantumCircuit quantumCircuit;




    @GameRegistry.ObjectHolder("icwth:molecular_transformer")
    public static BlockMolecularTransformer molecularTransformer;


    @GameRegistry.ObjectHolder("icwth:advanced_molecular_transformer")
    public static BlockAdvancedMolecularTransformer advancedMolecularTransformer;

















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

        matterUpgrade.initModel();
        mfsuSingularity.initModel();

        doubleDenseIridiumPlate.initModel();
        fiveCompressedDenseIridiumPlate.initModel();
        tripleDenseIridiumPlate.initModel();
        quadrupleDenseIridiumPlate.initModel();

        molecularTransformer.initModel();
        advancedMolecularTransformer.initModel();




    }
}
