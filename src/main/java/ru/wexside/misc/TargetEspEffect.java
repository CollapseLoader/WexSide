package ru.wexside.misc;

import ru.wexside.event.EntityAttackEvent;
import ru.wexside.event.WorldRenderEvent;

public interface TargetEspEffect {
   void setEntityAttackEvent(EntityAttackEvent var1);

   void setWorldRenderEvent(WorldRenderEvent var1);

   void update();

   default boolean isActive() {
      return true;
   }
}
