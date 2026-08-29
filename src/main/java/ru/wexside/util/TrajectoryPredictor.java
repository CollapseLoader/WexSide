package ru.wexside.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1542;
import net.minecraft.class_1676;
import net.minecraft.class_1683;
import net.minecraft.class_1753;
import net.minecraft.class_1764;
import net.minecraft.class_1771;
import net.minecraft.class_1776;
import net.minecraft.class_1779;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1812;
import net.minecraft.class_1823;
import net.minecraft.class_1835;
import net.minecraft.class_1890;
import net.minecraft.class_1893;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_3966;
import net.minecraft.class_4537;
import net.minecraft.class_6880;
import net.minecraft.class_9304;
import net.minecraft.class_239.class_240;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;
import ru.wexside.prediction.ProjectileImpact;
import ru.wexside.prediction.ProjectileMotionStep;
import ru.wexside.prediction.TrajectoryPrediction;
import ru.wexside.render.RenderCamera;

public final class TrajectoryPredictor {
   private final class_310 mc = class_310.method_1551();
   static final int slot = 12;
   static final float value3 = 10.0F;
   private class_243 vec = class_243.field_1353;
   static final int slot2 = 200;
   static final double value4 = 0.3;
   static final double value5 = 0.3;
   static final double value6 = 0.6;
   static final float value7 = (float) (Math.PI / 180.0);

   public List<TrajectoryPrediction> process(class_1309 screenHandler, class_1799 stack, float f) {
      if (stack != null && !stack.method_7960()) {
         if (ProjectileType.fromItem(stack.method_7909()) == null) {
            return List.of();
         } else {
            this.updateShooterVelocity(screenHandler);
            boolean bl = this.process22(stack);
            float[] fArray;
            if (bl) {
               float[] fArray2 = new float[]{0.0F, -10.0F, 0.0F};
               fArray = fArray2;
               fArray2[2] = 10.0F;
            } else {
               float[] fArray3;
               fArray = fArray3 = new float[1];
               fArray3[0] = 0.0F;
            }

            ArrayList<TrajectoryPrediction> arrayList = new ArrayList<>(fArray.length);

            for(float f2 : fArray) {
               TrajectoryPrediction trajectoryPrediction = this.process9(screenHandler, stack, f, f2);
               if (trajectoryPrediction != null) {
                  arrayList.add(trajectoryPrediction);
               }
            }

            return arrayList;
         }
      } else {
         return List.of();
      }
   }

   public TrajectoryPrediction process2(class_1297 entity2, float f) {
      if (this.mc.field_1687 == null || entity2 == null) {
         return null;
      } else if (entity2 instanceof class_1542 iIIIlIIIII2) {
         return this.process3(iIIIlIIIII2, f);
      } else {
         ProjectileType iiIlilIilI2 = ProjectileType.fromEntity(entity2);
         if (iiIlilIilI2 == null) {
            return null;
         } else {
            class_243 vec2 = new class_243(entity2.method_23317(), entity2.method_23318(), entity2.method_23321());
            class_243 vec3 = entity2.method_18798();
            boolean bl = this.process7(iiIlilIilI2);
            return this.process11(
               entity2,
               vec2,
               vec3,
               class_242.field_1348,
               iiIlilIilI2,
               (vec4, vec5) -> {
                  boolean bl2 = this.process24(vec4);
                  return bl
                     ? vec5.method_1023(0.0, -this.process6(entity2), 0.0).method_1021(this.process19(entity2, bl2))
                     : vec5.method_1021(this.process19(entity2, bl2)).method_1023(0.0, -this.process6(entity2), 0.0);
               },
               bl
            );
         }
      }
   }

   public TrajectoryPrediction process3(class_1542 iIIIlIIIII2, float f) {
      return this.mc.field_1687 != null && iIIIlIIIII2 != null
         ? this.process11(
            iIIIlIIIII2,
            new class_243(iIIIlIIIII2.method_23317(), iIIIlIIIII2.method_23318(), iIIIlIIIII2.method_23321()),
            iIIIlIIIII2.method_18798(),
            class_242.field_1348,
            ProjectileType.ITEM,
            (vec4, vec5) -> vec5.method_1021(this.process19(iIIIlIIIII2, this.process24(vec4))).method_1023(0.0, -this.process6(iIIIlIIIII2), 0.0),
            false
         )
         : null;
   }

