package me.rufia.fightorflight.entity.ai.tasks;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import me.rufia.fightorflight.PokemonInterface;
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
                        .apply(context, (hurtByAccessor, attackTargetAccessor) -> ((serverLevel, livingEntity, l) -> {
                            if (livingEntity instanceof PokemonEntity pokemonEntity) {
                                if (PokemonUtils.shouldAvoid(pokemonEntity)) {
                                    return false;
                                }
                                var hurtByEntity = context.get(hurtByAccessor);
                                if (hurtByEntity instanceof PokemonEntity pokemonEntity1 && PokemonUtils.tryToAvoidWildShiny(pokemonEntity1)) {
                                    return false;
                                }
                                String cmdData = ((PokemonInterface) pokemonEntity).getCommandData();
                                if (cmdData.isEmpty()) {
                                    attackTargetAccessor.set(hurtByEntity);
                                    return true;
                                }
                            }
                            return false;
                        })));
    }
}