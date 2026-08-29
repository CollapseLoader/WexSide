package ru.wexside.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.IntConsumer;
import org.joml.Matrix4f;
import ru.wexside.misc.BoundsProvider;
import ru.wexside.misc.CharacterInputHandler;
import ru.wexside.misc.FrameInterpolator;
import ru.wexside.misc.GuiRenderable;
import ru.wexside.misc.KeyPressHandler;
import ru.wexside.misc.LayoutUpdater;
import ru.wexside.misc.MouseButtonHandler;
import ru.wexside.misc.MouseScrollHandler;
import ru.wexside.misc.SegmentedControlOption;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.GuiElement;

public final class SegmentedControl
   extends GuiElement
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider {
   private float value;
   private boolean enabled;
   private int slot = -1;
   private final SegmentedControlStyle segmentedControlStyle;
   private final List<SegmentedControlOption> options = new ArrayList<>();
   private IntConsumer intConsumer = n -> {
   };

   public SegmentedControl(GuiBounds bounds2, String... stringArray) {
      this(bounds2, new SegmentedControlStyle(), stringArray);
   }

   public SegmentedControl(GuiBounds bounds2, SegmentedControlStyle segmentedControlStyle, String... stringArray) {
      super(bounds2);
      this.segmentedControlStyle = segmentedControlStyle == null ? new SegmentedControlStyle() : segmentedControlStyle;
      this.setString(stringArray);
   }

   @Override
   public void onMouseScroll(int n, int n2, double d) {
   }

   @Override
   public void update() {
   }

   @Override
   public boolean onMousePressed(int n, int n2, int n3) {
      if (n3 == 0 && !this.options.isEmpty()) {
         float f2 = (float)n - this.getBounds().getX();
         float f;
         if (!this.process5(f2, f = (float)n2 - this.getBounds().getY())) {
            return false;
         } else {
            for(int i = 0; i < this.options.size(); ++i) {
               if (this.options.get(i).onMousePressed((int)f2, (int)f, n3)) {
                  this.setIntType2(i);
                  return true;
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   @Override
   public float render(float f, Matrix4f matrix4f) {
      this.update4();
      GuiBounds bounds2 = this.getBounds();
      if (this.options.isEmpty()) {
         return bounds2.getY() + bounds2.getHeight();
      } else {
         GuiDrawApi drawApi = this.segmentedControlStyle.getGuiDrawApi();
         if (drawApi == null) {
            return bounds2.getY() + bounds2.getHeight();
         } else {
            Matrix4f matrix4f2 = new Matrix4f(matrix4f).translate(bounds2.getX(), bounds2.getY(), 0.0F);
            this.process8(drawApi, matrix4f2, bounds2.getWidth(), bounds2.getHeight());
            this.process9(drawApi, matrix4f2);

            for(SegmentedControlOption segmentedControlOption : this.options) {
               segmentedControlOption.render(f, matrix4f2);
            }

            return bounds2.getY() + bounds2.getHeight();
         }
      }
   }

   public void setIntConsumer(IntConsumer intConsumer) {
      this.intConsumer = intConsumer == null ? n -> {
      } : intConsumer;
   }

   public void setFloatType(float f) {
      if (!(Math.abs(this.segmentedControlStyle.getFloatType5() - f) < 0.01F)) {
         this.segmentedControlStyle.process2(f);
         this.update4();
      }
   }

   public List<String> getList() {
      ArrayList<String> arrayList = new ArrayList<>(this.options.size());

      for(SegmentedControlOption segmentedControlOption : this.options) {
         arrayList.add(segmentedControlOption.getString2());
      }

      return Collections.unmodifiableList(arrayList);
   }

   public void setIntType2(int n) {
      if (n >= 0 && n < this.options.size() && this.slot != n) {
         this.slot = n;
         this.refreshSelection();
         this.intConsumer.accept(n);
      }
   }

   public void setString(String... stringArray) {
      String string = this.getString();
      this.options.clear();
      this.options.clear();
      if (stringArray != null) {
         for(String string2 : stringArray) {
            if (string2 != null && !string2.isBlank()) {
               this.setString2(string2);
            }
         }
      }

      this.slot = this.process7(string);
      this.enabled = false;
      this.refreshSelection();
      this.update4();
   }

   public void process4(String string, String string2) {
      if (string != null && !string.isBlank()) {
         this.process6(string, string2);
         if (this.slot < 0) {
            this.slot = 0;
            this.enabled = false;
         }

         this.refreshSelection();
         this.update4();
      }
   }

   private boolean process5(float f, float f2) {
      return f >= 0.0F && f <= this.getBounds().getWidth() && f2 >= 0.0F && f2 <= this.getBounds().getHeight();
   }

   private void process6(String string, String string2) {
      SegmentedControlOption segmentedControlOption = new SegmentedControlOption(
         new GuiBounds(0.0F, 0.0F, this.segmentedControlStyle.getFloatType5(), this.segmentedControlStyle.getFloatType9()),
         string,
         string2,
         this.segmentedControlStyle
      );
      this.options.add(segmentedControlOption);
      this.addChild(segmentedControlOption);
   }

   private int process7(String string) {
      if (this.options.isEmpty()) {
         return -1;
      } else {
         if (string != null && !string.isBlank()) {
            for(int i = 0; i < this.options.size(); ++i) {
               if (string.equals(this.options.get(i).getString2())) {
                  return i;
               }
            }
         }

         return this.slot >= 0 && this.slot < this.options.size() ? this.slot : 0;
      }
   }

   private void setString2(String string) {
      this.process6(string, null);
   }

   public void setString3(String string) {
      if (string != null && !string.isBlank()) {
         for(int i = 0; i < this.options.size(); ++i) {
            if (string.equals(this.options.get(i).getString2())) {
               this.setIntType2(i);
               return;
            }
         }
      }
   }

   public int getIntType2() {
      return this.slot;
   }

   public void setString4(String string) {
      this.process4(string, null);
   }

   public String getString() {
      return this.slot >= 0 && this.slot < this.options.size() ? this.options.get(this.slot).getString2() : "";
   }

   private void process8(GuiDrawApi drawApi, Matrix4f matrix4f, float f, float f2) {
      drawApi.drawRoundedRectangle(matrix4f, 0.0F, 0.0F, f, f2, this.segmentedControlStyle.getFloatType2(), this.segmentedControlStyle.getIntType3());
      drawApi.drawRoundedRectangleOutlined(
         matrix4f,
         0.0F,
         0.0F,
         f,
         f2,
         this.segmentedControlStyle.getFloatType2(),
         this.segmentedControlStyle.getFloatType6(),
         this.segmentedControlStyle.getIntType2(),
         this.segmentedControlStyle.getIntType6()
      );
   }

   private void process9(GuiDrawApi drawApi, Matrix4f matrix4f) {
      if (this.slot >= 0 && this.slot < this.options.size()) {
         float f = (float)this.slot * this.segmentedControlStyle.getFloatType5();
         if (!this.enabled) {
            this.value = f;
            this.enabled = true;
         } else {
            this.value = FrameInterpolator.lerpTowards(this.value, f, this.segmentedControlStyle.getFloatType7());
         }

         drawApi.drawRoundedRectangle(
            matrix4f,
            this.value + 0.5F,
            0.5F,
            this.segmentedControlStyle.getFloatType5() - 1.0F,
            this.segmentedControlStyle.getFloatType9() - 1.0F,
            this.segmentedControlStyle.getFloatType(),
            this.segmentedControlStyle.getIntType5()
         );
      }
   }

   public SegmentedControlStyle getSegmentedControlStyle() {
      return this.segmentedControlStyle;
   }

   private void refreshSelection() {
      for(int i = 0; i < this.options.size(); ++i) {
         this.options.get(i).setBooleanType(i == this.slot);
      }
   }

   private void update4() {
      float f = this.segmentedControlStyle.getFloatType5();
      float f2 = this.segmentedControlStyle.getFloatType9();

      for(int i = 0; i < this.options.size(); ++i) {
         SegmentedControlOption segmentedControlOption = this.options.get(i);
         segmentedControlOption.getBounds().setPosition((float)i * f, 0.0F);
         segmentedControlOption.getBounds().setSize(f, f2);
      }

      this.getBounds().setSize(f * (float)this.options.size(), f2);
   }
}
