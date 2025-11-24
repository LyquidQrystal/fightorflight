package me.rufia.fightorflight.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.client.model.PokemonAreaEffectMagicModel;
import me.rufia.fightorflight.entity.PokemonAttackEffect;
import me.rufia.fightorflight.entity.areaeffect.PokemonAreaEffectMagic;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class PokemonAreaEffectMagicRenderer extends EntityRenderer<PokemonAreaEffectMagic> implements RenderLayerParent<PokemonAreaEffectMagic, PokemonAreaEffectMagicModel<PokemonAreaEffectMagic>> {
    private static final ResourceLocation LIGHTNING_TEXTURE_LOCATION = ResourceLocation.fromNamespaceAndPath(CobblemonFightOrFlight.MODID, "textures/entity/lightning.png");
    private static final ResourceLocation ERUPTION_TEXTURE_LOCATION = ResourceLocation.fromNamespaceAndPath(CobblemonFightOrFlight.MODID, "textures/entity/eruption.png");
    private static final ResourceLocation MAGIC_TEXTURE_LOCATION = ResourceLocation.fromNamespaceAndPath(CobblemonFightOrFlight.MODID, "textures/entity/magic.png");
    private static final ResourceLocation ERUPTION_FIRE_TEXTURE_LOCATION = ResourceLocation.fromNamespaceAndPath(CobblemonFightOrFlight.MODID, "textures/entity/eruption_fire.png");
    private static final List<String> specialTypes = Arrays.asList("fire", "ground", "electric");
    private final PokemonAreaEffectMagicModel<PokemonAreaEffectMagic> model;

    public PokemonAreaEffectMagicRenderer(EntityRendererProvider.Context context) {
        super(context);
        model = new PokemonAreaEffectMagicModel<>(context.bakeLayer(PokemonAreaEffectMagicModel.LAYER_LOCATION));
    }

    @Override
    public void render(PokemonAreaEffectMagic entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        boolean isWaiting = entity.isWaiting();
        poseStack.pushPose();
        float d = entity.getRadius() * 2;
        poseStack.scale(d, d, d);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180f));
        poseStack.translate(0, -0.05, 0);
        VertexConsumer vertexConsumer = buffer.getBuffer(model.renderType(getTextureLocation(entity)));
        Color color = getColor(entity);
        int colorCode = FastColor.ARGB32.colorFromFloat(0.4F, (float) color.getRed() / 255, (float) color.getGreen() / 255, (float) color.getBlue() / 255);
        model.renderBottom(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, colorCode);
        if (!isWaiting) {
            int waitTime = entity.getWaitTime();
            float tick = entity.tickCount + partialTicks - waitTime;
            float yScale;
            if (entity.getElementalType().equals("electric")) {
                poseStack.translate(0, -2, 0);
                yScale = Mth.clampedLerp(0.2f, 1f, tick / 4);
                poseStack.scale(1, yScale, 1);
                model.renderBeam(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, colorCode);
            } else {
                yScale = -Mth.clampedLerp(0.2f, 1f, tick / 4);
                poseStack.translate(0, 0.2, 0);
                poseStack.scale(1, yScale, 1);
                model.renderBeam(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, colorCode);
                VertexConsumer vertexConsumer1 = buffer.getBuffer(RenderType.entityTranslucentEmissive(ERUPTION_FIRE_TEXTURE_LOCATION));
                model.renderBeam(poseStack, vertexConsumer1, packedLight, OverlayTexture.NO_OVERLAY, colorCode);
            }
        }
        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(PokemonAreaEffectMagic entity) {
        String typeName = entity.getElementalType();
        return switch (typeName) {
            case "electric" -> LIGHTNING_TEXTURE_LOCATION;
            case "ground", "fire" -> ERUPTION_TEXTURE_LOCATION;
            default -> MAGIC_TEXTURE_LOCATION;
        };
    }

    public static boolean hasSpecialTexture(PokemonAreaEffectMagic entity) {
        String typeName = entity.getElementalType();
        return specialTypes.contains(typeName);
    }

    public static Color getColor(PokemonAreaEffectMagic entity) {
        return hasSpecialTexture(entity) ? Color.WHITE : PokemonAttackEffect.getColorFromType(entity.getElementalType());
    }

    @Override
    public PokemonAreaEffectMagicModel<PokemonAreaEffectMagic> getModel() {
        return model;
    }
}
