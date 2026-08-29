package ru.wexside.ui;

import java.util.function.BooleanSupplier;
import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.misc.FontRegistry;
import ru.wexside.misc.ThemeColors;
import ru.wexside.util.GuiDrawApi;

public final class CreateConfigButton extends GuiElement {
   private final BooleanSupplier enabled;
   private final Runnable action;

   public CreateConfigButton(GuiBounds bounds, BooleanSupplier enabled, Runnable action) {
      super(bounds);
      this.enabled = enabled;
      this.action = action;
   }

   @Override
   public boolean onMousePressed(int mouseX, int mouseY, int button) {
      if (button == 0 && this.getBounds().contains((float)mouseX, (float)mouseY)) {
         if (this.enabled.getAsBoolean()) {
            this.action.run();
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public float render(float delta, Matrix4f matrix) {
      GuiBounds bounds = this.getBounds();
      GuiDrawApi renderer = WexSideClient.getGuiRenderer();
      int color = this.enabled.getAsBoolean() ? ThemeColors.accent() : ThemeColors.borderPrimary();
      renderer.drawRoundedOutline(
         matrix,
         bounds.getX(),
         bounds.getY(),
         bounds.getWidth(),
         bounds.getHeight(),
         6.0F,
         0.75F,
         ThemeColors.withHoverOverlay(ThemeColors.notificationOutline())
      );
      String label = "Создать конфигурацию";
      float width = FontRegistry.font4.process3(label, 5.5F);
      float height = FontRegistry.font4.process4(label, 5.5F);
      FontRegistry.font4
         .process2(
            matrix, renderer, label, bounds.getX() + (bounds.getWidth() - width) / 2.0F, bounds.getY() + (bounds.getHeight() - height) / 2.0F, 5.5F, color
         );
      return bounds.getY() + bounds.getHeight();
   }
}
