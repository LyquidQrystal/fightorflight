package me.rufia.fightorflight.utils.msg;

public class FactionColor {
    private final int sidebarColor;
    private final int topColor;
    private final int bottomColor;
    public static final FactionColor DARK_MODE = new FactionColor(0xFF90887C, 0xFF101020, 0x80060608);
    public static final FactionColor NEUTRAL = new FactionColor(0xFF90887C, 0xFFE0D8CC, 0x80242020);
    public static final FactionColor ALLY = new FactionColor(0xFF40A0E0, 0xFF40A0C0, 0x80143040);
    public static final FactionColor HOSTILE = new FactionColor(0xFFE02002, 0xFFE01800, 0x80400808);
    public static final FactionColor ALPHA = new FactionColor(0xFFA01000, 0xFFA00010, 0x80400006);
//TEST color F07070

    public FactionColor(int sidebarColor, int topColor, int bottomColor) {
        this.sidebarColor = sidebarColor;
        this.topColor = topColor;
        this.bottomColor = bottomColor;
    }

    public int getSidebarColor() {
        return sidebarColor;
    }

    public int getTopColor() {
        return topColor;
    }

    public int getBottomColor() {
        return bottomColor;
    }

    public static FactionColor getColorFromFaction(int faction) {
        return switch (faction) {
            case 1 -> ALLY;
            case 2 -> HOSTILE;
            case 3 -> ALPHA;
            default -> NEUTRAL;
        };
    }
}
