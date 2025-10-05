package me.rufia.fightorflight.mixin;

import com.cobblemon.mod.common.entity.pokeball.EmptyPokeBallEntity;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.mixin.pokeball.ThrownEntityMixin;
import me.rufia.fightorflight.PokemonInterface;
import me.rufia.fightorflight.utils.PokemonUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EmptyPokeBallEntity.class)
public abstract class EmptyPokeBallEntityMixin extends ThrowableProjectile {
    @Shadow(remap = false)
    private PokemonEntity capturingPokemon;

    @Shadow(remap = false)
    public abstract EmptyPokeBallEntity.CaptureState getCaptureState();

    @Shadow
    protected abstract void drop();

    protected EmptyPokeBallEntityMixin(EntityType<? extends ThrowableProjectile> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "breakFree", at = @At("HEAD"), remap = false)
    private void breakFreeInject(CallbackInfo ci) {
        if (capturingPokemon != null && this.getOwner() != null) {
            ((PokemonInterface) capturingPokemon).setCapturedBy(this.getOwner().getId());
        }
    }

    @Inject(method = "onHitEntity", at = @At("HEAD"), remap = false)
    private void onHitEntityMixin(EntityHitResult hitResult, CallbackInfo ci) {
        if (getCaptureState() == EmptyPokeBallEntity.CaptureState.NOT) {
            if (!level().isClientSide && hitResult.getEntity() instanceof PokemonEntity pokemonEntity) {
                if (PokemonUtils.shouldFightTarget(pokemonEntity)) {
                    drop();
                }
            }
        }
    }
}
