package me.rufia.fightorflight.mixin;

import com.cobblemon.mod.common.api.ai.ActivityConfigurationContext;
import com.cobblemon.mod.common.api.ai.BehaviourConfigurationContext;
import com.cobblemon.mod.common.api.ai.config.BehaviourConfig;
import com.cobblemon.mod.common.entity.ai.SwapActivityTask;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.util.MoLangExtensionsKt;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import me.rufia.fightorflight.entity.ai.tasks.FOFFleeFromAttackerTask;
import me.rufia.fightorflight.utils.PokemonUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetAwayFrom;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.schedule.Activity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(BehaviourConfigurationContext.class)
public abstract class BehaviourConfigurationContextMixin {
    @Final
    @Shadow(remap = false)
    private List<ActivityConfigurationContext> activities;

    @Inject(method = "apply", at = @At("RETURN"))
    private void applyMixin(LivingEntity entity, List<? extends BehaviourConfig> behaviourConfigs, Dynamic<?> dynamic, CallbackInfo ci) {
        addPanicActivity(entity);
    }

    /*
    @Unique
    private void addAvoidActivity(LivingEntity livingEntity) {
        if (livingEntity instanceof PokemonEntity pokemonEntity) {
            //Try to find avoid activity, do nothing if the pokemon already has the avoid activity.
            for (ActivityConfigurationContext acc : activities) {
                if (acc.getActivity().getName().equals("avoid")) {
                    return;
                }
            }
            //CobblemonFightOrFlight.LOGGER.info("Trying to add avoid activity to {}", ChatFormatting.stripFormatting(pokemonEntity.getName().getString()));

            List<Pair<Integer, BehaviorControl<? super PokemonEntity>>> taskList = new ArrayList<>();
            taskList.add(new Pair<>(0, FOFTestTasks.avoidActivityTest()));
            taskList.add(new Pair<>(0, SetWalkTargetAwayFrom.entity(MemoryModuleType.AVOID_TARGET, 0.55F, 15, false)));
            taskList.add(new Pair<>(0, PokemonUtils.makeRandomWalkTask()));
            taskList.add(new Pair<>(0, SetEntityLookTargetSometimes.create(8f, UniformInt.of(30, 60))));
            taskList.add(new Pair<>(0, SwapActivityTask.INSTANCE.lacking(MemoryModuleType.AVOID_TARGET, Activity.IDLE)));
            pokemonEntity.getBrain().addActivity(Activity.AVOID, ImmutableList.copyOf(taskList));
        }
    }*/

    @Unique
    private void addPanicActivity(LivingEntity livingEntity) {
        if (livingEntity instanceof PokemonEntity pokemonEntity) {
            for (ActivityConfigurationContext acc : activities) {
                if (acc.getActivity().getName().equals("panic")) {
                    return;
                }
            }
            List<Pair<Integer, BehaviorControl<? super PokemonEntity>>> taskList = new ArrayList<>();
            taskList.add(new Pair<>(0, FOFFleeFromAttackerTask.create(MoLangExtensionsKt.asExpression(600))));
            taskList.add(new Pair<>(0, SwapActivityTask.INSTANCE.lacking(MemoryModuleType.AVOID_TARGET, Activity.IDLE)));
            taskList.add(new Pair<>(2, SetWalkTargetAwayFrom.entity(MemoryModuleType.HURT_BY_ENTITY, 0.7f, 9, false)));
            taskList.add(new Pair<>(3, PokemonUtils.makeRandomWalkTask()));
            pokemonEntity.getBrain().addActivity(Activity.PANIC, ImmutableList.copyOf(taskList));
        }
    }
}
