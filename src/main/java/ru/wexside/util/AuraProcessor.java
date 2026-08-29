package ru.wexside.util;

import net.minecraft.class_1268;
import net.minecraft.class_1309;
import net.minecraft.class_1802;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2846;
import net.minecraft.class_2868;
import net.minecraft.class_310;
import net.minecraft.class_746;
import net.minecraft.class_2846.class_2847;
import ru.wexside.misc.AttackOptions;
import ru.wexside.misc.HitCooldown;
import ru.wexside.misc.Inventories;
import ru.wexside.misc.MaceCheck;
import ru.wexside.misc.ReachHelper;
import ru.wexside.misc.ShieldDesync;

public class AuraProcessor {
   private final HitCooldown hitCooldown = new HitCooldown();
   private final CriticalsHandler criticals;
   private boolean enabled;
   private final MaceCheck maceCheck;
   private final ShieldDesync shieldDesync = new ShieldDesync();

   public AuraProcessor(CriticalsHandler criticals) {
      this.maceCheck = new MaceCheck();
      this.criticals = criticals;
   }

   public boolean attack(class_1309 target, AttackOptions options) {
      return this.process(target, options);
   }

   public void tick() {
      this.update();
   }

   public boolean process(class_1309 entity2, AttackOptions iliiiillII2) {
      if (entity2 != null && !this.enabled) {
         class_746 player2 = class_310.method_1551().field_1724;
         if (player2 == null) {
            return false;
         } else if (!this.process2(entity2, iliiiillII2)) {
            return false;
         } else {
            this.shieldDesync.setMode(iliiiillII2.sprintResetMode());
            boolean bl = player2.method_5624() && !player2.method_6115();
            if (iliiiillII2.sprintResetEnabled()) {
               this.shieldDesync.prepareForAttack();
               if (this.shieldDesync.delaysAttackUntilSprintStops() && bl) {
                  this.enabled = true;
                  return false;
               }
            }

            if (iliiiillII2.desyncShield()) {
               this.releaseShieldUse(player2);
            }

            int n2 = -1;
            if (iliiiillII2.breakShield() && entity2.method_6039()) {
               int n = Inventories.findAxeSlot();
               int n3 = player2.method_31548().method_67532();
               if (n != -1 && n != n3) {
                  n2 = n3;
                  player2.field_3944.method_52787(new class_2868(n));
               }
            }

            int n = iliiiillII2.accuracyPercent() < 100.0 && Math.random() * 100.0 > iliiiillII2.accuracyPercent() ? 1 : 0;
            if (n != 0) {
               player2.method_6104(class_1268.field_5808);
            } else {
               class_310.method_1551().field_1761.method_2918(player2, entity2);
               player2.method_6104(class_1268.field_5808);
            }

            if (n2 != -1) {
               player2.field_3944.method_52787(new class_2868(n2));
            }

            if (iliiiillII2.sprintResetEnabled() && this.shieldDesync.sendsSprintPackets()) {
               this.shieldDesync.restoreSprint();
            }

            long l = iliiiillII2.legacyCombat() ? Math.max(50L, 1000L / (long)Math.max(1, iliiiillII2.clicksPerSecond())) : 500L;
            this.hitCooldown.setLongType(l);
            return n == 0;
         }
      } else {
         return false;
      }
   }

   public HitCooldown getHitCooldown() {
      return this.hitCooldown;
   }

   public void update() {
      this.enabled = false;
   }

   public MaceCheck getMaceCheck() {
      return this.maceCheck;
   }

   private void releaseShieldUse(class_746 player2) {
      if (player2.method_6079().method_31574(class_1802.field_8255)) {
         if (player2.method_6115()) {
            player2.field_3944.method_52787(new class_2846(class_2847.field_12974, class_2338.field_10980, class_2350.field_11033));
         }
      }
   }

   public ShieldDesync getShieldDesync() {
      return this.shieldDesync;
   }

   private boolean process2(class_1309 entity2, AttackOptions iliiiillII2) {
      if (iliiiillII2.raycastEnabled() && !this.process3(entity2, iliiiillII2.lookAngle(), iliiiillII2.range(), iliiiillII2.allowsThroughWalls())) {
         return false;
      } else {
         class_746 player2 = class_310.method_1551().field_1724;
         if (player2 != null && this.maceCheck.process(player2)) {
            return this.maceCheck.process2(player2, this.hitCooldown, iliiiillII2.maceFallDistance(), iliiiillII2.legacyCombat());
         } else {
            return !this.hitCooldown.process(iliiiillII2.legacyCombat())
               ? false
               : this.criticals.process(iliiiillII2.criticalsOnly(), iliiiillII2.raycastMode());
         }
      }
   }

   private boolean process3(class_1309 entity2, Angle angle, float f, boolean bl) {
      if (angle == null) {
         return false;
      } else {
         return ReachHelper.raycastEntity(entity2, angle, f, bl) == entity2;
      }
   }
}
