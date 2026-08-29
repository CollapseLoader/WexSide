package ru.wexside.misc;

import net.minecraft.class_310;
import net.minecraft.class_746;
import net.minecraft.class_9779;
import ru.wexside.WexSideClient;

public class HitCooldown {
   private long longType = System.currentTimeMillis();
   private long anyOf = 0L;
   static final float value = 0.3F;
   static final float value2 = 0.9F;
   private boolean tpsSync;
   private boolean enabled;
   private boolean enabled2 = true;

   private float getFloatType() {
      class_746 player2 = class_310.method_1551().field_1724;
      return player2 == null ? 0.0F : player2.method_7261(this.getFloatType2());
   }

   public boolean process(boolean bl) {
      return this.process2(bl, 0.9F);
   }

   public void setLongType(long l) {
      this.longType = System.currentTimeMillis();
      this.anyOf = l;
   }

   private float getFloatType2() {
      float f;
      if (this.enabled) {
         f = 0.5F;
      } else {
         class_9779 renderTickCounter = class_310.method_1551().method_61966();
         if (renderTickCounter == null) {
            return 0.0F;
         }

         f = renderTickCounter.method_60636();
      }

      if (this.tpsSync && WexSideClient.getServerTickRate() != null) {
         f = WexSideClient.getServerTickRate().process(f);
      }

      return f;
   }

   public void setAttackRequested(boolean requested) {
      this.setBooleanType(requested);
   }

   public void setVanillaHitsAllowed(boolean allowed) {
      this.setBooleanType2(allowed);
   }

   public void setTpsSync(boolean tpsSync) {
      this.setBooleanType3(tpsSync);
   }

   public void setBooleanType(boolean bl) {
      this.enabled = bl;
   }

   public void setBooleanType2(boolean bl) {
      this.enabled2 = bl;
   }

   public void setBooleanType3(boolean bl) {
      this.tpsSync = bl;
   }

   public long getLongType() {
      return System.currentTimeMillis() - this.longType;
   }

   public boolean isActive() {
      return this.getLongType() >= this.anyOf;
   }

   private boolean isAvailable() {
      class_9779 renderTickCounter = class_310.method_1551().method_61966();
      if (renderTickCounter == null) {
         return false;
      } else {
         return renderTickCounter.method_60636() > 0.3F;
      }
   }

   public boolean process2(boolean bl, float f) {
      if (!this.enabled && !this.isAvailable()) {
         return false;
      } else if (!this.enabled && !this.enabled2) {
         return false;
      } else {
         float f2 = this.getFloatType();
         long l = System.currentTimeMillis() - this.longType;
         return l >= this.anyOf && (bl || f2 > f);
      }
   }
}