   public class_243 process4(class_1309 screenHandler, float f) {
      return screenHandler == this.mc.field_1724 && this.mc.field_1690.method_31044().method_31034() && RenderCamera.position() != null
         ? RenderCamera.position()
         : screenHandler.method_5836(f);
   }

   private class_243 process5(class_1309 screenHandler, class_1792 iiIilIIilI2, float f, float f2) {
      class_243 vec4 = this.process8(screenHandler.method_5695(f), screenHandler.method_5705(f), this.process17(iiIilIIilI2));
      return iiIilIIilI2 instanceof class_1764 && f2 != 0.0F ? this.process25(vec4, screenHandler.method_5828(f), f2).method_1029() : vec4;
   }

   private double process6(class_1297 entity2) {
      if (entity2 instanceof class_1683) {
         return 0.07;
      } else {
         ProjectileType iiIlilIilI2 = ProjectileType.fromEntity(entity2);
         if (iiIlilIilI2 == ProjectileType.TRIDENT || iiIlilIilI2 == ProjectileType.ARROW || iiIlilIilI2 == ProjectileType.POTION) {
            return 0.05;
         } else if (iiIlilIilI2 == ProjectileType.PEARL) {
            return 0.03;
         } else {
            return iiIlilIilI2 == ProjectileType.ITEM ? 0.04 : 0.03;
         }
      }
   }

   private boolean process7(ProjectileType iiIlilIilI2) {
      return iiIlilIilI2 == ProjectileType.POTION || iiIlilIilI2 == ProjectileType.PEARL;
   }

   private class_243 process8(float f, float f2, float f3) {
      float f4 = f * (float) (Math.PI / 180.0);
      float f5 = f2 * (float) (Math.PI / 180.0);
      float f6 = (f + f3) * (float) (Math.PI / 180.0);
      double d = -Math.sin((double)f5) * Math.cos((double)f4);
      double d2 = -Math.sin((double)f6);
      double d3 = Math.cos((double)f5) * Math.cos((double)f4);
      return new class_243(d, d2, d3).method_1029();
   }

   private TrajectoryPrediction process9(class_1309 screenHandler, class_1799 stack, float f, float f2) {
      if (this.mc.field_1687 == null) {
         return null;
      } else {
         class_1792 iiIilIIilI2 = stack.method_7909();
         float f3 = this.process18(screenHandler, stack);
         if (f3 <= 0.0F) {
            return null;
         } else {
            class_243 vec2 = this.process4(screenHandler, f);
            class_243 vec3 = this.process5(screenHandler, iiIilIIilI2, f, f2);
            class_243 vec6 = this.process21(screenHandler, iiIilIIilI2, f, f2);
            class_243 vec7 = this.process23(iiIilIIilI2);
            class_243 vec8 = vec3.method_1021((double)f3).method_18806(vec7);
            class_243 vec9 = vec6.method_1021((double)f3);
            ProjectileMotionStep ilIIlIIIlI2 = (vec4, vec5) -> {
               boolean inFluid = this.process24(vec4);
               return vec5.method_1021(this.process13(iiIilIIilI2, inFluid)).method_1023(0.0, -this.process20(iiIilIIilI2), 0.0);
            };
            ProjectileType iiIlilIilI2 = ProjectileType.fromItem(iiIilIIilI2);
            TrajectoryPrediction trajectoryPrediction = this.process11(screenHandler, vec2, vec8, class_242.field_1348, iiIlilIilI2, ilIIlIIIlI2, false);
            if (trajectoryPrediction == null) {
               return null;
            } else {
               boolean bl = this.process17(iiIilIIilI2) != 0.0F || vec7.method_1027() >= 1.0E-6;
               if (!bl) {
                  return trajectoryPrediction;
               } else {
                  TrajectoryPrediction trajectoryPrediction2 = this.process11(screenHandler, vec2, vec9, class_242.field_1348, iiIlilIilI2, ilIIlIIIlI2, false);
                  return this.process12(trajectoryPrediction, trajectoryPrediction2);
               }
            }
         }
      }
   }

