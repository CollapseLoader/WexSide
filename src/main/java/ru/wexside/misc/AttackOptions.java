package ru.wexside.misc;

import ru.wexside.util.Angle;

public record AttackOptions(
   Angle lookAngle,
   float range,
   int clicksPerSecond,
   boolean raycastEnabled,
   boolean sprintResetEnabled,
   SprintResetMode sprintResetMode,
   boolean criticalsOnly,
   RaycastMode raycastMode,
   boolean legacyCombat,
   double accuracyPercent,
   boolean breakShield,
   boolean desyncShield,
   boolean jumpOnly,
   float maceFallDistance
) {
   public AttackOptions(
      Angle lookAngle,
      float range,
      int clicksPerSecond,
      boolean raycastEnabled,
      boolean sprintResetEnabled,
      SprintResetMode sprintResetMode,
      boolean criticalsOnly,
      RaycastMode raycastMode,
      boolean legacyCombat,
      double accuracyPercent,
      boolean breakShield,
      boolean desyncShield,
      boolean jumpOnly,
      float maceFallDistance
   ) {
      sprintResetMode = sprintResetMode == null ? SprintResetMode.NONE : sprintResetMode;
      raycastMode = raycastMode == null ? RaycastMode.VISIBLE : raycastMode;
      clicksPerSecond = Math.max(1, clicksPerSecond);
      accuracyPercent = Math.clamp(accuracyPercent, 0.0, 100.0);
      range = Math.max(0.0F, range);
      maceFallDistance = Math.max(0.0F, maceFallDistance);
      this.lookAngle = lookAngle;
      this.range = range;
      this.clicksPerSecond = clicksPerSecond;
      this.raycastEnabled = raycastEnabled;
      this.sprintResetEnabled = sprintResetEnabled;
      this.sprintResetMode = sprintResetMode;
      this.criticalsOnly = criticalsOnly;
      this.raycastMode = raycastMode;
      this.legacyCombat = legacyCombat;
      this.accuracyPercent = accuracyPercent;
      this.breakShield = breakShield;
      this.desyncShield = desyncShield;
      this.jumpOnly = jumpOnly;
      this.maceFallDistance = maceFallDistance;
   }

   public boolean allowsThroughWalls() {
      return this.raycastMode == RaycastMode.THROUGH_WALLS;
   }
}
