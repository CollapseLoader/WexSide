package ru.wexside.input;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.class_11908;
import net.minecraft.class_2477;
import net.minecraft.class_310;
import net.minecraft.class_3675;
import org.lwjgl.glfw.GLFW;

public final class InputBindings {
   private static final Map<String, Integer> KEY_CODES = createKeyCodes();

   private InputBindings() {
   }

   public static boolean isPressed(BindInput input) {
      if (input != null && !input.isUnbound()) {
         return switch(input.device()) {
            case NONE -> false;
            case KEYBOARD -> isKeyPressed(input.code());
            case MOUSE -> isMouseButtonPressed(input.code());
            default -> throw new MatchException(null, null);
         };
      } else {
         return false;
      }
   }

   public static String displayName(BindInput input) {
      if (input != null && !input.isUnbound()) {
         return input.isMouse() ? mouseButtonName(input.code()) : keyName(input.code());
      } else {
         return "NONE";
      }
   }

   public static boolean isMouseButtonPressed(int button) {
      return GLFW.glfwGetMouseButton(class_310.method_1551().method_22683().method_4490(), button) == 1;
   }

   public static boolean isKeyPressed(int keyCode) {
      return GLFW.glfwGetKey(class_310.method_1551().method_22683().method_4490(), keyCode) == 1;
   }

   public static int keyCode(String name) {
      return name == null ? -1 : KEY_CODES.getOrDefault(name.toUpperCase(Locale.ROOT), -1);
   }

   public static List<String> keyNames() {
      ArrayList<String> names = new ArrayList<>(KEY_CODES.keySet());
      names.sort(String.CASE_INSENSITIVE_ORDER);
      return names;
   }

   public static String keyName(int keyCode) {
      String translationKey = class_3675.method_15985(new class_11908(keyCode, -1, 0)).method_1441();
      String prefix = "key.keyboard.";
      if (translationKey.startsWith(prefix)) {
         translationKey = translationKey.substring(prefix.length());
      }

      return translationKey.replace('.', '_').replace("grave_accent", "`").toUpperCase(Locale.ROOT);
   }

   public static String mouseButtonName(int button) {
      return switch(button) {
         case 0 -> class_2477.method_10517().method_48307("key.mouse.left");
         case 1 -> class_2477.method_10517().method_48307("key.mouse.right");
         case 2 -> class_2477.method_10517().method_48307("key.mouse.middle");
         default -> "MOUSE" + (button + 1);
      };
   }

   private static Map<String, Integer> createKeyCodes() {
      LinkedHashMap<String, Integer> keys = new LinkedHashMap<>();

      for(char key = 'A'; key <= 'Z'; ++key) {
         keys.put(String.valueOf(key), Integer.valueOf(key));
      }

      for(char key = '0'; key <= '9'; ++key) {
         keys.put(String.valueOf(key), Integer.valueOf(key));
      }

      for(int i = 1; i <= 12; ++i) {
         keys.put("F" + i, Integer.valueOf(290 + i - 1));
      }

      for(int i = 1; i <= 9; ++i) {
         keys.put("NUMPAD" + i, Integer.valueOf(321 + i - 1));
      }

      keys.put("SPACE", Integer.valueOf(32));
      keys.put("ENTER", Integer.valueOf(257));
      keys.put("ESCAPE", Integer.valueOf(256));
      keys.put("HOME", Integer.valueOf(268));
      keys.put("INSERT", Integer.valueOf(260));
      keys.put("DELETE", Integer.valueOf(261));
      keys.put("END", Integer.valueOf(269));
      keys.put("PAGEUP", Integer.valueOf(266));
      keys.put("PAGEDOWN", Integer.valueOf(267));
      keys.put("RIGHT", Integer.valueOf(262));
      keys.put("LEFT", Integer.valueOf(263));
      keys.put("DOWN", Integer.valueOf(264));
      keys.put("UP", Integer.valueOf(265));
      keys.put("RSHIFT", Integer.valueOf(344));
      keys.put("LSHIFT", Integer.valueOf(340));
      keys.put("RCONTROL", Integer.valueOf(345));
      keys.put("LCONTROL", Integer.valueOf(341));
      keys.put("RIGHT_ALT", Integer.valueOf(346));
      keys.put("LEFT_ALT", Integer.valueOf(342));
      keys.put("CAPSLOCK", Integer.valueOf(280));
      keys.put("APOSTROPHE", Integer.valueOf(39));
      keys.put("/", Integer.valueOf(47));
      keys.put("-", Integer.valueOf(45));
      keys.put("+", Integer.valueOf(61));
      keys.put("BACK", Integer.valueOf(259));
      keys.put("BACKSLASH", Integer.valueOf(92));
      keys.put(".", Integer.valueOf(46));
      keys.put("COMMA", Integer.valueOf(44));
      keys.put("[", Integer.valueOf(91));
      keys.put("]", Integer.valueOf(93));
      keys.put(";", Integer.valueOf(59));
      keys.put("`", Integer.valueOf(96));
      return Map.copyOf(keys);
   }
}
