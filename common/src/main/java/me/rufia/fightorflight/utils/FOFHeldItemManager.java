package me.rufia.fightorflight.utils;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import net.minecraft.world.item.Item;

public class FOFHeldItemManager {
    public static boolean canUseHeldItemGlobal() {
        return CobblemonFightOrFlight.commonConfig().can_use_held_item;
    }

    public static boolean canUseHeldItemDamageInfluencing() {
        return canUseHeldItemGlobal() && CobblemonFightOrFlight.commonConfig().can_use_held_item_damage_influencing;
    }

    public static boolean canUseHeldItemHPInfluencing() {
        return canUseHeldItemGlobal() && CobblemonFightOrFlight.commonConfig().can_use_held_item_hp_influencing;
    }

    public static boolean canUse(PokemonEntity pokemonEntity, Item item) {
        if (!canUseHeldItemGlobal()) {
            return false;
        }
        if (pokemonEntity != null) {
            return canUse(pokemonEntity.getPokemon(), item);
        }
        return false;
    }

    public static boolean canUse(Pokemon pokemon, Item item) {
        if (!canUseHeldItemGlobal()) {
            return false;
        }
        if (pokemon != null && item != null) {
            return pokemon.heldItem().is(item);
        }
        return false;
    }
}
