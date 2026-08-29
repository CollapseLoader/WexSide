package ru.wexside.misc;

import java.util.Objects;
import ru.wexside.input.BindInput;
import ru.wexside.setting.BindSetting;

public final class BindSettingAdapter implements KeybindDescriptor {
   private final BindSetting bindSetting;

   public BindSettingAdapter(BindSetting bindSetting) {
      this.bindSetting = bindSetting;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (!(object instanceof BindSettingAdapter)) {
         return false;
      } else {
         BindSettingAdapter bindSettingAdapter = (BindSettingAdapter)object;
         return Objects.equals(this.bindSetting, bindSettingAdapter.bindSetting);
      }
   }

   @Override
   public String toString() {
      String string = String.valueOf(this.bindSetting);
      return "OfBindSetting[setting=" + string + "]";
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.bindSetting);
   }

   @Override
   public String getString() {
      return this.bindSetting.getDisplayName();
   }

   @Override
   public String getString2() {
      return this.bindSetting.getDescription();
   }

   @Override
   public void setBindInput(BindInput bindInput) {
      this.bindSetting.setBindInput(bindInput);
   }

   @Override
   public BindInput getBindInput() {
      return this.bindSetting.getBindInput();
   }

   public BindSetting getBindSetting() {
      return this.bindSetting;
   }
}
