package ru.wexside.util;

import java.util.Locale;
import java.util.function.Function;

public enum Easing {
   LINEAR(value -> value),
   EASE_IN_SINE(value -> 1.0 - Math.cos(value * Math.PI / 2.0)),
   EASE_OUT_SINE(value -> Math.sin(value * Math.PI / 2.0)),
   EASE_IN_QUAD(value -> value * value),
   EASE_OUT_QUAD(value -> value * (2.0 - value)),
   EASE_IN_OUT_QUAD(value -> value < 0.5 ? 2.0 * value * value : 1.0 - Math.pow(-2.0 * value + 2.0, 2.0) / 2.0),
   EASE_IN_CUBIC(value -> value * value * value),
   EASE_OUT_CUBIC(value -> 1.0 - Math.pow(1.0 - value, 3.0)),
   EASE_IN_OUT_CUBIC(value -> value < 0.5 ? 4.0 * value * value * value : 1.0 - Math.pow(-2.0 * value + 2.0, 3.0) / 2.0),
   EASE_IN_QUART(value -> Math.pow(value, 4.0)),
   EASE_OUT_QUART(value -> 1.0 - Math.pow(1.0 - value, 4.0)),
   EASE_IN_OUT_QUART(value -> value < 0.5 ? 8.0 * Math.pow(value, 4.0) : 1.0 - Math.pow(-2.0 * value + 2.0, 4.0) / 2.0),
   EASE_IN_QUINT(value -> Math.pow(value, 5.0)),
   EASE_OUT_QUINT(value -> 1.0 - Math.pow(1.0 - value, 5.0)),
   EASE_IN_OUT_QUINT(value -> value < 0.5 ? 16.0 * Math.pow(value, 5.0) : 1.0 - Math.pow(-2.0 * value + 2.0, 5.0) / 2.0),
   EASE_IN_EXPO(value -> value == 0.0 ? 0.0 : Math.pow(2.0, 10.0 * value - 10.0)),
   EASE_OUT_EXPO(value -> value == 1.0 ? 1.0 : 1.0 - Math.pow(2.0, -10.0 * value)),
   EASE_IN_OUT_EXPO(
      value -> value == 0.0
            ? 0.0
            : (value == 1.0 ? 1.0 : (value < 0.5 ? Math.pow(2.0, 20.0 * value - 10.0) / 2.0 : (2.0 - Math.pow(2.0, -20.0 * value + 10.0)) / 2.0))
   ),
   EASE_IN_CIRC(value -> 1.0 - Math.sqrt(1.0 - value * value)),
   EASE_OUT_CIRC(value -> Math.sqrt(1.0 - Math.pow(value - 1.0, 2.0))),
   EASE_IN_OUT_CIRC(
      value -> value < 0.5 ? (1.0 - Math.sqrt(1.0 - Math.pow(2.0 * value, 2.0))) / 2.0 : (Math.sqrt(1.0 - Math.pow(-2.0 * value + 2.0, 2.0)) + 1.0) / 2.0
   ),
   EASE_IN_BACK(value -> 2.70158 * value * value * value - 1.70158 * value * value),
   EASE_OUT_BACK(value -> 1.0 + 2.70158 * Math.pow(value - 1.0, 3.0) + 1.70158 * Math.pow(value - 1.0, 2.0)),
   EASE_IN_OUT_BACK(
      value -> value < 0.5
            ? Math.pow(2.0 * value, 2.0) * (7.189819 * value - 2.5949095) / 2.0
            : (Math.pow(2.0 * value - 2.0, 2.0) * (3.5949095 * (2.0 * value - 2.0) + 2.5949095) + 2.0) / 2.0
   ),
   EASE_IN_ELASTIC(
      value -> value != 0.0 && value != 1.0 ? -Math.pow(2.0, 10.0 * value - 10.0) * Math.sin((10.0 * value - 10.75) * (Math.PI * 2.0 / 3.0)) : value
   ),
   EASE_OUT_ELASTIC(
      value -> value != 0.0 && value != 1.0 ? Math.pow(2.0, -10.0 * value) * Math.sin((10.0 * value - 0.75) * (Math.PI * 2.0 / 3.0)) + 1.0 : value
   ),
   EASE_IN_OUT_ELASTIC(
      value -> value == 0.0 || value == 1.0
            ? value
            : (
               value < 0.5
                  ? -(Math.pow(2.0, 20.0 * value - 10.0) * Math.sin((20.0 * value - 11.125) * (Math.PI * 4.0 / 9.0))) / 2.0
                  : Math.pow(2.0, -20.0 * value + 10.0) * Math.sin((20.0 * value - 11.125) * (Math.PI * 4.0 / 9.0)) / 2.0 + 1.0
            )
   ),
   SIGMOID(value -> 1.0 / (1.0 + Math.exp(-value)));

   private final Function<Double, Double> function;

   private Easing(Function<Double, Double> function) {
      this.function = function;
   }

   public double apply(double value) {
      return this.function.apply(value);
   }

   public float apply(float value) {
      return this.function.apply((double)value).floatValue();
   }

   public Function<Double, Double> getFunction() {
      return this.function;
   }

   @Override
   public String toString() {
      String words = this.name().toLowerCase(Locale.ROOT).replace('_', ' ');
      return Character.toUpperCase(words.charAt(0)) + words.substring(1);
   }
}
