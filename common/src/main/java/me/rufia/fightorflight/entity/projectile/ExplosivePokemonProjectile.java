package me.rufia.fightorflight.entity.projectile;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import me.rufia.fightorflight.entity.PokemonAttackEffect;
import me.rufia.fightorflight.utils.PokemonUtils;
import me.rufia.fightorflight.utils.explosion.FOFExplosion;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public abstract class ExplosivePokemonProjectile extends AbstractPokemonProjectile {
    public ExplosivePokemonProjectile(EntityType<? extends AbstractPokemonProjectile> entityType, Level level) {
        super(entityType, level);
    }

    protected void explode(PokemonEntity owner) {
        level().broadcastEntityEvent(this, (byte) 17);
        this.gameEvent(GameEvent.EXPLODE, this.getOwner());
        this.dealExplosionDamage(owner);
        this.discard();
    }

    protected void dealExplosionDamage(PokemonEntity owner) {
        if (owner == null) {
            return;
        }
        FOFExplosion explosion = FOFExplosion.createExplosion(this, owner, getX(), getY(), getZ(), true, true);
        if (explosion != null) {
            explosion.explode();
            explosion.finalizeExplosion();
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        BlockPos blockPos = new BlockPos(result.getBlockPos());
        this.level().getBlockState(blockPos).entityInside(this.level(), blockPos, this);
        if (!this.level().isClientSide() && getOwner() instanceof PokemonEntity pokemonEntity) {
            this.explode(pokemonEntity);
        }
        super.onHitBlock(result);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity owner = getOwner();
        if (owner instanceof PokemonEntity pokemonEntity) {
            Entity hurtTarget = result.getEntity();
            if (!PokemonUtils.pokemonTryForceEncounter(pokemonEntity, hurtTarget)) {
                if (result.getEntity() instanceof LivingEntity target) {
                    if (!PokemonAttackEffect.shouldHurtAllyMob(pokemonEntity, target)) {
                        PokemonAttackEffect.applyPostEffect(pokemonEntity, target, PokemonUtils.getMove(pokemonEntity), true);
                        explode(pokemonEntity);
                    }
                }
            }
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
    }
}
