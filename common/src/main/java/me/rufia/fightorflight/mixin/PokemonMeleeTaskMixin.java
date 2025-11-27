package me.rufia.fightorflight.mixin;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.entity.pokemon.ai.tasks.PokemonMeleeTask;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import net.minecraft.world.entity.ai.behavior.OneShot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(PokemonMeleeTask.class)
@Deprecated
public abstract class PokemonMeleeTaskMixin {
    //This class is not used currently. The default behavior config will use MeleeAttackTask directly.
    @Inject(method = "create", at = @At(value = "HEAD"), cancellable = true)
    private void createFOFMixin(int cooldownBetweenAttacks, CallbackInfoReturnable<OneShot<PokemonEntity>> cir) {
        //CobblemonFightOrFlight.LOGGER.info("Mixin usable");
        if (CobblemonFightOrFlight.commonConfig().use_fof_style_melee) {
            //CobblemonFightOrFlight.LOGGER.info("Mixin used");
            //cir.setReturnValue(FOFPokemonMeleeTask.create(cooldownBetweenAttacks));
        }
    }


}
