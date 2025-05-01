package com.ded.icwth.items.upgrades;

import com.ded.icwth.Tags;
import ic2.core.init.Localization;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class UpgradeItems {
    public static Item nightGenerationUpgrade;
    public static Item dayGenerationUpgrade;
    public static Item capacityUpgrade;
    public static Item efficiencyUpgrade;

    public static void init() {
        nightGenerationUpgrade = registerItem("night_generation_upgrade", "Night Generation x2");
        dayGenerationUpgrade = registerItem("day_generation_upgrade", "Day Generation x1.5");
        capacityUpgrade = registerItem("capacity_upgrade", "Capacity +50%");
        efficiencyUpgrade = registerItem("efficiency_upgrade", "Efficiency +25%");
    }

    private static Item registerItem(String name, String localizedName) {
        Item item = new Item() {
            @Override
            public String getTranslationKey(ItemStack stack) {
                return "item." + Tags.MODID + "." + name;
            }
        };

        item.setRegistryName(Tags.MODID, name);
        item.setTranslationKey(Tags.MODID + "." + name);
        item.setCreativeTab(ic2.core.IC2.tabIC2);
        ForgeRegistries.ITEMS.register(item);

        if (net.minecraftforge.fml.common.FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            initModel(item, name);
        }

        Localization.translate(Tags.MODID + "." + name, localizedName);
        return item;
    }

    @SideOnly(Side.CLIENT)
    private static void initModel(Item item, String name) {
        ModelLoader.setCustomModelResourceLocation(
                item, 0,
                new ModelResourceLocation(Tags.MODID + ":" + name, "inventory")
        );
    }
}