   private static double process10(double d) {
      d = Math.max(0.0, Math.min(1.0, d));
      return d * d * (3.0 - 2.0 * d);
   }

   private TrajectoryPrediction process11(
      class_1297 entity2,
      class_243 vec4,
      class_243 vec5,
      class_242 fluidHandling,
      ProjectileType iiIlilIilI2,
      ProjectileMotionStep motionStep,
      boolean applyBeforeMove
   ) {
      if (this.mc.field_1687 == null) {
         return null;
      } else {
         ArrayList<class_243> arrayList = new ArrayList(201);
         arrayList.add(vec4);
         class_243 vec2 = vec4;
         class_243 vec3 = vec5;

         for(int i = 0; i < 200; ++i) {
            if (applyBeforeMove) {
               vec3 = motionStep.apply(vec2, vec3);
            }

            class_243 vec7 = vec2.method_1019(vec3);
            class_3965 hit2 = this.mc.field_1687.method_17742(new class_3959(vec2, vec7, class_3960.field_17558, fluidHandling, entity2));
            class_3966 iIIIllIilI2 = this.process14(vec2, vec7, entity2, iiIlilIilI2);
            double d = hit2.method_17783() == class_240.field_1332 ? vec2.method_1025(hit2.method_17784()) : Double.MAX_VALUE;
            double d2 = iIIIllIilI2 != null ? vec2.method_1025(iIIIllIilI2.method_17784()) : Double.MAX_VALUE;
            class_243 vec6 = vec3.method_1027() > 1.0E-8 ? vec3.method_1029() : class_243.field_1353;
            if (iIIIllIilI2 != null && d2 <= d) {
               class_243 vec9 = iIIIllIilI2.method_17784();
               arrayList.add(vec9);
               class_2350 process17 = vec6.method_1027() > 1.0E-8
                  ? class_2350.method_10142(vec6.field_1352, vec6.field_1351, vec6.field_1350).method_10153()
                  : class_2350.field_11036;
               return new TrajectoryPrediction(arrayList, new ProjectileImpact(vec9, vec6, process17, iIIIllIilI2.method_17782()), i);
            }

            if (hit2.method_17783() == class_240.field_1332) {
               class_243 vec10 = hit2.method_17784();
               arrayList.add(vec10);
               return new TrajectoryPrediction(arrayList, new ProjectileImpact(vec10, vec6, hit2.method_17780(), null), i);
            }

            vec2 = vec7;
            arrayList.add(vec7);
            if (!applyBeforeMove) {
               vec3 = motionStep.apply(vec7, vec3);
            }

            if (vec7.field_1351 < (double)this.mc.field_1687.method_31607()) {
               break;
            }
         }

         return new TrajectoryPrediction(arrayList, null, Math.max(0, arrayList.size() - 1));
      }
   }

   private TrajectoryPrediction process12(TrajectoryPrediction trajectoryPrediction, TrajectoryPrediction trajectoryPrediction2) {
      if (trajectoryPrediction2 == null) {
         return trajectoryPrediction;
      } else {
         List<class_243> list = trajectoryPrediction.points();
         List<class_243> list2 = trajectoryPrediction2.points();
         int n = Math.min(12, Math.min(list.size(), list2.size()));
         ArrayList<class_243> arrayList = new ArrayList(list.size());

         for(int i = 0; i < list.size(); ++i) {
            if (i < n) {
               double d = process10((double)i / 12.0);
               class_243 vec4 = (class_243)list2.get(i);
               class_243 vec5 = (class_243)list.get(i);
               arrayList.add(
                  new class_243(
                     vec4.field_1352 + (vec5.field_1352 - vec4.field_1352) * d,
                     vec4.field_1351 + (vec5.field_1351 - vec4.field_1351) * d,
                     vec4.field_1350 + (vec5.field_1350 - vec4.field_1350) * d
                  )
               );
            } else {
               arrayList.add((class_243)list.get(i));
            }
         }

         return new TrajectoryPrediction(arrayList, trajectoryPrediction.impact(), trajectoryPrediction.flightTicks());
      }
   }

