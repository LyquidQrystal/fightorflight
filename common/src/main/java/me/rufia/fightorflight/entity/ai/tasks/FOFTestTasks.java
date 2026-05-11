package me.rufia.fightorflight.entity.ai.tasks;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.utils.PokemonUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.OneShot;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class FOFTestTasks {
    public static OneShot<LivingEntity> avoidActivityTest() {
        return BehaviorBuilder.create(context ->
                context.group(
                        context.present(MemoryModuleType.AVOID_TARGET)
                ).apply(context, (avoidTargetAccessor) -> ((serverLevel, livingEntity, l) -> {
                    if (livingEntity instanceof PokemonEntity pokemonEntity) {
                        if (PokemonUtils.shouldAvoid(pokemonEntity)) {
                            //var hurtByEntity = context.get(hurtByEntityAccessor);
                            CobblemonFightOrFlight.LOGGER.info("{} should be able to flee", PokemonUtils.getPokemonName(pokemonEntity));
                            //pokemonEntity.getBrain().setMemoryWithExpiry(MemoryModuleType.AVOID_TARGET, hurtByEntity, avoidDurationTicks);
                            return true;
                        }
                    }
                    return false;
                }
                )));
    }
}
