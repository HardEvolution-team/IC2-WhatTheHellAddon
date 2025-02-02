package com.ded.icwth.blocks;


import com.ded.icwth.blocks.panels.ArcsinusSolarPanel.TileArcsinusSolarPanel;
import com.ded.icwth.blocks.panels.BrauthemSolarPanel.TileBrauthemSolarPanel;
import com.ded.icwth.blocks.panels.DiffractionPanel.TileDiffractionSolarPanel;
import com.ded.icwth.blocks.panels.DispersionSolarPanel.TileDispersionSolarPanel;
import com.ded.icwth.blocks.panels.GravitonSolarPanel.TileGravitonSolarPanel;
import com.ded.icwth.blocks.panels.LoliSolarPanel.TileLoliSolarPanel;
import com.ded.icwth.blocks.panels.OmegaSolarPanel.TileOmegaSolarPanel;
import com.ded.icwth.blocks.panels.PhotonicSolarPanel.TilePhotonicSolarPanel;
import com.ded.icwth.blocks.panels.SpectralSolarPanel.TileSpectralSolarPanel;
import com.ded.icwth.blocks.panels.VectorSolarPanel.TileVectorSolarPanel;
import ic2.core.block.ITeBlock;
import ic2.core.block.TileEntityBlock;
import ic2.core.ref.TeBlock;
import net.minecraft.item.EnumRarity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

import ic2.core.block.ITeBlock;
import ic2.core.block.TileEntityBlock;
import ic2.core.ref.TeBlock;
import ic2.core.ref.TeBlock.DefaultDrop;
import ic2.core.ref.TeBlock.HarvestTool;
import ic2.core.util.Util;
import info.u_team.hycrafthds_wtf_ic2_addon.tileentity.energystorage.TileEntityEnergyStorageCompressedMFSU;
import info.u_team.hycrafthds_wtf_ic2_addon.tileentity.energystorage.TileEntityEnergyStorageConvertableMFSU;
import info.u_team.hycrafthds_wtf_ic2_addon.tileentity.energystorage.TileEntityEnergyStorageDoubleCompressedMFSU;
import info.u_team.hycrafthds_wtf_ic2_addon.tileentity.energystorage.TileEntityEnergyStorageExtremeMFSU;
import info.u_team.hycrafthds_wtf_ic2_addon.tileentity.energystorage.TileEntityEnergyStorageQuadrupleCompressedMFSU;
import info.u_team.hycrafthds_wtf_ic2_addon.tileentity.energystorage.TileEntityEnergyStorageTripleCompressedMFSU;
import info.u_team.hycrafthds_wtf_ic2_addon.tileentity.solarpanel.TileEntitySolarPanelAdvancedHigh;
import info.u_team.hycrafthds_wtf_ic2_addon.tileentity.solarpanel.TileEntitySolarPanelAdvancedLow;
import info.u_team.hycrafthds_wtf_ic2_addon.tileentity.solarpanel.TileEntitySolarPanelAdvancedMedium;
import info.u_team.hycrafthds_wtf_ic2_addon.tileentity.solarpanel.TileEntitySolarPanelAdvancedWTF;
import info.u_team.hycrafthds_wtf_ic2_addon.tileentity.solarpanel.TileEntitySolarPanelExtremeWTF;
import info.u_team.hycrafthds_wtf_ic2_addon.tileentity.solarpanel.TileEntitySolarPanelIntermediateHigh;
import info.u_team.hycrafthds_wtf_ic2_addon.tileentity.solarpanel.TileEntitySolarPanelIntermediateLow;
import info.u_team.hycrafthds_wtf_ic2_addon.tileentity.solarpanel.TileEntitySolarPanelIntermediateMedium;
import info.u_team.hycrafthds_wtf_ic2_addon.tileentity.solarpanel.TileEntitySolarPanelIntermediateWTF;
import info.u_team.hycrafthds_wtf_ic2_addon.tileentity.solarpanel.TileEntitySolarPanelLow;
import info.u_team.hycrafthds_wtf_ic2_addon.tileentity.solarpanel.TileEntitySolarPanelSuperior;
import info.u_team.u_team_core.util.CustomResourceLocation;
import java.util.Set;
import net.minecraft.block.material.Material;
import net.minecraft.item.EnumRarity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import static info.u_team.hycrafthds_wtf_ic2_addon.init.WTFIC2AddonTes.IDENTITY;

