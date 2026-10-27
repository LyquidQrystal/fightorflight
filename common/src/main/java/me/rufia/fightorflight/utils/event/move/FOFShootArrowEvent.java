package me.rufia.fightorflight.utils.event.move;

import com.cobblemon.mod.common.api.moves.Move;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import me.rufia.fightorflight.entity.PokemonAttackEffect;
import me.rufia.fightorflight.entity.projectile.PokemonArrow;
import net.minecraft.world.entity.LivingEntity;

public class FOFShootArrowEvent extends FOFShootProjectileEvent<PokemonArrow> {
    public FOFShootArrowEvent(int createTime, int duration, PokemonEntity user, LivingEntity target, Move move) {
        super(createTime, duration, user, target, move);
    }

    @Override
    public void launchProjectileEntity() {
        PokemonArrow arrow = new PokemonArrow(getUser().level(), getUser(), getTarget());
        PokemonAttackEffect.shootProjectileEntity(getTarget(), arrow);
        PokemonAttackEffect.addProjectileEntity(getUser(), getTarget(), arrow,getMove());
    }
}
