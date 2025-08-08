package me.rufia.fightorflight.entity.projectile;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import me.rufia.fightorflight.entity.EntityFightOrFlight;
import me.rufia.fightorflight.entity.PokemonAttackEffect;
import me.rufia.fightorflight.utils.PokemonUtils;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.Objects;

public class PokemonSpike extends AbstractPokemonSpike {
    public PokemonSpike(EntityType<? extends AbstractPokemonProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public PokemonSpike(Level level, LivingEntity shooter) {
        super(EntityFightOrFlight.SPIKE.get(), level);
        activated = false;
        life = 0;
        inGround = false;
        initPosition(shooter);
    }

    @Override
    protected void hurtEntity(LivingEntity target) {
        if (getOwner() instanceof PokemonEntity pokemonEntity) {
            if (PokemonAttackEffect.shouldHurtAllyMob(pokemonEntity, target)) {
                if (Objects.equals(getElementalType(), "poison")) {
                    target.addEffect(new MobEffectInstance(MobEffects.POISON, 200, 0), pokemonEntity);
                } else {
                    DamageSource damageSource = this.damageSources().indirectMagic(this, pokemonEntity);
                    if (target.hurt(damageSource, 3f)) {
                        pokemonEntity.setLastHurtByMob(pokemonEntity);
                        PokemonUtils.setHurtByPlayer(pokemonEntity, target);
                    }
                }
            }
        }
    }
}
