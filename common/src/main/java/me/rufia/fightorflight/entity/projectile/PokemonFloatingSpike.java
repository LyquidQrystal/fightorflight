package me.rufia.fightorflight.entity.projectile;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import me.rufia.fightorflight.entity.EntityFightOrFlight;
import me.rufia.fightorflight.entity.PokemonAttackEffect;
import me.rufia.fightorflight.utils.PokemonUtils;
import me.rufia.fightorflight.utils.TypeEffectiveness;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;


public class PokemonFloatingSpike extends AbstractPokemonSpike {
    protected short remainingFloatingTime;

    public PokemonFloatingSpike(EntityType<? extends AbstractPokemonProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public PokemonFloatingSpike(Level level, LivingEntity shooter) {
        super(EntityFightOrFlight.FLOATING_SPIKE.get(), level);
        activated = false;
        life = 0;
        inGround = false;
        remainingFloatingTime = 0;
        initPosition(shooter);
    }

    @Override
    protected void onActivated() {
        remainingFloatingTime = (short) level().random.nextIntBetweenInclusive(8, 12);
    }

    @Override
    public void tick() {
        if (remainingFloatingTime > 0) {
            --remainingFloatingTime;
        }
        super.tick();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putShort("remainingFloatingTime", remainingFloatingTime);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("remainingFloatingTime", CompoundTag.TAG_SHORT)) {
            remainingFloatingTime = compound.getShort("remainingFloatingTime");
        }
    }

    @Override
    protected void hurtEntity(LivingEntity target) {
        if (getOwner() instanceof PokemonEntity pokemonEntity) {
            if (PokemonAttackEffect.shouldHurtAllyMob(pokemonEntity, target)) {
                DamageSource damageSource = this.damageSources().indirectMagic(this, pokemonEntity);
                float multiplier = target instanceof PokemonEntity pokemon ? TypeEffectiveness.getTypeEffectivenessSimple("rock", pokemon) : 1f;
                if (target.hurt(damageSource, multiplier * 3f)) {
                    pokemonEntity.setLastHurtByMob(pokemonEntity);
                    PokemonUtils.setHurtByPlayer(pokemonEntity, target);
                }
            }
        }
    }

    @Override
    protected double getDefaultGravity() {
        double g = super.getDefaultGravity();
        if (activated) {
            if (remainingFloatingTime > 0) {
                return -g;
            } else {
                Vec3 v = getDeltaMovement();
                if (v.y > 0) {
                    setDeltaMovement(v.x, 0, v.z);
                }
                return 0;
            }
        }

        return g;
    }
}
