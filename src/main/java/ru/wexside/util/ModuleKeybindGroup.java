package ru.wexside.util;

import java.util.ArrayList;
import java.util.List;
import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.misc.BoundsProvider;
import ru.wexside.misc.CharacterInputHandler;
import ru.wexside.misc.GuiRenderable;
import ru.wexside.misc.IconPlacement;
import ru.wexside.misc.KeyPressHandler;
import ru.wexside.misc.KeybindDescriptor;
import ru.wexside.misc.LayoutUpdater;
import ru.wexside.misc.MouseButtonHandler;
import ru.wexside.misc.MouseScrollHandler;
import ru.wexside.misc.ThemeColors;
import ru.wexside.module.Module;
import ru.wexside.module.misc.EspFeatureModule;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.GuiElement;

public final class ModuleKeybindGroup
   extends GuiElement
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider {
   private final HudIconStyle hudIconStyle;
   private final List<KeybindRow> keybindRows;
   private final Module module;

   public ModuleKeybindGroup(Module module, List<KeybindDescriptor> list) {
      super(new GuiBounds(0.0F, 0.0F, 0.0F, 0.0F));
      this.module = module;
      this.hudIconStyle = new HudIconStyle(process5(module));
      this.keybindRows = new ArrayList<>(list.size());

      for(KeybindDescriptor callback10 : list) {
         KeybindRow keybindRow = new KeybindRow(callback10);
         this.keybindRows.add(keybindRow);
         this.addChild(keybindRow);
      }
   }

   @Override
   public void onMouseScroll(int n, int n2, double d) {
   }

   @Override
   public void update() {
   }

   @Override
   public float render(float f, Matrix4f matrix4f) {
      return this.process4(f, matrix4f, false);
   }

   public float getHorizontalPadding() {
      return 7.0F;
   }

   public float process2(boolean bl) {
      float f = this.hudIconStyle.getFloatType();
      float f2 = 7.0F + f + 4.0F;
      float f3 = Math.max(0.0F, this.getBounds().getWidth() - 14.0F);
      boolean bl2 = false;

      for(KeybindRow keybindRow : this.keybindRows) {
         if (!bl || keybindRow.getCallback10().isActive()) {
            f2 += keybindRow.process2(f3);
            f2 += 4.0F;
            bl2 = true;
         }
      }

      if (bl2) {
         f2 -= 4.0F;
      }

      float var9;
      return var9 = f2 + 7.0F;
   }

   public float getItemSpacing() {
      return 4.0F;
   }

   public float getHeaderSpacing() {
      return 4.0F;
   }

   public float getCornerRadius() {
      return 8.0F;
   }

   public float process4(float f, Matrix4f matrix4f, boolean bl) {
      GuiBounds bounds2 = this.getBounds();
      GuiDrawApi drawApi = WexSideClient.getGuiRenderer();
      float f2 = this.process2(bl);
      bounds2.setSize(bounds2.getWidth(), f2);
      drawApi.drawRoundedRectangleOutlined(
         matrix4f,
         bounds2.getX(),
         bounds2.getY(),
         bounds2.getWidth(),
         f2,
         8.0F,
         0.75F,
         ColorUtils.withAlpha(ThemeColors.backgroundPrimary(), 0.0F),
         ThemeColors.borderPrimary()
      );
      float f3 = bounds2.getX() + 7.0F;
      float f4 = bounds2.getY() + 7.0F;
      this.hudIconStyle.render(matrix4f, drawApi, f3, f4, 0.0F, IconPlacement.ICON_LEFT);
      float f5 = f4 + this.hudIconStyle.getFloatType() + 4.0F;
      float f6 = bounds2.getX() + 7.0F;
      float f7 = bounds2.getWidth() - 14.0F;

      for(KeybindRow keybindRow : this.keybindRows) {
         if (!bl || keybindRow.getCallback10().isActive()) {
            float f8 = keybindRow.process2(f7);
            keybindRow.getBounds().setPosition(f6, f5);
            keybindRow.getBounds().setSize(f7, f8);
            keybindRow.render(f, matrix4f);
            f5 += f8 + 4.0F;
         }
      }

      return bounds2.getY() + f2;
   }

   private static String process5(Module module) {
      String string;
      if (module instanceof EspFeatureModule cls0919Module) {
         String string2 = cls0919Module.getString();
         string = string2 + " / ";
      } else {
         string = "";
      }

      String string4 = module.getDisplayName();
      String string6 = module.getCategory().getName();
      return (string6 + " / " + string + string4).toUpperCase();
   }

   public List<KeybindRow> getList() {
      return this.keybindRows;
   }

   public float getBackgroundOpacity() {
      return 0.75F;
   }

   public HudIconStyle getHudIconStyle() {
      return this.hudIconStyle;
   }

   public boolean process6(boolean bl) {
      if (!bl) {
         return !this.keybindRows.isEmpty();
      } else {
         for(KeybindRow keybindRow : this.keybindRows) {
            if (keybindRow.getCallback10().isActive()) {
               return true;
            }
         }

         return false;
      }
   }

   public float getVerticalPadding() {
      return 7.0F;
   }

   public Module getModule() {
      return this.module;
   }
}
