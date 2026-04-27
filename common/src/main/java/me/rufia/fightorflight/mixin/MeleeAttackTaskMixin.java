package me.rufia.fightorflight.mixin;

import com.bedrockk.molang.Expression;
import com.cobblemon.mod.common.entity.npc.ai.MeleeAttackTask;
import com.cobblemon.mod.common.util.MoLangExtensionsKt;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.entity.ai.tasks.FOFPokemonMeleeTask;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.OneShot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MeleeAttackTask.class)
public abstract class MeleeAttackTaskMixin {
    @Inject(method = "create", at = @At(value = "HEAD"),cancellable = true)
    private void createFOFMixin(Expression range, Expression cooldownTicks, CallbackInfoReturnable<OneShot<LivingEntity>> cir) {
        if (CobblemonFightOrFlight.commonConfig().use_fof_style_melee) {
            int cooldownTicksValue = MoLangExtensionsKt.resolveInt(MoLangExtensionsKt.getMainThreadRuntime(), cooldownTicks,MoLangExtensionsKt.getContextOrEmpty(MoLangExtensionsKt.getMainThreadRuntime()));
            cir.setReturnValue(FOFPokemonMeleeTask.create(cooldownTicksValue));
        }
    }
}
