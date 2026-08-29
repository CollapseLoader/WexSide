package ru.wexside.misc;

public record SpatialTransform(
   double centerX, double centerY, double centerZ, float scaleX, float scaleY, float scaleZ, float yawDegrees, float pitchDegrees, float rollDegrees
) {
   public SpatialTransform(double centerX, double centerY, double centerZ, float scaleX, float scaleY, float scaleZ) {
      this(centerX, centerY, centerZ, scaleX, scaleY, scaleZ, 0.0F, 0.0F, 0.0F);
   }
}
