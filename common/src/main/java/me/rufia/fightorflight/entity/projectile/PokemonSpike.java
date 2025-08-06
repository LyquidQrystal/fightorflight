package me.rufia.fightorflight.entity.projectile;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import me.rufia.fightorflight.entity.EntityFightOrFlight;
import me.rufia.fightorflight.entity.PokemonAttackEffect;
import me.rufia.fightorflight.utils.FOFUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
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
    private static final EntityDataAccessor<Integer> IN_GROUND_TICK;

    static {
        IN_GROUND_TICK = SynchedEntityData.defineId(PokemonSpike.class, EntityDataSerializers.INT);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(IN_GROUND_TICK, -1);
    }

    public PokemonSpike(EntityType<? extends AbstractPokemonProjectile> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = true;
    }

    public PokemonSpike(Level level, LivingEntity shooter) {
        super(EntityFightOrFlight.SPIKE.get(), level);
        activated = false;
        life = 0;
        inGround = false;
        initPosition(shooter);
    }

    protected EntityHitResult findHitEntity(Vec3 startVec, Vec3 endVec) {
        return ProjectileUtil.getEntityHitResult(this.level(), this, startVec, endVec, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0), this::canHitEntity);
    }

    protected boolean shouldFall() {
        return inGround && this.level().noCollision((new AABB(this.position(), this.position())).inflate(0.1));
    }

    protected void startFalling() {
        inGround = false;
        Vec3 vec3 = this.getDeltaMovement();
        this.setDeltaMovement(vec3.multiply(this.random.nextFloat() * 0.2F, this.random.nextFloat() * 0.2F, this.random.nextFloat() * 0.2F));
    }

    protected void tickDespawn() {
        var owner = getOwner();
        if (owner != null && owner.isAlive()) {
            ++life;
        } else {
            life += 10;
        }
        if (this.life >= getMaxLife()) {
            this.discard();
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (inGround) {
            if (getInGroundTick() == -1) {
                int randomInterval = level().random.nextInt(20);
                setInGroundTick(tickCount + randomInterval);
            }
        } else {
            setInGroundTick(-1);
        }
        checkEntityCollision();
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
                        inGround = true;
                        break;
                    }
                }
            }
        }

        if (inGround && !flag) {
            if (!activated) {
                activated = true;
            }
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
                }
                if (hitResult != null && !flag) {
                    ProjectileDeflection projectileDeflection = this.hitTargetOrDeflectSelf(hitResult);
                    this.hasImpulse = true;
                    if (projectileDeflection != ProjectileDeflection.NONE) {
                        break;
                    }
                }
                if (entityHitResult == null) {
                    break;
                }
                hitResult = null;
            }

            vec3 = this.getDeltaMovement();
            double dx = vec3.x;
            double dy = vec3.y;
            double dz = vec3.z;

            double d4 = vec3.horizontalDistance();
            if (flag) {
                this.setYRot(FOFUtils.toAngle(Mth.atan2(-dx, -dz)));
            } else {
                this.setYRot(FOFUtils.toAngle(Mth.atan2(dx, dz)));
            }

            this.setXRot(FOFUtils.toAngle(Mth.atan2(dy, d4)));
            this.setXRot(lerpRotation(this.xRotO, this.getXRot()));
            this.setYRot(lerpRotation(this.yRotO, this.getYRot()));
        }
    }

    @Override
    protected double getDefaultGravity() {
        return 0.05;
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        this.lastState = this.level().getBlockState(result.getBlockPos());
        super.onHitBlock(result);
        Vec3 vec3 = result.getLocation().subtract(this.getX(), this.getY(), this.getZ());
        this.setDeltaMovement(vec3);

        Vec3 vec31 = vec3.normalize().scale(0.05);
        this.setPosRaw(this.getX() - vec31.x, this.getY() - vec31.y, this.getZ() - vec31.z);
        inGround = true;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putShort("life", life);
        compound.putBoolean("activated", activated);
        compound.putBoolean("inGround", inGround);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        life = compound.getShort("life");
        activated = compound.getBoolean("activated");
        inGround = compound.getBoolean("inGround");
    }

    @Override
    public void move(MoverType type, Vec3 pos) {
        super.move(type, pos);
        if (type != MoverType.SELF && this.shouldFall()) {
            this.startFalling();
        }
    }

    protected void checkEntityCollision() {
        if (!level().isClientSide && activated) {
            List<LivingEntity> list = level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(0.05));
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

    protected void hurtEntity(LivingEntity target) {
        if (getOwner() instanceof PokemonEntity pokemonEntity) {
            if (PokemonAttackEffect.shouldHurtAllyMob(pokemonEntity, target)) {
                DamageSource damageSource = this.damageSources().indirectMagic(this, pokemonEntity);
                if (target.hurt(damageSource, 2f)) {
                    pokemonEntity.setLastHurtMob(target);
                    if (pokemonEntity.getOwner() instanceof Player player) {
                        target.setLastHurtByPlayer(player);
                    }
                }
            }
        }
    }

    protected int getMaxLife() {
        return 300;
    }

    @Override
    protected boolean shoudlCreateParticle() {
        return false;
    }

    public void setInGroundTick(int value) {
        entityData.set(IN_GROUND_TICK, value);
    }

    public int getInGroundTick() {
        return entityData.get(IN_GROUND_TICK);
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        return false;
    }
}
