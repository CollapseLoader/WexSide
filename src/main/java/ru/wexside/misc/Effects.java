package ru.wexside.misc;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
import net.minecraft.class_10799;
import net.minecraft.class_1291;
import net.minecraft.class_1293;
import net.minecraft.class_1294;
import net.minecraft.class_310;
import net.minecraft.class_329;
import net.minecraft.class_6880;
import net.minecraft.class_746;
import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.render.IconAtlasEntry;
import ru.wexside.setting.ModeSetting;
import ru.wexside.setting.ModeSettingBuilder;
import ru.wexside.util.AbstractHudElement;
import ru.wexside.util.ColorUtils;
import ru.wexside.util.GuiDrawApi;

public final class Effects extends AbstractHudElement {
   private static final float MINIMUM_WIDTH = 110.0F;
   private static final float HEADER_HEIGHT = 18.0F;
   private static final float ROW_HEIGHT = 10.5F;
   private static final float CARD_HEIGHT = 23.0F;
   private final ModeSetting displayMode;
   private final Map<String, Effects.AnimatedEffect> animatedEffects = new LinkedHashMap<>();
   private float animatedWidth = 110.0F;
   private float animatedHeight = 18.0F;

   public Effects(BooleanSupplier visible) {
      super("Effects", visible);
      this.displayMode = ((ModeSettingBuilder)((ModeSettingBuilder)ModeSetting.getModeSettingBuilder().id("display")).name("Вид"))
         .options("Карточки", "Панель")
         .defaultOption("Карточки")
         .build();
      this.getHudElementConfig().addSetting(this.displayMode);
   }

   @Override
   protected float getWidth() {
      return this.animatedWidth;
   }

   @Override
   protected float getHeight() {
      return this.animatedHeight;
   }

   @Override
   protected boolean isContentVisible() {
      class_746 player = class_310.method_1551().field_1724;
      return player != null && !player.method_6026().isEmpty();
   }

   @Override
   protected void updateLayout() {
      this.synchronizeEffects();
      boolean cards = this.isCardMode();
      float targetWidth = cards ? 0.0F : this.titleWidth();
      float rowsHeight = 0.0F;
      this.animatedEffects.values().removeIf(effectx -> {
         effectx.updateAnimation();
         if (!effectx.isExpired()) {
            return false;
         } else {
            effectx.close();
            return true;
         }
      });

      for(Effects.AnimatedEffect effect : this.animatedEffects.values()) {
         if (!(effect.getVisibility() <= 0.001F)) {
            targetWidth = Math.max(targetWidth, effect.getWidth(cards));
            rowsHeight += effect.getVisibility() * ((cards ? 23.0F : 10.5F) + 3.0F);
         }
      }

      if (cards) {
         this.animatedHeight = FrameInterpolator.lerpTowards(this.animatedHeight, Math.max(0.0F, rowsHeight - 3.0F), 30.0F);
      } else {
         float targetHeight = 18.0F + (rowsHeight > 0.0F ? 4.5F + rowsHeight + 2.0F : 0.0F);
         targetWidth = Math.max(110.0F, targetWidth);
         this.animatedHeight = FrameInterpolator.lerpTowards(this.animatedHeight, targetHeight, 30.0F);
      }

      this.animatedWidth = FrameInterpolator.lerpTowards(this.animatedWidth, Math.max(cards ? 1.0F : 110.0F, targetWidth), 30.0F);
      this.collectIconBakes();
   }

   @Override
   protected void renderContent(GuiDrawApi renderer, Matrix4f matrix, float x, float y, float width, float height, float scale) {
      if (this.isCardMode()) {
         this.renderCards(renderer, matrix, x, y, width, scale);
      } else {
         this.renderPanelSurface(renderer, matrix, x, y, width, height, 8.0F, scale);
         renderer.beginStencil(1);
         renderer.drawRoundedRectangle(matrix, x, y, width, height, 8.0F * scale, -1);
         renderer.applyStencilMask(1);
         int titleColor = ThemeColors.hudTextPrimary();
         FontRegistry.font7.process2(matrix, renderer, "Effects", x + 5.0F * scale, y + 4.5F * scale, 8.0F * scale, titleColor);
         FontRegistry.font3.process5(matrix, renderer, "u", x + width - 13.0F * scale, y + 6.0F * scale, 8.0F * scale, titleColor);
         float rowY = y + 22.5F * scale;

         for(Effects.AnimatedEffect effect : this.animatedEffects.values()) {
            float visibility = effect.getVisibility();
            if (!(visibility <= 0.001F)) {
               effect.renderPanelRow(renderer, matrix, x, rowY, width, scale, visibility);
               rowY += visibility * 13.5F * scale;
            }
         }

         renderer.endStencil();
      }
   }

