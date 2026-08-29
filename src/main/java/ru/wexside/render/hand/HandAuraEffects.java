package ru.wexside.render.hand;

import net.minecraft.class_1268;
import net.minecraft.class_4587;
import net.minecraft.class_7833;

public final class HandAuraEffects {
   private static boolean energyEnabled;
   private static boolean materialEnabled;
   private static boolean frameTransformActive;
   private static EnergyAuraMode energyMode = EnergyAuraMode.STATIC;
   private static HandMaterialMode materialMode = HandMaterialMode.GLASS;
   private static float energyRadius = 1.0F;
   private static float materialRadius = 1.0F;
   private static int primaryEnergyColor;
   private static int secondaryEnergyColor;
   private static int primaryMaterialColor;
   private static int secondaryMaterialColor;
   private static boolean energyGradient;
   private static boolean materialGradient;
   private static boolean fillHands;

   private HandAuraEffects() {
   }

   public static EnergyAuraMode resolveEnergyMode(String setting) {
      return EnergyAuraMode.fromSetting(setting);
   }

   public static HandMaterialMode resolveMaterialMode(String setting) {
      return HandMaterialMode.fromSetting(setting);
   }

   public static void reset() {
      frameTransformActive = false;
   }

   public static void disableAll() {
      energyEnabled = false;
      materialEnabled = false;
   }

   public static void applyHandEnergy(class_1268 hand, class_4587 matrices) {
      if (energyEnabled && matrices != null) {
         float side = hand == class_1268.field_5808 ? 1.0F : -1.0F;
         float offset = (energyRadius - 1.0F) * 0.025F;
         matrices.method_46416(side * offset, -offset * 0.5F, 0.0F);
         if (energyMode == EnergyAuraMode.RIBBONS) {
            matrices.method_22907(class_7833.field_40718.rotationDegrees(side * 1.5F));
         }
      }
   }

   public static void configureEnergy(
      boolean enabled, EnergyAuraMode mode, int primaryColor, int secondaryColor, boolean gradient, float radius, boolean shouldFillHands
   ) {
      energyEnabled = enabled;
      energyMode = mode == null ? EnergyAuraMode.STATIC : mode;
      primaryEnergyColor = primaryColor;
      secondaryEnergyColor = secondaryColor;
      energyGradient = gradient;
      energyRadius = Math.max(0.5F, Math.min(2.0F, radius));
      fillHands = shouldFillHands;
   }

   public static void configureMaterial(boolean enabled, HandMaterialMode mode, float radius, int primaryColor, int secondaryColor, boolean gradient) {
      materialEnabled = enabled;
      materialMode = mode == null ? HandMaterialMode.GLASS : mode;
      materialRadius = Math.max(0.6F, Math.min(1.8F, radius));
      primaryMaterialColor = primaryColor;
      secondaryMaterialColor = secondaryColor;
      materialGradient = gradient;
   }

   public static void beforeHandRender(class_4587 matrices, float tickDelta) {
      if ((energyEnabled || materialEnabled) && matrices != null && !frameTransformActive) {
         matrices.method_22903();
         frameTransformActive = true;
         float radius = Math.max(energyEnabled ? energyRadius : 1.0F, materialEnabled ? materialRadius : 1.0F);
         float scale = 1.0F + (radius - 1.0F) * 0.035F;
         if (energyMode == EnergyAuraMode.HAND_AURA) {
            scale += (float)Math.sin(((double)System.nanoTime() / 1.0E9 + (double)tickDelta) * 4.0) * 0.006F;
         }

         if (materialMode == HandMaterialMode.CHROME) {
            scale += 0.004F;
         }

         matrices.method_22905(scale, scale, scale);
      }
   }

   public static void afterHandRender(class_4587 matrices) {
      if (frameTransformActive && matrices != null) {
         matrices.method_22909();
         frameTransformActive = false;
      }
   }
}
