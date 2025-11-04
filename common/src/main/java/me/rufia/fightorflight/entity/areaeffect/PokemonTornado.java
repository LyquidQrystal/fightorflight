package me.rufia.fightorflight.entity.areaeffect;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import me.rufia.fightorflight.entity.EntityFightOrFlight;
import me.rufia.fightorflight.utils.PokemonUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class PokemonTornado extends AbstractPokemonAreaEffect {
    public PokemonTornado(EntityType<? extends AbstractPokemonAreaEffect> entityType, Level level) {
        super(entityType, level);
    }

    public PokemonTornado(LivingEntity owner) {
        super(EntityFightOrFlight.TORNADO.get(), owner.level());
        setOwner(owner);
    }

    @Override
    protected void visualEffect() {
        if (owner instanceof PokemonEntity) {
            //Move move = PokemonUtils.findMove(pokemonEntity, getMoveName());
            if (isWaiting()) {
                PokemonUtils.makeParticle(10, this, ParticleTypes.ASH);
                PokemonUtils.makeParticle(2, this, ParticleTypes.SWEEP_ATTACK);
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
