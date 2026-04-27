package me.rufia.fightorflight.data.movedata.movedatas;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import me.rufia.fightorflight.data.movedata.MoveData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

import java.util.Objects;

public class StatusEffectMoveData extends MoveData {
    public StatusEffectMoveData(String target, String triggerEvent, float chance, boolean canActivateSheerForce, String name) {
        super("status", target, triggerEvent, chance, canActivateSheerForce, name);
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
        int duration = calculateEffectDuration(pokemonEntity);
        if (Objects.equals(getName(), "direclaw")) {
            float f = pokemonEntity.getRandom().nextFloat();
            if (f < 0.34) {
                setName("poison");
            } else if (f < 0.67) {
                setName("paralysis");
            } else {
                setName("sleep");
            }
        } else if (Objects.equals(getName(), "triattack")) {
            float f = pokemonEntity.getRandom().nextFloat();
            if (f < 0.34) {
                setName("burn");
            } else if (f < 0.67) {
                setName("paralysis");
            } else {
                setName("freeze");
            }
        }
        String name = getName();
        if (Objects.equals(name, "poison")) {
            finalTarget.addEffect(new MobEffectInstance(MobEffects.POISON, duration * 30, 0));
        } else if (Objects.equals(name, "badly_poison")) {
            finalTarget.addEffect(new MobEffectInstance(MobEffects.POISON, duration * 30, 1));
        } else if (Objects.equals(name, "sleep")) {
            finalTarget.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration * 25, 2));
            finalTarget.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, duration * 25, 1));
        } else if (Objects.equals(name, "freeze")) {
            finalTarget.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration * 25, 2));
            finalTarget.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, duration * 25, 1));
            finalTarget.setTicksFrozen(finalTarget.getTicksFrozen() + duration);
        } else if (Objects.equals(name, "paralysis")) {
            finalTarget.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration * 30, 0));
            finalTarget.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, duration * 30, 0));
        } else if (Objects.equals(name, "burn")) {
            finalTarget.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, duration * 30, 0));
            finalTarget.setRemainingFireTicks(duration * 30);
        } else if (Objects.equals(name, "flinch")) {
            finalTarget.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 2 * 30, 1));
            finalTarget.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 2 * 30, 0));
        } else if (Objects.equals(name, "confusion")) {
            finalTarget.addEffect(new MobEffectInstance(MobEffects.CONFUSION, duration * 30, 0));
        }
    }
}
