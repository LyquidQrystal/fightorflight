package me.rufia.fightorflight.entity.ai.config.task;

import com.bedrockk.molang.Expression;
import com.cobblemon.mod.common.api.ai.BehaviourConfigurationContext;
import com.cobblemon.mod.common.api.ai.ExpressionOrEntityVariableKt;
import com.cobblemon.mod.common.api.ai.config.task.SharedEntityVariables;
import com.cobblemon.mod.common.api.ai.config.task.SingleTaskConfig;
import com.cobblemon.mod.common.api.npc.configuration.MoLangConfigVariable;
import com.cobblemon.mod.common.util.MoLangExtensionsKt;
import com.mojang.datafixers.util.Either;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.entity.ai.tasks.FOFPokemonRangeTask;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FOFPokemonRangeTaskConfig implements SingleTaskConfig {
    private Either<Expression, MoLangConfigVariable> condition = booleanVariable(SharedEntityVariables.ATTACKING_CATEGORY, "attacks_melee", true).asExpressible();
    private Either<Expression, MoLangConfigVariable> range = numberVariable(SharedEntityVariables.ATTACKING_CATEGORY, "range_radius", 8F).asExpressible();
    private Either<Expression, MoLangConfigVariable> cooldownTicks = numberVariable(SharedEntityVariables.ATTACKING_CATEGORY, "melee_cooldown", 20).asExpressible();

    @Override
    public @Nullable BehaviorControl<? super LivingEntity> createTask(@NotNull LivingEntity livingEntity, @NotNull BehaviourConfigurationContext behaviourConfigurationContext) {
        CobblemonFightOrFlight.LOGGER.info("Range task created");
        var runtime = behaviourConfigurationContext.getRuntime();
        var expression = condition.left();
        if (expression.isPresent() && !MoLangExtensionsKt.resolveBoolean(runtime, expression.get(), MoLangExtensionsKt.getContextOrEmpty(runtime))) {
            return null;
        }
        behaviourConfigurationContext.addMemories(
                MemoryModuleType.ATTACK_TARGET,
                MemoryModuleType.ATTACK_COOLING_DOWN
        );
        return new FOFPokemonRangeTask();
        //return null;
    }

    @Override
    public @NotNull List<MoLangConfigVariable> getVariables(@NotNull LivingEntity livingEntity, @NotNull BehaviourConfigurationContext behaviourConfigurationContext) {
        List<Either<Expression, MoLangConfigVariable>> list = List.of(condition, range, cooldownTicks);
        return ExpressionOrEntityVariableKt.asVariables(list);
    }
}
