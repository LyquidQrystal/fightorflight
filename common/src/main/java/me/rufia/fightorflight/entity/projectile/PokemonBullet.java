package me.rufia.fightorflight.entity.projectile;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.entity.EntityFightOrFlight;
import me.rufia.fightorflight.entity.projectile.AbstractPokemonProjectile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class PokemonBullet extends ExplosivePokemonProjectile{
    public PokemonBullet(EntityType<? extends AbstractPokemonProjectile> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = true;
    }

    public PokemonBullet(Level level, LivingEntity shooter) {
        super(EntityFightOrFlight.BULLET.get(), level);
        initPosition(shooter);
    }

    public void tick() {
        super.tick();
        Vec3 vec3 = this.getDeltaMovement();
        if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
            double d = vec3.horizontalDistance();
            this.setYRot((float) (Mth.atan2(vec3.x, vec3.z) * 57.2957763671875));
            this.setXRot((float) (Mth.atan2(vec3.y, d) * 57.2957763671875));
            this.yRotO = this.getYRot();
            this.xRotO = this.getXRot();
        }
        double e = vec3.x;
        double f = vec3.y;
        double g = vec3.z;

        double h = this.getX() + e;
        double j = this.getY() + f;
        double k = this.getZ() + g;
        double l = vec3.horizontalDistance();

        if (this.noPhysics) {
            this.setYRot((float) (Mth.atan2(-e, -g) * 57.2957763671875));
        } else {
            this.setYRot((float) (Mth.atan2(e, g) * 57.2957763671875));
        }

        this.setXRot((float) (Mth.atan2(f, l) * 57.2957763671875));
        this.setXRot(lerpRotation(this.xRotO, this.getXRot()));
        this.setYRot(lerpRotation(this.yRotO, this.getYRot()));
        //float n = (float) getGravity();
        if (this.isInWater()) {
            for (int o = 0; o < 4; ++o) {
                float p = 0.25F;
                this.level().addParticle(ParticleTypes.BUBBLE, h - e * 0.25, j - f * 0.25, k - g * 0.25, e, f, g);
            }
        }

        HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (hitResult.getType() != HitResult.Type.MISS) {
            this.onHit(hitResult);
        }
        this.setPos(h, j, k);
        this.checkInsideBlocks();
    }

    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
    }

    protected void onHit(HitResult result) {
        super.onHit(result);
    }

    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
    }
}