   private void renderCards(GuiDrawApi renderer, Matrix4f matrix, float x, float y, float width, float scale) {
      boolean alignRight = x + width * 0.5F > (float)class_310.method_1551().method_22683().method_4486() * 0.5F;
      float rowY = y;

      for(Effects.AnimatedEffect effect : this.animatedEffects.values()) {
         float visibility = effect.getVisibility();
         if (!(visibility <= 0.001F)) {
            float cardWidth = effect.getWidth(true) * scale;
            float cardX = alignRight ? x + width - cardWidth : x;
            effect.renderCard(renderer, matrix, cardX, rowY, cardWidth, scale, visibility);
            rowY += visibility * 26.0F * scale;
         }
      }
   }

   private void synchronizeEffects() {
      for(Effects.AnimatedEffect effect : this.animatedEffects.values()) {
         effect.setPresent(false);
      }

      List<class_1293> effects = this.collectEffects();
      if (effects.isEmpty() && this.isEditorScreen()) {
         effects = this.previewEffects();
      }

      for(class_1293 effect : effects) {
         String key = effect.method_5579().method_55840() + "#" + effect.method_5578();
         this.animatedEffects.compute(key, (ignored, animated) -> {
            if (animated == null) {
               return new Effects.AnimatedEffect(this, effect);
            } else {
               animated.setEffect(effect);
               animated.setPresent(true);
               return animated;
            }
         });
      }
   }

   private List<class_1293> collectEffects() {
      class_746 player = class_310.method_1551().field_1724;
      if (player == null) {
         return List.of();
      } else {
         List<class_1293> effects = new ArrayList();

         for(class_1293 effect : player.method_6026()) {
            effects.add(new class_1293(effect));
         }

         effects.sort(Comparator.comparing(effectx -> ((class_1291)effectx.method_5579().comp_349()).method_5560().getString()));
         return effects;
      }
   }

   private List<class_1293> previewEffects() {
      return List.of(
         new class_1293(class_1294.field_5904, -1, 2),
         new class_1293(class_1294.field_5910, -1, 2),
         new class_1293(class_1294.field_5924, 1880, 0),
         new class_1293(class_1294.field_5918, -1, 2)
      );
   }

   private boolean isCardMode() {
      return "Карточки".equals(this.displayMode.getSelectedOption());
   }

   private float titleWidth() {
      return 5.0F + FontRegistry.font7.process3("Effects", 8.0F) + 19.0F;
   }

   private static String effectName(class_1293 effect) {
      class_1291 type = (class_1291)effect.method_5579().comp_349();
      String amplifier = effect.method_5578() > 0 ? " " + amplifierText(effect.method_5578() + 1) : "";
      return type.method_5560().getString() + amplifier;
   }

   private static String amplifierText(int level) {
      return switch(level) {
         case 2 -> "II";
         case 3 -> "III";
         case 4 -> "IV";
         case 5 -> "V";
         case 6 -> "VI";
         case 7 -> "VII";
         case 8 -> "VIII";
         case 9 -> "IX";
         case 10 -> "X";
         default -> Integer.toString(level);
      };
   }

   private static String effectDuration(class_1293 effect) {
      if (effect.method_48559()) {
         return "∞";
      } else {
         int seconds = Math.max(0, effect.method_5584() / 20);
         return "%d:%02d".formatted(seconds / 60, seconds % 60);
      }
   }

   private void collectIconBakes() {
      float framebufferScale = (float)class_310.method_1551().method_22683().method_4495();
      List<BakedIconEntry> bakes = new ArrayList<>();

      for(Effects.AnimatedEffect effect : this.animatedEffects.values()) {
         effect.collectIconBake(framebufferScale, bakes);
      }

      if (!bakes.isEmpty()) {
         WexSideClient.getRenderPipeline2().setList(bakes);
      }
   }

   private final class AnimatedEffect {
      private class_1293 effect;
      private boolean present = true;
      private float visibility = 1.0F;
      private final IconAtlasEntry iconTexture = new IconAtlasEntry(true);

      private AnimatedEffect(final Effects param1, class_1293 effect) {
         this.effect = effect;
      }

      private void setEffect(class_1293 effect) {
         this.effect = effect;
      }

      private void setPresent(boolean present) {
         this.present = present;
      }

      private void updateAnimation() {
         this.visibility = FrameInterpolator.lerpTowards(this.visibility, this.present ? 1.0F : 0.0F, 30.0F);
      }

      private boolean isExpired() {
         return !this.present && this.visibility <= 0.001F;
      }

      private float getVisibility() {
         return this.visibility;
      }

      private float getWidth(boolean cards) {
         String name = Effects.effectName(this.effect);
         String duration = Effects.effectDuration(this.effect);
         return cards
            ? 30.0F + Math.max(FontRegistry.font4.process3(name, 7.0F), FontRegistry.font6.process3(duration, 6.0F))
            : 19.0F + FontRegistry.font4.process3(name, 6.5F) + FontRegistry.font6.process3(duration, 5.5F) + 12.0F;
      }

