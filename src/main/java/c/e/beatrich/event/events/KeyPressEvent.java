package c.e.beatrich.event.events;

/**
 * 按键按下事件
 */
public record KeyPressEvent(int keyCode, int scanCode, int action, int modifiers) {
}
