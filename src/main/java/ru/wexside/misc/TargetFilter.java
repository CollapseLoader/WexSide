package ru.wexside.misc;

import java.util.Collection;
import java.util.List;
import net.minecraft.class_1293;
import net.minecraft.class_1294;
import net.minecraft.class_1304;
import net.minecraft.class_1308;
import net.minecraft.class_1309;
import net.minecraft.class_1429;
import net.minecraft.class_1657;
import net.minecraft.class_310;
import net.minecraft.class_3988;import net.minecraft.class_746;import ru.wexside.WexSideClient;
import ru.wexside.util.entity.NpcDetector;

public class TargetFilter {
   private final List<String> types;
   private final String sorting;
   private final int fov;

   public TargetFilter(Collection<String> types, String sorting, int fov) {
      this.types = List.copyOf(types);
      this.sorting = sorting;
      this.fov = fov;
   }

   public boolean matches(class_1309 entity) {
      if (entity == null) {
         return false;
      }
      if (this.types.isEmpty()) {
         return true;
      }
      class_746 localPlayer = class_310.method_1551().field_1724;
      if (entity == localPlayer) {
         return false;
      }
      boolean isPlayer = entity instanceof class_1657;
      boolean isAnimal = entity instanceof class_1429;
      boolean isMob = entity instanceof class_1308;
      boolean isVillager = entity instanceof class_3988;
      if (isPlayer) {
         boolean matched = false;
         if (this.types.contains("Players")) {
            matched = true;
         }
         if (!matched && this.types.contains("Friends")) {
            FriendList friends = WexSideClient.getFriends();
            if (friends != null && friends.contains(((class_1657)entity).method_5477().getString())) {
               matched = true;
            }
         }
         if (!matched && this.types.contains("Bots")) {
            NpcDetector npcDetector = WexSideClient.getNpcDetector();
            if (npcDetector != null && npcDetector.isNpc(entity)) {
               matched = true;
            }
         }
         if (!matched && this.types.contains("Naked")) {
            if (isNaked((class_1657)entity)) {
               matched = true;
            }
         }
         if (!matched && this.types.contains("Invisibles")) {
            if (hasInvisibilityEffect(entity)) {
               matched = true;
            }
         }
         return matched;
      }
      if (isVillager && this.types.contains("Villagers")) {
         return true;
      }
      if (isAnimal && this.types.contains("Animals")) {
         return true;
      }
      if (isMob && this.types.contains("Mobs")) {
         return true;
      }
      return false;
   }

   private static boolean isNaked(class_1657 player) {
      return player.method_6118(class_1304.field_6169).method_7960()
         && player.method_6118(class_1304.field_6174).method_7960()
         && player.method_6118(class_1304.field_6172).method_7960()
         && player.method_6118(class_1304.field_6166).method_7960();
   }

   private static boolean hasInvisibilityEffect(class_1309 entity) {
      for (class_1293 effect : entity.method_6026()) {
         if (effect.method_5579() == class_1294.field_5905) {
            return true;
         }
      }
      return false;
   }

   public List<String> getTypes() {
      return this.types;
   }

   public String getSorting() {
      return this.sorting;
   }

   public int getFov() {
      return this.fov;
   }
}
