package ru.wexside.misc;

import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.module.ModuleManager;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.GuiElement;
import ru.wexside.util.ColorUtils;
import ru.wexside.util.GuiDrawApi;
import ru.wexside.util.ModuleCard;
import ru.wexside.util.ScrollController;
import ru.wexside.util.Scrollbar;
import ru.wexside.util.TwoColumnLayout;

public final class ModuleListPanel
   extends GuiElement
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider {
   private final TwoColumnLayout twoColumnLayout;
   private Module module;
   private final ModuleCategory moduleCategory;
   private final ContainerDisplay containerDisplay;
   private final Scrollbar scrollbar;
   private final ScrollController scrollController = new ScrollController(18.0F, 30.0F);

   public ModuleListPanel(GuiBounds bounds2, ModuleCategory moduleCategory, ModuleManager moduleManager, ContainerDisplay containerDisplay) {
      super(bounds2);
      this.scrollbar = new Scrollbar();
      this.twoColumnLayout = new TwoColumnLayout(2, 6.0F, 6.0F);
      this.moduleCategory = moduleCategory;
      this.containerDisplay = containerDisplay;
      moduleManager.getModules()
         .stream()
         .filter(module -> module.getCategory() == moduleCategory)
         .forEach(module -> this.addChild(new ModuleCard(new GuiBounds(8.5F, 8.5F, 0.0F, 34.5F), module, containerDisplay)));
   }

   @Override
   public void onMouseScroll(int n, int n2, double d) {
      if (this.getBounds().contains((float)n, (float)n2)) {
         this.scrollController.scrollByWheel(d, this.getBounds().getHeight());
      }

      for(GuiElement element2 : this.children) {
         element2.onMouseScroll(n, n2, d);
      }
   }

   @Override
   public void update() {
      for(GuiElement element2 : this.children) {
         element2.update();
      }
   }

   @Override
   public boolean onMousePressed(int n, int n2, int n3) {
      if (!this.getBounds().contains((float)n, (float)n2)) {
         return false;
      } else if (this.scrollbar.onMousePressed(n, n2, n3)) {
         return true;
      } else {
         this.update4();
         return super.onMousePressed(n, n2, n3);
      }
   }

   @Override
   public float render(float f, Matrix4f matrix4f) {
      GuiBounds bounds2 = this.getBounds();
      GuiDrawApi drawApi = WexSideClient.getGuiRenderer();
      if (this.children.isEmpty()) {
         float f2 = bounds2.getY() + 15.0F;
         FontRegistry.font4
            .process2(matrix4f, drawApi, this.process10(this.moduleCategory), bounds2.getX() + 8.0F, bounds2.getY() + 8.0F, 8.0F, ThemeColors.textPrimary());
         FontRegistry.font2.process2(matrix4f, drawApi, "No modules in this category yet.", bounds2.getX() + 8.0F, f2 + 8.0F, 6.0F, ThemeColors.textMuted());
         return bounds2.getY() + bounds2.getHeight();
      } else {
         float f3 = this.isActive() ? this.getFloatType2() : this.getFloatType();
         this.scrollController.update(bounds2.getHeight(), f3);
         drawApi.beginStencil(1);
         drawApi.drawRoundedRectangleRadii(
            matrix4f,
            bounds2.getX() + 1.0F,
            bounds2.getY() + 1.0F,
            this.getBounds().getWidth() - 1.5F,
            this.getBounds().getHeight() - 1.5F,
            10.5F,
            0.0F,
            0.0F,
            0.0F,
            ColorUtils.rgba(0, 0, 0, 45)
         );
         drawApi.applyStencilMask(1);
         float f4 = this.isActive() ? this.process5(f, matrix4f) : this.process8(f, matrix4f);
         drawApi.endStencil();
         this.scrollController.setContentHeight(bounds2.getHeight(), f4);
         this.scrollbar
            .process(
               drawApi,
               matrix4f,
               bounds2.getX() + bounds2.getWidth(),
               bounds2.getY(),
               bounds2.getHeight(),
               this.scrollController,
               this.getLastMouseX(),
               this.getLastMouseY()
            );
         this.setFloatType(bounds2.getHeight());
         return bounds2.getY() + bounds2.getHeight();
      }
   }

   @Override
   public void update2() {
      this.scrollController.scrollToTop();
   }

   public void update3() {
      for(GuiElement element2 : this.children) {
         if (element2 instanceof ModuleCard moduleCard) {
            GuiBounds bounds2 = moduleCard.getBounds();
            bounds2.setSize(bounds2.getWidth(), moduleCard.getFloatType());
         }
      }

      this.update4();

      for(GuiElement element2 : this.children) {
         if (element2 instanceof ModuleCard moduleCard) {
            if (this.process4(moduleCard)) {
               moduleCard.setBooleanType(false);
            } else {
               moduleCard.collapse();
            }
         }
      }

      this.scrollController.scrollToTop();
   }

   private float getFloatType() {
      return 16.0F + this.twoColumnLayout.process2(this.process7(0.0F));
   }

   private boolean process4(GuiElement element2) {
      float f = 1.0F;
      float f2 = element2.getBounds().getY();
      float f3 = f2 + element2.getBounds().getHeight();
      float f4 = this.getBounds().getY() - f;
      float f5 = this.getBounds().getY() + this.getBounds().getHeight() + f;
      return f3 >= f4 && f2 <= f5;
   }

   private float process5(float f, Matrix4f matrix4f) {
      GuiBounds bounds2 = this.getBounds();
      float f2 = this.scrollController.getOffset();
      float f3 = bounds2.getX() + 8.0F;
      float f4 = bounds2.getY() + 8.0F + f2;
      float f5 = 319.0F;
      float f6 = f4;

      for(GuiElement element2 : this.children) {
         f6 = this.process6(element2, f, matrix4f, f3, f4, f5);
         f4 = f6 + 6.0F;
      }

      return this.process9(f6 - f2);
   }

   private float process6(GuiElement element2, float f, Matrix4f matrix4f, float f2, float f3, float f4) {
      element2.getBounds().setPosition(f2, f3);
      element2.getBounds().setSize(f4, element2.getBounds().getHeight());
      float f5 = element2.getBounds().getY();
      float f6 = f5 + element2.getBounds().getHeight();
      float f7 = 1.0F;
      float f8 = this.getBounds().getY() - f7;
      float f9 = this.getBounds().getY() + this.getBounds().getHeight() + f7;
      return !(f6 < f8) && !(f5 > f9) ? element2.render(f, matrix4f) : f6;
   }

   public void setModule(Module module) {
      this.module = module;
   }

   private void setFloatType(float f) {
      Module module = this.module;
      if (module != null) {
         this.module = null;
         if (!this.children.isEmpty()) {
            if (this.isActive()) {
               float f2 = 8.0F;

               for(GuiElement element2 : this.children) {
                  ModuleCard moduleCard;
                  if (element2 instanceof ModuleCard && (moduleCard = (ModuleCard)element2).getModule() == module) {
                     this.scrollController.scrollTo(-f2, f);
                     return;
                  }

                  f2 += element2.getBounds().getHeight() + 6.0F;
               }
            } else {
               float f3 = 8.0F;
               float f4 = 8.0F;

               for(int i = 0; i < this.children.size(); ++i) {
                  GuiElement element3 = this.children.get(i);
                  boolean bl = (i & 1) == 0;
                  float f5 = bl ? f3 : f4;
                  ModuleCard moduleCard;
                  if (element3 instanceof ModuleCard && (moduleCard = (ModuleCard)element3).getModule() == module) {
                     this.scrollController.scrollTo(-f5, f);
                     return;
                  }

                  if (bl) {
                     f3 += element3.getBounds().getHeight() + 6.0F;
                  } else {
                     f4 += element3.getBounds().getHeight() + 6.0F;
                  }
               }
            }
         }
      }
   }

   private ListLayout process7(float f) {
      return new GuiListLayoutAdapter(this, f);
   }

   private void update4() {
      if (!this.children.isEmpty()) {
         if (this.isActive()) {
            this.update5();
         } else {
            this.update6();
         }
      }
   }

   private float getFloatType2() {
      float f = 8.0F;

      for(GuiElement element2 : this.children) {
         f += element2.getBounds().getHeight() + 6.0F;
      }

      return f - 6.0F + 8.0F;
   }

   private boolean isActive() {
      return this.containerDisplay.getContainerColumnLayout() == ContainerColumnLayout.SINGLE_COLUMN;
   }

   private float process8(float f, Matrix4f matrix4f) {
      GuiBounds bounds2 = this.getBounds();
      float f2 = bounds2.getX() + 8.0F;
      float f3 = bounds2.getY() + 8.0F;
      float f4 = 319.0F;
      float f5 = this.twoColumnLayout
         .process(this.process7(f), matrix4f, f2, f3, f4, this.scrollController.getOffset(), bounds2.getY() - 1.0F, bounds2.getY() + bounds2.getHeight() + 1.0F);
      return f5 <= 0.0F ? 0.0F : f5 + 16.0F;
   }

   private void update5() {
      GuiBounds bounds2 = this.getBounds();
      float f = bounds2.getX() + 8.0F;
      float f2 = bounds2.getY() + 8.0F + this.scrollController.getOffset();
      float f3 = 319.0F;

      for(GuiElement element2 : this.children) {
         element2.getBounds().setPosition(f, f2);
         element2.getBounds().setSize(f3, element2.getBounds().getHeight());
         f2 += element2.getBounds().getHeight() + 6.0F;
      }
   }

   private void update6() {
      GuiBounds bounds2 = this.getBounds();
      float f2 = 319.0F;
      float f3 = (f2 - 6.0F) / 2.0F;
      float f4 = bounds2.getX() + 8.0F;
      float f5 = f4 + f3 + 6.0F;
      float f;
      float f6 = f = bounds2.getY() + 8.0F + this.scrollController.getOffset();
      float f7 = f;

      for(int i = 0; i < this.children.size(); ++i) {
         GuiElement element2 = this.children.get(i);
         boolean bl = (i & 1) == 0;
         float f8 = bl ? f4 : f5;
         float f9 = bl ? f6 : f7;
         element2.getBounds().setPosition(f8, f9);
         element2.getBounds().setSize(f3, element2.getBounds().getHeight());
         if (bl) {
            f6 = f9 + element2.getBounds().getHeight() + 6.0F;
         } else {
            f7 = f9 + element2.getBounds().getHeight() + 6.0F;
         }
      }
   }

   private float process9(float f) {
      GuiBounds bounds2 = this.getBounds();
      return f <= bounds2.getY() + 8.0F ? 0.0F : f - bounds2.getY() + 8.0F;
   }

   private String process10(ModuleCategory moduleCategory) {
      return switch(moduleCategory) {
         case COMBAT -> "Combat Modules";
         case MOVEMENT -> "Movement Modules";
         case RENDER -> "Render Modules";
         case PLAYER -> "Player Modules";
         case DISPLAY -> "Display Modules";
         case MISC -> "Miscellaneous Modules";
         case HIDDEN -> "Hidden Modules";
         default -> throw new MatchException(null, null);
      };
   }
}
