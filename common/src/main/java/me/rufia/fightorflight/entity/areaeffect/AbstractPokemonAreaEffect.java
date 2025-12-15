package me.rufia.fightorflight.entity.areaeffect;

import com.cobblemon.mod.common.api.moves.Move;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.entity.IPokemonAttack;
import me.rufia.fightorflight.entity.PokemonAttackEffect;
import me.rufia.fightorflight.utils.PokemonUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public abstract class AbstractPokemonAreaEffect extends Entity implements IPokemonAttack {
    private static final EntityDataAccessor<Float> DATA_RADIUS;
    private static final EntityDataAccessor<Float> DATA_HEIGHT;
    private static final EntityDataAccessor<String> DATA_TYPE;
    private static final EntityDataAccessor<Boolean> DATA_WAITING;
    private static final EntityDataAccessor<String> DATA_MOVE_NAME;
    protected int duration;
    protected int waitTime;
    protected boolean isInstant;
    protected boolean activated;
    @Nullable
    protected LivingEntity owner;
    @Nullable
    protected UUID ownerUUID;


    public AbstractPokemonAreaEffect(EntityType<? extends AbstractPokemonAreaEffect> entityType, Level level) {
        super(entityType, level);
        this.duration = 100;
        this.waitTime = 10;
        this.noPhysics = true;
        this.isInstant = false;
        this.activated = false;
    }

    public static AbstractPokemonAreaEffect tryToCreate(PokemonEntity owner, LivingEntity target, Move move) {
        if (move == null) {
            return null;
        }
        String moveName = move.getName();
        boolean canFloat = Arrays.stream(CobblemonFightOrFlight.moveConfig().delayed_aoe_can_float).toList().contains(moveName);
        boolean isInstant = Arrays.stream(CobblemonFightOrFlight.moveConfig().delayed_aoe_is_instant).toList().contains(moveName);
        boolean isTornado = Arrays.stream(CobblemonFightOrFlight.moveConfig().delayed_aoe_rise_up_tornado).toList().contains(moveName);
        boolean isWhirlpool = Arrays.stream(CobblemonFightOrFlight.moveConfig().delayed_aoe_bounding_whirlpool).toList().contains(moveName);
        float r = Mth.clamp(owner.getBbWidth(), 1f, 3f) * 1.5f;
        AbstractPokemonAreaEffect aoe;
        if (isTornado) {
            aoe = new PokemonTornado(owner);
        } else if (isWhirlpool) {
            aoe = new PokemonWhirlPool(owner);
        } else {
            aoe = new PokemonAreaEffectMagic(owner);
        }

        aoe.setMoveName(moveName);
        aoe.setOwner(owner);
        aoe.init(target, 30, 10, canFloat, isInstant);
        aoe.refreshHeight();
        aoe.setRadius(r);

        return aoe;
    }


    public void init(LivingEntity target, int duration, int waitTime, boolean canFloat, boolean isInstant) {
        this.duration = duration;
        this.waitTime = waitTime;
        this.isInstant = isInstant;

        if (canFloat) {
            setPos(target.position());
        } else {
            BlockPos blockPos = target.getOnPos();
            int y = blockPos.getY();
            boolean yFound = false;
            while (!yFound && y > level().getMinBuildHeight()) {
                BlockState blockState = target.level().getBlockState(blockPos);
                if (blockState.blocksMotion()) {
                    yFound = true;
                } else {
                    --y;
                    blockPos = blockPos.below();
                }
            }
            if (yFound) {
                setPos(target.getX(), y + 1, target.getZ());
            }
        }
    }

    public void refreshHeight() {
        float r = getRadius();
        if (this instanceof PokemonTornado) {
            setHeight(r);
        } else {
            setHeight(0.5f);
        }
        //CobblemonFightOrFlight.LOGGER.info("AOE Height:{}", getHeight());
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide) {
            boolean wasWaiting = isWaiting();
            visualEffect();

            if (this.tickCount > duration || getOwner() == null) {
                discard();
                return;
            }

            boolean isWaitingNow = tickCount < waitTime;
            if (wasWaiting != isWaitingNow) {
                setWaiting(isWaitingNow);
            }

            if (isWaitingNow) {
                return;
            }

            if ((tickCount + 1) % getApplicationTime() == 0) {
                if (!activated || !isInstant) {
                    activate();
                }
            }
        }
    }

    protected abstract void visualEffect();

    protected void activate() {
        if (!activated) {
            onActivated();
        }
        activated = true;
    }

    protected void onActivated() {
        playActivateSound();
    }

    protected void playActivateSound() {
        String typeName = getElementalType();
        switch (typeName) {
            case "Electric" ->
                    playSound(SoundEvents.LIGHTNING_BOLT_THUNDER, 4f, (1.0F + (level().random.nextFloat() - level().random.nextFloat()) * 0.2F) * 0.7F);
            case "Fire" ->
                    playSound(SoundEvents.GENERIC_BURN, 4f, (1.0F + (level().random.nextFloat() - level().random.nextFloat()) * 0.2F) * 0.7F);
            case "Water" ->
                    playSound(SoundEvents.GENERIC_SPLASH, 4f, (1.0F + (level().random.nextFloat() - level().random.nextFloat()) * 0.2F) * 0.7F);
            default -> playDefaultSound();
        }
    }

    protected void playDefaultSound() {
        playSound(SoundEvents.GENERIC_EXPLODE.value(), 3f, (1.0F + (level().random.nextFloat() - level().random.nextFloat()) * 0.2F) * 0.7F);
    }

    protected void dealDamageInTheArea() {
        if (owner instanceof PokemonEntity pokemonEntity && pokemonEntity.isAlive()) {
            List<LivingEntity> list = level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox());
            for (LivingEntity target : list) {
                if (!PokemonUtils.pokemonTryForceEncounter(pokemonEntity, target)) {
                    Move move = PokemonUtils.findMove(pokemonEntity, getMoveName());
                    if (move != null) {
                        if (PokemonAttackEffect.shouldHurtAllyMob(pokemonEntity, target)) {
                            boolean success = target.hurt(pokemonEntity.damageSources().indirectMagic(pokemonEntity, pokemonEntity), PokemonAttackEffect.calculatePokemonDamage(pokemonEntity, target, move));
                            PokemonUtils.setHurtByPlayer(pokemonEntity, target);
                            PokemonAttackEffect.applyOnHitVisualEffect(pokemonEntity, target, getMoveName());
                            PokemonAttackEffect.applyPostEffect(pokemonEntity, target, move, success);
                            applyExtraEffect(target);
                        }
                    }
                }
            }
        }
    }

    protected void applyExtraEffect(LivingEntity target) {

    }

    public int getApplicationTime() {
        return 5;
    }

    public float getRadius() {
        return entityData.get(DATA_RADIUS);
    }

    public void setRadius(float r) {
        entityData.set(DATA_RADIUS, r);
    }

    public float getHeight() {
        return entityData.get(DATA_HEIGHT);
    }

    public void setHeight(float h) {
        entityData.set(DATA_HEIGHT, h);
    }

    public boolean isActivated() {
        return activated;
    }

    @Override
    public @NotNull PushReaction getPistonPushReaction() {
        return PushReaction.IGNORE;
    }

    @Override
    public @NotNull EntityDimensions getDimensions(Pose pose) {
        //CobblemonFightOrFlight.LOGGER.info("AOE Height before refreshing dimensions:{}", height);
        return EntityDimensions.scalable(getRadius() * 2.0F, getHeight());
    }

    @Override
    public void refreshDimensions() {
        double d = this.getX();
        double e = this.getY();
        double f = this.getZ();
        super.refreshDimensions();
        this.setPos(d, e, f);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_RADIUS, 3f);
        builder.define(DATA_HEIGHT, 0.5f);
        builder.define(DATA_TYPE, "normal");
        builder.define(DATA_WAITING, true);
        builder.define(DATA_MOVE_NAME, "");
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> dataAccessor) {
        if (DATA_RADIUS.equals(dataAccessor)) {
            this.refreshDimensions();
        }

        super.onSyncedDataUpdated(dataAccessor);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        tickCount = compound.getInt("Age");
        duration = compound.getInt("Duration");
        waitTime = compound.getInt("WaitTime");
        setHeight(compound.getFloat("Height"));
        setRadius(compound.getFloat("Radius"));
        if (compound.hasUUID("Owner")) {
            this.ownerUUID = compound.getUUID("Owner");
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putInt("Age", tickCount);
        compound.putInt("Duration", duration);
        compound.putInt("WaitTime", waitTime);
        compound.putFloat("Radius", getRadius());
        compound.putFloat("Height", getHeight());
        if (ownerUUID != null) {
            compound.putUUID("Owner", this.ownerUUID);
        }
    }

    public void setOwner(@Nullable LivingEntity owner) {
        this.owner = owner;
        this.ownerUUID = owner == null ? null : owner.getUUID();
    }

    @Nullable
    public LivingEntity getOwner() {
        if (this.owner == null && this.ownerUUID != null && this.level() instanceof ServerLevel) {
            Entity entity = ((ServerLevel) this.level()).getEntity(this.ownerUUID);
            if (entity instanceof LivingEntity) {
                this.owner = (LivingEntity) entity;
            }
        }

        return this.owner;
    }

    @Override
    public String getElementalType() {
        return entityData.get(DATA_TYPE);
    }

    @Override
    public void setElementalType(String type) {
        entityData.set(DATA_TYPE, type);
    }

    protected void setWaiting(boolean waiting) {
        this.getEntityData().set(DATA_WAITING, waiting);
    }

    public boolean isWaiting() {
        return this.getEntityData().get(DATA_WAITING);
    }

    public void setMoveName(String moveName) {
        entityData.set(DATA_MOVE_NAME, moveName);
    }

    public String getMoveName() {
        return entityData.get(DATA_MOVE_NAME);
    }

    public int getDuration() {
        return duration;
    }

    public int getWaitTime() {
        return waitTime;
    }

    static {
        DATA_RADIUS = SynchedEntityData.defineId(AbstractPokemonAreaEffect.class, EntityDataSerializers.FLOAT);
        DATA_HEIGHT = SynchedEntityData.defineId(AbstractPokemonAreaEffect.class, EntityDataSerializers.FLOAT);
        DATA_TYPE = SynchedEntityData.defineId(AbstractPokemonAreaEffect.class, EntityDataSerializers.STRING);
        DATA_WAITING = SynchedEntityData.defineId(AbstractPokemonAreaEffect.class, EntityDataSerializers.BOOLEAN);
        DATA_MOVE_NAME = SynchedEntityData.defineId(AbstractPokemonAreaEffect.class, EntityDataSerializers.STRING);
    }
}
