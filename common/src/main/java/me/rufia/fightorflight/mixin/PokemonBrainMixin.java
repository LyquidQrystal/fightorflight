package me.rufia.fightorflight.mixin;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.CobblemonBehaviours;
import com.cobblemon.mod.common.api.ai.config.BehaviourConfig;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.ai.PokemonBrain;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;


@Mixin(PokemonBrain.class)
@Debug(export = true)
public abstract class PokemonBrainMixin {

    @Inject(method = "applyBrain", at = @At("RETURN"), remap = false)
    private void applyBrainMixin(PokemonEntity pokemonEntity, Pokemon pokemon, Dynamic<?> dynamic, CallbackInfo ci) {
        CobblemonFightOrFlight.LOGGER.info("Applying brain.");
        ResourceLocation rl = ResourceLocation.fromNamespaceAndPath(Cobblemon.MODID, "behaviours/fights_range.json");
        CobblemonFightOrFlight.LOGGER.info("Trying to add behaviour {} to the Pokemon's baseAI", rl);
        //var b = CobblemonBehaviours.INSTANCE.getBehaviours().get(rl);
        //pokemonEntity.getBehaviours().add(rl);
        //pokemonEntity.getForm().getBaseAI().add(b);
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
