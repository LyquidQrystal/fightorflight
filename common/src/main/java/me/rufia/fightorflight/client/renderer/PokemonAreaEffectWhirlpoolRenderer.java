package me.rufia.fightorflight.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.client.model.PokemonAreaEffectWhirlpoolModel;
import me.rufia.fightorflight.entity.PokemonAttackEffect;
import me.rufia.fightorflight.entity.areaeffect.AbstractPokemonAreaEffect;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;

import java.awt.*;

public class PokemonAreaEffectWhirlpoolRenderer extends EntityRenderer<AbstractPokemonAreaEffect> {
    private static final ResourceLocation TEXTURE_LOCATION = ResourceLocation.fromNamespaceAndPath(CobblemonFightOrFlight.MODID, "textures/entity/whirlpool.png");
    private final PokemonAreaEffectWhirlpoolModel<AbstractPokemonAreaEffect> model;

    public PokemonAreaEffectWhirlpoolRenderer(EntityRendererProvider.Context context) {
        super(context);
        model = new PokemonAreaEffectWhirlpoolModel<>(context.bakeLayer(PokemonAreaEffectWhirlpoolModel.LAYER_LOCATION));
    }

    @Override
    public void render(AbstractPokemonAreaEffect entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        boolean isWaiting = entity.isWaiting();
        poseStack.pushPose();
        float tick = entity.tickCount + partialTicks;
        float d = entity.getRadius() * 2;
        float rotSpeed = isWaiting ? 0.2f : (entity.isActivated() ? 0.1f : 0.3f);
        poseStack.scale(d, d, d);
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.cos(tick * rotSpeed) * 180f));
        poseStack.translate(0, 0.05, 0);
        VertexConsumer vertexConsumer = buffer.getBuffer(this.model.renderType(TEXTURE_LOCATION));
        Color color = PokemonAttackEffect.getColorFromType(entity.getElementalType());
        int colorCode = FastColor.ARGB32.colorFromFloat(0.4F, (float) color.getRed() / 255, (float) color.getGreen() / 255, (float) color.getBlue() / 255);
        model.renderPane(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, colorCode);
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.cos(tick * rotSpeed * 0.3f) * 180f));
        poseStack.mulPose(Axis.XP.rotationDegrees(180f));
        if (!isWaiting) {
            model.renderEffect(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, colorCode);
        }
        poseStack.popPose();

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(AbstractPokemonAreaEffect entity) {
        return TEXTURE_LOCATION;
    }
}

