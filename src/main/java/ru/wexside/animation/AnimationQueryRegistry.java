package ru.wexside.animation;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.ToDoubleFunction;
import net.minecraft.class_1959;
import net.minecraft.class_1972;
import net.minecraft.class_310;
import net.minecraft.class_6862;
import net.minecraft.class_6908;
import net.minecraft.class_746;
import ru.wexside.misc.AnimationQuery;

public final class AnimationQueryRegistry {
   private static final Map<String, AnimationQuery> QUERIES = new LinkedHashMap<>();

   private AnimationQueryRegistry() {
   }

   public static void registerDefaults() {
      if (QUERIES.isEmpty()) {
         register("wexside.sigmoid", 3, values -> sigmoid(values[0], values[1], values[2]));
         register("wexside.sigmoid_swing", 3, values -> sigmoid(values[2], values[0], values[1]));
         register("wexside.movewave_sin", 3, values -> movementWave(values, true));
         register("wexside.movewave_cos", 3, values -> movementWave(values, false));
         register("wexside.movewave", 3, values -> movementWave(values, true));
         register("wexside.linear_wave", 1, values -> triangleWave(values[0] + (values.length > 1 ? values[1] : 0.0)));
         register("wexside.lopsided_wave", 2, values -> lopsidedWave(values[0], values[1]));
         register("wexside.speed_vibration", 1, values -> values[0] * movementSpeed() * Math.sin(Math.toRadians(animationTime() * 1440.0)));
         register("wexside.interval", 2, values -> interval(values[0], values[1]));
         register("wexside.smoothclamp", 4, values -> smoothClamp(values[0], values[1], values[2], values[3]));
         register("wexside.smoothmin", 3, values -> smoothMinimum(values[0], values[1], values[2]));
         register("wexside.easeinback", 1, values -> easeInBack(values[0]));
         register("wexside.easeoutback", 1, values -> easeOutBack(values[0]));
         register("wexside.easeinoutback", 1, values -> easeInOutBack(values[0]));
         register("wexside.easeinoutquad", 1, values -> easeInOutPower(values[0], 2.0));
         register("wexside.easeinoutquint", 1, values -> easeInOutPower(values[0], 5.0));
         register("wexside.easeinoutsine", 1, values -> easeInOutSine(values[0]));
         register("wexside.random_integer", 2, values -> Math.floor(values[0] + Math.random() * (values[1] - values[0] + 1.0)));
         register("wexside.set_tick_step_value", 1, values -> values[0]);
         register("wexside.check_for_desert", 0, values -> isDesert());
         register("wexside.check_for_ocean", 0, values -> isInBiome(class_6908.field_36509));
         register("wexside.check_for_nether", 0, values -> isInBiome(class_6908.field_36518));
         register("wexside.check_for_theend", 0, values -> isInBiome(class_6908.field_37394));
         register("wexside.check_for_biome", 1, values -> 0.0);
         register("Math.sin", 1, values -> Math.sin(Math.toRadians(values[0])));
         register("Math.cos", 1, values -> Math.cos(Math.toRadians(values[0])));
         register("bmath.cos", 1, values -> Math.cos(Math.toRadians(values[0])));
         register("query.is_crouching_smooth_easeoutsine", 1, values -> easeOutSine(crouching()));
         register("query.is_crouching_smooth_easeoutexpo", 1, values -> easeOutExpo(crouching()));
         register("query.is_in_water_smooth", 1, values -> inWater());
         register("query.is_in_water_smooth_easeoutsine", 1, values -> easeOutSine(inWater()));
         register("query.is_in_water_smooth_easeoutback", 2, values -> easeOutBack(inWater()));
         register("query.is_on_ground_smooth", 1, values -> onGround());
         register("query.is_on_ground_smooth_easeoutexpo", 1, values -> easeOutExpo(onGround()));
         register("query.body_x_rotation_smooth_easeinoutsine", 1, values -> easeInOutSine(bodyPitchFraction()));
      }
   }

   public static Map<String, AnimationQuery> queries() {
      registerDefaults();
      return Map.copyOf(QUERIES);
   }

   private static void register(String name, int argumentCount, ToDoubleFunction<double[]> function) {
      QUERIES.putIfAbsent(name, new AnimationQuery(argumentCount, function));
   }

   private static class_746 player() {
      return class_310.method_1551().field_1724;
   }

   private static double animationTime() {
      class_310 client = class_310.method_1551();
      return client.field_1687 == null ? 0.0 : (double)((float)client.field_1687.method_75260() + client.method_61966().method_60637(false));
   }

   private static double movementSpeed() {
      class_746 player = player();
      return player == null ? 0.0 : Math.min(1.0, player.method_18798().method_37267() * 4.0);
   }

