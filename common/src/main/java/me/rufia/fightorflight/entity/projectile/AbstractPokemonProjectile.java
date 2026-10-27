package me.rufia.fightorflight.entity.projectile;

import com.cobblemon.mod.common.api.moves.Move;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import me.rufia.fightorflight.entity.IPokemonAttack;
import me.rufia.fightorflight.entity.PokemonAttackEffect;
import me.rufia.fightorflight.utils.PokemonUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

import java.util.Objects;

public abstract class AbstractPokemonProjectile extends ThrowableProjectile implements IPokemonAttack {
    private static final EntityDataAccessor<String> type = SynchedEntityData.defineId(AbstractPokemonProjectile.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Float> damage = SynchedEntityData.defineId(AbstractPokemonProjectile.class, EntityDataSerializers.FLOAT);

    public AbstractPokemonProjectile(EntityType<? extends AbstractPokemonProjectile> entityType, Level level) {
        super(entityType, level);
    }

    protected void initPosition(LivingEntity shooter) {
        this.setOwner(shooter);
        double radius = 0.5 * shooter.getBbWidth();
        //getYRot() is in degrees, Math.sin/cos expect radians.
        double yaw = Math.toRadians(shooter.getYRot());
        //Muzzle point: the front of the hitbox, measured from the entity center (blockPosition() would snap it
        //to the block center) and from the entity feet (blockPosition().getY() would snap it to the block floor).
        double d = shooter.getX() - radius * Math.sin(yaw);
        double e = shooter.getY() + Math.max(0.3F, shooter.getBbHeight() * 0.67F);
        double f = shooter.getZ() + radius * Math.cos(yaw);
        this.moveTo(d, e, f, this.getYRot(), this.getXRot());
    }

    public void tick() {
        super.tick();
        if (shoudlCreateParticle()) {
            makeParticle(2);
        }
    }

    protected boolean shoudlCreateParticle() {
        return true;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putString("Type", this.entityData.get(type));
        compound.putFloat("Damage", this.entityData.get(damage));
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.entityData.set(type, compound.getString("Type"));
        this.entityData.set(damage, compound.getFloat("Damage"));
    }

    protected void makeParticle(int particleAmount) {
        if (getElementalType() == null) {
            return;
        }
        PokemonAttackEffect.makeTypeEffectParticle(particleAmount, this, getElementalType());
    }

    public float getDamage() {
        return this.entityData.get(damage);
    }

    public void setDamage(float Damage) {
        this.entityData.set(damage, Damage);
    }

    @Override
    public String getElementalType() {
        return this.entityData.get(type);
    }

    @Override
    public void setElementalType(String Type) {
        this.entityData.set(type, Type);
    }

    public void applyTypeEffect(PokemonEntity pokemonEntity, LivingEntity hurtTarget) {
        if (!Objects.equals(getElementalType(), pokemonEntity.getPokemon().getPrimaryType().getName())) {
            PokemonAttackEffect.applyTypeEffect(pokemonEntity, hurtTarget, getElementalType());
        } else {
            PokemonAttackEffect.applyTypeEffect(pokemonEntity, hurtTarget);
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(type, "normal");
        builder.define(damage, 1f);
    }

    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        Entity owner = getOwner();
        Entity target = result.getEntity();
        if (owner instanceof PokemonEntity pokemonEntity && target instanceof LivingEntity) {
            Move move = PokemonUtils.getMove(pokemonEntity);
            PokemonUtils.setHurtByPlayer(pokemonEntity, target);
            PokemonAttackEffect.applyOnHitVisualEffect(pokemonEntity, target, move);
            PokemonAttackEffect.applySFX(pokemonEntity.level(),move,pokemonEntity.blockPosition());
        }
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        return target != getOwner() && super.canHitEntity(target);
    }

    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
    }

    /**
     * The gravity that is actually applied by this projectile every tick.
     * <p>
     * {@code 0.0} means the projectile flies in a straight line, which is the case for every projectile whose
     * {@code tick()} does not call {@code applyGravity()} (bullets, spikes, ...). The aim solver uses this instead
     * of {@link #getGravity()} so that a projectile which does not fall is never compensated for a fall.
     */
    protected double getEffectiveGravity() {
        return 0.0;
    }

    public void accurateShoot(double x, double y, double z, float velocity, float inaccuracy) {
        double g = getEffectiveGravity();
        double horizontalSqr = x * x + z * z;
        double v2 = velocity * velocity;
        double discriminant = v2 * v2 - 2.0 * v2 * g * y - g * g * horizontalSqr;
        if (g <= 0.0 || discriminant <= 0.0 || horizontalSqr < 1.0E-4) {
            //Straight flight, target out of range (no real solution) or an almost vertical shot:
            //fall back to the plain straight line instead of producing a NaN direction.
            this.shoot(x, y, z, velocity, inaccuracy);
            return;
        }
        //Low angle ballistic solution: t is the flight time that reaches the point at the given distance and height.
        double t = Math.sqrt(2.0 * (v2 - g * y - Math.sqrt(discriminant))) / g;
        //shoot() normalizes the vector, so only the ratio between the components matters:
        //the vertical displacement has to be raised by the fall that happens during the flight.
        this.shoot(x, y + 0.5 * g * t * t, z, velocity, inaccuracy);
    }
}
