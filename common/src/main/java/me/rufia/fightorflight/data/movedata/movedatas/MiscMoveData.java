package me.rufia.fightorflight.data.movedata.movedatas;

import com.cobblemon.mod.common.api.moves.Move;
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
            if (pokemonEntity.getTarget() != null) {
                int originalAttackTime = ((PokemonInterface) pokemonEntity).getAttackTime();
                PokemonAttackEffect.refreshAttackTime(pokemonEntity, originalAttackTime * 2);
            }
        }
        if (Objects.equals("heal_per_50", getName())) {
            finalTarget.heal(0.5f * finalTarget.getMaxHealth());
        }
        if (Objects.equals("spikes", getName())) {
            Move move = PokemonUtils.getStatusMove(pokemonEntity);
            PokemonAttackEffect.spreadSpikes(pokemonEntity, move);
        }
        if (Objects.equals("toxic_spikes", getName())) {
            Move move = PokemonUtils.getStatusMove(pokemonEntity);
            PokemonAttackEffect.spreadSpikes(pokemonEntity, move);
        }
    }
}
