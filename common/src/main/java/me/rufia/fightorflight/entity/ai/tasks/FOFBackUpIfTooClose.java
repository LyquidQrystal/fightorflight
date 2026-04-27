package me.rufia.fightorflight.entity.ai.tasks;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import me.rufia.fightorflight.utils.PokemonUtils;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.behavior.OneShot;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class FOFBackUpIfTooClose {
    public static OneShot<LivingEntity> create(int tooCloseDistance, float strafeSpeed) {
        return BehaviorBuilder.create((instance) -> instance.group(
                        instance.absent(MemoryModuleType.WALK_TARGET),
                        instance.registered(MemoryModuleType.LOOK_TARGET),
                        instance.present(MemoryModuleType.ATTACK_TARGET),
                        instance.present(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES))
                .apply(instance, (walkTargetAccessor, lookTargetAccessor, attackTargetAccessor, nearestVisibleLivingEntitiesAccessor) -> (serverLevel, pokemon, l) -> {
                    LivingEntity livingEntity = instance.get(attackTargetAccessor);
                    if (pokemon instanceof PokemonEntity pokemonEntity) {
                        if (livingEntity.closerThan(pokemonEntity, tooCloseDistance) && (instance.get(nearestVisibleLivingEntitiesAccessor)).contains(livingEntity)) {
                            float extraSpeedMultiplier = PokemonUtils.calculateExtraSpeed(pokemonEntity);
                            lookTargetAccessor.set(new EntityTracker(livingEntity, true));
                            pokemonEntity.getMoveControl().strafe(-strafeSpeed * extraSpeedMultiplier, 0.0F);
                            pokemon.setYRot(Mth.rotateIfNecessary(pokemonEntity.getYRot(), pokemonEntity.yHeadRot, 0.0F));
                            return true;
                        }
                    }
                    return false;
                }));
    }
}
