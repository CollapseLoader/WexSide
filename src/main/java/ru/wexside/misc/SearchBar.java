package ru.wexside.misc;

import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.GuiElement;
import ru.wexside.util.ColorUtils;
import ru.wexside.util.GuiDrawApi;

public final class SearchBar
   extends GuiElement
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider {
   private final SearchQueryState searchQueryState;
   private final String string4 = "Поиск по имени элемента или модуля...";
   private float value;
   private final NavigationState navigationState;
   private final Runnable runnable;
   private float value5;
   private final TextInputController textInputController;

   public SearchBar(GuiBounds bounds2, NavigationState navigationState, SearchQueryState searchQueryState, Runnable runnable) {
      super(bounds2);
      this.navigationState = navigationState;
      this.searchQueryState = searchQueryState;
      this.runnable = runnable;
      this.textInputController = new TextInputController(new SearchQueryTextAdapter(searchQueryState));
   }

   @Override
   public void onMouseScroll(int n, int n2, double d) {
   }

   @Override
   public void update() {
      this.textInputController.tick();
   }

   @Override
   public boolean onMousePressed(int n, int n2, int n3) {
      return !this.navigationState.isActive2() ? false : this.textInputController.onMousePressed(this.getBounds(), n, n2, n3);
   }

   @Override
   public float render(float f, Matrix4f matrix4f) {
      GuiBounds bounds2 = this.getBounds();
      GuiDrawApi drawApi = WexSideClient.getGuiRenderer();
      float f2 = this.navigationState.isActive2() ? 1.0F : 0.0F;
      this.value5 = FrameInterpolator.lerpTowards(this.value5, f2, 45.0F);
      if (this.value5 <= 0.01F) {
         return bounds2.getY() + bounds2.getHeight();
      } else {
         int n = (int)(255.0F * this.value5);
         float f3 = bounds2.getY();
         int n2 = ColorUtils.withAlpha(ThemeColors.textPrimary(), (float)n);
         FontRegistry.font3.process5(matrix4f, drawApi, "Ф", bounds2.getX() + 1.0F, f3, 7.5F, n2);
         float f4 = bounds2.getX() + 1.0F + FontRegistry.font3.process3("Ф", 7.5F) + 2.5F;
         String string = this.textInputController.getText();
         boolean bl = string.isBlank();
         boolean bl2 = bl && !this.textInputController.isFocused();
         String string2 = bl2 ? "Поиск по имени элемента или модуля..." : string;
         this.value = FrameInterpolator.lerpTowards(this.value, this.textInputController.isAllSelected() ? 1.0F : 0.0F, 30.0F);
         int n3 = bl2 ? ThemeColors.textMuted() : ThemeColors.textPrimary();
         int n4 = bl2 ? n3 : ColorUtils.lerp(n3, ThemeColors.adjustForTheme(n3), (double)this.value);
         int n5 = ColorUtils.withAlpha(n4, (float)n);
         float f5 = bounds2.getY();
         FontRegistry.font2.process2(matrix4f, drawApi, string2, f4, f5, 6.5F, n5);
         if (this.textInputController.isCaretVisible()) {
            float f6 = f4 + (bl ? 0.0F : FontRegistry.font2.process3(string, 6.5F));
            FontRegistry.font2.process2(matrix4f, drawApi, "|", f6, f5, 6.5F, ColorUtils.withAlpha(ThemeColors.textPrimary(), (float)n));
         }

         return bounds2.getY() + bounds2.getHeight();
      }
   }

   @Override
   public void onMouseReleased(int n, int n2, int n3) {
      this.textInputController.blurIfOutside(this.getBounds(), n, n2);
   }

   @Override
   public boolean onCharTyped(char c) {
      return !this.navigationState.isActive2() ? false : this.textInputController.onCharTyped(c);
   }

   @Override
   public void update2() {
      this.textInputController.blur();
      super.update2();
   }

   @Override
   public boolean onKeyPressed(int n) {
      if (!this.navigationState.isActive2()) {
         return false;
      } else if (n != 257 && n != 335) {
         return this.textInputController.onKeyPressed(n);
      } else {
         if (this.runnable != null) {
            this.runnable.run();
         }

         return true;
      }
   }

   public void update3() {
      this.textInputController.blur();
   }

   public void update4() {
      this.textInputController.focus();
   }
}
