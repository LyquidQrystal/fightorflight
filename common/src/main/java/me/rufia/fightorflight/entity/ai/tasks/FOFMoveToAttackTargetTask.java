package me.rufia.fightorflight.entity.ai.tasks;

import com.bedrockk.molang.Expression;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.util.MoLangExtensionsKt;
import me.rufia.fightorflight.utils.PokemonUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.OneShot;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;


public class FOFMoveToAttackTargetTask {
    public static OneShot<LivingEntity> create(Expression speedMultiplierExp, Expression closeEnoughDistanceExp) {
        return BehaviorBuilder.create(context ->
                context.group(
                        context.present(MemoryModuleType.ATTACK_TARGET),
                        context.registered(MemoryModuleType.WALK_TARGET)
                ).apply(context, (attackTargetAccessor, walkTargetAccessor) -> ((serverLevel, livingEntity, l) -> {
                    if (livingEntity instanceof PokemonEntity pokemonEntity && (PokemonUtils.shouldMelee(pokemonEntity) || PokemonUtils.shouldShoot(pokemonEntity)) && FOFPokemonAttackTask.sharedStartCondition(pokemonEntity)) {
                        float extraSpeedMultiplier = PokemonUtils.calculateExtraSpeed(pokemonEntity);
                        float speedMultiplier = extraSpeedMultiplier * MoLangExtensionsKt.resolveFloat(MoLangExtensionsKt.getMainThreadRuntime(), speedMultiplierExp, MoLangExtensionsKt.getContextOrEmpty(MoLangExtensionsKt.getMainThreadRuntime()));
                        int closeEnoughDistance = PokemonUtils.shouldShoot(pokemonEntity) ? (int) (PokemonUtils.getAttackRadius() * 0.6) : MoLangExtensionsKt.resolveInt(MoLangExtensionsKt.getMainThreadRuntime(), closeEnoughDistanceExp, MoLangExtensionsKt.getContextOrEmpty(MoLangExtensionsKt.getMainThreadRuntime()));

                        var attackTarget = context.get(attackTargetAccessor);
                        var position = attackTarget.position();
                        var walkTarget = context.tryGet(walkTargetAccessor).orElse(null);
                        if (walkTarget == null || walkTarget.getTarget().currentPosition().distanceToSqr(position) > closeEnoughDistance) {
                            livingEntity.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(attackTarget, speedMultiplier, closeEnoughDistance));
                            return true;
                        }
                    }
                    return false;
                }
                )));
    }
}
