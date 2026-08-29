package ru.wexside.misc;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.class_1799;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_437;
import ru.wexside.setting.BindSetting;

public class SwapWheelScreen extends class_437 {
   private static List<class_1799> items = List.of();
   private final BindSetting bind;
   private final List<BindSetting> binds;
   private final int segments;
   private final Consumer<class_1799> onSelect;
   private final Consumer<class_1799> onForget;
   private final Function<class_1799, SwapIcon> icon;

   public SwapWheelScreen(
      BindSetting bind,
      List<BindSetting> binds,
      int segments,
      Consumer<class_1799> onSelect,
      Consumer<class_1799> onForget,
      Function<class_1799, SwapIcon> icon
   ) {
      super(class_2561.method_43473());
      this.bind = bind;
      this.binds = binds;
      this.segments = segments;
      this.onSelect = onSelect;
      this.onForget = onForget;
      this.icon = icon;
   }

   @Override
   public void method_52752(class_332 context) {
   }

   @Override
   public boolean method_25421() {
      return false;
   }

   @Override
   public boolean method_25422() {
      return false;
   }

   public BindSetting bind() {
      return this.bind;
   }

   public List<BindSetting> binds() {
      return this.binds;
   }

   public int segments() {
      return this.segments;
   }

   public Consumer<class_1799> onSelect() {
      return this.onSelect;
   }

   public Consumer<class_1799> onForget() {
      return this.onForget;
   }

   public Function<class_1799, SwapIcon> icon() {
      return this.icon;
   }

   public static List<class_1799> items() {
      return items;
   }

   public static class_1799 stackAt(int index) {
      if (index >= 0 && index < items.size()) {
         class_1799 stack = (class_1799)items.get(index);
         return stack == null ? class_1799.field_8037 : stack;
      } else {
         return class_1799.field_8037;
      }
   }

   public static int indexAt(int code) {
      return code;
   }
}
