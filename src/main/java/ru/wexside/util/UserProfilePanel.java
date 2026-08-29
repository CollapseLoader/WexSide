package ru.wexside.util;

import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.misc.BoundsProvider;
import ru.wexside.misc.CharacterInputHandler;
import ru.wexside.misc.ClasspathResource;
import ru.wexside.misc.ClientProfile;
import ru.wexside.misc.FontRegistry;
import ru.wexside.misc.GuiRenderable;
import ru.wexside.misc.KeyPressHandler;
import ru.wexside.misc.LayoutUpdater;
import ru.wexside.misc.MouseButtonHandler;
import ru.wexside.misc.MouseScrollHandler;
import ru.wexside.misc.TextLayoutUtils;
import ru.wexside.misc.TextureResource;
import ru.wexside.misc.ThemeColors;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.GuiElement;

public class UserProfilePanel
   extends GuiElement
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider {
   private static final TextureResource DEFAULT_AVATAR = new TextureResource(new ClasspathResource("/assets/wexside/textures/menu/logotypes.png"));
   private float value2;
   private float value7;
   private float value;
   private final ClientProfile clientProfile2;

   public UserProfilePanel(GuiBounds bounds2, ClientProfile clientProfile2) {
      super(bounds2);
      this.clientProfile2 = clientProfile2;
      this.value7 = bounds2.getWidth();
      this.value2 = bounds2.getY();
   }

   public float getFloatType() {
      return 22.0F;
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

   public void process3(float f, float f2, float f3) {
      this.value = f;
      this.value7 = f2;
      this.value2 = f3;
   }

   private float process9(float f, float f2, float f3) {
      return f * (1.0F - f3) + f2 * f3;
   }

   @Override
   public float render(float f, Matrix4f matrix4f) {
      GuiBounds bounds2 = this.getBounds();
      GuiDrawApi drawApi = WexSideClient.getGuiRenderer();
      float f4 = 1.0F - this.value;
      int n3 = (int)Math.clamp(f4 * 255.0F, 0.0F, 255.0F);
      int n4 = (int)Math.clamp(this.value * 255.0F, 0.0F, 255.0F);
      float f5 = bounds2.getX();
      float f6 = bounds2.getY();
      float f7 = bounds2.getX() + (this.value7 - 13.0F) / 2.0F;
      float f8 = f7 + 2.5F;
      float f9 = this.value2 + 2.5F;
      float f10 = this.process9(8.0F, 8.0F, this.value);
      float f11 = this.process9(f5, f8, this.value);
      float f12 = this.process9(f6, f9, this.value);
      if (n3 > 2) {
         int n2 = ColorUtils.withAlpha(ThemeColors.textPrimary(), (float)n3);
         int n = ColorUtils.withAlpha(ThemeColors.textMuted(), (float)n3);
         String string = this.clientProfile2.getExpirationDate().replace("-", ".");
         float f3 = bounds2.getX() + 10.0F;
         float f2 = bounds2.getX() + bounds2.getWidth() - FontRegistry.font4.process3(string, 5.5F);
         String string2 = TextLayoutUtils.trimToWidth(this.clientProfile2.getUsername(), FontRegistry.font2, 5.5F, f2 - f3 - 0.0F);
         FontRegistry.font2.process2(matrix4f, drawApi, string2, f3, bounds2.getY() + 0.75F, 5.5F, n2);
         FontRegistry.font2.process2(matrix4f, drawApi, string, f2, bounds2.getY() + 0.75F, 5.5F, n);
      }

      if (n4 > 2) {
         int n2 = ColorUtils.withAlpha(ThemeColors.borderPrimary(), (float)n4);
         int n = ColorUtils.withAlpha(ThemeColors.accent(), (float)n4);
         float f13 = f7 + 6.5F;
         float f3 = f9 + 8.0F + 3.0F;
         float f2 = f3 + 3.0F;
         drawApi.drawRoundedRectangleOutlined(
            matrix4f, f7, this.value2, 13.0F, 22.0F, 9.0F, 1.0F, ColorUtils.withAlpha(ThemeColors.backgroundPrimary(), 0.0F), n2
         );
         drawApi.drawCircle(matrix4f, f13, f2, 0.0F, 360.0F, 1.5F, 3.0F, n2);
         drawApi.drawCircle(matrix4f, f13, f2, 0.0F, 270.0F, 1.5F, 3.0F, n);
      }

      TextureResource texture2;
      TextureResource texture3 = (texture2 = this.clientProfile2.getAvatarTexture()) != null ? texture2 : DEFAULT_AVATAR;
      int n5 = drawApi.bindTexture(texture3.getTextureId(), texture3.getWidth(), texture3.getHeight());
      drawApi.drawRoundedTextureTinted(matrix4f, f11, f12, f10, f10, 5.0F, n5, -1);
      return bounds2.getY() + bounds2.getHeight();
   }
}
