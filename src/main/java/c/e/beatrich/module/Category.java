package c.e.beatrich.module;

public enum Category {
    COMBAT("Combat", "战斗"),
    MOVEMENT("Movement", "移动"),
    RENDER("Render", "渲染"),
    WORLD("World", "世界"),
    PLAYER("Player", "玩家"),
    MISC("Misc", "杂项");

    public final String name;
    public final String description;

    Category(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
