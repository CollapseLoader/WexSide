package ru.wexside.render;

import net.minecraft.class_1799;

public final class ItemIconKey {
   private final class_1799 stack;

   public ItemIconKey(class_1799 stack) {
      this.stack = stack != null && !stack.method_7960() ? stack.method_7972() : class_1799.field_8037;
   }

   @Override
   public boolean equals(Object object) {
      if (object instanceof ItemIconKey other && class_1799.method_7973(this.stack, other.stack)) {
         return true;
      }

      return false;
   }

   @Override
   public int hashCode() {
      return 31 * System.identityHashCode(this.stack.method_7909()) + this.stack.method_57353().hashCode();
   }
}
