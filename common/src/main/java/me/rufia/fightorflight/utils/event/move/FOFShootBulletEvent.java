package me.rufia.fightorflight.utils.event.move;

import com.cobblemon.mod.common.api.moves.Move;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import me.rufia.fightorflight.entity.PokemonAttackEffect;
import me.rufia.fightorflight.entity.projectile.PokemonBullet;
import net.minecraft.world.entity.LivingEntity;

public class FOFShootBulletEvent extends FOFShootProjectileEvent<PokemonBullet> {
    public FOFShootBulletEvent(int createTime, int duration, PokemonEntity user, LivingEntity target, Move move) {
        super(createTime, duration, user, target, move);
    }

    @Override
    public void launchProjectileEntity() {
        PokemonBullet bullet = new PokemonBullet(getUser().level(), getUser());
        PokemonAttackEffect.shootProjectileEntity(getUser(), getTarget(), bullet);
        PokemonAttackEffect.addProjectileEntity(getUser(), getTarget(), bullet, getMove());
    }
}
