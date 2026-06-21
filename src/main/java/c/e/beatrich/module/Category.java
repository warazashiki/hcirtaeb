package c.e.beatrich.module;

/**
 * 模块分类，模仿 Meteor Client 的分类体系
 */
public enum Category {
    COMBAT("Combat", "战斗类模块"),
    MOVEMENT("Movement", "移动类模块"),
    RENDER("Render", "渲染类模块"),
    WORLD("World", "世界类模块"),
    PLAYER("Player", "玩家类模块"),
    MISC("Misc", "杂项类模块");

    public final String name;
    public final String description;

    Category(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