public enum ModTiles implements ITeBlock {
    tileEntitySpectralSolar(TileSpectralSolarPanel.class, 0, true, Util.horizontalFacings, false, HarvestTool.Pickaxe, DefaultDrop.Self, 5.0F, 10.0F, EnumRarity.COMMON);


    private final Class<? extends TileEntityBlock> teClass;
    private final int itemMeta;
    private final boolean hasActive;
    private final Set<EnumFacing> possibleFacings;
    private final boolean canBeWrenched;
    private final TeBlock.HarvestTool tool;
    private final TeBlock.DefaultDrop drop;
    private final float hardness;
    private final float explosionResistance;
    private final EnumRarity rarity;
    private final Material material;
    private final boolean isTransparent;
    private TileEntityBlock dummyTe;
    private TeBlock.ITePlaceHandler placeHandler;

    private ModTiles(Class teClass, int itemMeta, boolean hasActive, Set supportedFacings, boolean allowWrenchRotating, TeBlock.HarvestTool harvestTool, TeBlock.DefaultDrop defaultDrop, float hardness, float explosionResistance, EnumRarity rarity) {
        this(teClass, itemMeta, hasActive, supportedFacings, allowWrenchRotating, harvestTool, defaultDrop, hardness, explosionResistance, rarity, Material.IRON, false);
    }

    private ModTiles(Class teClass, int itemMeta, boolean hasActive, Set possibleFacings, boolean canBeWrenched, TeBlock.HarvestTool tool, TeBlock.DefaultDrop drop, float hardness, float explosionResistance, EnumRarity rarity, Material material, boolean isTransparent) {
        this.teClass = teClass;
        this.itemMeta = itemMeta;
        this.hasActive = hasActive;
        this.possibleFacings = possibleFacings;
        this.canBeWrenched = canBeWrenched;
        this.tool = tool;
        this.drop = drop;
        this.hardness = hardness;
        this.explosionResistance = explosionResistance;
        this.rarity = rarity;
        this.material = material;
        this.isTransparent = isTransparent;
    }

    public boolean hasItem() {
        return this.teClass != null && this.itemMeta != -1;
    }

    public String getName() {
        return this.name();
    }

    public ResourceLocation getIdentifier() {
        return IDENTITY;
    }

    public Class<? extends TileEntityBlock> getTeClass() {
        return this.teClass;
    }

    public boolean hasActive() {
        return this.hasActive;
    }

    public int getId() {
        return this.itemMeta;
    }

    public float getHardness() {
        return this.hardness;
    }

    public TeBlock.HarvestTool getHarvestTool() {
        return this.tool;
    }

    public TeBlock.DefaultDrop getDefaultDrop() {
        return this.drop;
    }

    public float getExplosionResistance() {
        return this.explosionResistance;
    }

    public boolean allowWrenchRotating() {
        return this.canBeWrenched;
    }

    public Set<EnumFacing> getSupportedFacings() {
        return this.possibleFacings;
    }

    public EnumRarity getRarity() {
        return this.rarity;
    }

    public Material getMaterial() {
        return this.material;
    }

    public boolean isTransparent() {
        return this.isTransparent;
    }

    public void setPlaceHandler(TeBlock.ITePlaceHandler handler) {
        this.placeHandler = handler;
    }

    public TeBlock.ITePlaceHandler getPlaceHandler() {
        return this.placeHandler;
    }

    public static void buildDummies() {
        ModTiles[] var0 = values();
        int var1 = var0.length;

        for(int var2 = 0; var2 < var1; ++var2) {
            ModTiles block = var0[var2];
            if (block.getTeClass() != null) {
                try {
                    block.dummyTe = (TileEntityBlock)block.teClass.newInstance();
                } catch (Exception var5) {
                    var5.printStackTrace();
                }
            }
        }

    }

    public TileEntityBlock getDummyTe() {
        return this.dummyTe;
    }

    static {
        ModTiles[] var0 = values();
        int var1 = var0.length;

        for(int var2 = 0; var2 < var1; ++var2) {
            ModTiles block = var0[var2];
            TileEntity.register((new CustomResourceLocation(IDENTITY, "_" + block.getName())).toString(), block.getTeClass());
        }

    }
}
