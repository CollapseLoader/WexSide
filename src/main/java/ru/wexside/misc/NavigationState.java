package ru.wexside.misc;

import java.util.Objects;

public final class NavigationState {
   private boolean enabled;
   private String selectedCategory;
   public static final String string2 = "__search__";
   private float openProgress;
   private String string3;

   public String getString() {
      return this.string3;
   }

   public void setString(String string) {
      this.selectedCategory = string;
   }

   public boolean process(String string) {
      return Objects.equals(this.selectedCategory, string);
   }

   public String string4() {
      return this.selectedCategory;
   }

   public boolean isActive() {
      return this.enabled;
   }

   public boolean isActive2() {
      return "__search__".equals(this.selectedCategory);
   }

   public void update() {
      this.enabled = !this.enabled;
   }

   public void setBooleanType(boolean bl) {
      this.enabled = bl;
   }

   public void setString2(String string) {
      this.string3 = string;
   }

   public float getFloatType() {
      return this.openProgress;
   }

   public void update2() {
      this.openProgress = FrameInterpolator.lerpTowards(this.openProgress, this.enabled ? 1.0F : 0.0F, 15.0F);
   }
}
