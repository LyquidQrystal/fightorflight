package me.rufia.fightorflight.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.entity.areaeffect.AbstractPokemonAreaEffect;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

public class PokemonAreaEffectTornadoModel<T extends AbstractPokemonAreaEffect> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(CobblemonFightOrFlight.MODID, "tornado"), "main");
    private final ModelPart tornado;
    private final ModelPart pre;

    public PokemonAreaEffectTornadoModel(ModelPart root) {
        this.tornado = root.getChild("tornado");
        this.pre = root.getChild("pre");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition tornado = partdefinition.addOrReplaceChild("tornado", CubeListBuilder.create().texOffs(0, 55).addBox(-4.0F, -1.0F, -4.0F, 8.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(0, 48).addBox(-3.0F, -2.0F, -3.0F, 6.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(0, 40).addBox(-2.0F, -6.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(0, 33).addBox(-3.0F, -7.0F, -3.0F, 6.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(0, 23).addBox(-4.0F, -9.0F, -4.0F, 8.0F, 2.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(56, 51).addBox(-5.0F, -12.0F, -5.0F, 10.0F, 3.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(32, 33).addBox(-8.0F, -14.0F, -8.0F, 16.0F, 2.0F, 16.0F, new CubeDeformation(0.0F))
                .texOffs(40, 18).addBox(-7.0F, -15.0F, -7.0F, 14.0F, 1.0F, 14.0F, new CubeDeformation(0.0F))
                .texOffs(48, 5).addBox(-6.0F, -16.0F, -6.0F, 12.0F, 1.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition pre = partdefinition.addOrReplaceChild("pre", CubeListBuilder.create().texOffs(-16, 0).addBox(-8.0F, 0.0F, -8.0F, 16.0F, 0.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 96, 64);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        tornado.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        pre.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }

    public void renderPreEffect(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        pre.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }

    public void renderTornado(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        tornado.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}
