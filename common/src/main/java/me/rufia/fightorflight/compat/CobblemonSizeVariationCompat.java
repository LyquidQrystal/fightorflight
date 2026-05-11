package me.rufia.fightorflight.compat;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import dev.cudzer.cobblemonsizevariation.CobblemonSizeVariation;
import dev.cudzer.cobblemonsizevariation.config.Size;
import dev.cudzer.cobblemonsizevariation.sizing.algorithms.BasicSizer;
import dev.cudzer.cobblemonsizevariation.sizing.algorithms.GenIXSizer;
import dev.cudzer.cobblemonsizevariation.sizing.algorithms.ISizer;
import me.rufia.fightorflight.CobblemonFightOrFlight;

import java.util.Arrays;

public class CobblemonSizeVariationCompat extends AbstractModCompat {
    @Override
    public String getModID() {
        return "cobblemonsizevariation";
    }

    public float getExtraAggression(PokemonEntity pokemonEntity) {
        ISizer sizer = CobblemonSizeVariation.SIZER;
        float scaleModifier = pokemonEntity.getPokemon().getScaleModifier();
        Size size = sizer.getSizeInformation(scaleModifier);
        if (sizer instanceof GenIXSizer) {
            return checkSize(size, CobblemonFightOrFlight.commonConfig().size_S_gen9_sizer, CobblemonFightOrFlight.commonConfig().size_M_gen9_sizer, CobblemonFightOrFlight.commonConfig().size_L_gen9_sizer);
        } else if (sizer instanceof BasicSizer) {
            return checkSize(size, CobblemonFightOrFlight.commonConfig().size_S_basic_sizer, CobblemonFightOrFlight.commonConfig().size_M_basic_sizer, CobblemonFightOrFlight.commonConfig().size_L_basic_sizer);
        }

        return 0f;
    }

    protected float checkSize(Size size, String[] smallSizes, String[] mediumSizes, String[] largeSizes) {
        if (size != null) {
            String sizeName = size.name();
            if (Arrays.stream(smallSizes).toList().contains(sizeName)) {
                return CobblemonFightOrFlight.commonConfig().aggression_size_S_value;
            } else if (Arrays.stream(mediumSizes).toList().contains(sizeName)) {
                return CobblemonFightOrFlight.commonConfig().aggression_size_M_value;
            } else if (Arrays.stream(largeSizes).toList().contains(sizeName)) {
                return CobblemonFightOrFlight.commonConfig().aggression_size_L_value;
            }
        }
        return 0f;
    }
}
