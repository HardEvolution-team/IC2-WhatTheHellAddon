package com.ded.icwth.blocks.panels;

import com.ded.icwth.MyMod;
import com.ded.icwth.Tags;
import ic2.core.init.Localization;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SolarPanelManager {
    private static final Map<String, Block> registeredPanels = new HashMap<>();
    private static int guiIdCounter = 1000;

    static {
        // Регистрация TileEntity для блока
        GameRegistry.registerTileEntity(SolarPanelTile.class,
                new ResourceLocation(Tags.MODID, "solar_panel_tile"));
    }

    /**
     * Регистрирует новый блок солнечной панели.
     *
     * @param name           Имя блока
     * @param tier           Уровень панели
     * @param generationRate Скорость генерации энергии
     * @param capacity       Вместимость энергии
     * @param localizedName  Локализованное имя
     */
    public static void registerPanel(String name, int tier, double generationRate,
                                     double capacity, String localizedName) {
        SolarPanelBlock block = new SolarPanelBlock(
                name,
                tier,
                generationRate,
                capacity,
                localizedName,
                guiIdCounter
        );

        guiIdCounter++;

        // Регистрация блока
        block.setRegistryName(new ResourceLocation(Tags.MODID, name));
        block.setTranslationKey(Tags.MODID + "." + name);
        ForgeRegistries.BLOCKS.register(block);

        // Регистрация ItemBlock
        SolarPanelItemBlock itemBlock = new SolarPanelItemBlock(block, capacity, generationRate);
        itemBlock.setRegistryName(block.getRegistryName());
        ForgeRegistries.ITEMS.register(itemBlock);

        // Инициализация модели на клиентской стороне
        if (net.minecraftforge.fml.common.FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            initModel(block, name);
        }

        registeredPanels.put(name, block);
        System.out.println("Registered solar panel: " + name + " with GUI ID: " + (guiIdCounter - 1) + " and localizedName: " + localizedName);
    }

    /**
     * Инициализирует модель блока на клиентской стороне.
     *
     * @param block Блок
     * @param name  Имя блока
     */
    @SideOnly(Side.CLIENT)
    private static void initModel(Block block, String name) {
        ModelLoader.setCustomModelResourceLocation(
                Item.getItemFromBlock(block), 0,
                new ModelResourceLocation(Tags.MODID + ":" + name, "inventory")
        );
    }

    /**
     * Блок солнечной панели.
     */
    public static class SolarPanelBlock extends BlockContainer {
        private final int tier;
        private final double generationRate;
        private final double capacity;
        private final String localizedName;
        private final int guiId;

        public SolarPanelBlock(String name, int tier, double generationRate,
                               double capacity, String localizedName, int guiId) {
            super(Material.IRON);
            this.tier = tier;
            this.generationRate = generationRate;
            this.capacity = capacity;
            this.localizedName = localizedName != null ? localizedName : "tile.default_solar.name";
            this.guiId = guiId;

            // Настройки блока
            this.setCreativeTab(ic2.core.IC2.tabIC2);
            this.setLightOpacity(255); // Максимальная непрозрачность
            this.setHardness(2.0F); // Установка прочности блока
            this.setResistance(10.0F); // Установка устойчивости к взрывам

            System.out.println("Created SolarPanelBlock with GUI ID: " + guiId + " and localizedName: " + localizedName);
        }

        @Override
        public TileEntity createNewTileEntity(World world, int meta) {
            System.out.println("Creating new SolarPanelTile with parameters: tier=" + tier +
                    ", generationRate=" + generationRate + ", capacity=" + capacity +
                    ", localizedName=" + localizedName + ", guiId=" + guiId);
            return new SolarPanelTile(generationRate, capacity, tier, localizedName, guiId);
        }

        @Override
        public boolean onBlockActivated(World world, BlockPos pos, IBlockState state,
                                        EntityPlayer player, EnumHand hand,
                                        EnumFacing facing, float hitX, float hitY, float hitZ) {
            if (!world.isRemote) {
                System.out.println("Opening GUI for " + localizedName + " with ID: " + guiId);
                player.openGui(MyMod.instance, guiId, world, pos.getX(), pos.getY(), pos.getZ());
            }
            return true;
        }

        // Указываем слой рендеринга
        @Nonnull
        @SideOnly(Side.CLIENT)
        @Override
        public BlockRenderLayer getRenderLayer() {
            return BlockRenderLayer.SOLID;
        }

        // Указываем, что блок непрозрачный
        @Override
        public boolean isOpaqueCube(IBlockState state) {
            return true;
        }

        // Указываем, что блок занимает полный куб
        @Override
        public boolean isFullCube(IBlockState state) {
            return true;
        }
    }

    /**
     * ItemBlock для блока солнечной панели.
     */
    public static class SolarPanelItemBlock extends ItemBlock {
        private final double capacity;
        private final double generationRate;

        public SolarPanelItemBlock(Block block, double capacity, double generationRate) {
            super(block);
            this.capacity = capacity;
            this.generationRate = generationRate;
        }

        @SideOnly(Side.CLIENT)
        @Override
        public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
            super.addInformation(stack, worldIn, tooltip, flagIn);
            tooltip.add(Localization.translate("icwth.mfsu.capacity") + " " + String.format("%.0f", this.capacity) + " EU");
            tooltip.add(Localization.translate("icwth.mfsu.generate") + " " + String.format("%.0f", this.generationRate) + " EU/t");
        }
    }

    /**
     * TileEntity для блока солнечной панели.
     */
    public static class SolarPanelTile extends TileEntitySolarBase {
        private final int guiId;
        private final String localizedName;

        public SolarPanelTile(double output, double capacity, int tier, String localizedName, int guiId) {
            super(output, capacity, tier);
            this.guiId = guiId;
            this.localizedName = localizedName != null ? localizedName : "tile.default_solar.name";
            System.out.println("SolarPanelTile created with parameters: output=" + output +
                    ", capacity=" + capacity + ", tier=" + tier +
                    ", localizedName=" + localizedName + ", guiId=" + guiId);
        }

        public SolarPanelTile() {
            super();
            this.guiId = 0;
            this.localizedName = "tile.default_solar.name";
        }

        @Override
        public String getName() {
            return this.localizedName;
        }

        @Override
        public double getEnergyStored() {
            return energy.getEnergyStored();
        }

        @Override
        public double getMaxStorage() {
            return energy.getCapacity();
        }

        @Override
        public double getOutput() {
            return output;
        }
    }
}