   private static double crouching() {
      class_746 player = player();
      return player != null && player.method_5715() ? 1.0 : 0.0;
   }

   private static double inWater() {
      class_746 player = player();
      return player != null && player.method_5799() ? 1.0 : 0.0;
   }

   private static double onGround() {
      class_746 player = player();
      return player != null && player.method_24828() ? 1.0 : 0.0;
   }

   private static double bodyPitchFraction() {
      class_746 player = player();
      return player == null ? 0.0 : Math.clamp(((double)player.method_36455() + 90.0) / 180.0, 0.0, 1.0);
   }

   private static double isInBiome(class_6862<class_1959> tag) {
      class_310 client = class_310.method_1551();
      class_746 player = client.field_1724;
      return client.field_1687 != null && player != null && client.field_1687.method_23753(player.method_24515()).method_40220(tag) ? 1.0 : 0.0;
   }

   private static double isDesert() {
      class_310 client = class_310.method_1551();
      class_746 player = client.field_1724;
      return client.field_1687 != null && player != null && client.field_1687.method_23753(player.method_24515()).method_40225(class_1972.field_9424)
         ? 1.0
         : 0.0;
   }

   private static double interval(double period, double activeFraction) {
      if (period <= 0.0) {
         return 0.0;
      } else {
         double position = animationTime() % period;
         return position < period * activeFraction ? 1.0 : 0.0;
      }
   }

   private static double movementWave(double[] values, boolean sine) {
      double angle = playerYaw() * values[0] + values[2];
      double wave = sine ? Math.sin(Math.toRadians(angle)) : Math.cos(Math.toRadians(angle));
      return wave * values[1] * movementSpeed();
   }

   private static double playerYaw() {
      class_746 player = player();
      return player == null ? 0.0 : (double)player.method_73188();
   }

   private static double lopsidedWave(double angle, double peakPercent) {
      double phase = (angle % 360.0 + 360.0) % 360.0 / 360.0;
      double split = Math.clamp(peakPercent / 100.0, 0.01, 0.99);
      double normalized = phase < split ? phase / split * 0.5 : 0.5 + (phase - split) / (1.0 - split) * 0.5;
      return Math.sin(normalized * Math.PI * 2.0);
   }

   private static double triangleWave(double angle) {
      double phase = (angle % 360.0 + 360.0) % 360.0 / 360.0;
      return phase < 0.5 ? phase * 2.0 : 2.0 - phase * 2.0;
   }

   private static double smoothClamp(double value, double minimum, double maximum, double smoothing) {
      return smoothing <= 0.0 ? Math.clamp(value, minimum, maximum) : smoothMinimum(-smoothMinimum(-value, -minimum, smoothing), maximum, smoothing);
   }

   private static double smoothMinimum(double first, double second, double smoothing) {
      if (smoothing <= 0.0) {
         return Math.min(first, second);
      } else {
         double factor = Math.clamp(0.5 + 0.5 * (second - first) / smoothing, 0.0, 1.0);
         return first * factor + second * (1.0 - factor) - smoothing * factor * (1.0 - factor);
      }
   }

   private static double easeOutSine(double value) {
      return Math.sin(value * Math.PI / 2.0);
   }

   private static double easeOutExpo(double value) {
      return value >= 1.0 ? 1.0 : 1.0 - Math.pow(2.0, -10.0 * value);
   }

   private static double easeInOutSine(double value) {
      return -(Math.cos(Math.PI * value) - 1.0) / 2.0;
   }

   private static double easeInBack(double value) {
      return value * value * (2.70158 * value - 1.70158);
   }

   private static double easeOutBack(double value) {
      double shifted = value - 1.0;
      return 1.0 + shifted * shifted * (2.70158 * shifted + 1.70158);
   }

   private static double easeInOutBack(double value) {
      double factor = 2.5949095;
      return value < 0.5
         ? Math.pow(2.0 * value, 2.0) * ((factor + 1.0) * 2.0 * value - factor) / 2.0
         : (Math.pow(2.0 * value - 2.0, 2.0) * ((factor + 1.0) * (2.0 * value - 2.0) + factor) + 2.0) / 2.0;
   }

   private static double easeInOutPower(double value, double power) {
      return value < 0.5 ? Math.pow(2.0, power - 1.0) * Math.pow(value, power) : 1.0 - Math.pow(-2.0 * value + 2.0, power) / 2.0;
   }

   private static double sigmoid(double value, double amplitude, double steepness) {
      return amplitude * (2.0 / (1.0 + Math.exp(-steepness * value)) - 1.0);
   }
}
