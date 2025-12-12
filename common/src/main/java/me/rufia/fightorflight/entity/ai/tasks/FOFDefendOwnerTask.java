package me.rufia.fightorflight.entity.ai.tasks;

import com.cobblemon.mod.common.CobblemonMemories;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import net.minecraft.world.entity.ai.behavior.OneShot;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class FOFDefendOwnerTask {
    public static OneShot<PokemonEntity> create() {
        return BehaviorBuilder.create(context ->
                context.group(
                                context.present(CobblemonMemories.NEAREST_VISIBLE_ATTACKER),
                                context.absent(MemoryModuleType.ATTACK_TARGET)
                        )
                        .apply(context, (visibleAttackerAccessor, attackTargetAccessor) -> ((serverLevel, pokemonEntity, l) -> {
                            var attacker = context.get(visibleAttackerAccessor);
                            if (attacker instanceof PokemonEntity targetPokemon) {
                                if (targetPokemon.getPokemon().getShiny() && CobblemonFightOrFlight.commonConfig().not_attacking_wild_shiny) {
                                    return false;
                                }
                            }
                            attackTargetAccessor.set(attacker);
                            return true;
                        })));

    }
}
