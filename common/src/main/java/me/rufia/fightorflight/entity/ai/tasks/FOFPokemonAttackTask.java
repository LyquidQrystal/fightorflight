package me.rufia.fightorflight.entity.ai.tasks;

import com.cobblemon.mod.common.battles.BattleRegistry;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import me.rufia.fightorflight.PokemonInterface;
import me.rufia.fightorflight.entity.PokemonAttackEffect;
import net.minecraft.server.level.ServerPlayer;

public abstract class FOFPokemonAttackTask {
     public static int getAttackTime(PokemonEntity pokemonEntity) {
        return ((PokemonInterface) pokemonEntity).getAttackTime();
    }

    public static void resetAttackTime(PokemonEntity pokemonEntity, double dis) {
        PokemonAttackEffect.resetAttackTime(pokemonEntity, dis);
    }

    public static boolean isTargetInBattle(PokemonEntity pokemonEntity) {
        if (pokemonEntity.getTarget() instanceof ServerPlayer targetAsPlayer) {
            return BattleRegistry.getBattleByParticipatingPlayer(targetAsPlayer) != null;
        }
        return false;
    }
}
