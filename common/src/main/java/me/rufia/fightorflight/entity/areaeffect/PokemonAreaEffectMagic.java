package me.rufia.fightorflight.entity.areaeffect;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import me.rufia.fightorflight.entity.EntityFightOrFlight;
import me.rufia.fightorflight.entity.PokemonAttackEffect;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class PokemonAreaEffectMagic extends AbstractPokemonAreaEffect {
    public PokemonAreaEffectMagic(EntityType<? extends AbstractPokemonAreaEffect> entityType, Level level) {
        super(entityType, level);
    }

    public PokemonAreaEffectMagic(LivingEntity owner) {
        super(EntityFightOrFlight.MAGIC_EFFECT.get(), owner.level());
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
}
