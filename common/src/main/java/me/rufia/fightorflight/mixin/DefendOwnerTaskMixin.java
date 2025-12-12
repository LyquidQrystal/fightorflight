package me.rufia.fightorflight.mixin;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.entity.pokemon.ai.tasks.DefendOwnerTask;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.entity.ai.tasks.FOFDefendOwnerTask;
import net.minecraft.world.entity.ai.behavior.OneShot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DefendOwnerTask.class)
public abstract class DefendOwnerTaskMixin {
    @Inject(method = "create", at = @At("HEAD"), remap = false, cancellable = true)
    private void createInject(CallbackInfoReturnable<OneShot<PokemonEntity>> cir) {
        if (CobblemonFightOrFlight.commonConfig().not_attacking_wild_shiny) {
            cir.setReturnValue(FOFDefendOwnerTask.create());
        }
    }

}
