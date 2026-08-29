package ru.wexside.misc;

import net.minecraft.class_4587;
import net.minecraft.class_7833;
import org.joml.Quaternionfc;

public final class HandRenderTransforms {
   private HandRenderTransforms() {
   }

   public static void translate(Object matrices, double x, double y, double z) {
      ((class_4587)matrices).method_22904(x, y, z);
   }

   public static void translateLocal(Object matrices, float x, float y, float z) {
      ((class_4587)matrices).method_46416(x, y, z);
   }

   public static void scale(Object matrices, float x, float y, float z) {
      ((class_4587)matrices).method_22905(x, y, z);
   }

   public static void multiply(Object matrices, Quaternionfc rotation) {
      ((class_4587)matrices).method_22907(rotation);
   }

   public static void rotateX(Object matrices, float degrees) {
      multiply(matrices, class_7833.field_40714.rotationDegrees(degrees));
   }

   public static void rotateY(Object matrices, float degrees) {
      multiply(matrices, class_7833.field_40716.rotationDegrees(degrees));
   }

   public static void rotateZ(Object matrices, float degrees) {
      multiply(matrices, class_7833.field_40718.rotationDegrees(degrees));
   }

   public static void rotateYNegative(Object matrices, float degrees) {
      multiply(matrices, class_7833.field_40715.rotationDegrees(degrees));
   }

   public static void rotateYNegativeRadians(Object matrices, float radians) {
      multiply(matrices, class_7833.field_40715.rotation(radians));
   }

   public static void rotateZSpin(Object matrices, float degrees) {
      rotateZ(matrices, degrees);
   }
}
