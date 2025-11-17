package me.rufia.fightorflight.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.client.model.PokemonAreaEffectMagicModel;
import me.rufia.fightorflight.client.model.PokemonAreaEffectTornadoModel;
import me.rufia.fightorflight.entity.PokemonAttackEffect;
import me.rufia.fightorflight.entity.areaeffect.AbstractPokemonAreaEffect;
import me.rufia.fightorflight.entity.areaeffect.PokemonAreaEffectMagic;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class PokemonAreaEffectMagicRenderer extends EntityRenderer<AbstractPokemonAreaEffect> {
    private static final ResourceLocation LIGHTNING_TEXTURE_LOCATION = ResourceLocation.fromNamespaceAndPath(CobblemonFightOrFlight.MODID, "textures/entity/lightning.png");
    private static final ResourceLocation ERUPTION_TEXTURE_LOCATION = ResourceLocation.fromNamespaceAndPath(CobblemonFightOrFlight.MODID, "textures/entity/eruption.png");
    private static final ResourceLocation MAGIC_TEXTURE_LOCATION = ResourceLocation.fromNamespaceAndPath(CobblemonFightOrFlight.MODID, "textures/entity/magic.png");
    private static final List<String> specialTypes = Arrays.asList("fire", "ground", "electric");
    private final PokemonAreaEffectMagicModel<AbstractPokemonAreaEffect> model;

    public PokemonAreaEffectMagicRenderer(EntityRendererProvider.Context context) {
        super(context);
        model = new PokemonAreaEffectMagicModel<>(context.bakeLayer(PokemonAreaEffectMagicModel.LAYER_LOCATION));
    }

    @Override
    public void render(AbstractPokemonAreaEffect entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        boolean isWaiting = entity.isWaiting();
        poseStack.pushPose();
        ///float tick = entity.tickCount + partialTicks;
        float d = entity.getRadius() * 2;
        //float rotSpeed = isWaiting ? 0.2f : (entity.isActivated() ? 0.1f : 0.3f);
        poseStack.scale(d, d, d);
        //poseStack.mulPose(Axis.YP.rotationDegrees(Mth.cos(tick * rotSpeed) * 180f));
        poseStack.mulPose(Axis.ZP.rotationDegrees(180f));
        poseStack.translate(0, -1.55, 0);
        VertexConsumer vertexConsumer = buffer.getBuffer(this.model.renderType(getTextureLocation(entity)));
        Color color = hasSpecialTexture(entity) ? Color.WHITE : PokemonAttackEffect.getColorFromType(entity.getElementalType());
        int colorCode = FastColor.ARGB32.colorFromFloat(0.4F, (float) color.getRed() / 255, (float) color.getGreen() / 255, (float) color.getBlue() / 255);
        model.renderBottom(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, colorCode);
        if (!isWaiting) {
            model.renderBeam(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, colorCode);
        }
        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(AbstractPokemonAreaEffect entity) {
        String typeName = entity.getElementalType();
        return switch (typeName) {
            case "electric" -> LIGHTNING_TEXTURE_LOCATION;
            case "ground", "fire" -> ERUPTION_TEXTURE_LOCATION;
            default -> MAGIC_TEXTURE_LOCATION;
        };
    }

    private boolean hasSpecialTexture(AbstractPokemonAreaEffect entity) {
        String typeName = entity.getElementalType();
        return specialTypes.contains(typeName);
    }
}
