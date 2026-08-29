package ru.wexside.module.render;

import net.minecraft.class_1297;
import net.minecraft.class_243;
import net.minecraft.class_310;

public abstract class TargetEspRenderer {
   protected final class_310 client() {
      return class_310.method_1551();
   }

   protected final boolean canRender(class_310 client) {
      return client != null
         && client.field_1724 != null
         && client.field_1687 != null
         && TargetESPModule.getInstance() != null
         && TargetESPModule.getInstance().getCurrentTarget() != null;
   }

   protected final <T extends class_1297> T target(class_310 client, Class<T> type) {
      TargetESPModule module = TargetESPModule.getInstance();
      class_1297 target = module == null ? null : module.getCurrentTarget();
      return (T)(type.isInstance(target) ? type.cast(target) : null);
   }

   protected final class_243 interpolatedPosition(class_1297 entity, float tickDelta) {
      return entity.method_30950(tickDelta);
   }

   protected final class_243 cameraPosition() {
      class_310 client = this.client();
      return client.field_1773.method_19418().method_71156();
   }

   protected final int primaryColor() {
      TargetESPModule module = TargetESPModule.getInstance();
      return module == null ? -1 : module.getPrimaryColor();
   }

   protected final int secondaryColor() {
      TargetESPModule module = TargetESPModule.getInstance();
      return module == null ? -1 : module.getSecondaryColor();
   }
}
