package com.ded.icwth.items.upgrades.overclocker;

import codechicken.lib.model.ModelRegistryHelper;
import codechicken.lib.util.TransformUtils;
import com.ded.icwth.Tags;
import ic2.api.item.IItemHudInfo;
import ic2.api.upgrade.IProcessingUpgrade;
import ic2.api.upgrade.IUpgradableBlock;
import ic2.api.upgrade.IUpgradeItem;
import ic2.api.upgrade.UpgradableProperty;
import ic2.core.IC2;
import morph.avaritia.api.IHaloRenderItem;
import morph.avaritia.api.registration.IModelRegister;
import morph.avaritia.client.render.item.HaloRenderItem;
import morph.avaritia.init.AvaritiaTextures;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.core.tools.picocli.CommandLine;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Optional.Interface(iface = "fox.spiteful.avaritia.api.IAvaritiaItem", modid = "avaritia")
public class ItemCreativeOverclocker extends Item implements IProcessingUpgrade, IUpgradeItem, IItemHudInfo, IHaloRenderItem, IModelRegister {


    private final double EnergyMultiplier = 0;
    private final double ProcessTimeMultiplier = 0;



    private final double TooltipTImeMultiplier = (1 - ProcessTimeMultiplier) * 100;
    private final double ToolTipEnergyMultiplier = EnergyMultiplier * 100;


    public ItemCreativeOverclocker() {
        setRegistryName("creative_overclocker");        // The unique name (within your mod) that identifies this item
        setTranslationKey(Tags.MODID + ".creative_overclocker");     // Used for localization (en_US.lang)
        setMaxStackSize(1);
        setCreativeTab(IC2.tabIC2);
    }

//    @SideOnly(Side.CLIENT)
//    public void initModel() {
//        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
//    }
    @Override
    public List<String> getHudInfo(ItemStack itemStack, boolean b) {
        return Collections.singletonList("Overclocker Upgrade Inf"); // Информация на HUD
    }

    @Override
    public boolean isSuitableFor(ItemStack stack, Set<UpgradableProperty> types) {
        return types.contains(UpgradableProperty.Processing); // Подходит для машин с обработкой
    }

    @Override
    public boolean onTick(ItemStack stack, IUpgradableBlock upgradableBlock) {
        return false; // Не требует обработки каждый тик
    }

    @Override
    public int getExtraProcessTime(ItemStack itemStack, IUpgradableBlock upgradableBlock) {
        return 0; // No extra processing time added
    }

    @Override
    public Collection<ItemStack> onProcessEnd(ItemStack itemStack, IUpgradableBlock iUpgradableBlock, Collection<ItemStack> collection) {
        return collection; // Return the original output collection
    }

    @Override
    public double getProcessTimeMultiplier(ItemStack itemStack, IUpgradableBlock upgradableBlock) {
        return ProcessTimeMultiplier; // Minimal processing time, adjust as needed
    }
    @Override
    public int getExtraEnergyDemand(ItemStack itemStack, IUpgradableBlock iUpgradableBlock) {
        return 0;
    }

    @Override
    public double getEnergyDemandMultiplier(ItemStack itemStack, IUpgradableBlock upgradableBlock) {
        return EnergyMultiplier; // Увеличивает энергопотребление на 70%
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(("Increases speed by ") + (int)TooltipTImeMultiplier + ("%"));
        tooltip.add(("Increases energy consumption by ") + (int)ToolTipEnergyMultiplier + ("%"));
    }

    @Override
    public boolean shouldDrawHalo(ItemStack itemStack) {
        return true;
    }

    @Override
    public TextureAtlasSprite getHaloTexture(ItemStack itemStack) {
        return AvaritiaTextures.HALO;
    }

    @Override
    public int getHaloColour(ItemStack itemStack) {
        return 0xFF000000;
    }

    @Override
    public int getHaloSize(ItemStack itemStack) {
        return 4;
    }

    @Override
    public boolean shouldDrawPulse(ItemStack itemStack) {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerModels() {
        ModelResourceLocation location = new ModelResourceLocation(getRegistryName(), "inventory");
        ModelLoader.registerItemVariants(this, location);
        IBakedModel wrappedModel = new HaloRenderItem(TransformUtils.DEFAULT_ITEM, modelRegistry -> modelRegistry.getObject(location));
        ModelRegistryHelper.register(location, wrappedModel);
        ModelLoader.setCustomMeshDefinition(this, stack -> location);
    }
}
