package me.rufia.fightorflight.entity.ai.tasks;

import com.cobblemon.mod.common.CobblemonMemories;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import me.rufia.fightorflight.utils.FOFTargetClaims;
import me.rufia.fightorflight.utils.FOFTargetPriority;
import me.rufia.fightorflight.utils.PokemonUtils;
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
                                if (PokemonUtils.tryToAvoidWildShiny(targetPokemon)) {
                                    return false;
                                }
                            }
                            //claim through the arbiter so a player order or self defense keeps precedence
                            FOFTargetClaims.tryClaim(pokemonEntity, attacker,
                                    FOFTargetPriority.OWNER_DEFENSE, FOFTargetClaims.TASK_CLAIM_LEASE);
                            return true;
                        })));
    }
}
