package ru.wexside.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.misc.BoundsProvider;
import ru.wexside.misc.CharacterInputHandler;
import ru.wexside.misc.FontRegistry;
import ru.wexside.misc.FrameInterpolator;
import ru.wexside.misc.GuiRenderable;
import ru.wexside.misc.KeyPressHandler;
import ru.wexside.misc.LayoutUpdater;
import ru.wexside.misc.MouseButtonHandler;
import ru.wexside.misc.MouseScrollHandler;
import ru.wexside.misc.ThemeColors;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.GuiElement;
import ru.wexside.ui.NavigationEntry;

public final class NavigationSection
   extends GuiElement
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider {
   private final String string3;
   private float value;
   private final float value2;
   private final float value3;
   private boolean enabled = true;
   private final float value4;
   private final float value5;
   private final float value6;
   private final List<NavigationEntry> entries = new ArrayList<>();
   private float value7 = 1.0F;
   private final float value8;
   private final String string4;
   private boolean enabled2;

   public NavigationSection(String string, String string2, GuiBounds bounds2) {
      super(bounds2);
      this.value6 = 13.0F;
      this.value8 = 2.0F;
      this.value5 = 4.0F;
      this.value4 = 5.25F;
      this.value2 = 10.5F;
      this.value3 = 4.5F;
      this.string3 = string;
      this.string4 = string2;
   }

   public boolean process(int n, int n2) {
      if (this.isActive3()) {
         return false;
      } else {
         float f = this.getBounds().getX();
         float f2 = this.getBounds().getY();
         float f3 = this.getFloatType2();
         float f4 = this.getBounds().getWidth();
         return (float)n >= f && (float)n <= f + f4 && (float)n2 >= f2 && (float)n2 <= f2 + f3;
      }
   }

   @Override
   public void onMouseScroll(int n, int n2, double d) {
      for(NavigationEntry navigationEntry : this.entries) {
         navigationEntry.onMouseScroll(n, n2, d);
      }
   }

   @Override
   public void update() {
      for(NavigationEntry navigationEntry : this.entries) {
         navigationEntry.update();
      }
   }

   public boolean isActive() {
      return this.enabled;
   }

   @Override
   public boolean onMousePressed(int n, int n2, int n3) {
      if (this.isActive3()) {
         return false;
      } else if (!this.getBounds().contains((float)n, (float)n2)) {
         return false;
      } else if (this.isHeaderHovered(n, n2)) {
         if (this.value <= 0.5F) {
            this.enabled = !this.enabled;
         }

         return true;
      } else if (this.process(n, n2)) {
         return true;
      } else if (!this.isActive()) {
         return true;
      } else {
         for(NavigationEntry navigationEntry : this.entries) {
            if (navigationEntry.getBounds().contains((float)n, (float)n2) && navigationEntry.onMousePressed(n, n2, n3)) {
               return true;
            }
         }

         return false;
      }
   }

   public String getString() {
      return this.string3;
   }

   public void process3(float f, boolean bl) {
      this.value = f;
      this.enabled2 = bl;
   }

   private float process9(float f, float f2, float f3) {
      return f * (1.0F - f3) + f2 * f3;
   }

   @Override
   public boolean isActive2() {
      if (this.isActive3()) {
         return false;
      } else {
         return this.getFloatType2() > 0.01F || this.isActive();
      }
   }

   @Override
   public float render(float f, Matrix4f matrix4f) {
      GuiBounds bounds2 = this.getBounds();
      this.value7 = FrameInterpolator.lerpTowards(this.value7, this.enabled ? 1.0F : 0.0F, 15.0F);
      if (this.isActive3()) {
         this.process4(bounds2, bounds2.getY());
         bounds2.setSize(bounds2.getWidth(), 0.0F);
         return bounds2.getY();
      } else {
         float f2 = this.getFloatType2();
         float f3 = bounds2.getY() + f2;
         this.process7(matrix4f, bounds2);
         if (!this.isActive()) {
            this.process4(bounds2, f3);
            bounds2.setSize(bounds2.getWidth(), f2);
            return f3;
         } else {
            f3 = this.process6(f, matrix4f, bounds2, f3);
            bounds2.setSize(bounds2.getWidth(), f3 - bounds2.getY());
            return f3;
         }
      }
   }

   public List<NavigationEntry> getList() {
      return Collections.unmodifiableList(this.entries);
   }

   private void process4(GuiBounds bounds2, float f) {
      for(NavigationEntry navigationEntry : this.entries) {
         if (navigationEntry instanceof NavigationEntry) {
            navigationEntry.setFloatType(this.value);
         }

         navigationEntry.getBounds().setPosition(bounds2.getX(), f);
         navigationEntry.getBounds().setSize(bounds2.getWidth(), 0.0F);
      }
   }

   public void addEntry(NavigationEntry navigationEntry) {
      this.entries.add(navigationEntry);
   }

   public float getFloatType() {
      return this.value;
   }

   private float getFloatType2() {
      return 10.5F * (1.0F - this.value);
   }

   public float getFloatType3() {
      return 5.25F;
   }

   public float getFloatType4() {
      return 4.0F;
   }

   public boolean isHeaderHovered(int n, int n2) {
      if (this.isActive3()) {
         return false;
      } else if (this.value > 0.5F) {
         return false;
      } else {
         float f = this.getBounds().getX();
         float f2 = this.getBounds().getY();
         float f3 = FontRegistry.font4.process3(this.string4, 5.25F);
         float f4 = f3 + 6.0F + 4.5F;
         float f5 = this.getFloatType9();
         return (float)n >= f && (float)n <= f + f4 && (float)n2 >= f2 && (float)n2 <= f2 + f5;
      }
   }

   public float getExpandAnimation() {
      return this.value7;
   }

   @Override
   public void setBooleanType(boolean bl) {
      this.enabled = bl;
   }

   private float getFloatType5() {
      return this.process9(2.0F, 4.0F, this.value);
   }

   public float getFloatType6() {
      return 2.0F;
   }

   public float getFloatType7() {
      return 4.5F;
   }

   private float process6(float f, Matrix4f matrix4f, GuiBounds bounds2, float f2) {
      float f3 = this.getFloatType5();

      for(int i = 0; i < this.entries.size(); ++i) {
         NavigationEntry navigationEntry = this.entries.get(i);
         if (navigationEntry instanceof NavigationEntry) {
            navigationEntry.setFloatType(this.value);
         }

         navigationEntry.getBounds().setPosition(bounds2.getX(), f2);
         navigationEntry.getBounds().setSize(bounds2.getWidth(), 13.0F);
         f2 = navigationEntry.render(f, matrix4f);
         if (i < this.entries.size() - 1) {
            f2 += f3;
         }
      }

      return f2;
   }

   public float getFloatType8() {
      return 10.5F;
   }

   private boolean isActive3() {
      return this.enabled2 && !this.enabled;
   }

   public List<NavigationEntry> getList2() {
      return this.entries;
   }

   private float getFloatType9() {
      return 5.25F * (1.0F - this.value);
   }

   public boolean isActive4() {
      return this.enabled2;
   }

   public float getFloatType10() {
      return 13.0F;
   }

   public String getString2() {
      return this.string4;
   }

   private void process7(Matrix4f matrix4f, GuiBounds bounds2) {
      GuiDrawApi drawApi = WexSideClient.getGuiRenderer();
      float f = 1.0F - this.value;
      int n = (int)Math.max(0.0F, Math.min(255.0F, f * 255.0F));
      if (n > 2) {
         int n2 = ColorUtils.withAlpha(ThemeColors.textMuted(), (float)n);
         FontRegistry.font4.process2(matrix4f, drawApi, this.string4, bounds2.getX(), bounds2.getY(), 5.25F, n2);
         float f2 = bounds2.getX() + 2.0F + FontRegistry.font4.process3(this.string4, 5.25F);
         float f3 = bounds2.getY() + 1.0F;
         float f4 = FontRegistry.font3.process3("D", 4.5F);
         float f5 = FontRegistry.font3.process4("D", 4.5F);
         float f6 = f2 + f4 / 2.0F;
         float f7 = f3 + f5 / 2.0F;
         Matrix4f matrix4f2 = new Matrix4f(matrix4f)
            .translate(f6, f7, 0.0F)
            .rotateZ((float)Math.toRadians((double)(90.0F * this.value7)))
            .translate(-f6, -f7, 0.0F);
         FontRegistry.font3.process5(matrix4f2, drawApi, "D", f2, f3, 4.5F, n2);
      }
   }
}
