package me.rufia.fightorflight.utils.event.move;

import com.cobblemon.mod.common.api.moves.Move;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import me.rufia.fightorflight.entity.PokemonAttackEffect;
import me.rufia.fightorflight.entity.projectile.PokemonTracingBullet;
import net.minecraft.world.entity.LivingEntity;

public class FOFShootTracingBulletEvent extends FOFShootProjectileEvent<PokemonTracingBullet> {
    public FOFShootTracingBulletEvent(int createTime, int duration, PokemonEntity user, LivingEntity target, Move move) {
        super(createTime, duration, user, target, move);
    }

    @Override
    public void launchProjectileEntity() {
        var bullet = new PokemonTracingBullet(getUser().level(), getUser(), getTarget(), getUser().getDirection().getAxis());
        PokemonAttackEffect.addProjectileEntity(getUser(), getTarget(), bullet, getMove());
    }
}
