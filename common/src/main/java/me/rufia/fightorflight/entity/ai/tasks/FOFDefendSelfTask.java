package me.rufia.fightorflight.entity.ai.tasks;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import me.rufia.fightorflight.utils.PokemonUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.OneShot;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class FOFDefendSelfTask {
    public static OneShot<LivingEntity> create() {
        return BehaviorBuilder.create(context ->
                context.group(
                                context.present(MemoryModuleType.HURT_BY_ENTITY),
                                context.registered(MemoryModuleType.ATTACK_TARGET)
                        )
                        .apply(context, (hurtByAccessor, attackTargetAccessor) -> ((serverLevel, pokemonEntity, l) -> {
                            var hurtByEntity = context.get(hurtByAccessor);
                            if (hurtByEntity instanceof PokemonEntity pokemonEntity1) {
                                if (PokemonUtils.tryToAvoidWildShiny(pokemonEntity1)) {
                                    return false;
                                }
                            }
                            var targetOpt = pokemonEntity.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET);
                            if (targetOpt.isPresent()) {
                                LivingEntity target = targetOpt.get();
                                //Try to add a priority queue here, or at least allow the Pokemon to attack the entity that hurts it.
                            } else {
                                attackTargetAccessor.set(hurtByEntity);
                            }


                            return true;
                        })));
    }
}