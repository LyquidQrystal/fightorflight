package me.rufia.fightorflight.utils;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.tags.CobblemonItemTags;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.evolution.variants.LevelUpEvolution;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.item.ItemFightOrFlight;

/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
//This class is a rewritten version of the ExperienceCalculator interface from Cobblemon
public class FOFExpCalculator {
    public static int calculate(Pokemon battlePokemon, Pokemon opponentPokemon) {
        float FOFExpMultiplier = CobblemonFightOrFlight.commonConfig().experience_multiplier;
        int baseExp = opponentPokemon.getForm().getBaseExperienceYield();
        int opponentLevel = opponentPokemon.getLevel();
        float term1 = (float) (baseExp * opponentLevel) / 5.0f;
        float victorLevel = battlePokemon.getLevel();
        float term2 = (float) Math.pow(((2.0f * opponentLevel) + 10) / (opponentLevel + victorLevel + 10), 2.5);
        boolean hasLuckyEgg = battlePokemon.heldItemNoCopy$common().is(CobblemonItemTags.LUCKY_EGG) || battlePokemon.heldItemNoCopy$common().is(ItemFightOrFlight.ORANLUCKYEGG.get());
        float luckyEggMultiplier = hasLuckyEgg ? (float) Cobblemon.config.getLuckyEggMultiplier() : 1.0f;
        float evolutionMultiplier = battlePokemon.getEvolutionProxy().server().stream().anyMatch(evolution -> {
            var requirements = evolution.getRequirements();
            return requirements.stream().anyMatch(evolutionRequirement -> evolutionRequirement instanceof LevelUpEvolution) && requirements.stream().allMatch(evolutionRequirement -> evolutionRequirement.check(battlePokemon));
        }) ? 1.2f : 1.0f;
        float affectionMultiplier = battlePokemon.getFriendship() >= 220 ? 1.2f : 1.0f;
        float gimmickBoost = Cobblemon.config.getExperienceMultiplier();
        float term3 = term1 * term2 + 1;
        return Math.round(term3 * luckyEggMultiplier * evolutionMultiplier * affectionMultiplier * gimmickBoost * FOFExpMultiplier) + 1;
    }
}
