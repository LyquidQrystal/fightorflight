package me.rufia.fightorflight.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.client.model.PokemonTransformingProjectileModel;
import me.rufia.fightorflight.entity.projectile.AbstractPokemonSpike;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;

import java.awt.*;

public class PokemonStickyWebRenderer extends EntityRenderer<AbstractPokemonSpike> {
    private static final ResourceLocation TEXTURE_LOCATION = ResourceLocation.fromNamespaceAndPath(CobblemonFightOrFlight.MODID, "textures/entity/sticky_web.png");
    private final PokemonTransformingProjectileModel<AbstractPokemonSpike> model;

    public PokemonStickyWebRenderer(EntityRendererProvider.Context context) {
        super(context);
        model = new PokemonTransformingProjectileModel<>(context.bakeLayer(PokemonTransformingProjectileModel.LAYER_LOCATION));

    }

    @Override
    public void render(AbstractPokemonSpike entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        int inGroundTick = entity.getInGroundTick();
        poseStack.pushPose();
        float h = inGroundTick == -1 ? (float) entity.tickCount + partialTicks : inGroundTick;
        float y = inGroundTick == -1 ? -1.5f : -1.45f;
        poseStack.scale(0.75f, 0.75f, 0.75f);
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.cos(h * 0.1F) * 180.0F));
        poseStack.translate(0f, y, 0f);
        VertexConsumer vertexConsumer = buffer.getBuffer(this.model.renderType(TEXTURE_LOCATION));
        Color color = Color.white;
        int colorCode = FastColor.ARGB32.colorFromFloat(0.5F, (float) color.getRed() / 255, (float) color.getGreen() / 255, (float) color.getBlue() / 255);
        if (inGroundTick == -1) {
            this.model.renderMain(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, colorCode);
        } else {
            this.model.renderSide(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, colorCode);
        }
        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(AbstractPokemonSpike entity) {
        return TEXTURE_LOCATION;
    }
}
