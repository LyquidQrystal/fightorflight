package me.rufia.fightorflight.entity.ai.tasks;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import me.rufia.fightorflight.PokemonInterface;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.OneShot;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class FOFPokemonRangeTask {
    public static OneShot<PokemonEntity> create(int cooldownBetweenAttacks) {
        return BehaviorBuilder.create(context ->
                context.group(
                        context.registered(MemoryModuleType.LOOK_TARGET),
                        context.present(MemoryModuleType.ATTACK_TARGET),
                        context.absent(MemoryModuleType.ATTACK_COOLING_DOWN),
                        context.present(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES)
                ).apply(context, (lookTargetAccessor, attackTargetAccessor, attackCooldownAccessor, visibleMobsAccessor) -> ((serverLevel, pokemonEntity, l) -> {
                            LivingEntity target = context.get(attackTargetAccessor);
                            ((PokemonInterface) pokemonEntity).setAttackTime(cooldownBetweenAttacks);
                            return true;
                        })
                )
        );
    }
}
