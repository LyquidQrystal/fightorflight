package me.rufia.fightorflight.goals;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import net.minecraft.world.entity.ai.goal.Goal;

// UNUSED CURRENTLY
//Some status moves are designed to be used passively,which means you just need to have it in the pokemon's moveset
@Deprecated
public class PokemonPassiveAbilityGoal extends Goal {
    protected PokemonEntity pokemonEntity;
    protected float radius;

    public PokemonPassiveAbilityGoal(PokemonEntity entity) {
        this.pokemonEntity = entity;
        this.radius = CobblemonFightOrFlight.moveConfig().status_move_radius;
    }

    public boolean canUse() {
        //return PokemonUtils.canTaunt(pokemonEntity);
        return false;
    }

    public void tick() {



    }
}
