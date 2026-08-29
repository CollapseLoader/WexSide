package ru.wexside.ui;

import java.util.Objects;
import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.misc.FontRegistry;
import ru.wexside.misc.FrameInterpolator;
import ru.wexside.misc.ThemeColors;
import ru.wexside.util.ColorUtils;
import ru.wexside.util.GuiDrawApi;
import ru.wexside.util.MsdfFontRenderer;

public abstract class NavigationEntry extends GuiElement {
   private final String id;
   private final String displayName;
   private final String icon;
   private final MsdfFontRenderer iconFont;
   private float compactProgress;
   private float activeProgress;
   private boolean active;

   protected NavigationEntry(String id, String displayName, String icon, MsdfFontRenderer iconFont) {
      super(new GuiBounds(0.0F, 0.0F, 0.0F, 0.0F));
      this.id = Objects.requireNonNull(id, "id");
      this.displayName = Objects.requireNonNull(displayName, "displayName");
      this.icon = Objects.requireNonNull(icon, "icon");
      this.iconFont = Objects.requireNonNull(iconFont, "iconFont");
   }

   protected NavigationEntry(String id, String displayName, String icon) {
      this(id, displayName, icon, FontRegistry.font3);
   }

   public String getString() {
      return this.id;
   }

   public String getDisplayName() {
      return this.displayName;
   }

   public String getIcon() {
      return this.icon;
   }

   public void setFloatType(float compactProgress) {
      this.compactProgress = compactProgress;
   }

   public boolean isActive() {
      return this.active;
   }

   public void setActive(boolean active) {
      this.active = active;
   }

   @Override
   public boolean onMousePressed(int mouseX, int mouseY, int button) {
      return this.getBounds().contains((float)mouseX, (float)mouseY);
   }

   @Override
   public float render(float delta, Matrix4f matrix) {
      GuiBounds bounds = this.getBounds();
      if (bounds.getHeight() <= 0.01F) {
         return bounds.getY();
      } else {
         GuiDrawApi renderer = WexSideClient.getGuiRenderer();
         this.activeProgress = FrameInterpolator.lerpTowards(this.activeProgress, this.isActive() ? 1.0F : 0.0F, 15.0F);
         int background = ColorUtils.lerp(ColorUtils.withAlpha(ThemeColors.borderSubtle(), 0.0F), ThemeColors.borderSubtle(), (double)this.activeProgress);
         int foreground = ColorUtils.lerp(ThemeColors.textSecondary(), ThemeColors.textPrimary(), (double)this.activeProgress);
         renderer.drawRoundedRectangle(matrix, bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight(), 6.5F, background);
         float iconSize = 7.0F;
         float iconWidth = this.iconFont.process3(this.icon, iconSize);
         float iconHeight = this.iconFont.process4(this.icon, iconSize);
         float expandedX = bounds.getX() + 3.5F;
         float compactX = bounds.getX() + (bounds.getWidth() - iconWidth) / 2.0F;
         float iconX = expandedX * (1.0F - this.compactProgress) + compactX * this.compactProgress;
         float iconY = bounds.getY() + (bounds.getHeight() - iconHeight) / 2.0F;
         int labelAlpha = (int)Math.clamp((1.0F - this.compactProgress) * 255.0F, 0.0F, 255.0F);
         if (labelAlpha > 2) {
            FontRegistry.font5
               .process2(
                  matrix, renderer, this.displayName, bounds.getX() + 13.5F, bounds.getY() + 2.75F, 6.25F, ColorUtils.withAlpha(foreground, (float)labelAlpha)
               );
         }

         this.iconFont.process5(matrix, renderer, this.icon, iconX, iconY, iconSize, foreground);
         return bounds.getY() + bounds.getHeight();
      }
   }
}
