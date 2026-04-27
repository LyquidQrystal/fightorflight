package me.rufia.fightorflight.goals;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.utils.PokemonUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.PanicGoal;

import java.util.Arrays;

public class PokemonPanicGoal extends PanicGoal {
    public PokemonPanicGoal(PathfinderMob mob, double speedModifier) {
        super(mob, speedModifier);
    }

    private LivingEntity lastCaughtByMob;
    private int lastCaughtByMobTimestamp;
    // Lazy implementation of just tracking this in both CaughtByTargetGoal and here,
    // because I can't be bothered to implement a globalfeature right now.
    // should probably fix


    protected boolean shouldPanic() {
        PokemonEntity pokemonEntity = (PokemonEntity) this.mob;
        if (pokemonEntity.isBusy()) {
            return false;
        }
        if (PokemonUtils.shouldStopRunningAfterHurt(pokemonEntity)) {
            if (pokemonEntity.getMaxHealth() != pokemonEntity.getHealth()) {
                return false;
            }
        }
        if (this.mob.isOnFire() || this.mob.isFreezing()) {
            return true;
        }
        if (this.mob.getLastHurtByMob() != null) {
            return !(CobblemonFightOrFlight.getFightOrFlightCoefficient(pokemonEntity) > CobblemonFightOrFlight.commonConfig().neutral_threshold) || Arrays.stream(CobblemonFightOrFlight.commonConfig().always_flee).toList().contains(pokemonEntity.getPokemon().getSpecies().getName().toLowerCase());
        }
        return false;
        //return super.shouldPanic();
    }
}
