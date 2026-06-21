package c.e.beatrich.event.events;

import c.e.beatrich.module.Module;

/**
 * 模块切换事件 — 当模块被开启或关闭时触发
 */
public record ModuleToggleEvent(Module module, boolean active) {
}
