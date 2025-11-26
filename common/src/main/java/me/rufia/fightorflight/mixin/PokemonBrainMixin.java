package me.rufia.fightorflight.mixin;

import com.cobblemon.mod.common.entity.ai.MoveToAttackTargetTask;
import com.cobblemon.mod.common.entity.ai.SwapActivityTask;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.entity.pokemon.ai.tasks.PokemonMeleeTask;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.ai.PokemonBrain;
import com.cobblemon.mod.common.util.MoLangExtensionsKt;
import com.mojang.datafixers.util.Pair;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.entity.ai.tasks.FOFPokemonMeleeTask;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.schedule.Activity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(PokemonBrain.class)
public abstract class PokemonBrainMixin {

    @Inject(method = "fightTasks", at = @At("HEAD"), remap = false, cancellable = true)
    private void fightTasksMixin(Pokemon pokemon, CallbackInfoReturnable<List<Pair<Integer, BehaviorControl<? super PokemonEntity>>>> cir) {
        CobblemonFightOrFlight.LOGGER.info("Brain Mixin is running");
        List<Pair<Integer, BehaviorControl<? super PokemonEntity>>> tasks = new ArrayList<>();
        tasks.add(new Pair<>(0, MoveToAttackTargetTask.INSTANCE.create(MoLangExtensionsKt.asExpression("0.5"), MoLangExtensionsKt.asExpression("0.5"))));
        if (CobblemonFightOrFlight.commonConfig().use_fof_style_melee) {
            tasks.add(new Pair<>(0, FOFPokemonMeleeTask.create(20)));
        } else {
            tasks.add(new Pair<>(0, PokemonMeleeTask.INSTANCE.create(20)));
        }
        tasks.add(new Pair<>(0, SwapActivityTask.INSTANCE.lacking(MemoryModuleType.ATTACK_TARGET, Activity.IDLE)));
        cir.setReturnValue(tasks);

    }
}
