package me.rufia.fightorflight.mixin;


import com.bedrockk.molang.Expression;
import com.cobblemon.mod.common.entity.ai.FleeFromAttackerTask;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.entity.ai.tasks.FOFFleeFromAttackerTask;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.OneShot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FleeFromAttackerTask.class)
public class FleeFromAttackerTaskMixin {
    @Inject(method = "create", at = @At("HEAD"), remap = false, cancellable = true)
    private void createMixin(Expression avoidDurationTicks, CallbackInfoReturnable<OneShot<LivingEntity>> cir) {
        if (CobblemonFightOrFlight.commonConfig().force_enable_flee) {
            cir.setReturnValue(FOFFleeFromAttackerTask.create(avoidDurationTicks));
        }
    }
}
