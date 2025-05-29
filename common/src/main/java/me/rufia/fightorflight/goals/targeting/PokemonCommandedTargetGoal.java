package me.rufia.fightorflight.goals.targeting;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import me.rufia.fightorflight.PokemonInterface;
import me.rufia.fightorflight.item.component.PokeStaffComponent;
import me.rufia.fightorflight.utils.PokemonUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PokemonCommandedTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
    protected PokemonEntity pokemonEntity;
    protected float safeDistanceSqr = 64;

    public PokemonCommandedTargetGoal(Mob mob, Class<T> targetType, boolean mustSee) {
        super(mob, targetType, 5, mustSee, false, (entity) -> {
            if (entity == mob) {
                return false;
            }
            if (mob instanceof PokemonEntity pokemonEntity) {
                if (PokemonUtils.getCommandMode(pokemonEntity).equals(PokeStaffComponent.CMDMODE.ATTACK)) {
                    String data = ((PokemonInterface) (Object) pokemonEntity).getCommandData();
                    if (data.startsWith("ENTITY_")) {
                        Pattern pattern = Pattern.compile("ENTITY_([a-z\\d]{8}-[a-z\\d]{4}-[a-z\\d]{4}-[a-z\\d]{4}-[a-z\\d]{12})");
                        Matcher m = pattern.matcher(data);
                        if (m.find()) {
                            //CobblemonFightOrFlight.LOGGER.info(m.group(1));
                            return entity.getStringUUID().equals(m.group(1));
                        }
                    }
                }
            }
            return false;
        });
        pokemonEntity = (PokemonEntity) mob;
    }

    public void stop() {
        PokemonUtils.clearCommand(pokemonEntity);
    }

    @Override
    public boolean canUse() {
        if (pokemonEntity.isBusy() || pokemonEntity.getOwner() == null) {
            return false;
        }
        return super.canUse();
    }
}
