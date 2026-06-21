package c.e.beatrich.setting.types;

import c.e.beatrich.setting.Setting;
import org.lwjgl.glfw.GLFW;

/**
 * 快捷键设置 — 存储 GLFW 键码。
 * <p>
 * 值为 {@code GLFW.GLFW_KEY_UNKNOWN} 时表示未设置快捷键。
 * </p>
 */
public class KeybindSetting extends Setting<Integer> {

    public KeybindSetting(String name, String description, int defaultKey) {
        super(name, description, defaultKey);
    }

    /** 设置键码并触发变更回调 */
    public void setKey(int key) {
        this.value = key;
        fireChange();
    }

    /** 获取键码 */
    public int getKey() {
        return value;
    }

    /** Key name used in ConfigManager serialize */
    public void setSilent(int key) {
        this.value = key;
    }

    /**
     * 将 GLFW 键码转为人类可读名称。
     */
    public static String getKeyName(int key) {
        if (key == GLFW.GLFW_KEY_UNKNOWN) return "NONE";

        // 鼠标按键
        switch (key) {
            case GLFW.GLFW_KEY_UNKNOWN: return "NONE";
            // 鼠标按键 (GLFW: 0=左, 1=右, 2=中, 3-7=侧键)
            case 0: return "MB1";
            case 1: return "MB2";
            case 2: return "MB3";
            case 3: return "MB4";
            case 4: return "MB5";
            case 5: return "MB6";
            case 6: return "MB7";
            case 7: return "MB8";
        }

        String name = GLFW.glfwGetKeyName(key, 0);
        if (name != null) return name.toUpperCase();

        // 特殊键
        return switch (key) {
            case GLFW.GLFW_KEY_SPACE -> "SPACE";
            case GLFW.GLFW_KEY_LEFT_SHIFT -> "LSHIFT";
            case GLFW.GLFW_KEY_RIGHT_SHIFT -> "RSHIFT";
            case GLFW.GLFW_KEY_LEFT_CONTROL -> "LCTRL";
            case GLFW.GLFW_KEY_RIGHT_CONTROL -> "RCTRL";
            case GLFW.GLFW_KEY_LEFT_ALT -> "LALT";
            case GLFW.GLFW_KEY_RIGHT_ALT -> "RALT";
            case GLFW.GLFW_KEY_TAB -> "TAB";
            case GLFW.GLFW_KEY_ENTER -> "ENTER";
            case GLFW.GLFW_KEY_ESCAPE -> "ESC";
            case GLFW.GLFW_KEY_BACKSPACE -> "BACK";
            case GLFW.GLFW_KEY_DELETE -> "DEL";
            case GLFW.GLFW_KEY_INSERT -> "INS";
            case GLFW.GLFW_KEY_HOME -> "HOME";
            case GLFW.GLFW_KEY_END -> "END";
            case GLFW.GLFW_KEY_PAGE_UP -> "PGUP";
            case GLFW.GLFW_KEY_PAGE_DOWN -> "PGDN";
            case GLFW.GLFW_KEY_UP -> "UP";
            case GLFW.GLFW_KEY_DOWN -> "DOWN";
            case GLFW.GLFW_KEY_LEFT -> "LEFT";
            case GLFW.GLFW_KEY_RIGHT -> "RIGHT";
            case GLFW.GLFW_KEY_CAPS_LOCK -> "CAPS";
            case GLFW.GLFW_KEY_NUM_LOCK -> "NUMLK";
            case GLFW.GLFW_KEY_F1 -> "F1";
            case GLFW.GLFW_KEY_F2 -> "F2";
            case GLFW.GLFW_KEY_F3 -> "F3";
            case GLFW.GLFW_KEY_F4 -> "F4";
            case GLFW.GLFW_KEY_F5 -> "F5";
            case GLFW.GLFW_KEY_F6 -> "F6";
            case GLFW.GLFW_KEY_F7 -> "F7";
            case GLFW.GLFW_KEY_F8 -> "F8";
            case GLFW.GLFW_KEY_F9 -> "F9";
            case GLFW.GLFW_KEY_F10 -> "F10";
            case GLFW.GLFW_KEY_F11 -> "F11";
            case GLFW.GLFW_KEY_F12 -> "F12";
            case GLFW.GLFW_KEY_KP_0 -> "NP0";
            case GLFW.GLFW_KEY_KP_1 -> "NP1";
            case GLFW.GLFW_KEY_KP_2 -> "NP2";
            case GLFW.GLFW_KEY_KP_3 -> "NP3";
            case GLFW.GLFW_KEY_KP_4 -> "NP4";
            case GLFW.GLFW_KEY_KP_5 -> "NP5";
            case GLFW.GLFW_KEY_KP_6 -> "NP6";
            case GLFW.GLFW_KEY_KP_7 -> "NP7";
            case GLFW.GLFW_KEY_KP_8 -> "NP8";
            case GLFW.GLFW_KEY_KP_9 -> "NP9";
            default -> "?" + key;
        };
    }
}
