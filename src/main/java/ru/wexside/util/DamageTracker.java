package ru.wexside.util;

import net.minecraft.class_1294;
import net.minecraft.class_2596;
import net.minecraft.class_2663;
import net.minecraft.class_2664;
import net.minecraft.class_310;
import net.minecraft.class_746;
import ru.wexside.event.DamageEvent;
import ru.wexside.event.IncomingPacketEvent;
import ru.wexside.misc.ElapsedTimer;

public final class DamageTracker {
   private static final byte HURT_STATUS = 2;
   private final class_310 client = class_310.method_1551();
   private final ElapsedTimer confirmationTimer = new ElapsedTimer();
   private boolean damageConfirmed;
   private boolean explosionPending;
   private boolean directDamagePending;
   private boolean projectileDamagePending;
   private boolean environmentalDamagePending;

   public boolean consumeAfter(long delayMillis) {
      if (this.damageConfirmed && this.confirmationTimer.process(delayMillis)) {
         this.damageConfirmed = false;
         this.confirmationTimer.update();
         return true;
      } else {
         if (!this.damageConfirmed) {
            this.confirmationTimer.update();
         }

         return false;
      }
   }

   public boolean isDamageConfirmed() {
      return this.damageConfirmed;
   }

   public void onIncomingPacket(IncomingPacketEvent event) {
      class_746 player = this.client.field_1724;
      if (player != null && this.client.field_1687 != null && !this.hasPeriodicHealthEffect(player)) {
         boolean damageWasPending = this.hasPendingDamage();
         class_2596<?> packet = event.getPacket();
         if (packet instanceof class_2664) {
            this.explosionPending = true;
         }

         if (!damageWasPending
            && packet instanceof class_2663 statusPacket
            && statusPacket.method_11470() == 2
            && statusPacket.method_11469(this.client.field_1687) == player) {
            this.damageConfirmed = true;
            return;
         }

         if (damageWasPending && player.field_6235 > 0) {
            this.damageConfirmed = false;
            this.clearPendingDamage();
         }
      }
   }

   public void onDamage(DamageEvent event) {
      switch(event.type()) {
         case DIRECT:
            this.directDamagePending = true;
            break;
         case PROJECTILE:
            this.projectileDamagePending = true;
            break;
         case ENVIRONMENTAL:
            this.environmentalDamagePending = true;
      }

      this.damageConfirmed = false;
   }

   public void reset() {
      this.damageConfirmed = false;
      this.clearPendingDamage();
      this.confirmationTimer.update();
   }

   private boolean hasPeriodicHealthEffect(class_746 player) {
      return player.method_6059(class_1294.field_5899) || player.method_6059(class_1294.field_5920) || player.method_6059(class_1294.field_5924);
   }

   private boolean hasPendingDamage() {
      return this.explosionPending || this.directDamagePending || this.projectileDamagePending || this.environmentalDamagePending;
   }

   private void clearPendingDamage() {
      this.explosionPending = false;
      this.directDamagePending = false;
      this.projectileDamagePending = false;
      this.environmentalDamagePending = false;
   }
}
