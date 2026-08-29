package ru.wexside.misc;

import java.util.Set;
import java.util.UUID;
import net.minecraft.class_1657;
import net.minecraft.class_1680;
import net.minecraft.class_1792;
import ru.wexside.event.WorldRenderEvent;
import ru.wexside.setting.ColorSetting;

public interface ConsumableEspEffect {
   default void process(WorldRenderEvent floatTypeEvent2, class_1657 player, ColorSetting colorSetting) {
      this.process5(floatTypeEvent2, player, colorSetting);
   }

   default void process2(WorldRenderEvent floatTypeEvent2, class_1680 snowballEntity, ColorSetting colorSetting) {
      this.process4(floatTypeEvent2, snowballEntity);
   }

   void update();

   default void setSet(Set<UUID> set) {
   }

   default void setUUID(UUID uUID) {
   }

   boolean process3(class_1792 var1);

   default void process4(WorldRenderEvent floatTypeEvent2, class_1680 snowballEntity) {
   }

   void process5(WorldRenderEvent var1, class_1657 var2, ColorSetting var3);
}
