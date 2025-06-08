package com.ded.icwth.blocks.moleculartransformer.based.renders;



import com.ded.icwth.blocks.moleculartransformer.based.TileEntityMolecularTransformer;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class MolecularTransformerTESR extends TileEntitySpecialRenderer<TileEntityMolecularTransformer> {
    private static final ResourceLocation transfTextloc = new ResourceLocation("advanced_solar_panels", "textures/models/textureMolecularTransformer.png");
    private static final ResourceLocation plazmaTextloc = new ResourceLocation("advanced_solar_panels", "textures/models/plazma.png");
    private static final ResourceLocation particlesTextloc = new ResourceLocation("advanced_solar_panels", "textures/models/particles.png");
    public static final MolecularTransformerModel model = new MolecularTransformerModel();
    private static final TObjectIntMap<List<Serializable>> textureSizeCache = new TObjectIntHashMap();
    private static final IResourceManager resources = Minecraft.getMinecraft().getResourceManager();
    public static boolean drawActiveCore = false;
    public int ticker;

    public MolecularTransformerTESR() {
    }

    public static int getTextureSize(String s, int dv) {
        if (textureSizeCache.containsKey(Arrays.asList(s, dv))) {
            return textureSizeCache.get(Arrays.asList(s, dv));
        } else {
            try {
                InputStream inputstream = resources.getResource(new ResourceLocation("advanced_solar_panels", s)).getInputStream();
                if (inputstream == null) {
                    throw new FileNotFoundException("Image not found: " + s);
                } else {
                    int size = ImageIO.read(inputstream).getWidth() / dv;
                    textureSizeCache.put(Arrays.asList(s, dv), size);
                    return size;
                }
            } catch (Exception var4) {
                return 16;
            }
        }
    }

    public void renderCore(TileEntity te, double x, double y, double z) {
        ++this.ticker;
        if (this.ticker > 160) {
            this.ticker = 0;
        }

        int plazmaSize = getTextureSize(plazmaTextloc.getPath(), 64);
        int particleSize = getTextureSize(particlesTextloc.getPath(), 32);
        float rotationX = ActiveRenderInfo.getRotationX();
        float rotationXZ = ActiveRenderInfo.getRotationXZ();
        float rotationZ = ActiveRenderInfo.getRotationZ();
        float rotationYZ = ActiveRenderInfo.getRotationYZ();
        float rotationXY = ActiveRenderInfo.getRotationXY();
        float scaleCore = 0.35F;
        float posX = (float)x + 0.5F;
        float posY = (float)y + 0.5F;
        float posZ = (float)z + 0.5F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        Color colour = new Color(12648447);
        GL11.glPushMatrix();
        GL11.glDepthMask(false);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 1);
        this.bindTexture(plazmaTextloc);
        int phase = this.ticker % 16;
        float quadPlazmaSize = (float)(plazmaSize * 4);
        float plasmaEdge = (float)plazmaSize - 0.01F;
        float xBottom = ((float)(phase % 4 * plazmaSize) + 0.0F) / quadPlazmaSize;
        float xTop = ((float)(phase % 4 * plazmaSize) + plasmaEdge) / quadPlazmaSize;
        float yBottom = ((float)(phase / 4 * plazmaSize) + 0.0F) / quadPlazmaSize;
        float yTop = ((float)(phase / 4 * plazmaSize) + plasmaEdge) / quadPlazmaSize;
        buffer.begin(7, DefaultVertexFormats.BLOCK);
        GL11.glColor4f((float)colour.getRed() / 255.0F, (float)colour.getGreen() / 255.0F, (float)colour.getBlue() / 255.0F, 1.0F);
        buffer.pos((double)(posX - rotationX * scaleCore - rotationYZ * scaleCore), (double)(posY - rotationXZ * scaleCore), (double)(posZ - rotationZ * scaleCore - rotationXY * scaleCore)).tex((double)xTop, (double)yTop).endVertex();
        buffer.pos((double)(posX - rotationX * scaleCore + rotationYZ * scaleCore), (double)(posY + rotationXZ * scaleCore), (double)(posZ - rotationZ * scaleCore + rotationXY * scaleCore)).tex((double)xTop, (double)yBottom).endVertex();
        buffer.pos((double)(posX + rotationX * scaleCore + rotationYZ * scaleCore), (double)(posY + rotationXZ * scaleCore), (double)(posZ + rotationZ * scaleCore + rotationXY * scaleCore)).tex((double)xBottom, (double)yBottom).endVertex();
        buffer.pos((double)(posX + rotationX * scaleCore - rotationYZ * scaleCore), (double)(posY - rotationXZ * scaleCore), (double)(posZ + rotationZ * scaleCore - rotationXY * scaleCore)).tex((double)xBottom, (double)yTop).endVertex();
        tessellator.draw();
        GL11.glDisable(3042);
        GL11.glDepthMask(true);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glDepthMask(false);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 1);
        this.bindTexture(particlesTextloc);
        phase += 24;
        float octParticleSize = (float)(particleSize * 8);
        plasmaEdge = (float)particleSize - 0.01F;
        xBottom = ((float)(phase % 8 * particleSize) + 0.0F) / octParticleSize;
        xTop = ((float)(phase % 8 * particleSize) + plasmaEdge) / octParticleSize;
        yBottom = ((float)(phase / 8 * particleSize) + 0.0F) / octParticleSize;
        yTop = ((float)(phase / 8 * particleSize) + plasmaEdge) / octParticleSize;
        scaleCore = 0.4F + MathHelper.sin((float)this.ticker / 10.0F) * 0.1F;
        buffer.begin(7, DefaultVertexFormats.BLOCK);
        GlStateManager.disableLighting();
        buffer.pos((double)(posX - rotationX * scaleCore - rotationYZ * scaleCore), (double)(posY - rotationXZ * scaleCore), (double)(posZ - rotationZ * scaleCore - rotationXY * scaleCore)).tex((double)xTop, (double)yTop).color(255, 255, 255, 255).endVertex();
        buffer.pos((double)(posX - rotationX * scaleCore + rotationYZ * scaleCore), (double)(posY + rotationXZ * scaleCore), (double)(posZ - rotationZ * scaleCore + rotationXY * scaleCore)).tex((double)xTop, (double)yBottom).color(255, 255, 255, 255).endVertex();
        buffer.pos((double)(posX + rotationX * scaleCore + rotationYZ * scaleCore), (double)(posY + rotationXZ * scaleCore), (double)(posZ + rotationZ * scaleCore + rotationXY * scaleCore)).tex((double)xBottom, (double)yBottom).color(255, 255, 255, 255).endVertex();
        buffer.pos((double)(posX + rotationX * scaleCore - rotationYZ * scaleCore), (double)(posY - rotationXZ * scaleCore), (double)(posZ + rotationZ * scaleCore - rotationXY * scaleCore)).tex((double)xBottom, (double)yTop).color(255, 255, 255, 255).endVertex();
        GlStateManager.enableLighting();
        tessellator.draw();
        GL11.glDisable(3042);
        GL11.glDepthMask(true);
        GL11.glPopMatrix();
    }

    protected int getTileLighting(TileEntity tile, int lightValue) {
        if (tile != null && tile.hasWorld()) {
            return tile.getWorld().getCombinedLight(tile.getPos(), lightValue);
        } else {
            int blockLight = EnumSkyBlock.BLOCK.defaultLightValue;
            if (blockLight < lightValue) {
                blockLight = lightValue;
            }

            return EnumSkyBlock.SKY.defaultLightValue << 20 | blockLight << 4;
        }
    }

    public void render(TileEntityMolecularTransformer tileTransformer, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)x + 0.5F, (float)y + 1.5F, (float)z + 0.5F);
        if (destroyStage >= 0) {
            this.bindTexture(DESTROY_STAGES[destroyStage]);
            GlStateManager.matrixMode(5890);
            GlStateManager.pushMatrix();
            GlStateManager.scale(4.0F, 4.0F, 1.0F);
            GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
            GlStateManager.matrixMode(5888);
        }

        GlStateManager.pushMatrix();
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        this.bindTexture(transfTextloc);
        model.render((Entity)null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
        GlStateManager.popMatrix();
        if (destroyStage >= 0) {
            return;
        }

        GlStateManager.popMatrix();
        if (tileTransformer != null && drawActiveCore && tileTransformer.isActive()) {
            GL11.glPushMatrix();
            GlStateManager.pushAttrib();
            this.renderCore(tileTransformer, x, y, z);
            GlStateManager.popAttrib();
            GL11.glPopMatrix();
        }

    }
}
