package me.rufia.fightorflight.mixin;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.ai.PokemonBrain;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;


@Mixin(PokemonBrain.class)
@Debug(export = true)
public abstract class PokemonBrainMixin {
    @Inject(method = "fightTasks", at = @At("HEAD"), remap = false, cancellable = true)
    private void fightTasksMixin(Pokemon pokemon, CallbackInfoReturnable<List<Pair<Integer, BehaviorControl<? super PokemonEntity>>>> cir) {
        //This will only be used when there is no behavior configuration
    }

    @Inject(method = "<clinit>", at = @At("HEAD"))
    private static void initTest(CallbackInfo ci) {
        //CobblemonFightOrFlight.LOGGER.info("Testing brain inject on init.");
    }

    @Inject(method = "applyBrain", at = @At("HEAD"), remap = false)
    private void applyBrainMixin(PokemonEntity entity, Pokemon pokemon, Dynamic<?> dynamic, CallbackInfo ci) {
        //CobblemonFightOrFlight.LOGGER.info("Applying brain.");
    }
/*
    @Unique
    private static List<Pair<Integer, BehaviorControl<? super PokemonEntity>>> addTasks() {
        //This will only be used when there is no behavior configuration

        List<Pair<Integer, BehaviorControl<? super PokemonEntity>>> tasks = new ArrayList<>();
        tasks.add(new Pair<>(0, MoveToAttackTargetTask.INSTANCE.create(MoLangExtensionsKt.asExpression("0.5"), MoLangExtensionsKt.asExpression("0.5"))));
        if (CobblemonFightOrFlight.commonConfig().use_fof_style_melee) {
            tasks.add(new Pair<>(0, FOFPokemonMeleeTask.create(20)));
        } else {
            tasks.add(new Pair<>(0, PokemonMeleeTask.INSTANCE.create(20)));
        }
        tasks.add(new Pair<>(0, SwapActivityTask.INSTANCE.lacking(MemoryModuleType.ATTACK_TARGET, Activity.IDLE)));
        return tasks;
        }*/
}
