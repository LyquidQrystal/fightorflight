package me.rufia.fightorflight.data.movedata.movedatas;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import me.rufia.fightorflight.data.movedata.MoveData;
import me.rufia.fightorflight.effects.FOFEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

import java.util.Objects;

public class StatChangeMoveData extends MoveData {
    private final int stage;

    public StatChangeMoveData(String target, String triggerEvent, float chance, boolean canActivateSheerForce, String name, int stage) {
        super("stat", target, triggerEvent, chance, canActivateSheerForce, name);
        this.stage = stage;
    }

    private boolean isPositive() {
        return stage > 0 && stage < 7;
    }

    private boolean isNegative() {
        return stage < 0 && stage > -7;
    }

    @Override
    public void invoke(PokemonEntity pokemonEntity, LivingEntity target) {
        if (!chanceTest(pokemonEntity.getRandom(), pokemonEntity) || pokemonEntity.getPokemon().getAbility().getName().equals("sheerforce") && canActivateSheerForce()) {
            return;
        }
        LivingEntity finalTarget = pickTarget(pokemonEntity, target);
        if (finalTarget == null) {
            return;
        }
        int duration = calculateEffectDuration(pokemonEntity) * 20;
        String name = getName();
        //CobblemonFightOrFlight.LOGGER.info("Trying to apply stat related effect");
        if (Objects.equals(name, "attack") || Objects.equals(name, "special_attack")) {
            if (isPositive()) {
                finalTarget.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, duration, stage - 1));
            } else if (isNegative()) {
                finalTarget.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, duration, -stage - 1));
            }
        } else if (Objects.equals(name, "defense") || Objects.equals(name, "special_defense")) {
            if (isPositive()) {
                finalTarget.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, duration, stage - 1));
            } else if (isNegative()) {
                finalTarget.addEffect(new MobEffectInstance(FOFEffects.RESISTANCE_WEAKENED, duration, -stage - 1));//It's not working?
            }
        } else if (Objects.equals(name, "speed")) {
            if (isPositive()) {
                finalTarget.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, duration, stage - 1));
            } else if (isNegative()) {
                finalTarget.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration, -stage - 1));
            }
        } else if (Objects.equals(name, "all")) {
            if (isPositive()) {
                finalTarget.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, duration, stage - 1));
                finalTarget.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, duration, stage - 1));
                finalTarget.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, duration, stage - 1));
            } else if (isNegative()) {
                finalTarget.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration, -stage - 1));
                finalTarget.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, duration, -stage - 1));
                finalTarget.addEffect(new MobEffectInstance(FOFEffects.RESISTANCE_WEAKENED, duration, -stage - 1));
            }
        } else if (Objects.equals(name, "accuracy")) {
            if (isNegative()) {
                finalTarget.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, duration, -stage - 1));
            }
        }
    }
}
