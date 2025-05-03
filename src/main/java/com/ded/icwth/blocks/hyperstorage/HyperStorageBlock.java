package com.ded.icwth.blocks.hyperstorage;

import com.ded.icwth.MyMod;
import com.ded.icwth.Tags;
import ic2.core.init.Localization;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
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

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Based on EnergyStorageManager.EnergyStorageBlock
public class HyperStorageBlock extends BlockContainer {




    //В стрингах отлично
    private final String стринги = "Отлично";
    private final String стринги1 = "Отлично";
    private final String стринги2 = "Отлично";
    private final String стринги3 = "Отлично";

    

    private final int tier;
    private final double output;
    private final double maxStorage; // Nominal max storage, not used for capacity limit in TileHyperStorage
    private final String storageName;
    private final int guiId;

    public static final PropertyDirection FACING = PropertyDirection.create("facing", Arrays.asList(EnumFacing.values()));

    public HyperStorageBlock(String name, int tier, double output, double maxStorage, String storageName, int guiId) {
        super(Material.IRON);
        this.setCreativeTab(ic2.core.IC2.tabIC2);
        this.setTranslationKey(Tags.MODID + "." + name);
        this.tier = tier;
        this.output = output;
        this.maxStorage = maxStorage;
        this.storageName = storageName;
        this.guiId = guiId;
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(FACING, EnumFacing.byIndex(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getIndex();
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        EnumFacing direction = placer.getHorizontalFacing().getOpposite();
        if (placer.getLookVec().y > 0.5) {
            direction = EnumFacing.UP;
        } else if (placer.getLookVec().y < -0.5) {
            direction = EnumFacing.DOWN;
        }
        return this.getDefaultState().withProperty(FACING, direction);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);
        EnumFacing direction = state.getValue(FACING);
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileHyperStorage) { // Updated to TileHyperStorage
            ((TileHyperStorage) tile).setFacing(direction);
        }
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        // Return the new TileHyperStorage instance
        return new TileHyperStorage(tier, output, maxStorage, storageName);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
                                    EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            // Use the assigned guiId to open the correct GUI
            player.openGui(MyMod.instance, guiId, world, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        super.onBlockAdded(world, pos, state);
        if (!world.isRemote) {
            world.notifyNeighborsOfStateChange(pos, this, true);
        }
    }

    // Add tooltip information if needed
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);
        tooltip.add(Localization.translate("ic2.item.tooltip.PowerTier", tier));
        // Add specific tooltip for hyper storage if desired
        tooltip.add("Stores virtually infinite energy.");
    }
}

