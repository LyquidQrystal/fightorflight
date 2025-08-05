package me.rufia.fightorflight.entity.projectile;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import me.rufia.fightorflight.entity.EntityFightOrFlight;
import me.rufia.fightorflight.entity.PokemonAttackEffect;
import me.rufia.fightorflight.utils.FOFUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public class PokemonSpike extends AbstractPokemonProjectile {
    protected boolean inGround;
    protected int inGroundTime;
    protected short life;
    protected String type;
    private BlockState lastState;
    private boolean activated;

    public PokemonSpike(EntityType<? extends AbstractPokemonProjectile> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = true;
        activated = false;
    }

    public PokemonSpike(Level level, LivingEntity shooter) {
        super(EntityFightOrFlight.SPIKE.get(), level);
        initPosition(shooter);
    }

    protected EntityHitResult findHitEntity(Vec3 startVec, Vec3 endVec) {
        return ProjectileUtil.getEntityHitResult(this.level(), this, startVec, endVec, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0), this::canHitEntity);
    }

    protected boolean shouldFall() {
        return this.inGround && this.level().noCollision((new AABB(this.position(), this.position())).inflate(0.06));
    }

    protected void startFalling() {
        this.inGround = false;
        Vec3 vec3 = this.getDeltaMovement();
        this.setDeltaMovement(vec3.multiply(this.random.nextFloat() * 0.2F, this.random.nextFloat() * 0.2F, this.random.nextFloat() * 0.2F));
    }

    protected void tickDespawn() {
        ++this.life;
        if (this.life >= getMaxLife()) {
            this.discard();
        }
        if (!activated) {
            activated = true;
        }
    }

    @Override
    public void tick() {
        super.tick();
        boolean flag = noPhysics;
        Vec3 vec3 = this.getDeltaMovement();
        if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
            double d0 = vec3.horizontalDistance();
            this.setYRot(FOFUtils.toAngle(Mth.atan2(vec3.x, vec3.z)));
            this.setXRot(FOFUtils.toAngle(Mth.atan2(vec3.y, d0)));
            this.yRotO = this.getYRot();
            this.xRotO = this.getXRot();
        }

        BlockPos blockpos = this.blockPosition();
        BlockState blockstate = this.level().getBlockState(blockpos);
        Vec3 vec33;
        if (!blockstate.isAir() && !flag) {
            VoxelShape voxelshape = blockstate.getCollisionShape(this.level(), blockpos);
            if (!voxelshape.isEmpty()) {
                vec33 = this.position();

                for (AABB aabb : voxelshape.toAabbs()) {
                    if (aabb.move(blockpos).contains(vec33)) {
                        this.inGround = true;
                        break;
                    }
                }
            }
        }

        if (this.inGround && !flag) {
            if (this.lastState != blockstate && this.shouldFall()) {
                this.startFalling();
            } else if (!this.level().isClientSide) {
                this.tickDespawn();
            }

            ++this.inGroundTime;
        } else {
            this.inGroundTime = 0;
            Vec3 vec32 = this.position();
            vec33 = vec32.add(vec3);
            HitResult hitResult = this.level().clip(new ClipContext(vec32, vec33, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
            if (hitResult.getType() != HitResult.Type.MISS) {
                vec33 = hitResult.getLocation();
            }

            while (!this.isRemoved()) {
                EntityHitResult entityHitResult = findHitEntity(vec32, vec33);
                if (entityHitResult != null) {
                    hitResult = entityHitResult;
                    if (hitResult.getType() == HitResult.Type.ENTITY) {
                        Entity entity = entityHitResult.getEntity();
                        Entity entity1 = this.getOwner();
                        if (entity instanceof Player && entity1 instanceof Player && !((Player) entity1).canHarmPlayer((Player) entity)) {
                            hitResult = null;
                            entityHitResult = null;
                        }
                    }

                    if (entityHitResult != null && !flag) {
                        ProjectileDeflection projectileDeflection = this.hitTargetOrDeflectSelf(hitResult);
                        this.hasImpulse = true;
                        if (projectileDeflection != ProjectileDeflection.NONE) {
                            break;
                        }
                    }
                }

                if (entityHitResult == null) {
                    break;
                }
            }

            vec3 = this.getDeltaMovement();
            double d5 = vec3.x;
            double d6 = vec3.y;
            double d1 = vec3.z;

            double d7 = this.getX() + d5;
            double d2 = this.getY() + d6;
            double d3 = this.getZ() + d1;
            double d4 = vec3.horizontalDistance();
            if (flag) {
                this.setYRot(FOFUtils.toAngle(Mth.atan2(-d5, -d1)));
            } else {
                this.setYRot(FOFUtils.toAngle(Mth.atan2(d5, d1)));
            }

            this.setXRot(FOFUtils.toAngle(Mth.atan2(d6, d4)));
            this.setXRot(lerpRotation(this.xRotO, this.getXRot()));
            this.setYRot(lerpRotation(this.yRotO, this.getYRot()));
            float f = 0.99F;
            if (this.isInWater()) {
                for (int j = 0; j < 4; ++j) {
                    this.level().addParticle(ParticleTypes.BUBBLE, d7 - d5 * 0.25, d2 - d6 * 0.25, d3 - d1 * 0.25, d5, d6, d1);
                }

                f = this.getWaterInertia();
            }

            this.setDeltaMovement(vec3.scale(f));
            if (!flag) {
                this.applyGravity();
            }

            this.setPos(d7, d2, d3);
            checkEntityCollision();
            this.checkInsideBlocks();
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        this.lastState = this.level().getBlockState(result.getBlockPos());
        super.onHitBlock(result);
        Vec3 vec3 = result.getLocation().subtract(this.getX(), this.getY(), this.getZ());
        this.setDeltaMovement(vec3);

        Vec3 vec31 = vec3.normalize().scale(0.05000000074505806);
        this.setPosRaw(this.getX() - vec31.x, this.getY() - vec31.y, this.getZ() - vec31.z);
        this.inGround = true;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putShort("life", life);
        compound.putBoolean("inGround", inGround);
        compound.putBoolean("activated", activated);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        life = compound.getShort("life");
        inGround = compound.getBoolean("inGround");
        activated = compound.getBoolean("activated");
    }

    protected void checkEntityCollision() {
        if (!level().isClientSide && activated) {
            int t = tickCount % 20;
            if ((t + 1) % 5 == 0) {
                List<LivingEntity> list = level().getEntitiesOfClass(LivingEntity.class, getBoundingBox());
                for (LivingEntity livingEntity : list) {
                    if (livingEntity.xOld != livingEntity.getX() && livingEntity.zOld != livingEntity.getZ()) {
                        double d = Math.abs(livingEntity.getX() - livingEntity.xOld);
                        double e = Math.abs(livingEntity.getZ() - livingEntity.zOld);
                        if (d >= 0.003 || e >= 0.003) {
                            hurtEntity(livingEntity);
                        }
                    }
                }
            }
        }
    }

    protected void hurtEntity(LivingEntity target) {
        if (getOwner() instanceof PokemonEntity pokemonEntity) {
            if (PokemonAttackEffect.shouldHurtAllyMob(pokemonEntity, target)) {
                DamageSource damageSource = this.damageSources().indirectMagic(this, pokemonEntity);
                if (target.hurt(damageSource, 2f)) {
                    pokemonEntity.setLastHurtMob(target);
                    /*
                    if (CobblemonFightOrFlight.commonConfig().activate_type_effect) {
                        applyTypeEffect(pokemonEntity, target);
                    }
                    if (CobblemonFightOrFlight.commonConfig().activate_move_effect) {
                        Move move = PokemonUtils.getMove(pokemonEntity);
                        PokemonAttackEffect.applyPostEffect(pokemonEntity, target, move, true);
                    }*/
                }
            }
        }
    }

    protected float getWaterInertia() {
        return 0.6F;
    }

    protected int getMaxLife() {
        return 300;
    }
}
