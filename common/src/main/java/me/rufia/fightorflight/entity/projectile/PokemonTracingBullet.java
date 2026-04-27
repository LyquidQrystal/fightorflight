package me.rufia.fightorflight.entity.projectile;

import com.google.common.collect.Lists;
import me.rufia.fightorflight.entity.EntityFightOrFlight;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;


public class PokemonTracingBullet extends ExplosivePokemonProjectile {
    private static final double SPEED = 0.2;
    private static final int min_interval = 10;
    private static final int random_interval = 2;
    @Nullable
    private Entity finalTarget;
    @Nullable
    private Direction currentMoveDirection;
    private int flightSteps;
    private double targetDeltaX;
    private double targetDeltaY;
    private double targetDeltaZ;
    @Nullable
    private UUID targetId;

    public PokemonTracingBullet(EntityType<? extends PokemonTracingBullet> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = true;
    }

    public PokemonTracingBullet(Level level, LivingEntity shooter, Entity finalTarget, Direction.Axis axis) {
        super(EntityFightOrFlight.TRACING_BULLET.get(), level);
        initPosition(shooter);
        setOwner(shooter);
        this.finalTarget = finalTarget;
        this.currentMoveDirection = Direction.UP;
        this.selectNextMoveDirection(axis);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.finalTarget != null) {
            compound.putUUID("Target", this.finalTarget.getUUID());
        }
        if (getMoveDirection() != null) {
            compound.putInt("Dir", this.currentMoveDirection.get3DDataValue());
        }
        compound.putInt("Steps", this.flightSteps);
        compound.putDouble("TXD", this.targetDeltaX);
        compound.putDouble("TYD", this.targetDeltaY);
        compound.putDouble("TZD", this.targetDeltaZ);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.flightSteps = compound.getInt("Steps");
        this.targetDeltaX = compound.getDouble("TXD");
        this.targetDeltaY = compound.getDouble("TYD");
        this.targetDeltaZ = compound.getDouble("TZD");
        if (compound.contains("Dir", 99)) {
            this.currentMoveDirection = Direction.from3DDataValue(compound.getInt("Dir"));
        }

