//package com.ded.icwth.blocks;
//
//import net.minecraft.block.BlockContainer;
//import net.minecraft.block.material.Material;
//import net.minecraft.block.state.IBlockState;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.renderer.*;
//import net.minecraft.client.renderer.culling.ICamera;
//import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
//import net.minecraft.client.renderer.vertex.VertexBuffer;
//import net.minecraft.client.renderer.vertex.VertexFormat;
//import net.minecraft.entity.Entity;
//import net.minecraft.entity.player.EntityPlayer;
//import net.minecraft.nbt.NBTTagCompound;
//import net.minecraft.network.NetworkManager;
//import net.minecraft.network.play.server.SPacketUpdateTileEntity;
//import net.minecraft.tileentity.TileEntity;
//import net.minecraft.util.EnumBlockRenderType;
//import net.minecraft.util.EnumFacing;
//import net.minecraft.util.EnumHand;
//import net.minecraft.util.math.AxisAlignedBB;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.util.math.MathHelper;
//import net.minecraft.util.math.Vec3d;
//import net.minecraft.world.World;
//import net.minecraftforge.client.event.RenderWorldLastEvent;
//import net.minecraftforge.common.MinecraftForge;
//import net.minecraftforge.common.config.Configuration;
//import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
//import net.minecraftforge.fml.relauncher.Side;
//import net.minecraftforge.fml.relauncher.SideOnly;
//import org.jetbrains.annotations.NotNull;
//import org.jetbrains.annotations.Nullable;
//import org.lwjgl.BufferUtils;
//import org.lwjgl.opengl.GL11;
//import org.lwjgl.util.vector.Matrix4f;
//import org.lwjgl.util.vector.Vector3f;
//import org.lwjgl.util.vector.Vector4f;
//
//import java.io.File;
//import java.nio.FloatBuffer;
//import java.util.*;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.CopyOnWriteArrayList;
//
///**
// * Ultra-Large, Highly Modular, Procedural Energy Sphere Renderer for Forge 1.12.2.
// * Features extreme detail, multiple effect layers, LOD, basic occlusion, and NO textures.
// * Designed for maximum code volume and visual complexity.
// * WARNING: Performance impact may be significant!
// */
//public class BlockEnergyCoreUltra extends BlockContainer {
//
//    // =========================================================================
//    // Constants & Static Fields
//    // =========================================================================
//
//    public static final String MOD_ID = "icwth_ultra"; // Example Mod ID
//    public static final String REGISTRY_NAME = "energy_core_ultra";
//    private static final float MAX_VISIBLE_RADIUS = 2.5f; // For 5x5 structure center
//    private static final int MAX_LOD_LEVEL = 0; // Highest detail
//    private static final int MIN_LOD_LEVEL = 4; // Lowest detail
//    private static final int[] LOD_SEGMENTS = {384, 256, 192, 128, 96}; // ULTRA HIGH SEGMENTS
//    private static final float[] LOD_DISTANCES_SQ = {20.0f * 20.0f, 40.0f * 40.0f, 80.0f * 80.0f, 160.0f * 160.0f, Float.MAX_VALUE};
//    private static final Random MASTER_RANDOM = new Random(System.nanoTime());
//    private static boolean eventHandlerRegistered = false;
//
//    // Geometry Cache (static, shared across all instances)
//    private static final SphereGeometryCacheUltra[] geometryCache = new SphereGeometryCacheUltra[LOD_SEGMENTS.length];
//
//    // Active Sphere Renderers (thread-safe)
//    private static final List<ProceduralSphereRendererUltra> ACTIVE_SPHERES = new CopyOnWriteArrayList<>();
//
//    // Configuration Settings
//    public static RenderConfig renderConfig = new RenderConfig();
//
//    // =========================================================================
//    // Utility Classes
//    // =========================================================================
//
//    /**
//     * Utility class for math operations not available in MathHelper
//     */
//    public static class MathUtils {
//        /**
//         * Linear interpolation between two values
//         * @param amount Interpolation factor (0.0-1.0)
//         * @param start Start value
//         * @param end End value
//         * @return Interpolated value
//         */
//        public static float lerp(float amount, float start, float end) {
//            return start + amount * (end - start);
//        }
//    }
//
//    /**
//     * Utility class for vector operations
//     */
//    public static class VectorUtils {
//        /**
//         * Convert Vector3f to Vec3d
//         * @param vec Vector3f to convert
//         * @return Equivalent Vec3d
//         */
//        public static Vec3d toVec3d(Vector3f vec) {
//            return new Vec3d(vec.x, vec.y, vec.z);
//        }
//
//        /**
//         * Convert Vec3d to Vector3f
//         * @param vec Vec3d to convert
//         * @return Equivalent Vector3f
//         */
//        public static Vector3f toVector3f(Vec3d vec) {
//            return new Vector3f((float)vec.x, (float)vec.y, (float)vec.z);
//        }
//
//        /**
//         * Scale a Vector3f by a factor
//         * @param vec Vector to scale
//         * @param scale Scale factor
//         * @param dest Destination vector (can be null)
//         * @return Scaled vector
//         */
//        public static Vector3f scale(Vector3f vec, float scale, Vector3f dest) {
//            if (dest == null) {
//                dest = new Vector3f();
//            }
//            dest.x = vec.x * scale;
//            dest.y = vec.y * scale;
//            dest.z = vec.z * scale;
//            return dest;
//        }
//
//        /**
//         * Get a random normalized vector
//         * @param rand Random instance
//         * @return Random unit vector
//         */
//        public static Vector3f getRandomNormalizedVector(Random rand) {
//            float x = rand.nextFloat() * 2.0f - 1.0f;
//            float y = rand.nextFloat() * 2.0f - 1.0f;
//            float z = rand.nextFloat() * 2.0f - 1.0f;
//
//            float length = (float) Math.sqrt(x * x + y * y + z * z);
//            if (length < 0.0001f) {
//                return new Vector3f(0, 1, 0); // Fallback
//            }
//
//            return new Vector3f(x / length, y / length, z / length);
//        }
//    }
//
//    /**
//     * Utility for working with Configuration
//     */
//    public static class ConfigUtils {
//        /**
//         * Get a long value from configuration
//         * @param config Configuration object
//         * @param category Category name
//         * @param key Property key
//         * @param defaultValue Default value
//         * @return Long value
//         */
//        public static long getLongFromConfig(Configuration config, String category, String key, long defaultValue) {
//            // If the value fits in an int, use getInt
//            if (defaultValue <= Integer.MAX_VALUE && defaultValue >= Integer.MIN_VALUE) {
//                return config.getInt(key, category, (int)defaultValue, Integer.MIN_VALUE, Integer.MAX_VALUE, "");
//            }
//            // Otherwise, use getString and parse
//            String stringValue = config.getString(key, category, String.valueOf(defaultValue), "");
//            try {
//                return Long.parseLong(stringValue);
//            } catch (NumberFormatException e) {
//                return defaultValue;
//            }
//        }
//    }
//
//    // =========================================================================
//    // Constructor & Initialization
//    // =========================================================================
//
//    public BlockEnergyCoreUltra(Material material) {
//        super(material);
//        this.setRegistryName(REGISTRY_NAME);
//        this.setTranslationKey(MOD_ID + "." + REGISTRY_NAME);
//        this.setHardness(10.0F);
//        this.setResistance(1000.0F);
//        this.setLightLevel(0.5F); // Base light level
//
//        // Register event handler and initialize cache only once on the client
//        if (!eventHandlerRegistered && Minecraft.getMinecraft() != null) {
//            System.out.println("[" + MOD_ID + "] Registering Render Event Handler and Initializing Geometry Cache...");
//            MinecraftForge.EVENT_BUS.register(RenderEventHandlerUltra.INSTANCE);
//            initializeGeometryCache();
//            // Load configuration
//            // Note: Config loading should ideally happen earlier in mod lifecycle
//            // File configFile = new File(Minecraft.getMinecraft().mcDataDir, "config/" + MOD_ID + "_render.cfg");
//            // renderConfig.load(configFile);
//            eventHandlerRegistered = true;
//        }
//    }
//
//    @SideOnly(Side.CLIENT)
//    private static void initializeGeometryCache() {
//        System.out.println("[" + MOD_ID + "] Initializing Ultra Sphere Geometry Cache (Segments: " + Arrays.toString(LOD_SEGMENTS) + ")...");
//        long startTime = System.nanoTime();
//        for (int i = 0; i < LOD_SEGMENTS.length; i++) {
//            geometryCache[i] = new SphereGeometryCacheUltra(LOD_SEGMENTS[i]);
//        }
//        long endTime = System.nanoTime();
//        System.out.printf("[" + MOD_ID + "] Ultra Sphere Geometry Cache initialized in %.3f ms%n", (endTime - startTime) / 1_000_000.0);
//    }
//
//    // =========================================================================
//    // Block Overrides
//    // =========================================================================
//
//    @NotNull
//    @Override
//    public TileEntity createNewTileEntity(@NotNull World worldIn, int meta) {
//        return new TileEntityEnergyCoreUltra();
//    }
//
//    @Override
//    public boolean onBlockActivated(@NotNull World worldIn, @NotNull BlockPos pos, @NotNull IBlockState state, @NotNull EntityPlayer playerIn, @NotNull EnumHand hand, @NotNull EnumFacing facing, float hitX, float hitY, float hitZ) {
//        TileEntity te = worldIn.getTileEntity(pos);
//        if (!(te instanceof TileEntityEnergyCoreUltra)) {
//            System.err.println("[" + MOD_ID + "] Error: TileEntity at " + pos + " is not TileEntityEnergyCoreUltra!");
//            return false;
//        }
//        TileEntityEnergyCoreUltra core = (TileEntityEnergyCoreUltra) te;
//
//        if (!worldIn.isRemote) {
//            // Server-side logic (example: adjust energy)
//            long currentEnergy = core.getEnergyLevel();
//            long maxEnergy = core.getMaxEnergyLevel();
//            long change = playerIn.isSneaking() ? -maxEnergy / 20 : maxEnergy / 20; // +/- 5%
//            core.setEnergyLevel(currentEnergy + change);
//            System.out.printf("[" + MOD_ID + "] Server: Energy changed at %s to %d / %d%n", pos, core.getEnergyLevel(), maxEnergy);
//            // Potentially open GUI here
//        } else {
//            // Client-side logic: Trigger render update
//            RenderEventHandlerUltra.INSTANCE.addOrUpdateSphere(pos, core.getEnergyLevel(), core.getMaxEnergyLevel(), core.getStability());
//            System.out.printf("[" + MOD_ID + "] Client: Sphere render update triggered for pos %s%n", pos);
//            // Play a sound effect maybe?
//            // Minecraft.getMinecraft().getSoundHandler().playSound(...);
//        }
//        return true;
//    }
//
//    @Override
//    public void onBlockAdded(@NotNull World worldIn, @NotNull BlockPos pos, @NotNull IBlockState state) {
//        super.onBlockAdded(worldIn, pos, state);
//        if (worldIn.isRemote) {
//            TileEntity te = worldIn.getTileEntity(pos);
//            if (te instanceof TileEntityEnergyCoreUltra) {
//                TileEntityEnergyCoreUltra core = (TileEntityEnergyCoreUltra) te;
//                RenderEventHandlerUltra.INSTANCE.addOrUpdateSphere(pos, core.getEnergyLevel(), core.getMaxEnergyLevel(), core.getStability());
//                System.out.println("[" + MOD_ID + "] Client: Sphere added at " + pos);
//            }
//        }
//    }
//
//    @Override
//    public void breakBlock(@NotNull World worldIn, @NotNull BlockPos pos, @NotNull IBlockState state) {
//        if (worldIn.isRemote) {
//            RenderEventHandlerUltra.INSTANCE.removeSphere(pos);
//            System.out.println("[" + MOD_ID + "] Client: Sphere removed at " + pos);
//        }
//        // Drop items if necessary
//        super.breakBlock(worldIn, pos, state);
//    }
//
//    // Make the block non-solid and use custom rendering
//    @Override
//    public boolean isOpaqueCube(IBlockState state) {
//        return false;
//    }
//
//    @Override
//    public boolean isFullCube(IBlockState state) {
//        return false;
//    }
//
//    @NotNull
//    @Override
//    public EnumBlockRenderType getRenderType(IBlockState state) {
//        return EnumBlockRenderType.INVISIBLE; // Handled by TESR/WorldLastEvent
//    }
//
//    // Increase light level based on energy
//    @Override
//    public int getLightValue(@NotNull IBlockState state, @NotNull net.minecraft.world.IBlockAccess world, @NotNull BlockPos pos) {
//        TileEntity te = world.getTileEntity(pos);
//        if (te instanceof TileEntityEnergyCoreUltra) {
//            float energyRatio = ((TileEntityEnergyCoreUltra) te).getEnergyRatio();
//            return Math.min(15, Math.max(1, (int) (energyRatio * 15.0f)));
//        }
//        return super.getLightValue(state, world, pos);
//    }
//
//    // =========================================================================
//    // Tile Entity: TileEntityEnergyCoreUltra
//    // =========================================================================
//
//    public static class TileEntityEnergyCoreUltra extends TileEntity {
//        private static final String NBT_ENERGY = "EnergyUltra";
//        private static final String NBT_MAX_ENERGY = "MaxEnergyUltra";
//        private static final String NBT_STABILITY = "StabilityUltra";
//
//        private long energy = 50_000_000L; // Default energy
//        private long maxEnergy = 200_000_000L; // Default max energy
//        private float stability = 1.0f; // 1.0 = stable, 0.0 = unstable
//        private float targetStability = 1.0f;
//        private float lastEnergyRatio = -1f;
//
//        // --- Getters ---
//        public long getEnergyLevel() { return energy; }
//        public long getMaxEnergyLevel() { return maxEnergy; }
//        public float getEnergyRatio() {
//            return maxEnergy > 0 ? MathHelper.clamp((float) energy / maxEnergy, 0.0f, 1.0f) : 0.0f;
//        }
//        public float getStability() { return stability; }
//
//        // --- Setters & Logic ---
//        public void setEnergyLevel(long newEnergy) {
//            long oldEnergy = this.energy;
//            this.energy = (long) MathHelper.clamp(newEnergy, 0, maxEnergy);
//            if (this.energy != oldEnergy) {
//                updateStability();
//                markDirty();
//                // Send update to client
//                if (!world.isRemote) {
//                    IBlockState state = world.getBlockState(pos);
//                    world.notifyBlockUpdate(pos, state, state, 3);
//                    // System.out.printf("[" + MOD_ID + "] Server: NBT Update Packet Sent for %s (Energy: %d)%n", pos, this.energy);
//                }
//            }
//        }
//
//        private void updateStability() {
//            float ratio = getEnergyRatio();
//            // Stability decreases near 0% and 100% energy
//            this.targetStability = 1.0f - (float)Math.pow(Math.abs(ratio - 0.5f) * 2.0f, 3.0f);
//            this.targetStability = MathHelper.clamp(this.targetStability, 0.05f, 1.0f);
//            // Actual stability changes gradually
//        }
//
//        // Called every tick
//        @Override
//        public void update() {
//            // Smoothly update stability towards target
//            if (Math.abs(this.stability - this.targetStability) > 0.01f) {
//                this.stability = MathUtils.lerp(0.1f, this.stability, this.targetStability);
//                // No need to markDirty or send packet for stability interpolation
//            }
//
//            // Optimization: Only update client render if energy ratio changes significantly
//            if (world.isRemote) {
//                float currentRatio = getEnergyRatio();
//                if (Math.abs(currentRatio - lastEnergyRatio) > 0.01f) {
//                    RenderEventHandlerUltra.INSTANCE.addOrUpdateSphere(pos, energy, maxEnergy, stability);
//                    lastEnergyRatio = currentRatio;
//                }
//            }
//        }
//
//        // --- NBT Handling ---
//        @Override
//        public void readFromNBT(NBTTagCompound compound) {
//            super.readFromNBT(compound);
//            this.energy = compound.getLong(NBT_ENERGY);
//            this.maxEnergy = compound.getLong(NBT_MAX_ENERGY);
//            this.stability = compound.getFloat(NBT_STABILITY);
//            if (this.maxEnergy == 0) this.maxEnergy = 200_000_000L; // Default if missing
//            this.targetStability = this.stability; // Initialize target stability
//            this.lastEnergyRatio = getEnergyRatio(); // Update last ratio
//            // System.out.printf("[" + MOD_ID + "] NBT Read (%s): E=%d, MaxE=%d, S=%.2f%n", world.isRemote ? "Client" : "Server", energy, maxEnergy, stability);
//        }
//
//        @NotNull
//        @Override
//        public NBTTagCompound writeToNBT(NBTTagCompound compound) {
//            super.writeToNBT(compound);
//            compound.setLong(NBT_ENERGY, this.energy);
//            compound.setLong(NBT_MAX_ENERGY, this.maxEnergy);
//            compound.setFloat(NBT_STABILITY, this.stability); // Save current stability
//            return compound;
//        }
//
//        // --- Network Synchronization ---
//        @Nullable
//        @Override
//        public SPacketUpdateTileEntity getUpdatePacket() {
//            // System.out.printf("[" + MOD_ID + "] Server: Creating Update Packet for %s%n", pos);
//            return new SPacketUpdateTileEntity(this.pos, 3, this.getUpdateTag());
//        }
//
//        @NotNull
//        @Override
//        public NBTTagCompound getUpdateTag() {
//            // Send all relevant data needed for client rendering
//            return this.writeToNBT(new NBTTagCompound());
//        }
//
//        @Override
//        @SideOnly(Side.CLIENT)
//        public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
//            // System.out.printf("[" + MOD_ID + "] Client: Received Update Packet for %s%n", pos);
//            NBTTagCompound tag = pkt.getNbtCompound();
//            readFromNBT(tag);
//            // Trigger render update on client after receiving data
//            RenderEventHandlerUltra.INSTANCE.addOrUpdateSphere(pos, this.energy, this.maxEnergy, this.stability);
//        }
//
//        // --- Render Bounding Box ---
//        @Override
//        @SideOnly(Side.CLIENT)
//        public AxisAlignedBB getRenderBoundingBox() {
//            // Ensure the sphere is rendered even if the block itself is outside the frustum
//            return new AxisAlignedBB(pos.add(-MAX_VISIBLE_RADIUS, -MAX_VISIBLE_RADIUS, -MAX_VISIBLE_RADIUS),
//                    pos.add(1 + MAX_VISIBLE_RADIUS, 1 + MAX_VISIBLE_RADIUS, 1 + MAX_VISIBLE_RADIUS));
//        }
//
//        @Override
//        public double getMaxRenderDistanceSquared() {
//            // Increase render distance significantly
//            return renderConfig.maxRenderDistance * renderConfig.maxRenderDistance;
//        }
//    }
//
//    // =========================================================================
//    // ProceduralSphereRendererUltra Class
//    // =========================================================================
//
//    @SideOnly(Side.CLIENT)
//    public static class ProceduralSphereRendererUltra {
//        private final BlockPos position;
//        private long energyStored;
//        private long maxEnergy;
//        private float stability;
//        private float baseRadius = 1.0f;
//        private float instabilityFactor = 0.0f;
//        private float time = 0.0f;
//        private float deltaTime = 0.0f;
//        private float lifetimeRatio = 1.0f;
//        private float flickerIntensity = 0.0f;
//        private Random random;
//
//        // Rotation axes and angles for different layers
//        public Vector3f coreRotationAxis = new Vector3f(0.1f, 1.0f, 0.2f);
//        public float coreRotationAngle = 0.0f;
//        public Vector3f swirlRotationAxis = new Vector3f(0.2f, 1.0f, 0.1f);
//        public float swirlRotationAngle = 0.0f;
//        public Vector3f outerRotationAxis = new Vector3f(0.0f, 1.0f, 0.3f);
//        public float outerRotationAngle = 0.0f;
//        public Vector3f shieldRotationAxis = new Vector3f(0.3f, 1.0f, 0.0f);
//        public float shieldRotationAngle = 0.0f;
//
//        // Layer renderers
//        private final List<ISphereLayerRendererUltra> layers = new ArrayList<>();
//
//        // Occlusion manager
//        private final SphereOcclusionManagerUltra occlusionManager;
//
//        public ProceduralSphereRendererUltra(BlockPos pos, long energy, long maxEnergy, float stability) {
//            this.position = pos;
//            this.energyStored = energy;
//            this.maxEnergy = maxEnergy;
//            this.stability = stability;
//            this.random = new Random(pos.hashCode());
//            this.occlusionManager = new SphereOcclusionManagerUltra(this);
//
//            // Initialize layers
//            if (renderConfig.enableLayerCore) {
//                layers.add(new CoreLayerUltra(this));
//            }
//            if (renderConfig.enableLayerSwirl) {
//                layers.add(new SwirlLayerUltra(this, renderConfig.swirlLayerCount));
//            }
//            if (renderConfig.enableLayerWave) {
//                layers.add(new OuterWaveLayerUltra(this));
//            }
//            if (renderConfig.enableLayerShield) {
//                layers.add(new ShieldLayerUltra(this));
//            }
//            if (renderConfig.enableLayerDistortion) {
//                layers.add(new DistortionLayerUltra(this));
//            }
//            if (renderConfig.enableLayerParticles) {
//                layers.add(new ParticleLayerUltra(this, renderConfig.particleMaxCount));
//            }
//            if (renderConfig.enableLayerArcs) {
//                layers.add(new EnergyArcLayerUltra(this, renderConfig.arcMaxCount));
//            }
//        }
//
//        // Getters
//        public BlockPos getPosition() { return position; }
//        public float getEnergyLevel() { return maxEnergy > 0 ? (float)energyStored / maxEnergy : 0.0f; }
//        public float getStability() { return stability; }
//        public float getBaseRadius() { return baseRadius; }
//        public float getInstabilityFactor() { return instabilityFactor; }
//        public float getTime() { return time; }
//        public float getDeltaTime() { return deltaTime; }
//        public float getLifetimeRatio() { return lifetimeRatio; }
//        public float getFlickerIntensity() { return flickerIntensity; }
//        public Random getRandom() { return random; }
//        public RenderConfig getConfig() { return renderConfig; }
//
//        // Update method
//        public void update(float dt) {
//            this.deltaTime = dt;
//            this.time += dt * renderConfig.animationBaseSpeed * (1.0f + getEnergyLevel() * renderConfig.animationEnergyScale);
//
//            // Update instability factor
//            this.instabilityFactor = (1.0f - stability) * renderConfig.instabilityEffectScale;
//
//            // Update flicker intensity
//            float flickerBase = (float)Math.sin(time * renderConfig.flickerFrequency) * 0.5f + 0.5f;
//            this.flickerIntensity = flickerBase * renderConfig.flickerAmplitude * (0.2f + instabilityFactor * 0.8f);
//
//            // Update rotation angles
//            this.coreRotationAngle = (time * 15.0f) % 360.0f;
//            this.swirlRotationAngle = (time * 10.0f) % 360.0f;
//            this.outerRotationAngle = (time * 5.0f) % 360.0f;
//            this.shieldRotationAngle = (time * 8.0f) % 360.0f;
//
//            // Update all layers
//            for (ISphereLayerRendererUltra layer : layers) {
//                layer.update(this);
//            }
//
//            // Update occlusion manager
//            occlusionManager.update(dt);
//        }
//
//        // Render method
//        public void render(int lodLevel) {
//            // Setup GL state
//            GlStateManager.pushMatrix();
//            GlStateManager.disableLighting();
//            GlStateManager.enableBlend();
//            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
//            GlStateManager.disableCull();
//
//            // Translate to block position
//            GlStateManager.translate(
//                    position.getX() + 0.5,
//                    position.getY() + 0.5,
//                    position.getZ() + 0.5
//            );
//
//            // Apply global scale based on energy level
//            float energyScale = 0.8f + getEnergyLevel() * 0.4f;
//            GlStateManager.scale(energyScale, energyScale, energyScale);
//
//            // Apply pulsation
//            float pulse = 1.0f + (float)Math.sin(time * renderConfig.pulseFrequency) * renderConfig.pulseAmplitude * getEnergyLevel();
//            GlStateManager.scale(pulse, pulse, pulse);
//
//            // Apply instability jitter
//            if (instabilityFactor > 0.1f) {
//                float jitterX = (random.nextFloat() - 0.5f) * 0.05f * instabilityFactor;
//                float jitterY = (random.nextFloat() - 0.5f) * 0.05f * instabilityFactor;
//                float jitterZ = (random.nextFloat() - 0.5f) * 0.05f * instabilityFactor;
//                GlStateManager.translate(jitterX, jitterY, jitterZ);
//            }
//
//            // Render all layers
//            for (ISphereLayerRendererUltra layer : layers) {
//                layer.render(this, lodLevel, occlusionManager);
//            }
//
//            // Restore GL state
//            GlStateManager.enableCull();
//            GlStateManager.disableBlend();
//            GlStateManager.enableLighting();
//            GlStateManager.popMatrix();
//        }
//
//        // Update energy and stability
//        public void updateEnergy(long energy, long maxEnergy, float stability) {
//            this.energyStored = energy;
//            this.maxEnergy = maxEnergy;
//            this.stability = stability;
//        }
//    }
//
//    // =========================================================================
//    // Layer Interface and Base Class
//    // =========================================================================
//
//    @SideOnly(Side.CLIENT)
//    public interface ISphereLayerRendererUltra {
//        void update(ProceduralSphereRendererUltra sphere);
//        void render(ProceduralSphereRendererUltra sphere, int lodLevel, SphereOcclusionManagerUltra occlusion);
//    }
//
//    @SideOnly(Side.CLIENT)
//    static abstract class BaseSphereLayerRendererUltra implements ISphereLayerRendererUltra {
//        protected final String layerName;
//        protected final ProceduralSphereRendererUltra parent;
//
//        public BaseSphereLayerRendererUltra(ProceduralSphereRendererUltra parent, String name) {
//            this.parent = parent;
//            this.layerName = name;
//        }
//
//        @Override
//        public void update(ProceduralSphereRendererUltra sphere) {
//            // Default implementation does nothing
//        }
//
//        protected void renderSphereSurface(int lodLevel, float radius, float alpha,
//                                           float hueShift, float saturation, float brightness,
//                                           float waveScale, int noiseOctaves,
//                                           SphereOcclusionManagerUltra occlusion) {
//            // Implementation would render a sphere surface with the given parameters
//            // This is a placeholder for the actual implementation
//        }
//    }
//
//    // =========================================================================
//    // Layer Implementations
//    // =========================================================================
//
//    // --- Layer 1: Core ---
//    @SideOnly(Side.CLIENT)
//    static class CoreLayerUltra extends BaseSphereLayerRendererUltra {
//        public CoreLayerUltra(ProceduralSphereRendererUltra parent) {
//            super(parent, "Core");
//        }
//
//        @Override
//        public void render(ProceduralSphereRendererUltra sphere, int lodLevel, SphereOcclusionManagerUltra occlusion) {
//            float energy = sphere.getEnergyLevel();
//            if (energy < 0.01f) return;
//
//            float coreScale = sphere.getBaseRadius() * renderConfig.coreSizeFactor;
//            float coreAlpha = renderConfig.coreBaseAlpha * energy;
//
//            GlStateManager.pushMatrix();
//            GlStateManager.rotate(sphere.coreRotationAngle, sphere.coreRotationAxis.x, sphere.coreRotationAxis.y, sphere.coreRotationAxis.z);
//
//            // Render main core
//            renderSphereSurface(lodLevel, coreScale, coreAlpha,
//                    renderConfig.coreHueShift, renderConfig.coreSaturation, renderConfig.coreBrightness,
//                    0.2f, // Low wave factor for core
//                    renderConfig.coreNoiseOctaves, occlusion);
//
//            // Render core glow at high energy
//            if (energy > 0.5f && renderConfig.enableCoreGlow) {
//                float glowIntensity = (energy - 0.5f) * 2.0f;
//                float glowScale = coreScale * renderConfig.coreGlowScaleFactor;
//                float glowAlpha = coreAlpha * renderConfig.coreGlowAlphaFactor * glowIntensity;
//                int glowLod = Math.min(lodLevel + renderConfig.coreGlowLodBias, MIN_LOD_LEVEL);
//
//                renderSphereSurface(glowLod, glowScale, glowAlpha,
//                        renderConfig.coreHueShift, renderConfig.coreSaturation * 0.8f, renderConfig.coreBrightness * 1.2f,
//                        0.1f, // Even lower wave factor for glow
//                        renderConfig.coreNoiseOctaves - 1, occlusion);
//            }
//
//            GlStateManager.popMatrix();
//        }
//    }
//
//    // --- Layer 2: Swirl ---
//    @SideOnly(Side.CLIENT)
//    static class SwirlLayerUltra extends BaseSphereLayerRendererUltra {
//        private final int layerCount;
//
//        public SwirlLayerUltra(ProceduralSphereRendererUltra parent, int layerCount) {
//            super(parent, "Swirl");
//            this.layerCount = layerCount;
//        }
//
//        @Override
//        public void render(ProceduralSphereRendererUltra sphere, int lodLevel, SphereOcclusionManagerUltra occlusion) {
//            float energy = sphere.getEnergyLevel();
//            if (energy < 0.01f) return;
//
//            float baseScale = sphere.getBaseRadius() * renderConfig.swirlSizeFactor;
//            float baseAlpha = renderConfig.swirlBaseAlpha * energy;
//            float instability = sphere.getInstabilityFactor();
//
//            // Render multiple swirl layers with different rotations
//            for (int i = 0; i < layerCount; i++) {
//                float layerRatio = (float) i / layerCount;
//                float scale = baseScale * (0.8f + layerRatio * 0.4f);
//                float alpha = baseAlpha * (1.0f - layerRatio * 0.3f);
//                float hueOffset = layerRatio * 0.2f;
//
//                GlStateManager.pushMatrix();
//                // Different rotation for each layer
//                float angle = sphere.swirlRotationAngle + layerRatio * 120.0f;
//                Vector3f axis = new Vector3f(
//                        sphere.swirlRotationAxis.x + layerRatio * 0.5f,
//                        sphere.swirlRotationAxis.y,
//                        sphere.swirlRotationAxis.z - layerRatio * 0.3f
//                );
//                GlStateManager.rotate(angle, axis.x, axis.y, axis.z);
//
//                // Add some wobble based on instability
//                if (instability > 0.2f) {
//                    float wobble = instability * 10.0f * (float) Math.sin(sphere.getTime() * (5.0f + i * 3.0f));
//                    GlStateManager.rotate(wobble, 1.0f, 0.0f, 0.0f);
//                    GlStateManager.rotate(wobble * 0.7f, 0.0f, 0.0f, 1.0f);
//                }
//
//                renderSphereSurface(lodLevel, scale, alpha,
//                        renderConfig.swirlHueShift + hueOffset, renderConfig.swirlSaturation, renderConfig.swirlBrightness,
//                        renderConfig.swirlWaveScale, // Medium wave factor
//                        renderConfig.swirlNoiseOctaves, occlusion);
//
//                GlStateManager.popMatrix();
//            }
//        }
//    }
//
//    // --- Layer 3: Outer Wave ---
//    @SideOnly(Side.CLIENT)
//    static class OuterWaveLayerUltra extends BaseSphereLayerRendererUltra {
//        public OuterWaveLayerUltra(ProceduralSphereRendererUltra parent) {
//            super(parent, "OuterWave");
//        }
//
//        @Override
//        public void render(ProceduralSphereRendererUltra sphere, int lodLevel, SphereOcclusionManagerUltra occlusion) {
//            float energy = sphere.getEnergyLevel();
//            if (energy < 0.01f) return;
//
//            float outerScale = sphere.getBaseRadius() * renderConfig.waveSizeFactor; // Usually 1.0
//            float outerAlpha = renderConfig.waveBaseAlpha * MathHelper.clamp(energy * 1.2f, 0.1f, 0.8f) * sphere.getStability();
//
//            GlStateManager.pushMatrix();
//            GlStateManager.rotate(sphere.outerRotationAngle, sphere.outerRotationAxis.x, sphere.outerRotationAxis.y, sphere.outerRotationAxis.z);
//
//            // Render main wave layer
//            renderSphereSurface(lodLevel, outerScale, outerAlpha,
//                    renderConfig.waveHueShift, renderConfig.waveSaturation, renderConfig.waveBrightness,
//                    1.0f, // Use full wave factor for this layer
//                    renderConfig.waveNoiseOctaves, occlusion);
//
//            // Render secondary "ripple" layer at high energy/instability
//            float instability = sphere.getInstabilityFactor();
//            float rippleThreshold = 0.5f;
//            if ((energy > 0.7f || instability > 0.6f) && renderConfig.enableWaveRipple) {
//                float rippleIntensity = Math.max(0, (energy - 0.6f) * 2.0f) + instability * 0.8f;
//                float rippleScale = outerScale * renderConfig.waveRippleScaleFactor;
//                float rippleAlpha = outerAlpha * renderConfig.waveRippleAlphaFactor * MathHelper.clamp(rippleIntensity, 0.0f, 1.0f);
//                float rippleWaveScale = 1.5f * (1.0f + instability * 0.5f);
//                int rippleLod = Math.min(lodLevel + renderConfig.waveRippleLodBias, MIN_LOD_LEVEL);
//
//                GlStateManager.pushMatrix();
//                // Faster, different rotation for ripples
//                GlStateManager.rotate(sphere.getTime() * -60.0f, 0.2f, 1.0f, -0.3f);
//                renderSphereSurface(rippleLod, rippleScale, rippleAlpha,
//                        renderConfig.waveHueShift + 0.1f, renderConfig.waveSaturation * 0.8f, renderConfig.waveBrightness * 1.1f,
//                        rippleWaveScale, renderConfig.waveNoiseOctaves, occlusion);
//                GlStateManager.popMatrix();
//            }
//
//            GlStateManager.popMatrix();
//        }
//    }
//
//    // --- Layer 4: Shield ---
//    @SideOnly(Side.CLIENT)
//    static class ShieldLayerUltra extends BaseSphereLayerRendererUltra {
//        private float currentShieldAlpha = 0.0f;
//        private float shieldHealth = 1.0f; // Example: shield takes damage
//
//        public ShieldLayerUltra(ProceduralSphereRendererUltra parent) {
//            super(parent, "Shield");
//        }
//
//        @Override
//        public void update(ProceduralSphereRendererUltra sphere) {
//            // Shield appears at high energy or low stability (configurable)
//            boolean shieldActive = sphere.getEnergyLevel() > renderConfig.shieldEnergyThreshold || sphere.getStability() < renderConfig.shieldStabilityThreshold;
//            float targetAlpha = shieldActive ? renderConfig.shieldBaseAlpha * shieldHealth : 0.0f;
//            // Smoothly interpolate alpha
//            currentShieldAlpha = MathUtils.lerp(0.1f * (60f * sphere.getDeltaTime()), currentShieldAlpha, targetAlpha); // Lerp based on frame time
//
//            // Example: shield flickers when hit (needs external trigger)
//            // if (shieldHit) shieldHealth -= 0.1f;
//            shieldHealth = MathHelper.clamp(shieldHealth + 0.01f * sphere.getDeltaTime(), 0.0f, 1.0f); // Regenerate slowly
//        }
//
//        @Override
//        public void render(ProceduralSphereRendererUltra sphere, int lodLevel, SphereOcclusionManagerUltra occlusion) {
//            if (currentShieldAlpha < 0.01f) return;
//
//            float shieldScale = sphere.getBaseRadius() * renderConfig.shieldSizeFactor;
//            float instability = sphere.getInstabilityFactor();
//            float flicker = sphere.getFlickerIntensity();
//
//            GlStateManager.pushMatrix();
//            GlStateManager.rotate(sphere.shieldRotationAngle, sphere.shieldRotationAxis.x, sphere.shieldRotationAxis.y, sphere.shieldRotationAxis.z);
//            // Apply jitter based on instability
//            float jitter = 1.0f + instability * 0.02f * MathHelper.sin(sphere.getTime() * 80.0f);
//            GlStateManager.scale(jitter, jitter, jitter);
//
//            // Use a slightly different color calculation for the shield
//            float hue = renderConfig.shieldHueShift + instability * 0.1f;
//            float saturation = renderConfig.shieldSaturation * (1.0f - instability * 0.3f);
//            float brightness = renderConfig.shieldBrightness * (0.8f + flicker * 0.4f) * shieldHealth;
//
//            // Render the shield surface - maybe less wavy than other layers
//            renderSphereSurface(lodLevel, shieldScale, currentShieldAlpha,
//                    hue, saturation, brightness,
//                    renderConfig.shieldWaveScale * instability, // Wave only when unstable
//                    renderConfig.shieldNoiseOctaves, occlusion);
//
//            GlStateManager.popMatrix();
//        }
//    }
//
//    // --- Layer 5: Distortion ---
//    // Note: Actual visual distortion is hard without shaders. This simulates it with alpha/color.
//    @SideOnly(Side.CLIENT)
//    static class DistortionLayerUltra extends BaseSphereLayerRendererUltra {
//        public DistortionLayerUltra(ProceduralSphereRendererUltra parent) {
//            super(parent, "Distortion");
//        }
//
//        @Override
//        public void render(ProceduralSphereRendererUltra sphere, int lodLevel, SphereOcclusionManagerUltra occlusion) {
//            float instability = sphere.getInstabilityFactor();
//            if (instability < 0.3f) return; // Only render when unstable
//
//            float distortionScale = sphere.getBaseRadius() * renderConfig.distortionSizeFactor;
//            float distortionAlpha = renderConfig.distortionBaseAlpha * (instability - 0.2f) * 1.5f;
//            distortionAlpha = MathHelper.clamp(distortionAlpha, 0.0f, 0.7f);
//
//            GlStateManager.pushMatrix();
//            // Fast, erratic rotation
//            GlStateManager.rotate(sphere.getTime() * 150.0f * instability, 1.0f, 1.0f, 1.0f);
//
//            // Render a semi-transparent layer with high wave factor to simulate distortion
//            renderSphereSurface(lodLevel + 1, // Lower LOD for performance
//                    distortionScale,
//                    distortionAlpha,
//                    renderConfig.distortionHueShift, // Use a neutral hue shift
//                    renderConfig.distortionSaturation, // Low saturation
//                    renderConfig.distortionBrightness, // Moderate brightness
//                    renderConfig.distortionWaveScale * instability, // Strong wave effect
//                    renderConfig.distortionNoiseOctaves, occlusion);
//
//            GlStateManager.popMatrix();
//        }
//    }
//
//    // --- Layer 6: Particles ---
//    @SideOnly(Side.CLIENT)
//    static class ParticleLayerUltra extends BaseSphereLayerRendererUltra {
//        private final int maxParticles;
//        private final List<EnergyParticleUltra> particles = new ArrayList<>();
//        private float particleSpawnTimer = 0;
//
//        public ParticleLayerUltra(ProceduralSphereRendererUltra parent, int maxParticles) {
//            super(parent, "Particles");
//            this.maxParticles = maxParticles;
//        }
//
//        @Override
//        public void update(ProceduralSphereRendererUltra sphere) {
//            float dt = sphere.getDeltaTime();
//            float energy = sphere.getEnergyLevel();
//            float instability = sphere.getInstabilityFactor();
//
//            // Update existing particles
//            particles.removeIf(p -> !p.update(dt, sphere));
//
//            // Spawn new particles
//            if (energy > 0.01f) {
//                particleSpawnTimer += dt;
//                // Spawn rate increases quadratically with energy and linearly with instability
//                float spawnRate = energy * energy * renderConfig.particleSpawnRateEnergyFactor
//                        + instability * renderConfig.particleSpawnRateInstabilityFactor;
//                float spawnInterval = 1.0f / (spawnRate + 0.01f);
//
//                while (particleSpawnTimer >= spawnInterval && particles.size() < maxParticles) {
//                    particles.add(new EnergyParticleUltra(sphere));
//                    particleSpawnTimer -= spawnInterval;
//                }
//            }
//        }
//
//        @Override
//        public void render(ProceduralSphereRendererUltra sphere, int lodLevel, SphereOcclusionManagerUltra occlusion) {
//            if (particles.isEmpty()) return;
//
//            Tessellator tessellator = Tessellator.getInstance();
//            BufferBuilder buffer = tessellator.getBuffer();
//            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR); // Particles as quads
//
//            // --- Billboard Setup ---
//            Matrix4f modelView = MatrixUtils.getOpenGLMatrix(GL11.GL_MODELVIEW_MATRIX);
//            Matrix4f cameraRotation = MatrixUtils.extractRotation(modelView);
//            cameraRotation.invert(); // Get rotation from world to camera space
//            Vector3f right = MatrixUtils.transform(cameraRotation, new Vector3f(1, 0, 0), null);
//            Vector3f up = MatrixUtils.transform(cameraRotation, new Vector3f(0, 1, 0), null);
//            //-----------------------
//
//            int renderedParticles = 0;
//            float globalAlpha = sphere.getLifetimeRatio();
//
//            for (EnergyParticleUltra p : particles) {
//                // Basic occlusion check for particle position
//                if (!occlusion.isPointVisible(new Vec3d(p.posX, p.posY, p.posZ))) {
//                    continue;
//                }
//
//                float particleAlpha = p.alpha * globalAlpha;
//                if (particleAlpha <= 0.01f) continue;
//
//                float r = p.colorR; float g = p.colorG; float b = p.colorB;
//                float size = p.size * renderConfig.particleSizeScale;
//
//                // Calculate quad corners using billboard vectors
//                Vector3f center = new Vector3f(p.posX, p.posY, p.posZ);
//                Vector3f rightScaled = VectorUtils.scale(right, size, null);
//                Vector3f upScaled = VectorUtils.scale(up, size, null);
//
//                Vector3f p1 = Vector3f.sub(center, Vector3f.add(rightScaled, upScaled, null), null);
//                Vector3f p2 = Vector3f.add(center, Vector3f.sub(rightScaled, upScaled, null), null);
//                Vector3f p3 = Vector3f.add(center, Vector3f.add(rightScaled, upScaled, null), null);
//                Vector3f p4 = Vector3f.sub(center, Vector3f.sub(rightScaled, upScaled, null), null);
//
//                // Add vertices to buffer
//                buffer.pos(p1.x, p1.y, p1.z).color(r, g, b, particleAlpha).endVertex();
//                buffer.pos(p2.x, p2.y, p2.z).color(r, g, b, particleAlpha).endVertex();
//                buffer.pos(p3.x, p3.y, p3.z).color(r, g, b, particleAlpha).endVertex();
//                buffer.pos(p4.x, p4.y, p4.z).color(r, g, b, particleAlpha).endVertex();
//                renderedParticles++;
//            }
//
//            // Draw if any particles were added
//            if (renderedParticles > 0) {
//                tessellator.draw();
//            }
//        }
//    }
//
//    // --- Layer 7: Energy Arcs ---
//    @SideOnly(Side.CLIENT)
//    static class EnergyArcLayerUltra extends BaseSphereLayerRendererUltra {
//        private final int maxArcs;
//        private final List<EnergyArcUltra> arcs = new ArrayList<>();
//        private float arcSpawnTimer = 0;
//
//        public EnergyArcLayerUltra(ProceduralSphereRendererUltra parent, int maxArcs) {
//            super(parent, "EnergyArcs");
//            this.maxArcs = maxArcs;
//        }
//
//        @Override
//        public void update(ProceduralSphereRendererUltra sphere) {
//            float dt = sphere.getDeltaTime();
//            float energy = sphere.getEnergyLevel();
//            float instability = sphere.getInstabilityFactor();
//
//            // Update existing arcs
//            arcs.removeIf(arc -> !arc.update(dt, sphere));
//
//            // Spawn new arcs (more likely when unstable)
//            if (instability > 0.1f || energy > 0.9f) { // Spawn condition
//                arcSpawnTimer += dt;
//                float spawnRate = (instability * renderConfig.arcSpawnRateInstabilityFactor)
//                        + (energy > 0.9f ? renderConfig.arcSpawnRateHighEnergyFactor : 0.0f);
//                float spawnInterval = 1.0f / (spawnRate + 0.01f);
//
//                while (arcSpawnTimer >= spawnInterval && arcs.size() < maxArcs) {
//                    arcs.add(new EnergyArcUltra(sphere));
//                    arcSpawnTimer -= spawnInterval;
//                }
//            }
//        }
//
//        @Override
//        public void render(ProceduralSphereRendererUltra sphere, int lodLevel, SphereOcclusionManagerUltra occlusion) {
//            if (arcs.isEmpty()) return;
//
//            Tessellator tessellator = Tessellator.getInstance();
//            BufferBuilder buffer = tessellator.getBuffer();
//
//            // Setup line rendering
//            GlStateManager.pushAttrib();
//            GL11.glPushAttrib(GL11.GL_LINE_BIT);
//            float baseLineWidth = renderConfig.arcBaseWidth * (0.8f + sphere.getEnergyLevel() * 1.2f);
//            GL11.glLineWidth(baseLineWidth);
//
//            buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR); // Use GL_LINES for segments
//
//            int renderedSegments = 0;
//            for (EnergyArcUltra arc : arcs) {
//                renderedSegments += arc.render(buffer, occlusion);
//            }
//
//            // Draw if any segments were added
//            if (renderedSegments > 0) {
//                tessellator.draw();
//            }
//
//            // Restore GL state
//            GL11.glPopAttrib();
//            GlStateManager.popAttrib();
//        }
//    }
//
//    // =========================================================================
//    // Particle & Arc Classes
//    // =========================================================================
//
//    // --- Energy Particle Class ---
//    @SideOnly(Side.CLIENT)
//    static class EnergyParticleUltra {
//        float posX, posY, posZ;
//        float velX, velY, velZ;
//        float size, targetSize;
//        float colorR, colorG, colorB;
//        float alpha;
//        float lifetime, maxLifetime;
//        float gravityFactor; // Positive = attract to center, Negative = repel
//        float swirlFactor;
//        float drag;
//
//        public EnergyParticleUltra(ProceduralSphereRendererUltra sphere) {
//            RenderConfig config = sphere.getConfig();
//            Random rand = sphere.getRandom();
//            float energy = sphere.getEnergyLevel();
//            float instability = sphere.getInstabilityFactor();
//
//            // Spawn position (on or near surface)
//            Vec3d spawnDir = VectorUtils.getRandomNormalizedVector(rand).toVec3d();
//            float spawnRadius = sphere.getBaseRadius() * (config.particleSpawnRadiusMin + rand.nextFloat() * (config.particleSpawnRadiusMax - config.particleSpawnRadiusMin));
//            this.posX = (float) spawnDir.x * spawnRadius;
//            this.posY = (float) spawnDir.y * spawnRadius;
//            this.posZ = (float) spawnDir.z * spawnRadius;
//
//            // Initial velocity (radial + tangential + instability burst)
//            float baseSpeed = config.particleBaseSpeed * (1.0f + energy * 1.5f);
//            float radialSpeed = baseSpeed * (0.3f + rand.nextFloat() * 0.7f);
//            float tangentSpeed = baseSpeed * (0.1f + rand.nextFloat() * 0.4f);
//            float instabilityBurst = baseSpeed * instability * (0.5f + rand.nextFloat());
//
//            // Calculate tangent direction (perpendicular to radial)
//            Vector3f radial = new Vector3f((float)spawnDir.x, (float)spawnDir.y, (float)spawnDir.z);
//            Vector3f up = new Vector3f(0, 1, 0);
//            Vector3f tangent = Vector3f.cross(radial, up, null);
//            if (tangent.length() < 0.1f) {
//                up = new Vector3f(1, 0, 0);
//                tangent = Vector3f.cross(radial, up, null);
//            }
//            tangent.normalise();
//
//            // Apply velocities
//            this.velX = radial.x * radialSpeed + tangent.x * tangentSpeed;
//            this.velY = radial.y * radialSpeed + tangent.y * tangentSpeed;
//            this.velZ = radial.z * radialSpeed + tangent.z * tangentSpeed;
//
//            // Add random instability burst
//            if (instability > 0.2f) {
//                this.velX += (rand.nextFloat() - 0.5f) * instabilityBurst;
//                this.velY += (rand.nextFloat() - 0.5f) * instabilityBurst;
//                this.velZ += (rand.nextFloat() - 0.5f) * instabilityBurst;
//            }
//
//            // Particle properties
//            this.maxLifetime = config.particleLifetimeMin + rand.nextFloat() * (config.particleLifetimeMax - config.particleLifetimeMin);
//            this.lifetime = this.maxLifetime;
//            this.alpha = config.particleAlphaMin + rand.nextFloat() * (config.particleAlphaMax - config.particleAlphaMin);
//            this.size = config.particleSizeMin + rand.nextFloat() * (config.particleSizeMax - config.particleSizeMin);
//            this.targetSize = this.size * (0.5f + rand.nextFloat());
//
//            // Color (HSB to RGB)
//            float hue = config.particleHueStart + rand.nextFloat() * config.particleHueRange;
//            float sat = config.particleSaturationMin + rand.nextFloat() * (config.particleSaturationMax - config.particleSaturationMin);
//            float bri = config.particleBrightnessMin + rand.nextFloat() * (config.particleBrightnessMax - config.particleBrightnessMin);
//
//            // Simple HSB to RGB conversion
//            int rgb = java.awt.Color.HSBtoRGB(hue, sat, bri);
//            this.colorR = ((rgb >> 16) & 0xFF) / 255.0f;
//            this.colorG = ((rgb >> 8) & 0xFF) / 255.0f;
//            this.colorB = (rgb & 0xFF) / 255.0f;
//
//            // Physics properties
//            this.gravityFactor = config.particleGravityFactor * (0.5f + rand.nextFloat());
//            this.swirlFactor = config.particleSwirlFactor * (0.5f + rand.nextFloat() * 1.5f);
//            this.drag = config.particleDragMin + rand.nextFloat() * (config.particleDragMax - config.particleDragMin);
//        }
//
//        public boolean update(float dt, ProceduralSphereRendererUltra sphere) {
//            // Update lifetime
//            lifetime -= dt;
//            if (lifetime <= 0) return false;
//
//            // Fade out near end of life
//            float lifeRatio = lifetime / maxLifetime;
//            if (lifeRatio < 0.3f) {
//                alpha *= (0.95f - 0.1f * (1.0f - lifeRatio / 0.3f));
//            }
//
//            // Interpolate size
//            size = MathUtils.lerp(0.1f, size, targetSize);
//
//            // Calculate center attraction/repulsion
//            float distSq = posX * posX + posY * posY + posZ * posZ;
//            float dist = (float) Math.sqrt(distSq);
//            if (dist > 0.001f) {
//                float force = gravityFactor * dt / dist;
//                velX -= posX * force / dist;
//                velY -= posY * force / dist;
//                velZ -= posZ * force / dist;
//            }
//
//            // Apply swirl (rotation around Y axis)
//            float swirlForce = swirlFactor * dt;
//            float tempX = velX;
//            velX += posZ * swirlForce;
//            velZ -= tempX * swirlForce;
//
//            // Apply drag
//            velX *= (1.0f - drag * dt);
//            velY *= (1.0f - drag * dt);
//            velZ *= (1.0f - drag * dt);
//
//            // Update position
//            posX += velX * dt;
//            posY += velY * dt;
//            posZ += velZ * dt;
//
//            return true;
//        }
//    }
//
//    // --- Energy Arc Class ---
//    @SideOnly(Side.CLIENT)
//    static class EnergyArcUltra {
//        private Vec3d startPoint;
//        private Vec3d endPoint;
//        private List<Vec3d> segments;
//        private float lifetime;
//        private float maxLifetime;
//        private float alpha;
//        private float colorR, colorG, colorB;
//        private int segmentCount;
//        private float noiseOffset;
//        private int updateCounter;
//
//        public EnergyArcUltra(ProceduralSphereRendererUltra sphere) {
//            Random rand = sphere.getRandom();
//            RenderConfig config = sphere.getConfig();
//
//            // Generate two random points on/near the sphere surface
//            float radius = sphere.getBaseRadius();
//            float startRadius = radius * (config.arcSpawnRadiusMin + rand.nextFloat() * (config.arcSpawnRadiusMax - config.arcSpawnRadiusMin));
//            float endRadius = radius * (config.arcSpawnRadiusMin + rand.nextFloat() * (config.arcSpawnRadiusMax - config.arcSpawnRadiusMin));
//
//            Vec3d startDir = VectorUtils.getRandomNormalizedVector(rand).toVec3d();
//            Vec3d endDir = VectorUtils.getRandomNormalizedVector(rand).toVec3d();
//
//            this.startPoint = startDir.scale(startRadius);
//            this.endPoint = endDir.scale(endRadius);
//
//            // Arc properties
//            this.maxLifetime = config.arcLifetimeMin + rand.nextFloat() * (config.arcLifetimeMax - config.arcLifetimeMin);
//            this.lifetime = this.maxLifetime;
//            this.alpha = config.arcAlphaMin + rand.nextFloat() * (config.arcAlphaMax - config.arcAlphaMin);
//            this.segmentCount = config.arcSegmentMin + rand.nextInt(config.arcSegmentMax - config.arcSegmentMin + 1);
//            this.noiseOffset = rand.nextFloat() * 1000.0f;
//            this.updateCounter = 0;
//
//            // Color (HSB to RGB)
//            float hue = config.arcHueStart + rand.nextFloat() * config.arcHueRange;
//            float sat = config.arcSaturationMin + rand.nextFloat() * (config.arcSaturationMax - config.arcSaturationMin);
//            float bri = config.arcBrightnessMin + rand.nextFloat() * (config.arcBrightnessMax - config.arcBrightnessMin);
//
//            // Simple HSB to RGB conversion
//            int rgb = java.awt.Color.HSBtoRGB(hue, sat, bri);
//            this.colorR = ((rgb >> 16) & 0xFF) / 255.0f;
//            this.colorG = ((rgb >> 8) & 0xFF) / 255.0f;
//            this.colorB = (rgb & 0xFF) / 255.0f;
//
//            // Generate initial segments
//            generateSegments(sphere);
//        }
//
//        private void generateSegments(ProceduralSphereRendererUltra sphere) {
//            segments = new ArrayList<>(segmentCount + 1);
//            segments.add(startPoint);
//
//            Vec3d direction = endPoint.subtract(startPoint);
//            double length = direction.lengthVector();
//            Vec3d unitDir = direction.normalize();
//
//            // Create a perpendicular vector for offset calculation
//            Vec3d perpA = new Vec3d(0, 1, 0);
//            if (Math.abs(unitDir.dotProduct(perpA)) > 0.9) {
//                perpA = new Vec3d(1, 0, 0);
//            }
//            Vec3d perpB = unitDir.crossProduct(perpA).normalize();
//            perpA = perpB.crossProduct(unitDir).normalize();
//
//            // Generate intermediate points with noise
//            float time = sphere.getTime();
//            float maxOffset = (float) length * renderConfig.arcMaxOffsetFactor;
//            for (int i = 1; i < segmentCount; i++) {
//                float ratio = (float) i / segmentCount;
//                Vec3d basePoint = startPoint.add(direction.scale(ratio));
//
//                // Apply noise-based offset
//                float noiseX = SimplexNoise.noise(ratio * renderConfig.arcNoiseFrequency + noiseOffset, time * renderConfig.arcNoiseSpeed);
//                float noiseY = SimplexNoise.noise(ratio * renderConfig.arcNoiseFrequency + noiseOffset + 100, time * renderConfig.arcNoiseSpeed);
//
//                float offsetScale = maxOffset * (float) Math.sin(Math.PI * ratio); // Maximum in the middle
//                Vec3d offset = perpA.scale(noiseX * offsetScale).add(perpB.scale(noiseY * offsetScale));
//
//                segments.add(basePoint.add(offset));
//            }
//
//            segments.add(endPoint);
//        }
//
//        public boolean update(float dt, ProceduralSphereRendererUltra sphere) {
//            // Update lifetime
//            lifetime -= dt;
//            if (lifetime <= 0) return false;
//
//            // Fade out near end of life
//            float lifeRatio = lifetime / maxLifetime;
//            if (lifeRatio < 0.5f) {
//                alpha = renderConfig.arcAlphaMax * lifeRatio / 0.5f;
//            }
//
//            // Periodically regenerate segments for animation
//            updateCounter++;
//            if (updateCounter >= renderConfig.arcUpdateFrequency) {
//                generateSegments(sphere);
//                updateCounter = 0;
//            }
//
//            return true;
//        }
//
//        public int render(BufferBuilder buffer, SphereOcclusionManagerUltra occlusion) {
//            if (segments == null || segments.size() < 2) return 0;
//
//            int renderedSegments = 0;
//            float currentAlpha = alpha;
//
//            // Render line segments
//            for (int i = 0; i < segments.size() - 1; i++) {
//                Vec3d p1 = segments.get(i);
//                Vec3d p2 = segments.get(i + 1);
//
//                // Skip if both endpoints are occluded
//                if (!occlusion.isPointVisible(p1) && !occlusion.isPointVisible(p2)) {
//                    continue;
//                }
//
//                // Vary alpha slightly along the arc for visual interest
//                float segmentAlpha = currentAlpha * (0.8f + 0.2f * (float) Math.sin(i * 0.5f));
//
//                buffer.pos(p1.x, p1.y, p1.z).color(colorR, colorG, colorB, segmentAlpha).endVertex();
//                buffer.pos(p2.x, p2.y, p2.z).color(colorR, colorG, colorB, segmentAlpha).endVertex();
//                renderedSegments++;
//            }
//
//            return renderedSegments;
//        }
//    }
//
//    // =========================================================================
//    // Occlusion Manager
//    // =========================================================================
//
//    @SideOnly(Side.CLIENT)
//    static class SphereOcclusionManagerUltra {
//        private final ProceduralSphereRendererUltra parent;
//        private Vec3d cameraPosition = Vec3d.ZERO;
//        private Vec3d sphereToCamera = Vec3d.ZERO;
//        private double distanceToCamera = 0;
//        private boolean isBehindCamera = false;
//
//        public SphereOcclusionManagerUltra(ProceduralSphereRendererUltra parent) {
//            this.parent = parent;
//        }
//
//        public void update(float dt) {
//            // Get camera position in world space
//            Entity renderViewEntity = Minecraft.getMinecraft().getRenderViewEntity();
//            if (renderViewEntity != null) {
//                double renderPosX = renderViewEntity.lastTickPosX + (renderViewEntity.posX - renderViewEntity.lastTickPosX) * dt;
//                double renderPosY = renderViewEntity.lastTickPosY + (renderViewEntity.posY - renderViewEntity.lastTickPosY) * dt;
//                double renderPosZ = renderViewEntity.lastTickPosZ + (renderViewEntity.posZ - renderViewEntity.lastTickPosZ) * dt;
//
//                cameraPosition = new Vec3d(renderPosX, renderPosY, renderPosZ);
//
//                // Calculate vector from sphere center to camera
//                BlockPos pos = parent.getPosition();
//                Vec3d sphereCenter = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
//                sphereToCamera = cameraPosition.subtract(sphereCenter);
//                distanceToCamera = sphereToCamera.lengthVector();
//
//                // Check if sphere is behind camera
//                Vec3d cameraLook = renderViewEntity.getLook(dt);
//                isBehindCamera = sphereToCamera.dotProduct(cameraLook) < 0;
//            }
//        }
//
//        public boolean isPointVisible(Vec3d point) {
//            // If camera is inside the sphere, everything is visible
//            if (distanceToCamera < parent.getBaseRadius()) {
//                return true;
//            }
//
//            // If sphere is behind camera, use simpler check
//            if (isBehindCamera) {
//                // Only render points on the side facing the camera
//                return point.dotProduct(sphereToCamera) > 0;
//            }
//
//            // Otherwise, check if point is on the visible hemisphere
//            return point.dotProduct(sphereToCamera) > 0;
//        }
//    }
//
//    // =========================================================================
//    // Render Config
//    // =========================================================================
//
//    public static class RenderConfig {
//        // General Settings
//        public boolean enableRendering = true;
//        public float maxRenderDistance = 128.0f;
//        public int lodBias = 0;
//        public long noiseSeed = 12345L;
//        public boolean logRenderStats = false;
//        public float sphereMaxLifetime = 60.0f; // Seconds
//
//        // Animation
//        public float animationBaseSpeed = 1.0f;
//        public float animationEnergyScale = 4.0f;
//        public float instabilityEffectScale = 1.0f;
//        public float pulseFrequency = 0.08f;
//        public float pulseAmplitude = 0.1f;
//        public float waveFrequency = 0.05f;
//        public float waveAmplitude = 0.8f;
//        public float flickerFrequency = 15.0f;
//        public float flickerAmplitude = 0.6f;
//
//        // Layer Enables
//        public boolean enableLayerCore = true;
//        public boolean enableCoreGlow = true;
//        public boolean enableLayerSwirl = true;
//        public boolean enableLayerWave = true;
//        public boolean enableLayerShield = true;
//        public boolean enableLayerDistortion = true;
//        public boolean enableLayerParticles = true;
//        public boolean enableLayerArcs = true;
//
//        // Core Layer
//        public float coreSizeFactor = 0.5f;
//        public float coreBaseAlpha = 0.95f;
//        public float coreHueShift = 0.0f;
//        public float coreSaturation = 1.0f;
//        public float coreBrightness = 1.1f;
//        public int coreNoiseOctaves = 3;
//        public float coreGlowScaleFactor = 1.1f;
//        public float coreGlowAlphaFactor = 0.5f;
//        public int coreGlowLodBias = 1;
//
//        // Swirl Layer
//        public int swirlLayerCount = 3;
//        public float swirlSizeFactor = 0.75f;
//        public float swirlBaseAlpha = 0.7f;
//        public float swirlHueShift = 0.1f;
//        public float swirlSaturation = 0.9f;
//        public float swirlBrightness = 1.0f;
//        public float swirlWaveScale = 0.6f;
//        public int swirlNoiseOctaves = 4;
//
//        // Wave Layer
//        public float waveSizeFactor = 1.0f;
//        public float waveBaseAlpha = 0.55f;
//        public float waveHueShift = 0.5f;
//        public float waveSaturation = 0.8f;
//        public float waveBrightness = 0.9f;
//        public int waveNoiseOctaves = 5;
//        public float waveDetailFrequency = 1.0f;
//        public float waveDetailAmplitude = 1.0f;
//        public boolean enableWaveRipple = true;
//        public float waveRippleScaleFactor = 1.03f;
//        public float waveRippleAlphaFactor = 0.7f;
//        public int waveRippleLodBias = 1;
//
//        // Shield Layer
//        public float shieldEnergyThreshold = 0.9f; // Activate above 90% energy
//        public float shieldStabilityThreshold = 0.3f; // Activate below 30% stability
//        public float shieldSizeFactor = 1.1f;
//        public float shieldBaseAlpha = 0.4f;
//        public float shieldHueShift = 0.8f; // Cyan/Blue shift
//        public float shieldSaturation = 0.6f;
//        public float shieldBrightness = 1.2f;
//        public float shieldWaveScale = 0.3f;
//        public int shieldNoiseOctaves = 2;
//
//        // Distortion Layer
//        public float distortionSizeFactor = 1.05f;
//        public float distortionBaseAlpha = 0.3f;
//        public float distortionHueShift = 0.0f;
//        public float distortionSaturation = 0.1f;
//        public float distortionBrightness = 0.8f;
//        public float distortionWaveScale = 2.5f;
//        public int distortionNoiseOctaves = 3;
//
//        // Particle Layer
//        public int particleMaxCount = 1500;
//        public float particleSpawnRateEnergyFactor = 800.0f;
//        public float particleSpawnRateInstabilityFactor = 400.0f;
//        public float particleSpawnRadiusMin = 0.7f;
//        public float particleSpawnRadiusMax = 1.2f;
//        public float particleBaseSpeed = 0.8f;
//        public float particleLifetimeMin = 1.5f;
//        public float particleLifetimeMax = 4.0f;
//        public float particleAlphaMin = 0.7f;
//        public float particleAlphaMax = 1.0f;
//        public float particleSizeMin = 0.01f;
//        public float particleSizeMax = 0.06f;
//        public float particleSizeScale = 1.0f;
//        public float particleHueStart = 0.5f;
//        public float particleHueRange = 0.4f;
//        public float particleSaturationMin = 0.7f;
//        public float particleSaturationMax = 1.0f;
//        public float particleBrightnessMin = 0.6f;
//        public float particleBrightnessMax = 1.0f;
//        public float particleGravityFactor = 0.05f; // Slight attraction to center
//        public float particleSwirlFactor = 0.3f;
//        public float particleDragMin = 0.05f;
//        public float particleDragMax = 0.2f;
//
//        // Arc Layer
//        public int arcMaxCount = 25;
//        public float arcSpawnRateInstabilityFactor = 30.0f;
//        public float arcSpawnRateHighEnergyFactor = 10.0f;
//        public float arcSpawnRadiusMin = 0.6f;
//        public float arcSpawnRadiusMax = 1.1f;
//        public float arcLifetimeMin = 0.2f;
//        public float arcLifetimeMax = 0.6f;
//        public int arcSegmentMin = 15;
//        public int arcSegmentMax = 30;
//        public float arcAlphaMin = 0.8f;
//        public float arcAlphaMax = 1.0f;
//        public float arcHueStart = 0.6f;
//        public float arcHueRange = 0.2f;
//        public float arcSaturationMin = 0.8f;
//        public float arcSaturationMax = 1.0f;
//        public float arcBrightnessMin = 0.9f;
//        public float arcBrightnessMax = 1.2f;
//        public float arcMaxOffsetFactor = 0.25f;
//        public float arcNoiseSpeed = 8.0f;
//        public float arcNoiseFrequency = 1.5f;
//        public int arcUpdateFrequency = 2; // Update segments every N ticks
//        public float arcBaseWidth = 1.5f;
//
//        // Load configuration from file (Example structure)
//        public void load(File configFile) {
//            Configuration config = new Configuration(configFile);
//            try {
//                config.load();
//                // Load general settings
//                enableRendering = config.getBoolean("Enable Rendering", "General", enableRendering, "Enable/disable the entire sphere rendering.");
//                maxRenderDistance = config.getFloat("Max Render Distance", "General", maxRenderDistance, 16.0f, 512.0f, "Maximum distance at which spheres are rendered.");
//                lodBias = config.getInt("LOD Bias", "General", lodBias, -MIN_LOD_LEVEL, MAX_LOD_LEVEL, "Adjust Level of Detail (-1 = higher detail, +1 = lower detail).");
//                noiseSeed = ConfigUtils.getLongFromConfig(config, "General", "Noise Seed", noiseSeed);
//                logRenderStats = config.getBoolean("Log Render Stats", "Debug", logRenderStats, "Log rendered triangle counts per layer to console.");
//                sphereMaxLifetime = config.getFloat("Sphere Max Lifetime", "General", sphereMaxLifetime, 10.0f, 600.0f, "Time in seconds before an un-updated sphere fades out.");
//
//                // Load animation settings...
//                // Load layer enable settings...
//                // Load settings for each layer...
//                // Load particle settings...
//                // Load arc settings...
//
//            } catch (Exception e) {
//                System.err.println("[" + MOD_ID + "] Error loading render configuration: " + e.getMessage());
//            } finally {
//                if (config.hasChanged()) {
//                    config.save();
//                }
//            }
//        }
//    }
//
//    // =========================================================================
//    // Simplex Noise Implementation
//    // =========================================================================
//
//    public static class SimplexNoise {
//        public static float noise(float x, float y) {
//            // Placeholder for actual simplex noise implementation
//            return (float) Math.sin(x * 0.1f) * (float) Math.cos(y * 0.1f);
//        }
//    }
//
//    // =========================================================================
//    // Matrix Utilities
//    // =========================================================================
//
//    public static class MatrixUtils {
//        public static Matrix4f getOpenGLMatrix(int matrixType) {
//            // Implementation would get the current OpenGL matrix
//            return new Matrix4f();
//        }
//
//        public static Matrix4f extractRotation(Matrix4f matrix) {
//            // Implementation would extract rotation component from matrix
//            return new Matrix4f();
//        }
//
//        public static Vector3f transform(Matrix4f matrix, Vector3f vec, Vector3f dest) {
//            // Implementation would transform a vector by a matrix
//            if (dest == null) {
//                dest = new Vector3f();
//            }
//            return dest;
//        }
//    }
//}
//// <<< Manus: End of Ultra-Large Procedural Sphere Renderer Code >>>
