package ru.wexside.util;

import java.util.ArrayList;
import java.util.List;
import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.misc.BoundsProvider;
import ru.wexside.misc.CharacterInputHandler;
import ru.wexside.misc.GuiRenderable;
import ru.wexside.misc.KeyPressHandler;
import ru.wexside.misc.LabeledSegmentedControl;
import ru.wexside.misc.LayoutUpdater;
import ru.wexside.misc.ModuleKeybindEntryFactory;
import ru.wexside.misc.MouseButtonHandler;
import ru.wexside.misc.MouseScrollHandler;
import ru.wexside.misc.SettingsListLayout;
import ru.wexside.module.ModuleCategory;
import ru.wexside.module.ModuleManager;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.GuiElement;

public final class KeybindsPanel
   extends GuiElement
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider {
   private final TwoColumnLayout twoColumnLayout;
   private final ScrollController scrollController = new ScrollController(18.0F, 30.0F);
   private final List<ModuleKeybindGroup> moduleGroups;
   private final KeybindFilter keybindFilter;
   private final LabeledSegmentedControl labeledSegmentedControl;
   private final Scrollbar scrollbar = new Scrollbar();

   public KeybindsPanel(GuiBounds bounds2, ModuleManager moduleManager) {
      super(bounds2);
      this.labeledSegmentedControl = new LabeledSegmentedControl("Л", "Все кейбинды", "Активные кейбинды");
      this.keybindFilter = new KeybindFilter();
      this.moduleGroups = ModuleKeybindEntryFactory.process2(moduleManager);
      this.twoColumnLayout = new TwoColumnLayout(2, 6.0F, 6.0F);
      this.addChild(this.labeledSegmentedControl);
      this.addChild(this.keybindFilter);

      for(ModuleKeybindGroup moduleKeybindGroup : this.moduleGroups) {
         this.addChild(moduleKeybindGroup);
      }
   }

   @Override
   public void onMouseScroll(int n, int n2, double d) {
      if (this.getBounds().contains((float)n, (float)n2)) {
         this.scrollController.scrollByWheel(d, this.getFloatType());
      }
   }

   @Override
   public void update() {
   }

   @Override
   public boolean onMousePressed(int n, int n2, int n3) {
      if (n3 == 0 && this.getBounds().contains((float)n, (float)n2)) {
         if (this.scrollbar.onMousePressed(n, n2, n3)) {
            return true;
         } else if (this.labeledSegmentedControl.onMousePressed(n, n2, n3)) {
            return true;
         } else if (this.keybindFilter.onMousePressed(n, n2, n3)) {
            return true;
         } else {
            for(ModuleKeybindGroup moduleKeybindGroup : this.getTabs()) {
               if (moduleKeybindGroup.onMousePressed(n, n2, n3)) {
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
      GuiBounds bounds2 = this.getBounds();
      GuiDrawApi drawApi = WexSideClient.getGuiRenderer();
      this.layoutTabs(bounds2);
      this.labeledSegmentedControl.render(f, matrix4f);
      this.keybindFilter.render(f, matrix4f);
      boolean bl = this.isActive();
      List<ModuleKeybindGroup> list = this.process5(bl);
      SettingsListLayout settingsListLayout = new SettingsListLayout(list, f, bl);
      float f2 = bounds2.getY() + 8.0F + this.labeledSegmentedControl.getBounds().getHeight() + 6.0F;
      float f3 = this.getFloatType();
      this.scrollController.update(f3, this.twoColumnLayout.process2(settingsListLayout) + 8.0F);
      drawApi.beginStencil(1);
      drawApi.drawRoundedRectangleRadii(
         matrix4f, bounds2.getX() + 1.0F, f2, bounds2.getWidth() - 1.5F, f3 - 0.5F, 10.5F, 0.0F, 0.0F, 0.0F, ColorUtils.rgba(0, 0, 0, 45)
      );
      drawApi.applyStencilMask(1);
      float f4 = bounds2.getX() + 8.0F;
      float f5 = bounds2.getWidth() - 16.0F;
      float f6 = this.twoColumnLayout
         .process(
            settingsListLayout, matrix4f, f4, f2, f5, this.scrollController.getOffset(), bounds2.getY() - 1.0F, bounds2.getY() + bounds2.getHeight() + 1.0F
         );
      drawApi.endStencil();
      this.scrollController.setContentHeight(f3, f6 <= 0.0F ? 0.0F : f6 + 8.0F);
      this.scrollbar.process(drawApi, matrix4f, bounds2.getX() + bounds2.getWidth(), f2, f3, this.scrollController, this.getLastMouseX(), this.getLastMouseY());
      return bounds2.getY() + bounds2.getHeight();
   }

   @Override
   public void update2() {
      this.scrollController.scrollToTop();
   }

   private static boolean process4(ModuleCategory moduleCategory, List<String> list) {
      for(String string : list) {
         if (moduleCategory.getName().equalsIgnoreCase(string)) {
            return true;
         }
      }

      return false;
   }

   private List<ModuleKeybindGroup> getTabs() {
      return this.process5(this.isActive());
   }

   private List<ModuleKeybindGroup> process5(boolean bl) {
      List<String> list = this.keybindFilter.getList();
      boolean bl2 = list != null && !list.isEmpty();
      ArrayList<ModuleKeybindGroup> arrayList = new ArrayList<>(this.moduleGroups.size());

      for(ModuleKeybindGroup moduleKeybindGroup : this.moduleGroups) {
         if ((!bl2 || process4(moduleKeybindGroup.getModule().getCategory(), list)) && moduleKeybindGroup.process6(bl)) {
            arrayList.add(moduleKeybindGroup);
         }
      }

      return arrayList;
   }

   private float getFloatType() {
      return Math.max(0.0F, this.getBounds().getHeight() - 8.0F - this.labeledSegmentedControl.getBounds().getHeight() - 6.0F);
   }

   private void layoutTabs(GuiBounds bounds2) {
      this.labeledSegmentedControl.getBounds().setPosition(bounds2.getX() + 8.0F, bounds2.getY() + 8.0F);
      GuiBounds bounds3 = this.keybindFilter.getBounds();
      bounds3.setPosition(bounds2.getX() + bounds2.getWidth() - 8.0F - bounds3.getWidth(), bounds2.getY() + 8.0F);
   }

   private boolean isActive() {
      return this.labeledSegmentedControl.getIntType2() == 1;
   }
}
