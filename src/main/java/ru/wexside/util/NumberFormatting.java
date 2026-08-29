package ru.wexside.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import ru.wexside.setting.NumberSetting;

public final class NumberFormatting {
   private NumberFormatting() {
   }

   public static double snap(double value, double minimum, double maximum, double step) {
      double clamped = Math.max(minimum, Math.min(maximum, value));
      return step <= 0.0 ? clamped : Math.max(minimum, Math.min(maximum, minimum + (double)Math.round((clamped - minimum) / step) * step));
   }

   public static double round(double value, int precision) {
      return BigDecimal.valueOf(value).setScale(Math.max(0, precision), RoundingMode.HALF_UP).doubleValue();
   }

   public static String format(double value, int precision) {
      BigDecimal rounded = BigDecimal.valueOf(value).setScale(Math.max(0, precision), RoundingMode.HALF_UP);
      return precision == 0 ? rounded.toBigInteger().toString() : rounded.toPlainString();
   }

   public static String unit(NumberSetting setting) {
      return setting.getFormattedValue();
   }

   public static double normalize(double value, double minimum, double maximum) {
      return maximum <= minimum ? 0.0 : Math.max(0.0, Math.min(1.0, (value - minimum) / (maximum - minimum)));
   }
}
