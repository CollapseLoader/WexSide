package ru.wexside.util.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_310;
import net.minecraft.class_640;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;

public final class NpcDetector {
   private static final int REFRESH_INTERVAL_TICKS = 20;
   private final class_310 client = class_310.method_1551();
   private final Set<UUID> npcIds = new HashSet<>();
   private Object currentWorld;
   private int ticksUntilRefresh;

   public NpcDetector(EventBus eventBus) {
      eventBus.subscribe(ClientTickEvent.class, ignored -> this.tick());
   }

   public boolean isNpc(class_1309 entity) {
      return entity instanceof class_1657 && this.npcIds.contains(entity.method_5667());
   }

   private void tick() {
      if (this.client.field_1724 != null && this.client.field_1687 != null && this.client.method_1562() != null) {
         if (this.currentWorld != this.client.field_1687) {
            this.currentWorld = this.client.field_1687;
            this.npcIds.clear();
            this.ticksUntilRefresh = 0;
         }

         if (this.ticksUntilRefresh-- <= 0) {
            this.ticksUntilRefresh = 20;
            Set<UUID> listedPlayers = new HashSet<>();

            for(class_640 entry : this.client.method_1562().method_2880()) {
               listedPlayers.add(entry.method_2966().id());
            }

            this.npcIds.clear();

            for(class_1657 player : this.client.field_1687.method_18456()) {
               if (player != this.client.field_1724 && !listedPlayers.contains(player.method_5667())) {
                  this.npcIds.add(player.method_5667());
               }
            }
         }
      } else {
         this.reset();
      }
   }

   private void reset() {
      this.currentWorld = null;
      this.npcIds.clear();
      this.ticksUntilRefresh = 0;
   }
}
