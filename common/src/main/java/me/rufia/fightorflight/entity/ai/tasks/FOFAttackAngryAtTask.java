package me.rufia.fightorflight.entity.ai.tasks;

import com.cobblemon.mod.common.CobblemonMemories;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.utils.FOFTargetClaims;
import me.rufia.fightorflight.utils.FOFTargetPriority;
import me.rufia.fightorflight.utils.FOFUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.OneShot;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class FOFAttackAngryAtTask {
    public static OneShot<LivingEntity> create() {
        return BehaviorBuilder.create(context ->
                context.group(
                        context.present(MemoryModuleType.ANGRY_AT),
                        context.absent(MemoryModuleType.ATTACK_TARGET),
                        context.absent(CobblemonMemories.POKEMON_SLEEPING)
                ).apply(context, (angryAtAccessor, attackTargetAccessor, isSleepingAccessor) -> ((serverLevel, livingEntity, l) -> {
                    if (livingEntity instanceof PokemonEntity pokemonEntity) {
                        var angryAt = context.get(angryAtAccessor);
                        LivingEntity targetEntity = null;
                        if (serverLevel.getEntity(angryAt) instanceof LivingEntity tmp) {
                            targetEntity = tmp;
                        }
                        if (targetEntity == null) {
                            //the grudge points at something that no longer exists. Cobblemon only records a new
                            //attacker while ANGRY_AT is empty, so leaving it would lock the grudge forever
                            pokemonEntity.getBrain().eraseMemory(MemoryModuleType.ANGRY_AT);
                            return false;
                        }
                        if (FOFUtils.isPeaceful(targetEntity)) {
                            pokemonEntity.getBrain().eraseMemory(MemoryModuleType.ANGRY_AT);
                            return true;
                        }
                        //claim through the arbiter (was setMemory with `livingEntity`, i.e. the Pokemon itself)
                        boolean claimed = FOFTargetClaims.tryClaim(pokemonEntity, targetEntity,
                                FOFTargetPriority.PROVOKED, FOFTargetClaims.TASK_CLAIM_LEASE);
                        if (claimed) {
                            //consume the grudge: otherwise the absent(ANGRY_AT) gate in GetAngryAtAttackerTask
                            //stays closed and the second attacker never gets registered
                            pokemonEntity.getBrain().eraseMemory(MemoryModuleType.ANGRY_AT);
                        }
                        return claimed;
                    }
                    return false;
                }
                )));
    }
}
