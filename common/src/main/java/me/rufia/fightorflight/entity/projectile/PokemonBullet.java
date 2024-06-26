package me.rufia.fightorflight.entity.projectile;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
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

public class PokemonBullet extends AbstractPokemonProjectile{
    public PokemonBullet(EntityType<? extends AbstractPokemonProjectile> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = true;
    }
    public PokemonBullet(Level level, LivingEntity shooter, Entity finalTarget) {
        super(EntityFightOrFlight.BULLET.get(), level);
        this.setOwner(shooter);
        BlockPos blockPos = shooter.blockPosition();
        double d = (double) blockPos.getX() + 0.5;
        double e = (double) blockPos.getY() + Math.max(0.3f, shooter.getBbHeight() / 2);
        double f = (double) blockPos.getZ() + 0.5;
        this.moveTo(d, e, f, this.getYRot(), this.getXRot());
    }
    public void lerpTo(double x, double y, double z, float yRot, float xRot, int lerpSteps, boolean teleport) {
        this.setPos(x, y, z);
        this.setRot(yRot, xRot);
    }
    public void tick() {
        super.tick();
        Vec3 vec3 = this.getDeltaMovement();
        if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
            double d = vec3.horizontalDistance();
            this.setYRot((float)(Mth.atan2(vec3.x, vec3.z) * 57.2957763671875));
            this.setXRot((float)(Mth.atan2(vec3.y, d) * 57.2957763671875));
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
        float m = 0.99F;
        float n = 0.05F;
        if (this.isInWater()) {
            for (int o = 0; o < 4; ++o) {
                float p = 0.25F;
                this.level().addParticle(ParticleTypes.BUBBLE, h - e * 0.25, j - f * 0.25, k - g * 0.25, e, f, g);
            }
        }

        if (!this.isNoGravity() && !this.noPhysics) {
            Vec3 vec34 = this.getDeltaMovement();
            this.setDeltaMovement(vec34.x, vec34.y - 0.05, vec34.z);
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
        Entity target = result.getEntity();
        Entity entity2 = this.getOwner();
        DamageSource damageSource;

        if (entity2 instanceof LivingEntity livingEntity) {
            damageSource = this.damageSources().mobAttack(livingEntity);
            livingEntity.setLastHurtMob(target);
        } else {
            damageSource = this.damageSources().generic();
        }

        boolean bl = target.getType() == EntityType.ENDERMAN;

        if (target.hurt(damageSource, getDamage())) {
            if (bl) {
                return;
            }
            if(target instanceof LivingEntity livingEntity){
                if(entity2 instanceof  PokemonEntity pokemonEntity){
                    applyTypeEffect(pokemonEntity,livingEntity);
                }
            }
            this.discard();
        }
    }

    private void destroy() {
        this.discard();
        this.level().gameEvent(GameEvent.ENTITY_DAMAGE, this.position(), GameEvent.Context.of(this));
    }

    protected void onHit(HitResult result) {
        super.onHit(result);
        this.destroy();
    }

    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!this.level().isClientSide) {
            ((ServerLevel) this.level()).sendParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY(), this.getZ(), 2, 0.2, 0.2, 0.2, 0.0);
        }
    }
}
