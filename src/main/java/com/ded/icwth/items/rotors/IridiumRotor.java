package com.ded.icwth.items.rotors;

import ic2.api.item.IKineticRotor;
import ic2.core.IC2;
import ic2.core.init.Localization;
import net.lrsoft.mets.MoreElectricTools;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class IridiumRotor extends Item implements IKineticRotor {
    protected static final ResourceLocation texture = new ResourceLocation("icwth", "textures/rotors/iridium_rotor.png");

    public IridiumRotor() {
        this.setTranslationKey("iridium_rotor");
        this.setRegistryName("iridium_rotor");
        this.setCreativeTab(IC2.tabIC2);
        this.setMaxDamage(192800*5);
        this.setMaxStackSize(1);
    }

    public int getDiameter(ItemStack stack) {
        return 13;
    }

    public ResourceLocation getRotorRenderTexture(ItemStack stack) {
        return texture;
    }

    public float getEfficiency(ItemStack stack) {
        return 1.2F;
    }

    public int getMinWindStrength(ItemStack stack) {
        return 10;
    }

    public int getMaxWindStrength(ItemStack stack) {
        return 200;
    }

    public boolean isAcceptedType(ItemStack stack, IKineticRotor.GearboxType type) {
        return true;
    }

    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(Localization.translate("ic2.itemrotor.wind.info", new Object[]{this.getMinWindStrength(stack), this.getMaxWindStrength(stack)}));
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }
}
