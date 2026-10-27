package me.rufia.fightorflight.utils.event.move;

import com.cobblemon.mod.common.api.moves.Move;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import me.rufia.fightorflight.entity.projectile.AbstractPokemonProjectile;
import net.minecraft.world.entity.LivingEntity;

public abstract class FOFShootProjectileEvent<T extends AbstractPokemonProjectile> extends FOFRangeAttackEvent {

    public FOFShootProjectileEvent(int createTime, int duration, PokemonEntity user, LivingEntity target, Move move) {
        super(createTime, duration, user, target, move);
    }

    public abstract void launchProjectileEntity();

    @Override
    public void use() {
        launchProjectileEntity();
    }
}
