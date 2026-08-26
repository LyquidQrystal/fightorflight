package me.rufia.fightorflight.entity.ai.tasks;

import com.cobblemon.mod.common.battles.BattleRegistry;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import me.rufia.fightorflight.utils.PokemonUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

public abstract class FOFPokemonAttackTask {
    public static boolean isTargetInBattle(PokemonEntity pokemonEntity) {
        if (getTarget(pokemonEntity) instanceof ServerPlayer targetAsPlayer) {
            return BattleRegistry.getBattleByParticipatingPlayer(targetAsPlayer) != null;
        }
        return false;
    }

    public static boolean sharedStartCondition(PokemonEntity pokemonEntity) {
        return PokemonUtils.shouldFightTarget(pokemonEntity);
    }

    public static LivingEntity getTarget(PokemonEntity pokemonEntity) {
        return PokemonUtils.getTarget(pokemonEntity);
    }
}
