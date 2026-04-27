package me.rufia.fightorflight.utils.signednumber;

import me.rufia.fightorflight.CobblemonFightOrFlight;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignedInt extends SignedNumber {
    int value;

    @Override
    public boolean load(String str) {
        String pattern = "([<>=])(\\d+)";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(str);
        if (m.find()) {
            try {
                if (m.groupCount() == 2) {
                    value = Integer.parseInt(m.group(2));
                    sign = m.group(1);
                    return true;
                }

            } catch (NumberFormatException e) {
                CobblemonFightOrFlight.LOGGER.warn("[FOF] Failed to convert the int in the datapack");
                return false;
            }
        }
        return false;
    }

    public boolean check(int i) {
        if (Objects.equals(sign, "=")) {
            return i == value;
        } else if (Objects.equals(sign, "<")) {
            return i < value;
        } else if (Objects.equals(sign, ">")) {
            return i > value;
        }
        return false;
    }
}
