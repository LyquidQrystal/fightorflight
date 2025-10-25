package me.rufia.fightorflight.utils;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class RayTrace {
    public static LivingEntity rayTraceEntity(LivingEntity viewer, double distance) {
        var level = viewer.level();
        Vec3 eyePosition = viewer.getEyePosition();
        Vec3 viewVector = viewer.getViewVector(1.0f).normalize();
        Vec3 extendedViewVector = viewVector.scale(distance);
        AABB area = new AABB(eyePosition, eyePosition.add(extendedViewVector));
        LivingEntity livingEntity = null;
        float minDistance = -1;
        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, area, e -> canChooseAsTarget(e, viewer))) {
            var optional = entity.getBoundingBox().clip(eyePosition, eyePosition.add(extendedViewVector));
            if (optional.isPresent()) {
                var dis = entity.distanceTo(viewer);
                if (minDistance < 0 || minDistance > dis) {
                    livingEntity = entity;
                    minDistance = dis;
                }
            }
        }
        /*
        if (livingEntity != null) {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.GLOWING, 100, 0));
        }//DEBUG ONLY
        */
        return livingEntity;
    }

    public static BlockHitResult rayTraceBlock(LivingEntity viewer, double distance) {
        var level = viewer.level();
        Vec3 eyePosition = viewer.getEyePosition();
        Vec3 viewVector = viewer.getViewVector(1.0f).normalize();
        Vec3 extendedViewVector = viewVector.scale(distance);
        ClipContext context = new ClipContext(eyePosition, eyePosition.add(extendedViewVector), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, viewer);
        BlockHitResult blockHitResult = level.clip(context);
        return blockHitResult;
    }

    private static boolean canChooseAsTarget(LivingEntity entity, Entity viewer) {
        return !entity.isRemoved() && !entity.isSpectator() && entity != viewer.getVehicle() && entity != viewer;
    }
}
