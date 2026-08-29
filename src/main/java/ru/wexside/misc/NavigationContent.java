package ru.wexside.misc;

import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.GuiElement;
import ru.wexside.ui.NavigationEntry;
import ru.wexside.util.ClientMenuContent;
import ru.wexside.util.ColorUtils;
import ru.wexside.util.GuiDrawApi;
import ru.wexside.util.NavigationSection;

public final class NavigationContent
   extends GuiElement
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider {
   private float value = 1.0F;
   private final NavigationState navigationState;
   private final ClientMenuContent clientMenuContent;

   public NavigationContent(GuiBounds bounds2, ClientMenuContent clientMenuContent, NavigationState navigationState) {
      super(bounds2);
      this.clientMenuContent = clientMenuContent;
      this.navigationState = navigationState;
   }

   @Override
   public void onMouseScroll(int n, int n2, double d) {
   }

   @Override
   public void update() {
   }

   @Override
   public boolean onMousePressed(int n, int n2, int n3) {
      return false;
   }

   @Override
   public float render(float f, Matrix4f matrix4f) {
      GuiBounds bounds2 = this.getBounds();
      float f2 = this.navigationState.isActive2() ? 0.0F : 1.0F;
      this.value = FrameInterpolator.lerpTowards(this.value, f2, 45.0F);
      if (this.value <= 0.01F) {
         return bounds2.getY() + bounds2.getHeight();
      } else {
         String string = this.navigationState.string4();
         if (string == null) {
            return bounds2.getY() + bounds2.getHeight();
         } else {
            NavigationEntry navigationEntry = this.clientMenuContent.process10(string);
            NavigationSection navigationSection = this.clientMenuContent.process4(string);
            if (navigationEntry != null && navigationSection != null) {
               float f3 = 5.9F;
               float f4 = 6.0F;
               int n = (int)(255.0F * this.value);
               int n2 = ColorUtils.withAlpha(ThemeColors.textMuted(), (float)n);
               int n3 = ColorUtils.withAlpha(ThemeColors.textPrimary(), (float)n);
               String string2 = navigationSection.getString2();
               String string3 = string2 + " / ";
               GuiDrawApi drawApi = WexSideClient.getGuiRenderer();
               FontRegistry.font2.process2(matrix4f, drawApi, string3, bounds2.getX(), bounds2.getY(), f3, n2);
               float f5 = bounds2.getX() + FontRegistry.font2.process3(string3, f3);
               String icon = navigationEntry.getIcon();
               FontRegistry.font3.process5(matrix4f, drawApi, icon, f5 + 1.0F, bounds2.getY() + 0.5F, f4, n3);
               float var18;
               FontRegistry.font2
                  .process2(
                     matrix4f, drawApi, navigationEntry.getDisplayName(), var18 = f5 + FontRegistry.font3.process3(icon, f4) + 3.0F, bounds2.getY(), f3, n3
                  );
               return bounds2.getY() + bounds2.getHeight();
            } else {
               return bounds2.getY() + bounds2.getHeight();
            }
         }
      }
   }
}