   private double process13(class_1792 iiIilIIilI2, boolean bl) {
      if (iiIilIIilI2 instanceof class_1835) {
         return 0.99;
      } else if (iiIilIIilI2 instanceof class_1776 || iiIilIIilI2 instanceof class_1823 || iiIilIIilI2 instanceof class_1771) {
         return bl ? 0.8 : 0.99;
      } else if (iiIilIIilI2 instanceof class_1753 || iiIilIIilI2 instanceof class_1764) {
         return bl ? 0.6 : 0.99;
      } else if (!this.process16(iiIilIIilI2) && !(iiIilIIilI2 instanceof class_1779)) {
         return 0.98;
      } else {
         return bl ? 0.8 : 0.99;
      }
   }

   private class_3966 process14(class_243 vec4, class_243 vec5, class_1297 entity2, ProjectileType iiIlilIilI2) {
      if (this.mc.field_1687 == null) {
         return null;
      } else {
         class_238 box = new class_238(vec4, vec5).method_1014(0.3);
         class_1297 iIiiiilIiI2 = null;
         class_243 vec2 = null;
         double d = Double.MAX_VALUE;

         for(class_1297 candidate : this.mc.field_1687.method_8335(entity2, box)) {
            double d2;
            Optional intersection;
            class_1676 projectile;
            if (candidate.method_5805()
               && (!(entity2 instanceof class_1676) || (projectile = (class_1676)entity2).method_24921() != candidate)
               && (iiIlilIilI2 != ProjectileType.POTION || ProjectileType.fromEntity(candidate) != ProjectileType.POTION)
               && !(intersection = candidate.method_5829().method_1014(0.3).method_992(vec4, vec5)).isEmpty()
               && (d2 = vec4.method_1025((class_243)intersection.get())) < d) {
               d = d2;
               iIiiiilIiI2 = candidate;
               vec2 = (class_243)intersection.get();
            }
         }

         return iIiiiilIiI2 != null && vec2 != null ? new class_3966(iIiiiilIiI2, vec2) : null;
      }
   }

   public class_243 process15(class_1297 entity2, float f) {
      return new class_243(
         class_3532.method_16436((double)f, entity2.field_6014, entity2.method_23317()),
         class_3532.method_16436((double)f, entity2.field_6036, entity2.method_23318()),
         class_3532.method_16436((double)f, entity2.field_5969, entity2.method_23321())
      );
   }

   private boolean process16(class_1792 iiIilIIilI2) {
      return iiIilIIilI2 instanceof class_4537 || iiIilIIilI2 instanceof class_1812;
   }

   private float process17(class_1792 iiIilIIilI2) {
      return !(iiIilIIilI2 instanceof class_1779) && !this.process16(iiIilIIilI2) ? 0.0F : -20.0F;
   }

   private float process18(class_1309 screenHandler, class_1799 stack) {
      class_1792 iiIilIIilI2 = stack.method_7909();
      if (iiIilIIilI2 instanceof class_1779) {
         return 0.7F;
      } else if (this.process16(iiIilIIilI2)) {
         return 0.5F;
      } else if (iiIilIIilI2 instanceof class_1776 || iiIilIIilI2 instanceof class_1823 || iiIilIIilI2 instanceof class_1771) {
         return 1.5F;
      } else if (iiIilIIilI2 instanceof class_1753) {
         int n = screenHandler.method_6115() ? stack.method_7935(screenHandler) - screenHandler.method_6014() : 20;
         return class_1753.method_7722(n) * 3.0F;
      } else if (iiIilIIilI2 instanceof class_1764) {
         return 3.15F;
      } else {
         return iiIilIIilI2 instanceof class_1835 ? 2.5F + Math.max(0.0F, class_1890.method_60123(stack, screenHandler)) * 0.5F : 1.5F;
      }
   }

