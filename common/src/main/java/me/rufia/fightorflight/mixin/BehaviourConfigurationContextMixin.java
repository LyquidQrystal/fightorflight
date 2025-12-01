package me.rufia.fightorflight.mixin;

import com.cobblemon.mod.common.api.ai.BehaviourConfigurationContext;
import com.cobblemon.mod.common.api.ai.config.AddTasksToActivity;
import com.cobblemon.mod.common.api.ai.config.BehaviourConfig;
import com.mojang.serialization.Dynamic;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(BehaviourConfigurationContext.class)
public class BehaviourConfigurationContextMixin {
    /*
    @ModifyArg(method = "apply", at = @At(value = "INVOKE", target = ""), remap = false)
    private void applyMixin(LivingEntity entity, List<? extends BehaviourConfig> behaviourConfigs, Dynamic<?> dynamic, CallbackInfo ci) {
        CobblemonFightOrFlight.LOGGER.info("{} added its behavior", entity.getName().getString());
        //AddTasksToActivity config = new AddTasksToActivity();
        //config.configure();
    }*/

}
