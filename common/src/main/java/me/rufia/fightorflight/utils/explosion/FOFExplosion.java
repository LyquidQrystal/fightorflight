package me.rufia.fightorflight.utils.explosion;

import com.cobblemon.mod.common.api.moves.Move;
import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.api.types.ElementalTypes;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.entity.PokemonAttackEffect;
import me.rufia.fightorflight.utils.PokemonUtils;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class FOFExplosion extends Explosion {
    protected static final ExplosionDamageCalculator EXPLOSION_DAMAGE_CALCULATOR = new ExplosionDamageCalculator();
    protected static final int MAX_DROPS_PER_COMBINED_STACK = 16;
    protected final boolean fire;
    protected final Explosion.BlockInteraction blockInteraction;
    protected final RandomSource random;
    protected final Level level;
    protected final double x;
    protected final double y;
    protected final double z;
    protected final PokemonEntity pokemon;
    protected final boolean shouldHurtAlly;
    @Nullable
    public Entity source;
    public float radius;
    protected final DamageSource damageSource;
    protected final ExplosionDamageCalculator damageCalculator;
    protected final ObjectArrayList<BlockPos> toBlow;
    protected final Map<Player, Vec3> hitPlayers;

    public FOFExplosion(Level level, @Nullable Entity source, PokemonEntity pokemon, @Nullable DamageSource damageSource, @Nullable ExplosionDamageCalculator damageCalculator, double toBlowX, double toBlowY, double toBlowZ, float radius, boolean fire, Explosion.BlockInteraction blockInteraction, boolean shouldHurtAlly, boolean isProjectileExplosion) {
        super(level, source, damageSource, damageCalculator, toBlowX, toBlowY, toBlowZ, radius, fire, blockInteraction, null, null, SoundEvents.GENERIC_EXPLODE);
        this.random = RandomSource.create();
        this.toBlow = new ObjectArrayList<>();
        this.hitPlayers = Maps.newHashMap();
        this.level = level;
        this.source = source;
        this.radius = radius + pokemonExplosionBaseValue(isProjectileExplosion);
        this.x = toBlowX;
        this.y = toBlowY;
        this.z = toBlowZ;
        this.fire = fire;
        this.blockInteraction = blockInteraction;
        this.damageSource = damageSource == null ? level.damageSources().generic() : damageSource;
        this.damageCalculator = damageCalculator == null ? this.makeDamageCalculator(source) : damageCalculator;
        this.pokemon = pokemon;
        this.shouldHurtAlly = shouldHurtAlly;
    }

    protected ExplosionDamageCalculator makeDamageCalculator(@Nullable Entity entity) {
        return (entity == null ? EXPLOSION_DAMAGE_CALCULATOR : new EntityBasedExplosionDamageCalculator(entity));
    }

    public static Optional<Float> getBlockExplosionResistance(BlockState state, FluidState fluid) {
        return state.isAir() && fluid.isEmpty() ? Optional.empty() : Optional.of(Math.max(state.getBlock().getExplosionResistance(), fluid.getExplosionResistance()));
    }

    public boolean shouldBlockExplode(Explosion explosion, BlockGetter reader, BlockPos pos, BlockState state, float power) {
        return true;
    }

    public void explode() {
        this.level.gameEvent(this.source, GameEvent.EXPLODE, new Vec3(this.x, this.y, this.z));
        Set<BlockPos> set = Sets.newHashSet();

        for (int j = 0; j < 16; ++j) {
            for (int k = 0; k < 16; ++k) {
                for (int l = 0; l < 16; ++l) {
                    if (j == 0 || j == 15 || k == 0 || k == 15 || l == 0 || l == 15) {
                        double d = (double) ((float) j / 15.0F * 2.0F - 1.0F);
                        double e = (double) ((float) k / 15.0F * 2.0F - 1.0F);
                        double f = (double) ((float) l / 15.0F * 2.0F - 1.0F);
                        double g = Math.sqrt(d * d + e * e + f * f);
                        d /= g;
                        e /= g;
                        f /= g;
                        float h = this.radius * (0.8F + this.level.random.nextFloat() * 0.2F);
                        double m = this.x;
                        double n = this.y;
                        double o = this.z;

                        for (float p = 0.3F; h > 0.0F; h -= 0.22500001F) {
                            BlockPos blockPos = BlockPos.containing(m, n, o);
                            BlockState blockState = this.level.getBlockState(blockPos);
                            FluidState fluidState = this.level.getFluidState(blockPos);
                            if (!this.level.isInWorldBounds(blockPos)) {
                                break;
                            }

                            Optional<Float> optional = getBlockExplosionResistance(blockState, fluidState);
                            if (optional.isPresent()) {
                                h -= (optional.get() + 0.3F) * 0.3F;
                                //CobblemonFightOrFlight.LOGGER.info(Float.toString(h));
                            }

                            if (h > 0.0F) {
                                set.add(blockPos);
                            }

                            m += d * 0.3;
                            n += e * 0.3;
                            o += f * 0.3;
                        }
                    }
                }
            }
        }

        this.toBlow.addAll(set);
        PokemonAttackEffect.dealAoEDamage(pokemon, source, shouldHurtAlly, false);
    }

    public void finalizeExplosion() {
        if (this.level.isClientSide) {
            this.level.playLocalSound(this.x, this.y, this.z, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 4.0F, (1.0F + (this.level.random.nextFloat() - this.level.random.nextFloat()) * 0.2F) * 0.7F, false);
        } else {
            this.level.playSound(null, x, y, z, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 4.0F, (1.0F + (this.level.random.nextFloat() - this.level.random.nextFloat()) * 0.2F) * 0.7F);
        }
        //CobblemonFightOrFlight.LOGGER.info("Explosion finalizing");
        boolean bl = this.interactsWithBlocks();

        ParticleOptions particleType;
        if (!(this.radius < 2.0F)) {
            particleType = ParticleTypes.EXPLOSION_EMITTER;
        } else {
            particleType = ParticleTypes.EXPLOSION;
        }

        if (this.level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(particleType, this.x, this.y, this.z, 1, 1.0, 0.0, 0.0, 1.0f);
        }
        //replace it with type-specific particle

        if (bl) {
            ObjectArrayList<Pair<ItemStack, BlockPos>> objectArrayList = new ObjectArrayList<>();
            boolean bl2 = pokemon.getOwner() instanceof Player;
            Util.shuffle(this.toBlow, this.level.random);
            ObjectListIterator<BlockPos> it1 = this.toBlow.iterator();
            while (it1.hasNext()) {
                BlockPos blockPos = it1.next();
                BlockState blockState = this.level.getBlockState(blockPos);
                net.minecraft.world.level.block.Block block = blockState.getBlock();
                if (!blockState.isAir()) {
                    BlockPos blockPos2 = blockPos.immutable();
                    this.level.getProfiler().push("explosion_blocks");
                    if (block.dropFromExplosion(this)) {
                        if (this.level instanceof ServerLevel serverLevel) {
                            BlockEntity blockEntity = blockState.hasBlockEntity() ? this.level.getBlockEntity(blockPos) : null;
                            LootParams.Builder builder = (new LootParams.Builder(serverLevel)).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(blockPos)).withParameter(LootContextParams.TOOL, ItemStack.EMPTY).withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockEntity).withOptionalParameter(LootContextParams.THIS_ENTITY, this.source);
                            if (this.blockInteraction == BlockInteraction.DESTROY_WITH_DECAY) {
                                builder.withParameter(LootContextParams.EXPLOSION_RADIUS, this.radius);
                            }
                            blockState.spawnAfterBreak(serverLevel, blockPos, ItemStack.EMPTY, bl2);
                            blockState.getDrops(builder).forEach((itemStack) -> addBlockDrops(objectArrayList, itemStack, blockPos2));
                        }
                    }

                    this.level.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 3);
                    block.wasExploded(this.level, blockPos, this);
                    this.level.getProfiler().pop();
                }
            }

            for (Pair<ItemStack, BlockPos> pair : objectArrayList) {
                //CobblemonFightOrFlight.LOGGER.info("Scanning");
                net.minecraft.world.level.block.Block.popResource(this.level, pair.getSecond(), pair.getFirst());
            }
        }

        if (this.fire) {
            for (BlockPos blockPos3 : this.toBlow) {
                if (this.random.nextInt(3) == 0 && this.level.getBlockState(blockPos3).isAir() && this.level.getBlockState(blockPos3.below()).isSolidRender(this.level, blockPos3.below())) {
                    this.level.setBlockAndUpdate(blockPos3, BaseFireBlock.getState(this.level, blockPos3));
                }
            }
        }
    }

    public boolean interactsWithBlocks() {
        return this.blockInteraction != Explosion.BlockInteraction.KEEP;
    }

    protected static void addBlockDrops(ObjectArrayList<Pair<ItemStack, BlockPos>> dropPositionArray, ItemStack stack, BlockPos pos) {
        int i = dropPositionArray.size();

        for (int j = 0; j < i; ++j) {
            Pair<ItemStack, BlockPos> pair = dropPositionArray.get(j);
            ItemStack itemStack = pair.getFirst();
            if (ItemEntity.areMergable(itemStack, stack)) {
                ItemStack itemStack2 = ItemEntity.merge(itemStack, stack, 16);
                dropPositionArray.set(j, Pair.of(itemStack2, pair.getSecond()));
                if (stack.isEmpty()) {
                    return;
                }
            }
        }

        dropPositionArray.add(Pair.of(stack, pos));
    }

    public static FOFExplosion createExplosion(Entity source, PokemonEntity pokemonEntity, double x, double y, double z, boolean shouldHurtAlly, boolean isProjectileExplosion) {
        if (pokemonEntity == null) {
            CobblemonFightOrFlight.LOGGER.warn("trying to create a new FOFExplosion without PokemonEntity");
            return null;
        }
        float radius = calculateRadius(pokemonEntity);
        ElementalType type1 = pokemonEntity.getPokemon().getPrimaryType();
        ElementalType type2 = pokemonEntity.getPokemon().getSecondaryType();
        boolean shouldCreateFire = CobblemonFightOrFlight.moveConfig().should_create_fire && (type1.equals(ElementalTypes.INSTANCE.getFIRE()) || (type2 != null && type2.equals(ElementalTypes.INSTANCE.getFIRE())));
        BlockInteraction blockInteraction1;
        if (CobblemonFightOrFlight.moveConfig().pokemon_griefing) {
            blockInteraction1 = BlockInteraction.DESTROY_WITH_DECAY;
            CobblemonFightOrFlight.LOGGER.info("will destroy blocks");
        } else {
            blockInteraction1 = BlockInteraction.KEEP;
        }
        return new FOFExplosion(source.level(), source, pokemonEntity, source.damageSources().explosion(pokemonEntity, pokemonEntity), null, x, y, z, radius, shouldCreateFire, blockInteraction1, shouldHurtAlly, isProjectileExplosion);
    }

    protected static float calculateRadius(PokemonEntity pokemonEntity) {
        Move move = PokemonUtils.shouldShoot(pokemonEntity) ? PokemonUtils.getRangeAttackMove(pokemonEntity) : PokemonUtils.getMeleeMove(pokemonEntity);
        if (move == null) {
            return 0.0f;
        }
        return PokemonAttackEffect.getAoERadius(pokemonEntity, move);
    }

    protected float pokemonExplosionBaseValue(boolean isProjectileExplosion) {
        return (isProjectileExplosion ? 0.0f : 5.0f);
    }
}
