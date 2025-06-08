package me.rufia.fightorflight.goals.targeting;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.utils.PokemonUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.phys.AABB;

public class PokemonTauntedTargetGoal extends NearestAttackableTargetGoal<PokemonEntity> {
    protected PokemonEntity pokemonEntity;
    protected PokemonEntity targetPokemon;
    protected float safeDistanceSqr = (float) Math.pow(CobblemonFightOrFlight.moveConfig().status_move_radius, 2);

    public PokemonTauntedTargetGoal(PokemonEntity entity, boolean mustSee) {
        super(entity, PokemonEntity.class, 10, mustSee, false, (livingEntity) -> {
            if (livingEntity instanceof PokemonEntity pokemon) {
                return pokemon.getOwner() != null;
            }
            return false;
        });
        pokemonEntity = entity;
    }

    public boolean isTaunted() {
        if (target instanceof PokemonEntity pokemonEntity1) {
            if (pokemonEntity1.getOwner() != null && PokemonUtils.canTaunt(pokemonEntity1)) {
                targetPokemon = pokemonEntity1;
                return PokemonUtils.WildPokemonCanPerformUnprovokedAttack(pokemonEntity);
            }
        }
        targetPokemon = null;
        return false;
    }

    public boolean canUse() {
        if (!CobblemonFightOrFlight.moveConfig().wild_pokemon_taunt || pokemonEntity.getOwner() != null) {
            return false;
        }
        if (super.canUse()) {
            if (isTaunted()) {
                target = targetPokemon;
                return true;
            } else {
                target = null;
            }
        }
        return false;
    }
}
