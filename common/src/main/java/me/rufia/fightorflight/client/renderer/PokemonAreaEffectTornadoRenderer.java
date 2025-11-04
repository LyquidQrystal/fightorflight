package me.rufia.fightorflight.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.client.model.PokemonAreaEffectTornadoModel;
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

public class PokemonAreaEffectTornadoRenderer extends EntityRenderer<AbstractPokemonAreaEffect> {
    private static final ResourceLocation TEXTURE_LOCATION = ResourceLocation.fromNamespaceAndPath(CobblemonFightOrFlight.MODID, "textures/entity/tornado.png");
    private final PokemonAreaEffectTornadoModel<AbstractPokemonAreaEffect> model;

    public PokemonAreaEffectTornadoRenderer(EntityRendererProvider.Context context) {
        super(context);
        model = new PokemonAreaEffectTornadoModel<>(context.bakeLayer(PokemonAreaEffectTornadoModel.LAYER_LOCATION));
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
        poseStack.mulPose(Axis.ZP.rotationDegrees(180f));
        poseStack.translate(0, -1.55, 0);//-1.44 too low, -1.92 too high
        VertexConsumer vertexConsumer = buffer.getBuffer(this.model.renderType(TEXTURE_LOCATION));
        Color color = PokemonAttackEffect.getColorFromType(entity.getElementalType());
        int colorCode = FastColor.ARGB32.colorFromFloat(0.4F, (float) color.getRed() / 255, (float) color.getGreen() / 255, (float) color.getBlue() / 255);
        if (isWaiting) {
            model.renderPreEffect(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, colorCode);
        } else {
            model.renderTornado(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, colorCode);
        }
        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(AbstractPokemonAreaEffect entity) {
        return TEXTURE_LOCATION;
    }
}