        if (compound.hasUUID("Target")) {
            this.targetId = compound.getUUID("Target");
        }
    }

    @Nullable
    private Direction getMoveDirection() {
        return this.currentMoveDirection;
    }

    private void setMoveDirection(@Nullable Direction direction) {
        this.currentMoveDirection = direction;
    }

    private void selectNextMoveDirection(@Nullable Direction.Axis axis) {
        double d = 0.5;
        BlockPos blockPos;
        if (this.finalTarget == null || !this.finalTarget.isAlive()) {
            blockPos = this.blockPosition().below();
        } else {
            d = (double) this.finalTarget.getBbHeight() * 0.7;
            blockPos = BlockPos.containing(this.finalTarget.getX(), this.finalTarget.getY() + d, this.finalTarget.getZ());
        }

        double e = (double) blockPos.getX() + 0.5;
        double f = (double) blockPos.getY() + d;
        double g = (double) blockPos.getZ() + 0.5;
        Direction direction = null;
        if (!blockPos.closerToCenterThan(this.position(), 2.0)) {
            BlockPos blockPos2 = this.blockPosition();
            List<Direction> list = Lists.newArrayList();
            if (axis != Direction.Axis.X) {
                if (blockPos2.getX() < blockPos.getX() && this.level().isEmptyBlock(blockPos2.east())) {
                    list.add(Direction.EAST);
                } else if (blockPos2.getX() > blockPos.getX() && this.level().isEmptyBlock(blockPos2.west())) {
                    list.add(Direction.WEST);
                }
            }

            if (axis != Direction.Axis.Y) {
                if (blockPos2.getY() < blockPos.getY() && this.level().isEmptyBlock(blockPos2.above())) {
                    list.add(Direction.UP);
                } else if (blockPos2.getY() > blockPos.getY() && this.level().isEmptyBlock(blockPos2.below())) {
                    list.add(Direction.DOWN);
                }
            }

            if (axis != Direction.Axis.Z) {
                if (blockPos2.getZ() < blockPos.getZ() && this.level().isEmptyBlock(blockPos2.south())) {
                    list.add(Direction.SOUTH);
                } else if (blockPos2.getZ() > blockPos.getZ() && this.level().isEmptyBlock(blockPos2.north())) {
                    list.add(Direction.NORTH);
                }
            }

            direction = Direction.getRandom(this.random);
            if (!list.isEmpty()) {
                direction = list.get(this.random.nextInt(list.size()));
            }
            for (int i = 5; !this.level().isEmptyBlock(blockPos2.relative(direction)) && i > 0; --i) {
                direction = Direction.getRandom(this.random);
            }
            if (finalTarget == null && getOwner() != null) {
                direction = Direction.DOWN;
            }

            e = this.getX() + (double) direction.getStepX();
            f = this.getY() + (double) direction.getStepY();
            g = this.getZ() + (double) direction.getStepZ();
        }

        this.setMoveDirection(direction);
        double h = e - this.getX();
        double j = f - this.getY();
        double k = g - this.getZ();
        double l = Math.sqrt(h * h + j * j + k * k);
        if (l == 0.0) {
            this.targetDeltaX = 0.0;
            this.targetDeltaY = 0.0;
            this.targetDeltaZ = 0.0;
        } else {
            this.targetDeltaX = h / l * SPEED;
            this.targetDeltaY = j / l * SPEED;
            this.targetDeltaZ = k / l * SPEED;
        }

        this.hasImpulse = true;
        this.flightSteps = min_interval + this.random.nextInt(random_interval) * 3;
    }

    public void checkDespawn() {
        if (this.level().getDifficulty() == Difficulty.PEACEFUL) {
            this.discard();
        }
    }


    public void tick() {
        super.tick();
        Vec3 vec3;
        if (!this.level().isClientSide) {
            if (this.finalTarget == null && this.targetId != null) {
                this.finalTarget = ((ServerLevel) this.level()).getEntity(this.targetId);
                if (this.finalTarget == null) {
                    this.targetId = null;
                }
            }

            if (canAttackTarget()) {
                this.targetDeltaX = Mth.clamp(this.targetDeltaX * 1.025, -1.0, 1.0);
                this.targetDeltaY = Mth.clamp(this.targetDeltaY * 1.025, -1.0, 1.0);
                this.targetDeltaZ = Mth.clamp(this.targetDeltaZ * 1.025, -1.0, 1.0);
                vec3 = this.getDeltaMovement();
                this.setDeltaMovement(vec3.add((this.targetDeltaX - vec3.x) * 0.2, (this.targetDeltaY - vec3.y) * 0.2, (this.targetDeltaZ - vec3.z) * 0.2));
            }

            HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
            if (hitResult.getType() != HitResult.Type.MISS) {
                this.onHit(hitResult);
            }
        }

        this.checkInsideBlocks();
        vec3 = this.getDeltaMovement();
        this.setPos(this.getX() + vec3.x, this.getY() + vec3.y, this.getZ() + vec3.z);
        ProjectileUtil.rotateTowardsMovement(this, 0.5F);
        if (this.level().isClientSide) {
            makeParticle(1);
        } else if (this.finalTarget != null && !this.finalTarget.isRemoved()) {
            if (this.flightSteps > 0) {
                --this.flightSteps;
                if (this.flightSteps == 0) {
                    this.selectNextMoveDirection(getMoveDirection() == null ? null : getMoveDirection().getAxis());
                }
            }

            if (getMoveDirection() != null) {
                BlockPos blockPos = this.blockPosition();
                Direction.Axis axis = getMoveDirection().getAxis();
                if (this.level().loadedAndEntityCanStandOn(blockPos.relative(getMoveDirection()), this)) {
                    this.selectNextMoveDirection(axis);
                } else {
                    BlockPos blockPos2 = this.finalTarget.blockPosition();
                    if (axis == Direction.Axis.X && blockPos.getX() == blockPos2.getX() || axis == Direction.Axis.Z && blockPos.getZ() == blockPos2.getZ() || axis == Direction.Axis.Y && blockPos.getY() == blockPos2.getY()) {
                        this.selectNextMoveDirection(axis);
                    }
                }
            }
        }
    }

    @Override
    protected double getDefaultGravity() {
        if (canAttackTarget()) {
            return 0f;
        }
        return 0.04;
    }

    protected boolean canAttackTarget() {
        return !(this.finalTarget == null || !this.finalTarget.isAlive() || this.finalTarget instanceof Player && this.finalTarget.isSpectator());
    }

    protected boolean canHitEntity(Entity target) {
        return super.canHitEntity(target) && !target.noPhysics;
    }

    public boolean isOnFire() {
        return false;
    }

    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < 16384.0;
    }

    protected void onHitEntity(EntityHitResult result) {
        Entity target = result.getEntity();
        Entity owner = getOwner();
        if (!target.equals(owner)) {
            if (target instanceof Projectile projectile && owner != null) {
                if (owner.equals(projectile.getOwner())) {
                    return;
                }
            }
            super.onHitEntity(result);
        }
    }

    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
    }

    private void destroy() {
        this.discard();
        this.level().gameEvent(GameEvent.ENTITY_DAMAGE, this.position(), GameEvent.Context.of(this));
    }

    protected void onHit(HitResult result) {
        super.onHit(result);
    }

    public boolean hurt(DamageSource source, float amount) {
        if (!this.level().isClientSide) {
            ((ServerLevel) this.level()).sendParticles(ParticleTypes.CRIT, this.getX(), this.getY(), this.getZ(), 15, 0.2, 0.2, 0.2, 0.0);
            this.destroy();
        }
        return true;
    }

    public void recreateFromPacket(ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);
        double d = packet.getXa();
        double e = packet.getYa();
        double f = packet.getZa();
        this.setDeltaMovement(d, e, f);
    }
}
