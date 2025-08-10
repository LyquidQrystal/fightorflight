package me.rufia.fightorflight.client.model;

// Made with Blockbench 4.12.5
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.entity.projectile.AbstractPokemonSpike;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

public class PokemonTransformingProjectileModel<T extends AbstractPokemonSpike> extends EntityModel<T> {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(CobblemonFightOrFlight.MODID, "sticky_web"), "main");
    private final ModelPart Main;
    private final ModelPart Side;

    public PokemonTransformingProjectileModel(ModelPart root) {
        this.Main = root.getChild("Main");
        this.Side = root.getChild("Side");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition Main = partdefinition.addOrReplaceChild("Main", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -4.0F, -1.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, 26.0F, -1.0F));

        PartDefinition Side = partdefinition.addOrReplaceChild("Side", CubeListBuilder.create().texOffs(-16, 8).addBox(-8.0F, 0.0F, -8.0F, 16.0F, 0.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 32, 24);
    }

    @Override
    public void setupAnim(AbstractPokemonSpike entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        Main.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        Side.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }

    public void renderMain(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        Main.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }

    public void renderSide(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        Side.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}
