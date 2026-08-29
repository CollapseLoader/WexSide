package ru.wexside.util;

import net.minecraft.class_2596;
import net.minecraft.class_2708;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_746;
import net.minecraft.class_2828.class_2831;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.event.IncomingPacketEvent;
import ru.wexside.event.OutgoingPacketEvent;
import ru.wexside.misc.RotationApplyResult;
import ru.wexside.misc.RotationStrategy;
import ru.wexside.misc.RotationStrategyFactory;

public class RotationController {
   static final int slot = 4;
   private float value = 90.0F;
   private boolean enabled;
   private Angle field40;
   private RotationStrategy field44;
   private int slot2;
   private String field48;
   private float value2 = 90.0F;
   private Angle field52;
   private Angle field56;
   private float value3 = 90.0F;
   private boolean enabled2 = true;
   private Angle field60;
   private int slot3;
   private boolean process = true;
   private int slot4;
   private RotationIntent field64 = RotationIntent.empty();
   private final RotationStrategyFactory field68;
   private boolean enabled3;

   private void setAspectRatioEvent12(OutgoingPacketEvent gameEvent12) {
      class_2596<?> packet2 = gameEvent12.getPacket();
      class_2831 lookAndOnGround;
      if (packet2 instanceof class_2831 && (lookAndOnGround = (class_2831)packet2).method_36172()) {
         this.field60 = new Angle(lookAndOnGround.method_12271(0.0F), lookAndOnGround.method_12270(0.0F));
      }
   }

   public float getFloatType() {
      this.update6();
      return this.value;
   }

   public Float getFloatType2() {
      if (this.slot2 > 0) {
         return null;
      } else {
         this.update6();
         return this.enabled2 ? this.value2 : null;
      }
   }

   public boolean isActive() {
      return this.enabled && this.field56 != null;
   }

   public void update() {
      this.slot3 = 0;
   }

   public boolean isAvailable() {
      return this.field56 != null;
   }

   public void update2() {
      this.slot4 = 0;
   }

   public Angle getAngle() {
      return this.field56;
   }

   public int slot5() {
      return this.slot4;
   }

   public void update3() {
      if (this.field44 != null) {
         RotationState iIiIlIIliI2 = this.process5(this.field64);
         this.field44.onDeactivated(iIiIlIIliI2);
      }

      this.field56 = null;
      this.field40 = null;
      this.field52 = null;
      this.field64 = RotationIntent.empty();
   }

   public RotationApplyResult apply(RotationIntent intent, String rotationMode) {
      return this.process2(intent, rotationMode);
   }

   public void reset() {
      this.update3();
   }

   public void markAttacked() {
      this.update();
   }

   public void notifyHit() {
      this.update2();
   }

   public boolean hasRotation() {
      return this.isAvailable();
   }

   public int ticksSinceHit() {
      return this.slot5();
   }

   public RotationApplyResult process2(RotationIntent intent2, String string) {
      this.setString(string);
      this.field64 = intent2;
      RotationState iIiIlIIliI2 = this.process5(intent2);
      RotationApplyResult rotationApplyResult = this.field44.process(iIiIlIIliI2, intent2);
      this.field40 = this.field56;
      this.field56 = this.process4(rotationApplyResult.angle(), iIiIlIIliI2.getPlayerAngle());
      return rotationApplyResult;
   }

   public boolean process3(float f, float f2) {
      return this.process && Math.abs(this.value3 - f) <= f2;
   }

   public RotationIntent empty() {
      return this.field64;
   }

   public void update4() {
      this.enabled = false;
   }

   public void update5() {
      this.enabled = true;
   }

   private void member2527(ClientTickEvent gameEvent6) {
      this.update7();
   }

   private void update6() {
      if (!this.enabled3) {
         class_746 player2 = class_310.method_1551().field_1724;
         if (player2 != null) {
            this.enabled3 = true;
            float f = player2.method_36454();
            float f2 = player2.method_36455();
            if (!this.enabled2) {
               this.value2 = f;
               this.value = f2;
               this.enabled2 = true;
            }

            float f3 = this.field56 != null ? this.field56.getFloatType() : f;
            float f4 = this.field56 != null ? this.field56.getFloatType2() : f2;
            this.value2 += class_3532.method_15393(f3 - this.value2);
            this.value = f4;
         }
      }
   }

   private void setString(String string) {
      if (this.field44 == null || !string.equals(this.field48)) {
         if (this.field44 != null) {
            this.field44.onDeactivated(this.process5(this.field64));
         }

         this.field44 = this.field68.process(string);
         this.field48 = string;
         this.field44.onActivated(this.process5(this.field64));
      }
   }

   private void setAspectRatioEvent5(IncomingPacketEvent gameEvent5) {
      class_2596<?> packet2 = gameEvent5.getPacket();
      if (packet2 instanceof class_2708 positionPacket) {
         this.field60 = new Angle(positionPacket.comp_3228().comp_3150(), positionPacket.comp_3228().comp_3151());
         this.enabled2 = false;
         this.slot2 = 4;
      }
   }

   private Angle process4(Angle angle, Angle angle2) {
      if (angle == null) {
         return null;
      } else {
         Angle angle3 = this.field56 != null ? this.field56 : angle2;
         if (angle3 == null) {
            return angle;
         } else {
            Angle angle5 = angle3.process4(angle);
            float f = angle3.getFloatType() + class_3532.method_15393(angle5.getFloatType());
            float f2 = class_3532.method_15363(angle3.getFloatType2() + class_3532.method_15393(angle5.getFloatType2()), -90.0F, 90.0F);
            return new Angle(f, f2);
         }
      }
   }

   private RotationState process5(RotationIntent intent2) {
      class_746 player2 = class_310.method_1551().field_1724;
      Angle angle = player2 != null ? new Angle(player2.method_36454(), player2.method_36455()) : Angle.angle;
      float f = player2 != null ? player2.method_7261(0.0F) : 0.0F;
      boolean bl = f >= 0.9F;
      return new RotationState(this.field56, this.field40, this.field60, angle, intent2.target(), intent2.aimPoint(), this.slot3, this.slot4, f, bl);
   }

   private void update7() {
      if (this.slot2 > 0) {
         --this.slot2;
      }

      this.enabled3 = false;
      ++this.slot3;
      ++this.slot4;
      class_746 player2;
      this.field52 = this.field56 != null
         ? this.field56
         : ((player2 = class_310.method_1551().field_1724) != null ? new Angle(player2.method_36454(), player2.method_36455()) : null);
   }

   public void onTick(float f) {
      this.value3 = f;
      this.process = true;
   }

   public RotationController(RotationStrategyFactory callback35, EventBus eventBus) {
      this.field68 = callback35;
      eventBus.subscribe(ClientTickEvent.class, ignored -> this.update7());
      eventBus.subscribe(IncomingPacketEvent.class, this::setAspectRatioEvent5);
      eventBus.subscribe(OutgoingPacketEvent.class, this::setAspectRatioEvent12);
   }
}
