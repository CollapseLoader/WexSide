package ru.wexside.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BooleanSupplier;
import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.misc.BoundsProvider;
import ru.wexside.misc.CharacterInputHandler;
import ru.wexside.misc.GuiRenderable;
import ru.wexside.misc.KeyPressHandler;
import ru.wexside.misc.LayoutUpdater;
import ru.wexside.misc.MenuToolbar;
import ru.wexside.misc.ModuleCategoryNavigationEntry;
import ru.wexside.misc.MouseButtonHandler;
import ru.wexside.misc.MouseScrollHandler;
import ru.wexside.misc.NavigationSelectionListener;
import ru.wexside.misc.NavigationState;
import ru.wexside.module.ModuleCategory;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.GuiElement;
import ru.wexside.ui.NavigationEntry;

public final class ClientMenuContent
   extends GuiElement
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider {
   private final float value;
   private final float value2;
   private final float value3;
   private final float value4;
   private final float value5;
   private final float value6;
   private final float value7;
   private final float value8;
   private final float value9;
   private final float value10;
   private final MenuToolbar menuToolbar;
   private final float value11;
   private final float value12;
   private final float value13;
   private final float value14;
   private final float value15;
   private final float value16;
   private final float value17;
   private final NavigationState navigationState;
   private final float value18;
   private final float value19;
   private final UserProfilePanel userProfilePanel;
   private final ClippedContentRenderer clippedContentRenderer;
   private final float value20;
   private final float value21;
   private final float value22;
   private final float value23;
   private final float value24;
   private final float value25;
   private final List<NavigationSection> navigationSections = new ArrayList<>();
   private final float value26;
   private final ScrollController scrollController = new ScrollController(18.0F, 30.0F);
   private final float value27;
   private final float value28;
   private final float value29;
   private final float value30;
   private final NavigationSelectionListener callback24;
   private final float value31;

   public ClientMenuContent(
      GuiBounds bounds2, NavigationState navigationState, NavigationSelectionListener callback24, Runnable runnable, BooleanSupplier booleanSupplier
   ) {
      super(bounds2);
      this.value16 = 0.0F;
      this.value30 = 90.0F;
      this.value3 = 25.0F;
      this.value13 = 89.5F;
      this.value2 = 90.0F;
      this.value29 = 65.0F;
      this.value21 = 6.0F;
      this.value10 = 6.0F;
      this.value26 = 71.0F;
      this.value22 = 78.0F;
      this.value14 = 13.0F;
      this.value31 = -0.5F;
      this.value5 = 0.5F;
      this.value24 = 9.0F;
      this.value18 = 4.0F;
      this.value25 = 4.0F;
      this.value27 = 8.5F;
      this.value7 = 6.0F;
      this.value20 = 22.0F;
      this.value4 = 5.0F;
      this.value12 = 5.0F;
      this.value11 = 28.0F;
      this.value17 = 1.0F;
      this.value = 4.0F;
      this.value8 = 4.0F;
      this.value6 = 10.0F;
      this.value19 = 10.0F;
      this.clippedContentRenderer = new ClippedContentRenderer(4.0F, 10.0F, 10.0F, true);
      this.navigationState = navigationState;
      this.callback24 = callback24;
      this.getBounds().setPosition(6.0F, bounds2.getY());
      this.getBounds().setSize(78.0F, bounds2.getHeight());
      this.value23 = 30.0F;
      this.value9 = 8.0F;
      this.userProfilePanel = new UserProfilePanel(new GuiBounds(6.0F, this.value23, 78.0F, this.value9), WexSideClient.getInstance().getClientProfile());
      this.addChild(this.userProfilePanel);
      this.value15 = 12.0F;
      this.value28 = this.getBounds().getY() + this.getBounds().getHeight() - (4.75F + this.value15);
      GuiBounds bounds3 = new GuiBounds(5.5F, this.value28, 78.0F, this.value15);
      GuiBounds bounds4 = new GuiBounds(70.5F, this.value28, 13.0F, this.value15);
      this.menuToolbar = new MenuToolbar(bounds3, bounds4, navigationState, runnable, booleanSupplier);
      this.addChild(this.menuToolbar);
   }

   public String getString() {
      for(NavigationSection navigationSection : this.navigationSections) {
         Iterator<NavigationEntry> iterator = navigationSection.getList().iterator();
         if (iterator.hasNext()) {
            NavigationEntry navigationEntry = iterator.next();
            return navigationEntry.getString();
         }
      }

      return null;
   }

   public float getFloatType() {
      float f = this.navigationState.getFloatType();
      return 6.0F * (1.0F - f) + 71.0F * f;
   }

   public float getFloatType2() {
      float f = this.navigationState.getFloatType();
      return 78.0F * (1.0F - f) + 13.0F * f;
   }

   @Override
   public void onMouseScroll(int n, int n2, double d) {
      if (this.getBounds().contains((float)n, (float)n2) && this.process6(n2)) {
         this.scrollController.scrollByWheel(d, this.getFloatType12());
      }
   }

   @Override
   public void update() {
      for(NavigationSection navigationSection : this.navigationSections) {
         for(NavigationEntry navigationEntry : navigationSection.getList()) {
            navigationEntry.setBooleanType(this.navigationState.process(navigationEntry.getString()));
         }
      }

      for(NavigationSection navigationSection : this.navigationSections) {
         navigationSection.update();
      }

      this.userProfilePanel.update();
      this.menuToolbar.update();
   }

   public float getFloatType3() {
      return 89.5F;
   }

   public String process2(ModuleCategory moduleCategory) {
      for(NavigationSection navigationSection : this.navigationSections) {
         for(NavigationEntry navigationEntry : navigationSection.getList()) {
            ModuleCategoryNavigationEntry categoryEntry;
            if (navigationEntry instanceof ModuleCategoryNavigationEntry
               && (categoryEntry = (ModuleCategoryNavigationEntry)navigationEntry).getModuleCategory() == moduleCategory) {
               return categoryEntry.getString();
            }
         }
      }

      return null;
   }

   public float getFloatType4() {
      return 90.0F;
   }

   @Override
   public boolean onMousePressed(int n, int n2, int n3) {
      if (!this.process6(n2)) {
         return super.onMousePressed(n, n2, n3);
      } else {
         for(NavigationSection navigationSection : this.navigationSections) {
            if (navigationSection.getBounds().contains((float)n, (float)n2)) {
               boolean bl = navigationSection.onMousePressed(n, n2, n3);
               if (navigationSection.process(n, n2)) {
                  return bl;
               }

               NavigationEntry navigationEntry = this.process7(n, n2);
               if (navigationEntry != null) {
                  boolean bl2 = this.navigationState.process(navigationEntry.getString());
                  this.navigationState.setString(navigationEntry.getString());
                  this.callback24.onNavigationSelected(navigationEntry, bl2);
               }

               return bl;
            }
         }

         return super.onMousePressed(n, n2, n3);
      }
   }

   private boolean process3(int n) {
      for(int i = n; i < this.navigationSections.size(); ++i) {
         if (this.navigationSections.get(i).isActive2()) {
            return true;
         }
      }

      return false;
   }

   private float getFloatType5() {
      float f = this.value28 - 28.0F * this.navigationState.getFloatType();
      return f - 4.0F;
   }

   public NavigationSection process4(String string) {
      for(NavigationSection navigationSection : this.navigationSections) {
         for(NavigationEntry navigationEntry : navigationSection.getList()) {
            if (navigationEntry.getString().equals(string)) {
               return navigationSection;
            }
         }
      }

      return null;
   }

   private float getFloatType6() {
      return this.getFloatType10() + this.userProfilePanel.getFloatType() + 5.0F;
   }

   public String process5(Class<? extends NavigationEntry> clazz) {
      for(NavigationSection navigationSection : this.navigationSections) {
         for(NavigationEntry navigationEntry : navigationSection.getList()) {
            if (clazz.isInstance(navigationEntry)) {
               return navigationEntry.getString();
            }
         }
      }

      return null;
   }

   public void setNavigationSection(NavigationSection navigationSection) {
      this.navigationSections.add(navigationSection);
   }

   public float getFloatType7() {
      return 78.0F;
   }

   private boolean process6(int n) {
      return (float)n >= this.getFloatType11() - 1.0F && (float)n <= this.getFloatType5();
   }

   public float getFloatType8() {
      float f = this.navigationState.getFloatType();
      return 90.0F * (1.0F - f) + 25.0F * f;
   }

   private NavigationEntry process7(int n, int n2) {
      for(NavigationSection navigationSection : this.navigationSections) {
         if (navigationSection.isActive()) {
            for(NavigationEntry navigationEntry : navigationSection.getList()) {
               if (navigationEntry.getBounds().contains((float)n, (float)n2)) {
                  return navigationEntry;
               }
            }
         }
      }

      return null;
   }

   private float process8(float f, Matrix4f matrix4f, GuiBounds bounds2, float f2, float f3, float f4) {
      float f5 = this.process9(9.0F, 8.5F, f3);
      float f6 = 4.0F * f3;

      for(int i = 0; i < this.navigationSections.size(); ++i) {
         NavigationSection navigationSection = this.navigationSections.get(i);
         navigationSection.getBounds().setPosition(bounds2.getX(), f4);
         navigationSection.getBounds().setSize(f2, navigationSection.getBounds().getHeight());
         float f7 = f4;
         f4 = navigationSection.render(f, matrix4f);
         if (!navigationSection.isActive2()) {
            f4 = f7;
         } else {
            if (this.process3(i + 1) && f3 > 0.01F) {
               int n = (int)Math.max(0.0F, Math.min(255.0F, f3 * 255.0F));
               int n2 = 14277081 | n << 24;
               float f8 = bounds2.getX() + (f2 - 6.0F) / 2.0F;
               WexSideClient.getGuiRenderer().fillRectangle(matrix4f, f8, f4 + f6, 6.0F, 0.5F, n2);
            }

            if (this.process3(i + 1)) {
               f4 += f5;
            }
         }
      }

      return f4;
   }

   public void setString(String string) {
      NavigationEntry navigationEntry = this.process10(string);
      if (navigationEntry != null) {
         float f = this.getFloatType12();
         float f2 = navigationEntry.getBounds().getY() - this.getFloatType11();
         float f3 = f2 + navigationEntry.getBounds().getHeight();
         float f4 = this.scrollController.getOffset();
         if (f2 + f4 < 0.0F) {
            this.scrollController.scrollTo(-f2, f);
         } else if (f3 + f4 > f) {
            this.scrollController.scrollTo(f - f3, f);
         }
      }
   }

   private float process9(float f, float f2, float f3) {
      return f * (1.0F - f3) + f2 * f3;
   }

   public float getFloatType9() {
      float f = this.navigationState.getFloatType();
      return 0.0F * (1.0F - f) + 65.0F * f;
   }

   private float getFloatType10() {
      return 27.0F;
   }

   private float getFloatType11() {
      return this.process9(this.getBounds().getY(), this.getFloatType6(), this.navigationState.getFloatType());
   }

   public NavigationEntry process10(String string) {
      for(NavigationSection navigationSection : this.navigationSections) {
         for(NavigationEntry navigationEntry : navigationSection.getList()) {
            if (navigationEntry.getString().equals(string)) {
               return navigationEntry;
            }
         }
      }

      return null;
   }

   private float getFloatType12() {
      return Math.max(0.0F, this.getFloatType5() - this.getFloatType11());
   }

   @Override
   public float render(float f, Matrix4f matrix4f) {
      GuiBounds bounds2 = this.getBounds();
      this.navigationState.update2();
      float f2 = this.navigationState.getFloatType();
      float f3 = this.getFloatType2();
      float f4 = this.getFloatType();
      float f5 = this.getFloatType10();
      bounds2.setPosition(f4, bounds2.getY());
      bounds2.setSize(f3, bounds2.getHeight());
      this.userProfilePanel.getBounds().setPosition(bounds2.getX(), this.value23);
      this.userProfilePanel.getBounds().setSize(f3, this.value9);
      this.userProfilePanel.process3(f2, f3, f5);
      float f6 = this.getFloatType11();
      float f7 = this.getFloatType12();
      this.scrollController.update(f7, this.scrollController.getContentHeight());

      for(NavigationSection navigationSection : this.navigationSections) {
         navigationSection.process3(f2, this.navigationState.isActive());
      }

      float[] contentBottom = new float[]{f6 + this.scrollController.getOffset()};
      this.clippedContentRenderer
         .render(
            WexSideClient.getGuiRenderer(),
            matrix4f,
            bounds2.getX(),
            f6,
            f3,
            f7,
            this.scrollController.getOffset(),
            this.scrollController.getMinimumOffset(f7),
            arg_0 -> this.member5202(contentBottom, f, bounds2, f3, f2, arg_0)
         );
      this.scrollController.setContentHeight(f7, contentBottom[0] - this.scrollController.getOffset() - f6);
      this.menuToolbar.setFloatType(f2);
      super.render(f, matrix4f);
      return bounds2.getY() + bounds2.getHeight();
   }

   private void member5202(float[] fArray, float f, GuiBounds bounds2, float f2, float f3, Matrix4f matrix4f) {
      fArray[0] = this.process8(f, matrix4f, bounds2, f2, f3, fArray[0]);
   }
}
