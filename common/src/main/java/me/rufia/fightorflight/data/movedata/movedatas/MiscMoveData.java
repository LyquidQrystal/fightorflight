package me.rufia.fightorflight.data.movedata.movedatas;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import me.rufia.fightorflight.PokemonInterface;
import me.rufia.fightorflight.data.movedata.MoveData;
import me.rufia.fightorflight.entity.PokemonAttackEffect;
import me.rufia.fightorflight.utils.PokemonUtils;
import net.minecraft.world.entity.LivingEntity;

import java.util.Objects;

public class MiscMoveData extends MoveData {
    public MiscMoveData(String target, String triggerEvent, float chance, boolean canActivateSheerForce, String name) {
        super("misc", target, triggerEvent, chance, canActivateSheerForce, name);
    }

    @Override
    public void invoke(PokemonEntity pokemonEntity, LivingEntity target) {
        LivingEntity finalTarget = pickTarget(pokemonEntity, target);
        if (finalTarget == null) {
            return;
        }
        if (Objects.equals("recharge_1_turn", getName()) || Objects.equals("charge_1_turn", getName())) {
            if (PokemonUtils.getTarget(pokemonEntity) != null) {
                int originalAttackTime = ((PokemonInterface) pokemonEntity).getAttackTime();
                PokemonAttackEffect.refreshAttackTime(pokemonEntity, originalAttackTime * 2);
            }
        }
        if (Objects.equals("heal_per_50", getName())) {
            finalTarget.heal(0.5f * finalTarget.getMaxHealth());
        }
        if (Objects.equals("spikes", getName())) {
            PokemonAttackEffect.spreadSpikes(pokemonEntity, getName());
        }
        if (Objects.equals("toxic_spikes", getName())) {
            PokemonAttackEffect.spreadSpikes(pokemonEntity, getName());
        }
        if (Objects.equals("stealth_rock", getName())) {
            PokemonAttackEffect.spreadSpikes(pokemonEntity, getName());
        }
        if (Objects.equals("sticky_web", getName())) {
            PokemonAttackEffect.spreadSpikes(pokemonEntity, getName());
        }
        if (Objects.equals("taunt", getName())) {
            PokemonUtils.taunt(pokemonEntity);
        }
    }
}
