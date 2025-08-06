package me.rufia.fightorflight.entity.projectile;

import com.cobblemon.mod.common.api.moves.Move;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import me.rufia.fightorflight.entity.PokemonAttackEffect;
import me.rufia.fightorflight.utils.PokemonUtils;
import net.minecraft.core.BlockPos;
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

public abstract class AbstractPokemonProjectile extends ThrowableProjectile {
    public AbstractPokemonProjectile(EntityType<? extends AbstractPokemonProjectile> entityType, Level level) {
        super(entityType, level);
    }

    protected void initPosition(LivingEntity shooter) {
        this.setOwner(shooter);
        BlockPos blockPos = shooter.blockPosition();
        float angle = shooter.getYRot();
        //CobblemonFightOrFlight.LOGGER.info(String.valueOf(angle));
        double radius = 0.5 * shooter.getBbWidth();
        double d = (double) blockPos.getX() + 0.5 - radius * Math.sin(angle);
        double e = (double) blockPos.getY() + Math.max(0.3f, shooter.getBbHeight() * 0.67);
        double f = (double) blockPos.getZ() + 0.5 + radius * Math.cos(angle);
        this.moveTo(d, e, f, this.getYRot(), this.getXRot());
    }


    private static final EntityDataAccessor<String> type = SynchedEntityData.defineId(AbstractPokemonProjectile.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Float> damage = SynchedEntityData.defineId(AbstractPokemonProjectile.class, EntityDataSerializers.FLOAT);
    //private static final EntityDataAccessor<Integer> category = SynchedEntityData.defineId(AbstractPokemonProjectile.class, EntityDataSerializers.INT);

    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putString("Type", this.entityData.get(type));
        compound.putFloat("Damage", this.entityData.get(damage));
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

    public String getElementalType() {
        return this.entityData.get(type);
    }

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
        }
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        return target != getOwner() && super.canHitEntity(target);
    }

    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
    }

    public void accurateShoot(double x, double y, double z, float velocity, float inaccuracy) {
        double horizontalDistance = Math.sqrt(x * x + z * z);
        float g = (float) getGravity();
        double v2 = velocity * velocity;
        double delta = Math.sqrt(2 * v2 * g * y + v2 * v2 - g * g * horizontalDistance * horizontalDistance);
        double t = Math.sqrt(2 * (g * y + v2 - delta)) / g;
        double result = y + 0.5 * g * t * t;
        this.shoot(x, result, z, velocity, inaccuracy);
    }
}
