package ru.wexside.notification;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.minecraft.class_1291;
import net.minecraft.class_1293;
import net.minecraft.class_310;
import net.minecraft.class_6880;
import ru.wexside.WexSideClient;
import ru.wexside.module.Module;
import ru.wexside.notification.EffectExpiredNotification;
import ru.wexside.notification.ModuleToggleNotification;
import ru.wexside.notification.NotificationCenter;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.Setting;

public final class NotificationTracker {
    private final Map<Module, Boolean> moduleStates = new HashMap<Module, Boolean>();
    private Set<class_6880<class_1291>> activeEffects;

    public void tick() {
        class_310 client = class_310.method_1551();
        NotificationCenter center = WexSideClient.getNotificationCenter();
        if (client.field_1724 == null || center == null) {
            this.activeEffects = null;
            return;
        }
        this.trackModules(center);
        this.trackEffects(client.field_1724.method_6026(), center);
    }

    private void trackModules(NotificationCenter center) {
        Iterator<Module> iterator = WexSideClient.getInstance().getModuleManager().getModules().iterator();
        while (iterator.hasNext()) {
            BooleanSetting toggle;
            Module module;
            Setting setting = (module = iterator.next()).getToggleSetting();
            boolean enabled = setting instanceof BooleanSetting && (toggle = (BooleanSetting)setting).isEnabled();
            Boolean previous = this.moduleStates.put(module, enabled);
            if (previous == null || previous == enabled) continue;
            center.push(new ModuleToggleNotification(module.getDisplayName(), enabled));
        }
    }

    private void trackEffects(Iterable<class_1293> effects, NotificationCenter center) {
        HashSet<class_6880<class_1291>> current = new HashSet<class_6880<class_1291>>();
        for (class_1293 class_12932 : effects) {
            current.add((class_6880<class_1291>)class_12932.method_5579());
        }
        if (this.activeEffects != null) {
            for (class_6880 class_68802 : this.activeEffects) {
                if (current.contains(class_68802)) continue;
                center.push(new EffectExpiredNotification((class_6880<class_1291>)class_68802));
            }
        }
        this.activeEffects = current;
    }
}

