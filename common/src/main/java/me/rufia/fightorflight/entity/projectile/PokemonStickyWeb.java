package me.rufia.fightorflight.entity.projectile;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import me.rufia.fightorflight.entity.EntityFightOrFlight;
import me.rufia.fightorflight.entity.PokemonAttackEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class PokemonStickyWeb extends AbstractPokemonSpike {

    public PokemonStickyWeb(EntityType<? extends AbstractPokemonProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public PokemonStickyWeb(Level level, LivingEntity shooter) {
        super(EntityFightOrFlight.STICKY_WEB.get(), level);
        activated = false;
        life = 0;
        inGround = false;
        initPosition(shooter);
    }

    @Override
    protected void hurtEntity(LivingEntity target) {
        if (getOwner() instanceof PokemonEntity pokemonEntity) {
            if (PokemonAttackEffect.shouldHurtAllyMob(pokemonEntity, target)) {
                target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 0), pokemonEntity);
            }
        }
    }
}
