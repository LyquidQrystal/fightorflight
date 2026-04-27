package me.rufia.fightorflight.effects;

import me.rufia.fightorflight.platform.EffectRegister;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public interface FOFEffects {
    Holder<MobEffect> RESISTANCE_WEAKENED = EffectRegister.register("resistance_weakened", () -> new FOFStatusEffect(MobEffectCategory.HARMFUL, 0xB40C0C));
    static void bootstrap() {
    }
}
