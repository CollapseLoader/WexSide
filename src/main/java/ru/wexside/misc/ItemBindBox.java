package ru.wexside.misc;

import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import ru.wexside.setting.BindSetting;

public final class ItemBindBox {
   private final BindSetting bindSetting;
   private final Predicate<class_1799> predicate;
   private final BooleanSupplier booleanSupplier;
   private final class_1792 item2;

   public ItemBindBox(class_1792 iiIilIIilI2, BindSetting bindSetting, Predicate<class_1799> predicate, BooleanSupplier booleanSupplier) {
      this.item2 = iiIilIIilI2;
      this.bindSetting = bindSetting;
      this.predicate = predicate;
      this.booleanSupplier = booleanSupplier;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (!(object instanceof ItemBindBox)) {
         return false;
      } else {
         ItemBindBox itemBindBox = (ItemBindBox)object;
         return Objects.equals(this.item2, itemBindBox.item2)
            && Objects.equals(this.bindSetting, itemBindBox.bindSetting)
            && Objects.equals(this.predicate, itemBindBox.predicate);
      }
   }

   @Override
   public String toString() {
      String string = String.valueOf(this.predicate);
      String string2 = String.valueOf(this.bindSetting);
      String string3 = String.valueOf(this.item2);
      return "ItemBindBox[displayIcon=" + string3 + ", key=" + string2 + ", matcher=" + string + "]";
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.item2, this.bindSetting, this.predicate);
   }

   public BindSetting getBindSetting() {
      return this.bindSetting;
   }

   public boolean isActive() {
      return this.booleanSupplier == null || this.booleanSupplier.getAsBoolean();
   }

   public class_1792 getItem() {
      return this.item2;
   }

   public boolean isActive2() {
      return !this.bindSetting.getBindInput().isUnbound();
   }

   public Predicate<class_1799> getPredicate() {
      return this.predicate;
   }
}
