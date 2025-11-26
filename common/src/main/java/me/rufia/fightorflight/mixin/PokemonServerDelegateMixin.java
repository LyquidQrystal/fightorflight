package me.rufia.fightorflight.mixin;

import com.cobblemon.mod.common.api.entity.PokemonSideDelegate;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.entity.pokemon.PokemonServerDelegate;
import com.cobblemon.mod.common.pokemon.Pokemon;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.utils.PokemonUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PokemonServerDelegate.class)
public abstract class PokemonServerDelegateMixin implements PokemonSideDelegate {
    @Shadow(remap = false)
    public PokemonEntity entity;

    @Inject(method = "updateAttributes", at = @At("HEAD"), cancellable = true, remap = false)
    public void updateAttributesMixin(Pokemon pokemon, CallbackInfo ci) {
        //CobblemonFightOrFlight.LOGGER.info("UPDATING ATTRIBUTES");

    }

    @Inject(method = "maxHpToMaxHealthCurve", at = @At("HEAD"), cancellable = true, remap = false)
    public void maxHpToMaxHealthCurveMixin(int max_hp, CallbackInfoReturnable<Integer> cir) {
        if (CobblemonFightOrFlight.commonConfig().use_fof_style_hp_calculation) {
            if (max_hp == 1) {
                cir.setReturnValue(1);
            }
            cir.setReturnValue(PokemonUtils.getMaxHealth(entity));
        }
    }
}
