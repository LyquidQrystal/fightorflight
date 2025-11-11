package me.rufia.fightorflight.entity.areaeffect;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import me.rufia.fightorflight.entity.EntityFightOrFlight;
import me.rufia.fightorflight.entity.PokemonAttackEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class PokemonWhirlPool extends AbstractPokemonAreaEffect {
    public PokemonWhirlPool(EntityType<? extends AbstractPokemonAreaEffect> entityType, Level level) {
        super(entityType, level);
    }

    public PokemonWhirlPool(LivingEntity owner) {
        super(EntityFightOrFlight.WHIRLPOOL.get(), owner.level());
        setOwner(owner);
    }

    @Override
    protected void visualEffect() {
        if (owner instanceof PokemonEntity) {
            if (isWaiting()) {
                PokemonAttackEffect.makeTypeEffectParticle(6, this, getElementalType());
            }
        }
    }

    @Override
    protected void activate() {
        super.activate();
        if (owner instanceof PokemonEntity pokemonEntity && pokemonEntity.isAlive()) {
            dealDamageInTheArea();
        } else {
            discard();
        }
    }

    @Override
    protected void applyExtraEffect(LivingEntity target) {
        var instance = new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 10, 0);
        target.addEffect(instance, owner);
    }
}
