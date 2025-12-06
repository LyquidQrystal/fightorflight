package me.rufia.fightorflight.mixin;

import com.cobblemon.mod.common.api.ai.BehaviourConfigurationContext;
import com.cobblemon.mod.common.api.ai.config.BehaviourConfig;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.mojang.serialization.Dynamic;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.entity.ai.sensors.FOFSensors;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.sensing.SensorType;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Set;

@Mixin(BehaviourConfigurationContext.class)
public abstract class BehaviourConfigurationContextMixin {
    @Final
    @Shadow(remap = false)
    private Set<SensorType<?>> sensors;

    @Shadow
    public abstract void addSensors(@NotNull SensorType<?>... sensor);

    @Inject(method = "apply", at = @At(value = "INVOKE", target = "Lcom/cobblemon/mod/common/entity/MoLangScriptingEntity;assignNewBrainWithMemoriesAndSensors(Lcom/mojang/serialization/Dynamic;Ljava/util/Set;Ljava/util/Set;)Lnet/minecraft/world/entity/ai/Brain;"))
    private void applyMixin(LivingEntity entity, List<? extends BehaviourConfig> behaviourConfigs, Dynamic<?> dynamic, CallbackInfo ci) {
        //CobblemonFightOrFlight.LOGGER.info("[FOF] Trying to add sensors.");
        //sensors.add(FOFSensors.POKEMON_HELP_OWNER);
        //addSensors(FOFSensors.POKEMON_HELP_OWNER);
    }
}