      private void renderPanelRow(GuiDrawApi renderer, Matrix4f matrix, float x, float y, float width, float scale, float alpha) {
         float slide = (1.0F - alpha) * 4.0F * scale;
         float contentX = x + slide;
         int primary = ColorUtils.multiplyAlpha(ThemeColors.hudTextPrimary(), alpha);
         int durationColor = ColorUtils.multiplyAlpha(
            ((class_1291)this.effect.method_5579().comp_349()).method_5573() ? ThemeColors.hudTextMuted() : ThemeColors.danger(), alpha
         );
         this.renderIcon(renderer, matrix, contentX + 5.0F * scale, y + 1.75F * scale, 7.0F * scale, alpha);
         String name = Effects.effectName(this.effect);
         float nameHeight = FontRegistry.font4.process4(name, 6.5F) * scale;
         FontRegistry.font4.process2(matrix, renderer, name, contentX + 15.0F * scale, y + (10.5F * scale - nameHeight) * 0.5F, 6.5F * scale, primary);
         String duration = Effects.effectDuration(this.effect);
         float durationWidth = FontRegistry.font6.process3(duration, 5.5F) * scale;
         float badgeWidth = durationWidth + 8.0F * scale;
         float badgeX = x + width - 5.0F * scale - badgeWidth + slide;
         renderer.drawRoundedRectangle(matrix, badgeX, y, badgeWidth, 10.5F * scale, 6.0F * scale, ColorUtils.multiplyAlpha(ThemeColors.controlFill(), alpha));
         renderer.drawRoundedOutline(
            matrix, badgeX, y, badgeWidth, 10.5F * scale, 6.0F * scale, scale, ColorUtils.multiplyAlpha(ThemeColors.separatorHover(), alpha)
         );
         FontRegistry.font6
            .process2(
               matrix,
               renderer,
               duration,
               badgeX + (badgeWidth - durationWidth) * 0.5F,
               y + (10.5F * scale - FontRegistry.font6.process4(duration, 5.5F) * scale) * 0.5F,
               5.5F * scale,
               durationColor
            );
      }

      private void renderCard(GuiDrawApi renderer, Matrix4f matrix, float x, float y, float width, float scale, float alpha) {
         float slide = (1.0F - alpha) * 4.0F * scale;
         x += slide;
         float height = 23.0F * scale;
         renderer.drawRoundedShadow(matrix, x, y, width, height, 14.0F * scale, 12.0F * scale, ColorUtils.rgba(0, 0, 0, Math.round(11.0F * alpha)));
         int background = ColorUtils.multiplyAlpha(ThemeColors.hudBackground(), alpha);
         int outline = ColorUtils.multiplyAlpha(ThemeColors.withHoverOverlay(ThemeColors.notificationOutline()), alpha);
         if (ThemeManager.getThemeManager().isHudBlurEnabled()) {
            renderer.drawBlurredRoundedRectangle(matrix, x, y, width, height, 7.0F * scale);
         }

         renderer.drawRoundedRectangle(matrix, x, y, width, height, 7.0F * scale, background);
         renderer.drawRoundedOutline(matrix, x, y, width, height, 7.0F * scale, scale, outline);
         this.renderIcon(renderer, matrix, x + 6.0F * scale, y + 4.5F * scale, 14.0F * scale, alpha);
         String name = Effects.effectName(this.effect);
         String duration = Effects.effectDuration(this.effect);
         float nameHeight = FontRegistry.font4.process4(name, 7.0F);
         float durationHeight = FontRegistry.font6.process4(duration, 6.0F);
         float textHeight = nameHeight + 1.0F + durationHeight;
         float textX = x + 24.0F * scale;
         float textY = y + (23.0F - textHeight) * 0.5F * scale;
         FontRegistry.font4.process2(matrix, renderer, name, textX, textY, 7.0F * scale, ColorUtils.multiplyAlpha(ThemeColors.hudTextPrimary(), alpha));
         int durationColor = ((class_1291)this.effect.method_5579().comp_349()).method_5573() ? ThemeColors.hudTextMuted() : ThemeColors.danger();
         FontRegistry.font6
            .process2(matrix, renderer, duration, textX, textY + (nameHeight + 1.0F) * scale, 6.0F * scale, ColorUtils.multiplyAlpha(durationColor, alpha));
      }

      private void collectIconBake(float framebufferScale, List<BakedIconEntry> output) {
         if (this.iconTexture.process(framebufferScale)) {
            class_6880<class_1291> effectType = this.effect.method_5579();
            output.add(
               new BakedIconEntry(
                  this.iconTexture,
                  (context, x, y, size) -> context.method_52706(class_10799.field_56883, class_329.method_71644(effectType), x, y, size, size)
               )
            );
         }
      }

      private void renderIcon(GuiDrawApi renderer, Matrix4f matrix, float x, float y, float size, float alpha) {
         if (this.iconTexture.isActive()) {
            int textureSize = this.iconTexture.getIntType();
            int texture = renderer.bindTexture(this.iconTexture.getIntType4(), textureSize, textureSize);
            renderer.drawTexture(matrix, x, y, size, size, 0.0F, 1.0F, 1.0F, 0.0F, texture, ColorUtils.rgba(255, 255, 255, Math.round(alpha * 255.0F)));
         }
      }

      private void close() {
         this.iconTexture.update2();
      }
   }
}
