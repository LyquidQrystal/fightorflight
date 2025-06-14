package me.rufia.fightorflight.utils;

import com.cobblemon.mod.common.api.pokemon.stats.Stat;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.api.tags.CobblemonItemTags;
import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.Map;

public class FOFEVCalculator {
    protected static Map<Stat, TagKey<Item>> POWER_ITEM = new HashMap<>() {
        {
            put(Stats.HP, CobblemonItemTags.POWER_WEIGHT);
            put(Stats.ATTACK, CobblemonItemTags.POWER_BRACER);
            put(Stats.DEFENCE, CobblemonItemTags.POWER_BELT);
            put(Stats.SPECIAL_ATTACK, CobblemonItemTags.POWER_LENS);
            put(Stats.SPECIAL_DEFENCE, CobblemonItemTags.POWER_BAND);
            put(Stats.SPEED, CobblemonItemTags.POWER_ANKLET);
        }
    };

    public static Map<Stat, Integer> calculate(Pokemon battlePokemon, Pokemon opponentPokemon) {
        var item = battlePokemon.heldItem();

        var yield = opponentPokemon.getForm().getEvYield();
        var total = new HashMap<>(yield);
        for (Map.Entry<Stat, Integer> entry : yield.entrySet()) {
            int boost = FOFHeldItemManager.canUseHeldItemGlobal() && !item.isEmpty() && item.is(POWER_ITEM.get(entry.getKey())) ? 8 : 0;
            total.put(entry.getKey(), boost + yield.get(entry.getKey()));
        }
        return total;
    }
}
