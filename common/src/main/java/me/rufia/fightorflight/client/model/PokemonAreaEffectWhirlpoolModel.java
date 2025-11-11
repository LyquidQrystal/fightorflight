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

public class PokemonAreaEffectWhirlpoolModel<T extends AbstractPokemonAreaEffect> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(CobblemonFightOrFlight.MODID, "whirlpool"), "main");
    private final ModelPart effect;
    private final ModelPart pane;

    public PokemonAreaEffectWhirlpoolModel(ModelPart root) {
        this.effect = root.getChild("effect");
        this.pane = root.getChild("pane");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition effect = partdefinition.addOrReplaceChild("effect", CubeListBuilder.create().texOffs(0, 10).addBox(0.0F, 0.0F, 0.0F, 0.0F, 2.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition e8_r1 = effect.addOrReplaceChild("e8_r1", CubeListBuilder.create().texOffs(0, 10).addBox(0.0F, 0.0F, 0.0F, 0.0F, 2.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 2.3562F, 0.0F));

        PartDefinition e7_r1 = effect.addOrReplaceChild("e7_r1", CubeListBuilder.create().texOffs(0, 10).addBox(0.0F, 0.0F, 0.0F, 0.0F, 2.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        PartDefinition e6_r1 = effect.addOrReplaceChild("e6_r1", CubeListBuilder.create().texOffs(0, 10).addBox(0.0F, 0.0F, 0.0F, 0.0F, 2.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.7854F, 0.0F));

        PartDefinition e5_r1 = effect.addOrReplaceChild("e5_r1", CubeListBuilder.create().texOffs(0, 10).addBox(0.0F, 0.0F, 0.0F, 0.0F, 2.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        PartDefinition e4_r1 = effect.addOrReplaceChild("e4_r1", CubeListBuilder.create().texOffs(0, 10).addBox(0.0F, 0.0F, 0.0F, 0.0F, 2.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -2.3562F, 0.0F));

        PartDefinition e3_r1 = effect.addOrReplaceChild("e3_r1", CubeListBuilder.create().texOffs(0, 10).addBox(0.0F, 0.0F, 0.0F, 0.0F, 2.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        PartDefinition e2_r1 = effect.addOrReplaceChild("e2_r1", CubeListBuilder.create().texOffs(0, 10).addBox(0.0F, 0.0F, 0.0F, 0.0F, 2.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

        PartDefinition pane = partdefinition.addOrReplaceChild("pane", CubeListBuilder.create().texOffs(-16, 0).addBox(-8.0F, 0.0F, -8.0F, 16.0F, 0.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 16, 20);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        effect.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        pane.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }

    public void renderEffect(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        effect.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }

    public void renderPane(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        pane.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}
