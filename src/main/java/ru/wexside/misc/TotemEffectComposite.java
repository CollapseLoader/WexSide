package ru.wexside.misc;

import java.util.List;
import ru.wexside.event.TotemPopEvent;
import ru.wexside.event.WorldRenderEvent;

public final class TotemEffectComposite implements TotemEffectRenderer {
   private final List<TotemEffectRenderer> renderers;

   public TotemEffectComposite(TotemEffectRenderer... cls0512Array) {
      this.renderers = List.of(cls0512Array);
   }

   @Override
   public void renderWorld(WorldRenderEvent floatTypeEvent2) {
      this.renderers.forEach(callback41 -> callback41.renderWorld(floatTypeEvent2));
   }

   @Override
   public void setTotemPopEvent(TotemPopEvent lIiillIliIEvent) {
      this.renderers.forEach(callback41 -> callback41.setTotemPopEvent(lIiillIliIEvent));
   }

   @Override
   public void update2() {
      this.renderers.forEach(TotemEffectRenderer::update2);
   }
}
