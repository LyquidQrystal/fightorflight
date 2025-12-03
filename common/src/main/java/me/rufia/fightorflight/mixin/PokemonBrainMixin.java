package me.rufia.fightorflight.mixin;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.ai.PokemonBrain;
import com.mojang.serialization.Dynamic;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;


@Mixin(PokemonBrain.class)
public abstract class PokemonBrainMixin {
    @Final
    @Shadow(remap = false)
    private static Collection<SensorType<? extends Sensor<? super PokemonEntity>>> SENSORS;

    @Inject(method = "applyBrain", at = @At("RETURN"), remap = false)
    private void applyBrainMixin(PokemonEntity pokemonEntity, Pokemon pokemon, Dynamic<?> dynamic, CallbackInfo ci) {
    }

    @Inject(method = "<clinit>", at = @At("HEAD"), remap = false)
    private static void initMixin(CallbackInfo ci) {
        //SENSORS.add(FOFSensors.POKEMON_HELP_OWNER);//It uses a ImmutableList, it can't be modified.
    }

}
