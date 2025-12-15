package me.rufia.fightorflight.utils;

import com.cobblemon.mod.common.CobblemonSounds;
import com.cobblemon.mod.common.api.types.ElementalType;
import net.minecraft.sounds.SoundEvent;

public class FOFSoundManager {
    public static SoundEvent getTypeSound(String typeName) {
        String name = typeName.toLowerCase();
        return switch (name) {
            case "fire" -> CobblemonSounds.IMPACT_FIRE;
            case "ice" -> CobblemonSounds.IMPACT_ICE;
            case "poison" -> CobblemonSounds.IMPACT_POISON;
            case "psychic" -> CobblemonSounds.IMPACT_PSYCHIC;
            case "fairy" -> CobblemonSounds.IMPACT_FAIRY;
            case "fighting" -> CobblemonSounds.IMPACT_FIGHTING;
            case "ground" -> CobblemonSounds.IMPACT_GROUND;
            case "rock" -> CobblemonSounds.IMPACT_ROCK;
            case "steel" -> CobblemonSounds.IMPACT_STEEL;
            case "ghost" -> CobblemonSounds.IMPACT_GHOST;
            case "dark" -> CobblemonSounds.IMPACT_DARK;
            case "electric" -> CobblemonSounds.IMPACT_ELECTRIC;
            case "bug" -> CobblemonSounds.IMPACT_BUG;
            case "grass" -> CobblemonSounds.IMPACT_GRASS;
            case "dragon" -> CobblemonSounds.IMPACT_DRAGON;
            case "flying" -> CobblemonSounds.IMPACT_FLYING;
            case "water" -> CobblemonSounds.IMPACT_WATER;
            case "normal" -> CobblemonSounds.IMPACT_NORMAL;
            default -> CobblemonSounds.IMPACT_NORMAL;
        };
    }

    public static SoundEvent getTypeSound(ElementalType type) {
        return getTypeSound(type.getName());
    }
}