   private double process19(class_1297 entity2, boolean bl) {
      ProjectileType iiIlilIilI2 = ProjectileType.fromEntity(entity2);
      if (iiIlilIilI2 == ProjectileType.TRIDENT) {
         return 0.99;
      } else if (iiIlilIilI2 == ProjectileType.PEARL) {
         return bl ? 0.8 : 0.99;
      } else if (iiIlilIilI2 == ProjectileType.ARROW) {
         return bl ? 0.6 : 0.99;
      } else if (iiIlilIilI2 == ProjectileType.POTION) {
         return bl ? 0.8 : 0.99;
      } else {
         return iiIlilIilI2 == ProjectileType.ITEM ? 0.98 : 0.99;
      }
   }

   private double process20(class_1792 iiIilIIilI2) {
      if (iiIilIIilI2 instanceof class_1835) {
         return 0.05;
      } else if (iiIilIIilI2 instanceof class_1776 || iiIilIIilI2 instanceof class_1823 || iiIilIIilI2 instanceof class_1771) {
         return 0.03;
      } else if (iiIilIIilI2 instanceof class_1753 || iiIilIIilI2 instanceof class_1764) {
         return 0.05;
      } else if (iiIilIIilI2 instanceof class_1779) {
         return 0.07;
      } else {
         return this.process16(iiIilIIilI2) ? 0.05 : 0.04;
      }
   }

   private class_243 process21(class_1309 screenHandler, class_1792 iiIilIIilI2, float f, float f2) {
      class_243 vec4 = this.process8(screenHandler.method_5695(f), screenHandler.method_5705(f), 0.0F);
      return iiIilIIilI2 instanceof class_1764 && f2 != 0.0F ? this.process25(vec4, screenHandler.method_5828(f), f2).method_1029() : vec4;
   }

   private boolean process22(class_1799 stack) {
      if (!(stack.method_7909() instanceof class_1764)) {
         return false;
      } else {
         class_9304 itemEnchantmentsComponent = class_1890.method_57532(stack);

         for(class_6880 enchantment : itemEnchantmentsComponent.method_57534()) {
            if (enchantment.method_40225(class_1893.field_9108) && itemEnchantmentsComponent.method_57536(enchantment) > 0) {
               return true;
            }
         }

         return false;
      }
   }

   private class_243 process23(class_1792 iiIilIIilI2) {
      if (ProjectileType.fromItem(iiIilIIilI2) == ProjectileType.CROSSBOW) {
         return class_243.field_1353;
      } else {
         class_243 vec4 = this.vec;
         double d = vec4.method_1027();
         if (d > 0.36) {
            vec4 = vec4.method_1021(0.6 / Math.sqrt(d));
         }

         return vec4;
      }
   }

   private boolean process24(class_243 vec4) {
      if (this.mc.field_1687 == null) {
         return false;
      } else {
         return !this.mc.field_1687.method_8316(class_2338.method_49638(vec4)).method_15769();
      }
   }

   private void updateShooterVelocity(class_1309 screenHandler) {
      double d = screenHandler.method_23317() - screenHandler.field_6014;
      double d2 = screenHandler.method_24828() ? 0.0 : Math.min(0.0, screenHandler.method_23318() - screenHandler.field_6036);
      double d3 = screenHandler.method_23321() - screenHandler.field_5969;
      this.vec = new class_243(
         this.vec.field_1352 + (d - this.vec.field_1352) * 0.3,
         this.vec.field_1351 + (d2 - this.vec.field_1351) * 0.3,
         this.vec.field_1350 + (d3 - this.vec.field_1350) * 0.3
      );
   }

   private class_243 process25(class_243 vec4, class_243 vec5, float f) {
      if (vec5.method_1027() < 1.0E-8) {
         return vec4;
      } else {
         class_243 vec2 = vec5.method_1029();
         double d = Math.toRadians((double)f);
         double d2 = Math.cos(d);
         double d3 = Math.sin(d);
         return vec4.method_1021(d2).method_1019(vec2.method_1036(vec4).method_1021(d3)).method_1019(vec2.method_1021(vec2.method_1026(vec4) * (1.0 - d2)));
      }
   }
}
