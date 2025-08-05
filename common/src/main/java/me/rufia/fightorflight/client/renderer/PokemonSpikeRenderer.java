package me.rufia.fightorflight.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.client.model.PokemonSpikeModel;
import me.rufia.fightorflight.entity.PokemonAttackEffect;
import me.rufia.fightorflight.entity.projectile.PokemonSpike;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;

import java.awt.*;

public class PokemonSpikeRenderer extends EntityRenderer<PokemonSpike> {
    private static final ResourceLocation TEXTURE_LOCATION = ResourceLocation.fromNamespaceAndPath(CobblemonFightOrFlight.MODID, "textures/entity/spike.png");
    private static final RenderType RENDER_TYPE;
    private final PokemonSpikeModel<PokemonSpike> model;

    public PokemonSpikeRenderer(EntityRendererProvider.Context context) {
        super(context);
        model = new PokemonSpikeModel<>(context.bakeLayer(PokemonSpikeModel.LAYER_LOCATION));
    }

    @Override
    public void render(PokemonSpike entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight){
        Color color = getColor(entity);
        poseStack.pushPose();
        float f = Mth.rotLerp(partialTicks, entity.yRotO, entity.getYRot());
        float g = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());
        float h = (float) entity.tickCount + partialTicks;
        poseStack.translate(0.0F, 0.15F, 0.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.sin(h * 0.1F) * 180.0F));
        poseStack.mulPose(Axis.XP.rotationDegrees(Mth.cos(h * 0.1F) * 180.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.sin(h * 0.15F) * 360.0F));
        poseStack.scale(-0.5F, -0.5F, 0.5F);
        this.model.setupAnim(entity, 0.0F, 0.0F, 0.0F, f, g);
        VertexConsumer vertexConsumer = buffer.getBuffer(this.model.renderType(TEXTURE_LOCATION));
        this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.scale(1.5F, 1.5F, 1.5F);
        VertexConsumer vertexConsumer2 = buffer.getBuffer(RENDER_TYPE);
        int colorCode = FastColor.ARGB32.colorFromFloat((float) color.getRed() / 255, (float) color.getGreen() / 255, (float) color.getBlue() / 255, 0.75F);
        this.model.renderToBuffer(poseStack, vertexConsumer2, packedLight, OverlayTexture.NO_OVERLAY, colorCode);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    public Color getColor(PokemonSpike entity){
        return Color.white;
    }

    @Override
    public ResourceLocation getTextureLocation(PokemonSpike entity) {
        return TEXTURE_LOCATION;
    }

    static {
        RENDER_TYPE = RenderType.entityTranslucent(TEXTURE_LOCATION);
    }
}
