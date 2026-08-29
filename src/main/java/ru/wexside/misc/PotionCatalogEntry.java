package ru.wexside.misc;

import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1842;
import net.minecraft.class_1844;
import net.minecraft.class_6880;
import net.minecraft.class_9334;
import ru.wexside.server.ServerHelperAction;
import ru.wexside.server.ServerItemMatcher;

public final class PotionCatalogEntry {
   private final ServerHelperAction action;
   private final String id;
   private final String displayName;
   private final class_6880<class_1842> potion;
   public static final String SERVER_ACTION_PREFIX = "server:";
   private final int color;

   public boolean matches(class_1799 stack, ServerKind serverKind) {
      if (stack == null || stack.method_7960()) {
         return false;
      } else if (this.action != null) {
         return ServerItemMatcher.matches(stack, this.action, serverKind);
      } else {
         class_1844 contents = (class_1844)stack.method_58694(class_9334.field_49651);
         return contents != null && contents.method_57401(this.potion);
      }
   }

   private PotionCatalogEntry(String id, String displayName, int color, ServerHelperAction action, class_6880<class_1842> potion) {
      this.id = id;
      this.displayName = displayName;
      this.color = color;
      this.action = action;
      this.potion = potion;
   }

   public class_6880<class_1842> getPotion() {
      return this.potion;
   }

   public String getId() {
      return this.id;
   }

   public static PotionCatalogEntry forServerAction(ServerHelperAction action) {
      return new PotionCatalogEntry("server:" + action.id(), action.selectorLabel(), action.color(), action, null);
   }

   public static PotionCatalogEntry forPotion(String id, String displayName, int color, class_6880<class_1842> potion) {
      return new PotionCatalogEntry(id, displayName, color, null, potion);
   }

   public String getDisplayName() {
      return this.displayName;
   }

   public boolean isServerAction() {
      return this.action != null;
   }

   public class_1799 createDefaultStack() {
      return this.action != null ? new class_1799(this.action.icon()) : class_1844.method_57400(class_1802.field_8436, this.potion);
   }

   public int getColor() {
      return this.color;
   }

   public ServerHelperAction getAction() {
      return this.action;
   }
}
