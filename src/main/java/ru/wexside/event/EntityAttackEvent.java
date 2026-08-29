package ru.wexside.event;

import net.minecraft.class_1297;

public class EntityAttackEvent extends CancellableEvent implements Event {
   private final class_1297 entity;

   public EntityAttackEvent(class_1297 entity) {
      this.entity = entity;
   }

   public class_1297 getEntity() {
      return this.entity;
   }
}
