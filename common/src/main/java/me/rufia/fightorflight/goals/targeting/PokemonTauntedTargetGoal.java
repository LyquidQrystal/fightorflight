package me.rufia.fightorflight.goals.targeting;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.utils.PokemonUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.phys.AABB;

public class PokemonTauntedTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
    protected PokemonEntity pokemonEntity;
    protected PokemonEntity targetPokemon;
    protected float safeDistanceSqr = (float) Math.pow(CobblemonFightOrFlight.moveConfig().status_move_radius, 2);

    public PokemonTauntedTargetGoal(Mob mob, Class<T> targetType, boolean mustSee) {
        super(mob, targetType, 10, mustSee, false, (entity) -> {
            if (entity instanceof TamableAnimal tamable) {
                return tamable.getOwner() != null || CobblemonFightOrFlight.moveConfig().wild_pokemon_taunt;
            }
            return false;
        });
        pokemonEntity = (PokemonEntity) mob;
    }

    public boolean isTaunted() {
        if (pokemonEntity.getOwner() != null) {
            return false;
        }
        for (PokemonEntity pokemonEntity1 : pokemonEntity.level().getEntitiesOfClass(PokemonEntity.class, AABB.ofSize(pokemonEntity.position(), safeDistanceSqr, safeDistanceSqr, safeDistanceSqr), (entity) -> entity.getOwner() != null || CobblemonFightOrFlight.moveConfig().wild_pokemon_taunt)) {
            if (PokemonUtils.canTaunt(pokemonEntity1)) {
                targetPokemon = pokemonEntity1;
                return PokemonUtils.WildPokemonCanPerformUnprovokedAttack(pokemonEntity);
            }
        }
        targetPokemon = null;
        return false;
    }

    public boolean canUse() {
        if (isTaunted()) {
            target = targetPokemon;
            return true;
        }
        return false;
    }
}
