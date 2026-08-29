package ru.wexside.event;

import net.minecraft.class_1297;
import net.minecraft.class_243;

public final class TotemPopEvent implements Event {
   private final class_243 position;
   private final class_1297 entity;
   private final long time;

   public TotemPopEvent(class_1297 entity, class_243 position, long time) {
      this.entity = entity;
      this.position = position;
      this.time = time;
   }

   public class_1297 getEntity() {
      return this.entity;
   }

   public class_243 toVec3d() {
      return this.position;
   }

   public long getLongType() {
      return this.time;
   }
}
