package ru.wexside.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import net.minecraft.class_1799;
import net.minecraft.class_310;
import net.minecraft.class_746;
import ru.wexside.WexSideClient;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.misc.AttackUrgency;
import ru.wexside.misc.Bundles;
import ru.wexside.misc.CorrectionMode;
import ru.wexside.misc.ItemAlerts;
import ru.wexside.misc.ServerHelper;
import ru.wexside.misc.ServerKind;
import ru.wexside.misc.SwapTiming;
import ru.wexside.server.ServerHelperAction;
import ru.wexside.server.ServerHelperActions;
import ru.wexside.server.ServerItemMatcher;

public class FTSnap {
   private final BooleanSupplier field40;
   private Angle field44;
   private final Supplier<ServerKind> field48;
   private int slot;
   private final BooleanSupplier field52;
   private int slot2;
   private final float value;
   private final String field56;
   private final BooleanSupplier field60;
   private final int slot3;
   private final int slot4;
   private final BooleanSupplier field64;
   private final float value2;
   private final List<ServerHelperAction> pendingActions;
   private final ServerHelper field72 = new ServerHelper();
   private final int slot5;
   private final BooleanSupplier field76;
   private final Supplier<SwapTiming> field80;

   public FTSnap(
      EventBus eventBus,
      Supplier<ServerKind> supplier,
      Supplier<SwapTiming> supplier2,
      BooleanSupplier booleanSupplier,
      BooleanSupplier booleanSupplier2,
      BooleanSupplier booleanSupplier3,
      BooleanSupplier booleanSupplier4,
      BooleanSupplier booleanSupplier5
   ) {
      this.value = 90.0F;
      this.value2 = 0.5F;
      this.slot3 = 10;
      this.slot5 = 3;
      this.slot4 = 2;
      this.field56 = "FT Snap";
      this.pendingActions = new ArrayList<>();
      this.field48 = supplier;
      this.field80 = supplier2;
      this.field64 = booleanSupplier;
      this.field76 = booleanSupplier2;
      this.field52 = booleanSupplier3;
      this.field60 = booleanSupplier4;
      this.field40 = booleanSupplier5;
      eventBus.subscribe(ClientTickEvent.class, gameEvent6 -> this.update4());
   }

   public boolean isActive() {
      return !this.pendingActions.isEmpty();
   }

   private boolean isActive2() {
      RotationController rotations = WexSideClient.getRotationController();
      return rotations != null && rotations.process3(90.0F, 0.5F);
   }

   private class_1799 findActionStack(class_746 player, ServerHelperAction action, ServerKind serverKind) {
      if (player == null) {
         return class_1799.field_8037;
      } else {
         return ServerItemMatcher.matches(player.method_6079(), action, serverKind)
            ? player.method_6079()
            : ServerItemMatcher.findStack(player.method_31548(), action, serverKind);
      }
   }

   private boolean shouldAimDown(ServerHelperAction action) {
      if (action == ServerHelperActions.WIND_CHARGE) {
         return this.field76.getAsBoolean();
      } else {
         return action.splashPotion() && !action.debuff() ? this.field64.getAsBoolean() : false;
      }
   }

   private void process3(RotationController rotations, Angle angle) {
      class_746 player2 = class_310.method_1551().field_1724;
      if (player2 != null) {
         RotationIntent intent = new RotationIntent(player2, null, angle, AttackUrgency.HIT, CorrectionMode.NONE, false);
         rotations.process2(intent, "FT Snap");
      }
   }

   public void queueAction(ServerHelperAction action) {
      if (action != null) {
         if (!this.pendingActions.contains(action)) {
            this.pendingActions.add(action);
         }
      }
   }

   public void update() {
      this.pendingActions.clear();
      this.slot = 0;
   }

   private Runnable process4(ServerHelperAction action) {
      if (action != ServerHelperActions.WIND_CHARGE) {
         return null;
      } else {
         return this.field76.getAsBoolean() && this.field52.getAsBoolean() ? () -> {
            InventoryController inventory = WexSideClient.getInventoryController();
            if (inventory != null) {
               int n = inventory.isActive() ? 1 : 2;
               inventory.setIntType(Math.max(0, n));
            }
         } : null;
      }
   }

   private Runnable process5(ServerHelperAction action) {
      if (action == ServerHelperActions.WIND_CHARGE) {
         return this.field76.getAsBoolean() ? this::update5 : null;
      } else if (action.splashPotion() && !action.debuff()) {
         return this.field64.getAsBoolean() ? this::update5 : null;
      } else {
         return null;
      }
   }

   private void update2() {
      if (!this.pendingActions.isEmpty()) {
         ServerKind serverKind2 = this.field48.get();
         SwapTiming timing = this.field80.get();
         boolean bl = this.field60.getAsBoolean();
         boolean bl2 = this.field40.getAsBoolean();
         class_746 player2 = class_310.method_1551().field_1724;
         Iterator<ServerHelperAction> iterator = this.pendingActions.iterator();

         while(iterator.hasNext()) {
            ServerHelperAction action = iterator.next();
            String string = action.selectorLabel();
            class_1799 stack2 = this.findActionStack(player2, action, serverKind2);
            boolean bl3 = bl
               && player2 != null
               && Bundles.contains(player2.method_31548(), stackInBundle -> ServerItemMatcher.matches(stackInBundle, action, serverKind2));
            if (stack2.method_7960() && !bl3) {
               ItemAlerts.warnMissing(new class_1799(action.icon()), string);
               iterator.remove();
               this.slot = 0;
            } else {
               class_1799 stack = stack2.method_7960() ? new class_1799(action.icon()) : stack2;
               if (ItemAlerts.isBusy(player2.field_7512, stack, string)) {
                  iterator.remove();
                  this.slot = 0;
               } else {
                  if (this.shouldAimDown(action) && !this.isActive2()) {
                     this.update5();
                     if (this.slot++ < 10) {
                        continue;
                     }
                  }

                  this.field72.process(action, serverKind2, timing, this.process5(action), this.process4(action), bl, bl2);
                  iterator.remove();
                  this.slot = 0;
               }
            }
         }
      }
   }

   private void update3() {
      if (this.slot2 > 0) {
         RotationController rotations = WexSideClient.getRotationController();
         if (rotations == null) {
            this.slot2 = 0;
            this.field44 = null;
         } else {
            --this.slot2;
            if (this.slot2 > 0 && this.field44 != null) {
               this.process3(rotations, this.field44);
            } else {
               rotations.update3();
               this.field44 = null;
            }
         }
      }
   }

   private void update4() {
      this.update3();
      this.update2();
   }

   private void update5() {
      class_746 player2 = class_310.method_1551().field_1724;
      RotationController rotations = WexSideClient.getRotationController();
      if (player2 != null && rotations != null) {
         Angle angle;
         this.field44 = angle = new Angle(player2.method_36454(), 90.0F);
         this.slot2 = 4;
         this.process3(rotations, angle);
      }
   }
}
