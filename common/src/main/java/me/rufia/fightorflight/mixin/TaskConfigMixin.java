package me.rufia.fightorflight.mixin;

import com.cobblemon.mod.common.api.ai.config.task.TaskConfig;
import com.cobblemon.mod.common.util.MiscUtilsKt;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.entity.ai.config.task.FOFPokemonRangeTaskConfig;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(TaskConfig.Companion.class)
public class TaskConfigMixin {

    @Shadow(remap = false)
    private static Map<ResourceLocation, Class<? extends TaskConfig>> types;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void injectTasks(CallbackInfo ci) {
        types.putIfAbsent(MiscUtilsKt.cobblemonResource("range_attack"), FOFPokemonRangeTaskConfig.class);
        CobblemonFightOrFlight.LOGGER.info("FOF tasks injected");
    }

}
