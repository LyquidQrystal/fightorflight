package me.rufia.fightorflight.client.renderer;

import com.cobblemon.mod.common.api.moves.Move;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.PokemonInterface;
import me.rufia.fightorflight.entity.PokemonAttackEffect;
import me.rufia.fightorflight.utils.PokemonUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.awt.*;

//This class hasn't been used!
public class PokemonPostRenderer {
    private static final ResourceLocation BEAM_LOCATION = ResourceLocation.fromNamespaceAndPath(CobblemonFightOrFlight.MODID, "textures/entity/beam.png");
    private static final RenderType BEAM_RENDER_TYPE;

    private static Vec3 getPosition(LivingEntity livingEntity, double yOffset, float partialTick) {
        double d = Mth.lerp((double) partialTick, livingEntity.xOld, livingEntity.getX());
        double e = Mth.lerp((double) partialTick, livingEntity.yOld, livingEntity.getY()) + yOffset;
        double f = Mth.lerp((double) partialTick, livingEntity.zOld, livingEntity.getZ());
        return new Vec3(d, e, f);
    }

    public static void postRender(PokemonEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        LivingEntity livingEntity = entity.getTarget();
        int attackTime = ((PokemonInterface) entity).getAttackTime();
        boolean enabled = ((PokemonInterface) entity).usingBeam() && !entity.isBattling();
        if (livingEntity != null) {
            if (livingEntity.isAlive() && attackTime > 0 && enabled) {
                Move move = PokemonUtils.getMove(entity);
                if (move == null) {
                    CobblemonFightOrFlight.LOGGER.info("Trying to use a null move");
                    return;
                }
                float g = 40 + partialTicks + attackTime;//entity.getClientSideAttackTime() + partialTicks;
                Color color = PokemonAttackEffect.getColorFromType(move.getType().getName());
                float h = g * 0.5F % 1.0F;
                float i = entity.getEyeHeight();
                poseStack.pushPose();
                poseStack.translate(0.0F, i, 0.0F);
                Vec3 vec3 = getPosition(livingEntity, (double) livingEntity.getBbHeight() * 0.5, partialTicks);
                Vec3 vec32 = getPosition(entity, i, partialTicks);
                Vec3 vec33 = vec3.subtract(vec32);
                float j = (float) (vec33.length() + 1.0);
                vec33 = vec33.normalize();
                float k = (float) Math.acos(vec33.y);
                float l = (float) Math.atan2(vec33.z, vec33.x);
                poseStack.mulPose(Axis.YP.rotationDegrees((1.5707964F - l) * 57.295776F));
                poseStack.mulPose(Axis.XP.rotationDegrees(k * 57.295776F));
                boolean m = true;
                float n = g * 0.05F * -1.5F;
                int p = color.getRed();
                int q = color.getGreen();
                int r = color.getBlue();
                float s = 0.2F;
                float t = 0.282F;
                float u = Mth.cos(n + 2.3561945F) * 0.282F;
                float v = Mth.sin(n + 2.3561945F) * 0.282F;
                float w = Mth.cos(n + 0.7853982F) * 0.282F;
                float x = Mth.sin(n + 0.7853982F) * 0.282F;
                float y = Mth.cos(n + 3.926991F) * 0.282F;
                float z = Mth.sin(n + 3.926991F) * 0.282F;
                float aa = Mth.cos(n + 5.4977875F) * 0.282F;
                float ab = Mth.sin(n + 5.4977875F) * 0.282F;
                float ac = Mth.cos(n + 3.1415927F) * 0.2F;
                float ad = Mth.sin(n + 3.1415927F) * 0.2F;
                float ae = Mth.cos(n + 0.0F) * 0.2F;
                float af = Mth.sin(n + 0.0F) * 0.2F;
                float ag = Mth.cos(n + 1.5707964F) * 0.2F;
                float ah = Mth.sin(n + 1.5707964F) * 0.2F;
                float ai = Mth.cos(n + 4.712389F) * 0.2F;
                float aj = Mth.sin(n + 4.712389F) * 0.2F;
                float al = 0.0F;
                float am = 0.4999F;
                float an = -1.0F + h;
                float ao = j * 2.5F + an;
                VertexConsumer vertexConsumer = buffer.getBuffer(BEAM_RENDER_TYPE);
                PoseStack.Pose pose = poseStack.last();

                vertex(vertexConsumer, pose, ac, j, ad, p, q, r, 0.4999F, ao);
                vertex(vertexConsumer, pose, ac, 0.0F, ad, p, q, r, 0.4999F, an);
                vertex(vertexConsumer, pose, ae, 0.0F, af, p, q, r, 0.0F, an);
                vertex(vertexConsumer, pose, ae, j, af, p, q, r, 0.0F, ao);
                vertex(vertexConsumer, pose, ag, j, ah, p, q, r, 0.4999F, ao);
                vertex(vertexConsumer, pose, ag, 0.0F, ah, p, q, r, 0.4999F, an);
                vertex(vertexConsumer, pose, ai, 0.0F, aj, p, q, r, 0.0F, an);
                vertex(vertexConsumer, pose, ai, j, aj, p, q, r, 0.0F, ao);
                float ap = 0.0F;
                if (entity.tickCount % 2 == 0) {
                    ap = 0.5F;
                }

                vertex(vertexConsumer, pose, u, j, v, p, q, r, 0.5F, ap + 0.5F);
                vertex(vertexConsumer, pose, w, j, x, p, q, r, 1.0F, ap + 0.5F);
                vertex(vertexConsumer, pose, aa, j, ab, p, q, r, 1.0F, ap);
                vertex(vertexConsumer, pose, y, j, z, p, q, r, 0.5F, ap);
                poseStack.popPose();
            }
        }
    }

    private static void vertex(VertexConsumer consumer, PoseStack.Pose pose, float x, float y, float z, int red, int green, int blue, float u, float v) {
        consumer.addVertex(pose, x, y, z).setColor(red, green, blue, 255).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(15728880).setNormal(pose, 0.0F, 1.0F, 0.0F);
    }

    static {
        BEAM_RENDER_TYPE = RenderType.entityCutoutNoCull(BEAM_LOCATION);
    }
}
