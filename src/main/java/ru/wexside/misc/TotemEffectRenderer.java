package ru.wexside.misc;

import ru.wexside.event.TotemPopEvent;
import ru.wexside.event.WorldRenderEvent;

public interface TotemEffectRenderer {
   void renderWorld(WorldRenderEvent var1);

   default void render(WorldRenderEvent event) {
      this.renderWorld(event);
   }

   void setTotemPopEvent(TotemPopEvent var1);

   void update2();
}
