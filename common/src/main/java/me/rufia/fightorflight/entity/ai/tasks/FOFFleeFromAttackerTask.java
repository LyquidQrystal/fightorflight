package me.rufia.fightorflight.entity.ai.tasks;

import com.bedrockk.molang.Expression;
import com.cobblemon.mod.common.api.molang.MoLangFunctions;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.util.MoLangExtensionsKt;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.utils.PokemonUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.OneShot;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class FOFFleeFromAttackerTask {
    public static OneShot<LivingEntity> create(Expression avoidDurationTicksExp) {
        //CobblemonFightOrFlight.LOGGER.info("Trying to create a flee task.");
        return BehaviorBuilder.create(context ->
                context.group(
                        context.present(MemoryModuleType.HURT_BY_ENTITY),
                        context.registered(MemoryModuleType.AVOID_TARGET)
                ).apply(context, (hurtByEntityAccessor, avoidTargetAccessor) -> ((serverLevel, livingEntity, l) -> {
                    if (livingEntity instanceof PokemonEntity pokemonEntity) {
                        if (PokemonUtils.shouldAvoid(pokemonEntity)) {
                            var hurtByEntity = context.get(hurtByEntityAccessor);
                            var avoidTarget = context.tryGet(avoidTargetAccessor).orElse(null);
                            if (avoidTarget != null && avoidTarget == hurtByEntity) {
                                return false;
                            }
                            CobblemonFightOrFlight.LOGGER.info("Trying to let {} flee", pokemonEntity.getPokemon().getDisplayName(true));
                            MoLangExtensionsKt.withQueryValue(MoLangExtensionsKt.getMainThreadRuntime(), "entity", MoLangFunctions.INSTANCE.asMostSpecificMoLangValue(pokemonEntity));
                            int avoidDurationTicks = MoLangExtensionsKt.resolveInt(MoLangExtensionsKt.getMainThreadRuntime(), avoidDurationTicksExp, MoLangExtensionsKt.getContextOrEmpty(MoLangExtensionsKt.getMainThreadRuntime()));
                            pokemonEntity.getBrain().setMemoryWithExpiry(MemoryModuleType.AVOID_TARGET, hurtByEntity, avoidDurationTicks);
                            return true;
                        }
                    }
                    return false;
                }
                )));
    }
}
