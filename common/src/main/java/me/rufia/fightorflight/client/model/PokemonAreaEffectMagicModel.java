package me.rufia.fightorflight.client.model;


// Made with Blockbench 5.0.4
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class PokemonAreaEffectMagicModel<T extends Entity> extends EntityModel<T> {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(CobblemonFightOrFlight.MODID, "magic_effect"), "main");
    private final ModelPart bottom;
    private final ModelPart effect_beam_1;

    public PokemonAreaEffectMagicModel(ModelPart root) {
        this.bottom = root.getChild("bottom");
        this.effect_beam_1 = root.getChild("effect_beam_1");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bottom = partdefinition.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(-16, 0).addBox(-8.0F, 0.0F, -8.0F, 16.0F, 0.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition effect_beam_1 = partdefinition.addOrReplaceChild("effect_beam_1", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition beam_1_r1 = effect_beam_1.addOrReplaceChild("beam_1_r1", CubeListBuilder.create().texOffs(0, 16).addBox(-8.0F, -37.0F, -1.0F, 16.0F, 32.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 5.0F, -1.0F, 0.0F, 2.3562F, 0.0F));

        PartDefinition beam_0_r1 = effect_beam_1.addOrReplaceChild("beam_0_r1", CubeListBuilder.create().texOffs(0, 16).addBox(-9.0F, -37.0F, 1.0F, 16.0F, 32.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 5.0F, -2.0F, 0.0F, 0.7854F, 0.0F));

        return LayerDefinition.create(meshdefinition, 32, 48);
    }

    @Override
    public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        bottom.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        effect_beam_1.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }

    public void renderBottom(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        bottom.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }

    public void renderBeam(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        effect_beam_1.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}
