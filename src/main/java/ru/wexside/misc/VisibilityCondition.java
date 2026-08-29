package ru.wexside.misc;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public final class VisibilityCondition {
   final List<Supplier<Boolean>> member5465;
   final String string2;

   private VisibilityCondition(String string, List<Supplier<Boolean>> list) {
      this.string2 = Objects.requireNonNull(string, "id must be set");
      this.member5465 = List.copyOf(list);
   }

   public static VisibilityCondition process(String string, Supplier... supplierArray) {
      return new VisibilityCondition(string, Arrays.asList(supplierArray));
   }

   public boolean isActive() {
      return this.member5465.stream().allMatch(Supplier::get);
   }

   public List<Supplier<Boolean>> getList() {
      return this.member5465;
   }

   public String getString() {
      return this.string2;
   }
}
