package me.rufia.fightorflight.data.movedata;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class MoveData {
    public static final Map<String, List<MoveData>> moveData = new HashMap<>();
    private final String type;
    private final String target;
    private final float chance;
    private final boolean canActivateSheerForce;
    private final String triggerEvent;
    private String name;

    protected LivingEntity pickTarget(PokemonEntity pokemonEntity, LivingEntity hurtTarget) {
        if (target.equals("self")) {
            return pokemonEntity;
        } else if (target.equals("target")) {
            return hurtTarget;
        }
        return null;
    }

    protected boolean chanceTest(RandomSource source) {
        return chanceTest(source, null);
    }

    protected boolean chanceTest(RandomSource source, PokemonEntity pokemonEntity) {
        float sereneGraceMultiplier = 1f;
        if (pokemonEntity != null) {
            sereneGraceMultiplier = Objects.equals(pokemonEntity.getPokemon().getAbility().getName(), "serenegrace") ? 2f : 1f;
        }
        float rand = source.nextFloat();
        return chance * sereneGraceMultiplier > rand;
    }

    public String getType() {
        return type;
    }

    public String getTarget() {
        return target;
    }

    public float getChance() {
        return chance;
    }

    public boolean canActivateSheerForce() {
        return canActivateSheerForce;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTriggerEvent() {
        return triggerEvent;
    }

    public boolean isOnHit() {
        return Objects.equals("on_hit", triggerEvent);
    }

    public boolean isOnUse() {
        return Objects.equals("on_use", triggerEvent);
    }

    public int calculateEffectDuration(PokemonEntity pokemonEntity) {
        return Math.max(pokemonEntity.getPokemon().getLevel() / 20, 1)+7;
    }

    public MoveData(String type, String target, String triggerEvent, float chance, boolean canActivateSheerForce, String name) {
        this.type = type;
        this.target = target;
        this.chance = chance;
        this.canActivateSheerForce = canActivateSheerForce;
        this.name = name;
        this.triggerEvent = triggerEvent;
    }

    public abstract void invoke(PokemonEntity pokemonEntity, LivingEntity target);
}
