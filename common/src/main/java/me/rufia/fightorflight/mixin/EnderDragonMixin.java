package me.rufia.fightorflight.mixin;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhaseManager;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderDragon.class)
public abstract class EnderDragonMixin extends LivingEntity {
    @Shadow
    @Final
    private EnderDragonPhaseManager phaseManager;


    @Shadow
    @Final
    public EnderDragonPart head;

    @Shadow
    protected abstract boolean reallyHurt(DamageSource damageSource, float amount);

    @Shadow
    private float sittingDamageReceived;

    protected EnderDragonMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "hurt(Lnet/minecraft/world/entity/boss/EnderDragonPart;Lnet/minecraft/world/damagesource/DamageSource;F)Z", at = @At("HEAD"), cancellable = true)
    private void hurtInject(EnderDragonPart part, DamageSource source, float damage, CallbackInfoReturnable<Boolean> cir) {
        if (this.phaseManager.getCurrentPhase().getPhase() == EnderDragonPhase.DYING) {
            cir.setReturnValue(false);
        } else {
            damage = this.phaseManager.getCurrentPhase().onHurt(source, damage);
            if (part != this.head) {
                damage = damage / 4.0F + Math.min(damage, 1.0F);
            }

            if (damage < 0.01F) {
                cir.setReturnValue(false);
            } else {
                if (source.getEntity() instanceof PokemonEntity pokemonEntity) {
                    boolean isPlayerOwned = pokemonEntity.getPokemon().isPlayerOwned();
                    if (isPlayerOwned && CobblemonFightOrFlight.commonConfig().player_pokemon_can_hurt_ender_dragon || !isPlayerOwned && CobblemonFightOrFlight.commonConfig().wild_pokemon_can_hurt_ender_dragon) {
                        float f = this.getHealth();
                        this.reallyHurt(source, damage);
                        if (this.isDeadOrDying() && !this.phaseManager.getCurrentPhase().isSitting()) {
                            this.setHealth(1.0F);
                            this.phaseManager.setPhase(EnderDragonPhase.DYING);
                        }
                        if (this.phaseManager.getCurrentPhase().isSitting()) {
                            this.sittingDamageReceived = this.sittingDamageReceived + f - this.getHealth();
                            if (this.sittingDamageReceived > 0.25F * this.getMaxHealth()) {
                                this.sittingDamageReceived = 0.0F;
                                this.phaseManager.setPhase(EnderDragonPhase.TAKEOFF);
                            }
                        }
                        cir.setReturnValue(true);
                    }
                }
            }
        }
    }

}
