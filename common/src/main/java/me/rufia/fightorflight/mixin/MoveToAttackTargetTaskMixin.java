package me.rufia.fightorflight.mixin;

import com.bedrockk.molang.Expression;
import com.cobblemon.mod.common.entity.ai.MoveToAttackTargetTask;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.entity.ai.tasks.FOFMoveToAttackTargetTask;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.OneShot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MoveToAttackTargetTask.class)
public abstract class MoveToAttackTargetTaskMixin {
    @Inject(method = "create", at = @At(value = "HEAD"), cancellable = true)
    private void createMixin(Expression speedMultiplier, Expression closeEnoughDistance, CallbackInfoReturnable<OneShot<LivingEntity>> cir) {
        if (CobblemonFightOrFlight.commonConfig().use_fof_style_melee) {
            cir.setReturnValue(FOFMoveToAttackTargetTask.create(speedMultiplier, closeEnoughDistance));
        }
    }
}
