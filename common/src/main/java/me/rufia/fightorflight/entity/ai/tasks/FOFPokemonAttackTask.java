package me.rufia.fightorflight.entity.ai.tasks;

import com.cobblemon.mod.common.battles.BattleRegistry;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import me.rufia.fightorflight.PokemonInterface;
import me.rufia.fightorflight.entity.PokemonAttackEffect;
import me.rufia.fightorflight.utils.PokemonUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public abstract class FOFPokemonAttackTask {
    public static int getAttackTime(PokemonEntity pokemonEntity) {
        return ((PokemonInterface) pokemonEntity).getAttackTime();
    }

    public static void resetAttackTime(PokemonEntity pokemonEntity, double dis) {
        PokemonAttackEffect.resetAttackTime(pokemonEntity, dis);
    }

    public static void refreshAttackTime(PokemonEntity pokemonEntity, int ticks) {
        PokemonAttackEffect.refreshAttackTime(pokemonEntity, ticks);
    }

    public static boolean isTargetInBattle(PokemonEntity pokemonEntity) {
        if (getTarget(pokemonEntity) instanceof ServerPlayer targetAsPlayer) {
            return BattleRegistry.getBattleByParticipatingPlayer(targetAsPlayer) != null;
        }
        return false;
    }

    public static LivingEntity getTarget(PokemonEntity pokemonEntity) {
        return PokemonUtils.getTarget(pokemonEntity);
    }
}
