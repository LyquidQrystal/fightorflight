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
    private final ModelPart pane;
    private final ModelPart secPane;
    private final ModelPart pane2;
    private final ModelPart secPane2;

    public PokemonAreaEffectWhirlpoolModel(ModelPart root) {
        this.pane = root.getChild("pane");
        this.secPane = root.getChild("secPane");
        this.pane2 = root.getChild("pane2");
        this.secPane2 = root.getChild("secPane2");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition pane = partdefinition.addOrReplaceChild("pane", CubeListBuilder.create().texOffs(-16, 0).addBox(-8.0F, 0.0F, -8.0F, 16.0F, 0.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition secPane = partdefinition.addOrReplaceChild("secPane", CubeListBuilder.create().texOffs(16, 0).addBox(-8.0F, 0.0F, -8.0F, 16.0F, 0.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition pane2 = partdefinition.addOrReplaceChild("pane2", CubeListBuilder.create().texOffs(-16, 16).addBox(-8.0F, 0.0F, -8.0F, 16.0F, 0.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 23.9F, 0.0F));

        PartDefinition secPane2 = partdefinition.addOrReplaceChild("secPane2", CubeListBuilder.create().texOffs(16, 16).addBox(-8.0F, 0.0F, -8.0F, 16.0F, 0.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 23.9F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        pane.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        secPane.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        pane2.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        secPane2.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }

    public void renderEffect(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color,int secondaryColor) {
        pane2.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        secPane2.render(poseStack, vertexConsumer, packedLight, packedOverlay, secondaryColor);
    }

    public void renderPane(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        pane.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }

    public void renderSecondaryPane(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        secPane.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}
