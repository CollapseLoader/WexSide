package ru.wexside.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.misc.BoundsProvider;
import ru.wexside.misc.BoundsSupplier;
import ru.wexside.misc.CharacterInputHandler;
import ru.wexside.misc.FrameInterpolator;
import ru.wexside.misc.GuiRenderable;
import ru.wexside.misc.KeyPressHandler;
import ru.wexside.misc.LayoutGroup;
import ru.wexside.misc.LayoutUpdater;
import ru.wexside.misc.MouseButtonHandler;
import ru.wexside.misc.MouseScrollHandler;
import ru.wexside.misc.PopupOwner;
import ru.wexside.misc.PositionedLayoutGroup;
import ru.wexside.misc.PreparedLayer;
import ru.wexside.misc.SettingsColumnLayout;
import ru.wexside.misc.ThemeColors;
import ru.wexside.misc.VisibilityCondition;
import ru.wexside.render.RenderFrameClock;
import ru.wexside.ui.FloatingPanelProvider;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.GuiElement;
import ru.wexside.ui.setting.SettingRow;

public final class SettingsContainer
   extends GuiElement
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider,
   BoundsSupplier {
   private SettingsColumnLayout settingsColumnLayout;
   private float value = 2.0F;
   private float value2 = 0.5F;
   private Map<String, Float> map2 = new HashMap<>();
   private Map<String, Integer> map3 = new HashMap<>();
   private float value3 = 10.0F;
   private float value4 = 2.0F;
   private float value5 = 142.5F;

   public SettingsContainer(GuiBounds bounds2) {
      super(bounds2);
      this.settingsColumnLayout = SettingsColumnLayout.SINGLE_COLUMN;
   }

   @Override
   public void onMouseScroll(int n, int n2, double d) {
      for(PositionedLayoutGroup positionedLayoutGroup : this.getList2()) {
         if (this.process11(positionedLayoutGroup.getLayoutGroup())) {
            for(GuiElement element2 : positionedLayoutGroup.getLayoutGroup().getList()) {
               element2.onMouseScroll(n, n2, d);
            }
         }
      }
   }

   @Override
   public void update() {
      for(PositionedLayoutGroup positionedLayoutGroup : this.getList2()) {
         if (this.process11(positionedLayoutGroup.getLayoutGroup())) {
            for(GuiElement element2 : positionedLayoutGroup.getLayoutGroup().getList()) {
               element2.update();
            }
         }
      }
   }

   @Override
   public boolean onMousePressed(int n, int n2, int n3) {
      GuiBounds bounds2 = this.getBounds();
      int n4 = (int)((float)n - bounds2.getX());
      int n5 = (int)((float)n2 - bounds2.getY());

      for(PositionedLayoutGroup positionedLayoutGroup : this.getList2()) {
         if (this.process11(positionedLayoutGroup.getLayoutGroup())) {
            for(GuiElement element2 : positionedLayoutGroup.getLayoutGroup().getList()) {
               if (element2.onMousePressed(n4, n5, n3)) {
                  return true;
               }
            }
         }
      }

      return bounds2.contains((float)n, (float)n2);
   }

   @Override
   public float render(float f, Matrix4f matrix4f) {
      GuiBounds bounds2 = this.getBounds();
      List<PositionedLayoutGroup> list = this.getList2();
      if (!list.isEmpty() && !(bounds2.getHeight() <= 0.01F)) {
         GuiDrawApi drawApi = WexSideClient.getGuiRenderer();
         Matrix4f matrix4f2 = new Matrix4f(matrix4f).translate(bounds2.getX(), bounds2.getY(), 0.0F);

         for(PositionedLayoutGroup positionedLayoutGroup : list) {
            this.process6(f, matrix4f2, drawApi, positionedLayoutGroup);
         }

         return bounds2.getY() + bounds2.getHeight();
      } else {
         return bounds2.getY() + bounds2.getHeight();
      }
   }

   @Override
   public void onMouseReleased(int n, int n2, int n3) {
      GuiBounds bounds2 = this.getBounds();
      int n4 = (int)((float)n - bounds2.getX());
      int n5 = (int)((float)n2 - bounds2.getY());

      for(PositionedLayoutGroup positionedLayoutGroup : this.getList2()) {
         if (this.process11(positionedLayoutGroup.getLayoutGroup())) {
            for(GuiElement element2 : positionedLayoutGroup.getLayoutGroup().getList()) {
               element2.onMouseReleased(n4, n5, n3);
            }
         }
      }
   }

   @Override
   public boolean onCharTyped(char c) {
      for(PositionedLayoutGroup positionedLayoutGroup : this.getList2()) {
         if (this.process11(positionedLayoutGroup.getLayoutGroup())) {
            for(GuiElement element2 : positionedLayoutGroup.getLayoutGroup().getList()) {
               if (element2.onCharTyped(c)) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   @Override
   public boolean onKeyPressed(int n) {
      for(PositionedLayoutGroup positionedLayoutGroup : this.getList2()) {
         if (this.process11(positionedLayoutGroup.getLayoutGroup())) {
            for(GuiElement element2 : positionedLayoutGroup.getLayoutGroup().getList()) {
               if (element2.onKeyPressed(n)) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   @Override
   public void addChild(GuiElement element2) {
      super.addChild(element2);
   }

   public void setSettingsColumnLayout(SettingsColumnLayout settingsColumnLayout) {
      this.settingsColumnLayout = settingsColumnLayout == null ? SettingsColumnLayout.SINGLE_COLUMN : settingsColumnLayout;
   }

   public boolean isActive() {
      return !this.getChildren().isEmpty();
   }

   @Override
   public void update2() {
      for(PositionedLayoutGroup positionedLayoutGroup : this.getList2()) {
         for(GuiElement element2 : positionedLayoutGroup.getLayoutGroup().getList()) {
            if (element2 instanceof SettingRow settingRow2) {
               settingRow2.resetVisibilityAnimation();
            }
         }
      }
   }

   private void process6(float f, Matrix4f matrix4f2, GuiDrawApi drawApi, PositionedLayoutGroup positionedLayoutGroup) {
      LayoutGroup layoutGroup2 = positionedLayoutGroup.getLayoutGroup();
      if (!layoutGroup2.getList().isEmpty() && !(layoutGroup2.getFloatType() <= 0.0F)) {
         float f2 = positionedLayoutGroup.getFloatType();
         float f3 = positionedLayoutGroup.getFloatType2();
         float f4 = positionedLayoutGroup.getFloatType3();
         boolean bl2 = positionedLayoutGroup.isActive();
         float f5 = this.process17(layoutGroup2, bl2);
         float f6 = this.process8(layoutGroup2, bl2);
         int n = ColorUtils.withAlpha(-1, 255.0F * layoutGroup2.getFloatType());
         boolean bl = layoutGroup2.getFloatType() > 0.0F && layoutGroup2.getFloatType() < 1.0F;
         if (!bl) {
            ClippedLayerRenderer.process(
               drawApi, matrix4f2, f2, f3, f4, f6, 0.0F, false, -1, matrix4f -> this.process9(layoutGroup2, f, matrix4f, f2, f3, f4, bl2)
            );
         } else {
            PreparedLayer preparedLayer = drawApi.prepareDedicatedLayer(matrix4f2, f2, f3, f4, f6, 0.0F);
            drawApi.beginLayerFrame(preparedLayer.getTexture());
            Matrix4f matrix4f3 = new Matrix4f(preparedLayer.getContentMatrix()).translate(preparedLayer.contentX(), preparedLayer.contentY(), 0.0F);
            this.process9(layoutGroup2, f, matrix4f3, f2, f3, f4, bl2);
            drawApi.endLayerFrame();
            float f7 = Math.min(1.0F, f5 / Math.max(f6, 1.0E-4F));
            drawApi.drawLayerTexture(
               matrix4f2,
               preparedLayer.getTexture(),
               preparedLayer.drawX(),
               preparedLayer.drawY(),
               preparedLayer.drawWidth(),
               f5,
               0.0F,
               preparedLayer.maxV() * f7,
               preparedLayer.maxU(),
               0.0F,
               n
            );
            drawApi.flushPending();
         }
      }
   }

   public float getFloatType() {
      List<PositionedLayoutGroup> list = this.getList2();
      if (list.isEmpty()) {
         return 0.0F;
      } else {
         float f = 0.0F;

         for(PositionedLayoutGroup positionedLayoutGroup : list) {
            float f2 = positionedLayoutGroup.getFloatType2() + this.process17(positionedLayoutGroup.getLayoutGroup(), positionedLayoutGroup.isActive());
            f = Math.max(f, f2);
         }

         return f + this.value4;
      }
   }

   private float process7(String string, boolean bl) {
      int n = RenderFrameClock.currentFrame();
      Integer n2 = this.map3.get(string);
      Float f2 = this.map2.get(string);
      float f = bl ? 1.0F : 0.0F;
      if (f2 == null) {
         this.map2.put(string, f);
         this.map3.put(string, n);
         return f;
      } else if (n2 != null && n2 == n) {
         return f2;
      } else {
         float f4 = FrameInterpolator.lerpTowards(f2, f, 20.0F);
         if (Math.abs(f - f4) <= 0.001F) {
            f4 = f;
         }

         this.map2.put(string, f4);
         this.map3.put(string, n);
         return f4;
      }
   }

   private float process8(LayoutGroup layoutGroup2, boolean bl) {
      float f = 0.0F;

      for(int i = 0; i < layoutGroup2.getList().size(); ++i) {
         f += this.process12(layoutGroup2.getList().get(i));
         if (i < layoutGroup2.getList().size() - 1) {
            f += this.value;
         }
      }

      if (bl) {
         f += this.value;
      }

      return f;
   }

   private void process9(LayoutGroup layoutGroup2, float f, Matrix4f matrix4f, float f2, float f3, float f4, boolean bl) {
      int n = ColorUtils.withAlpha(ThemeColors.borderPrimary(), 255.0F * layoutGroup2.getFloatType());
      float f5 = bl ? this.value : 0.0F;
      if (bl) {
         float f7 = (f4 - this.value5) / 2.0F;
         float f8 = (f5 - this.value2) / 2.0F;
         WexSideClient.getGuiRenderer().fillRectangle(matrix4f, f7, f8, this.value5, this.value2, n);
      }

      for(int i = 0; i < layoutGroup2.getList().size(); ++i) {
         GuiElement element2 = layoutGroup2.getList().get(i);
         GuiBounds bounds2 = element2.getBounds();
         float f9 = bounds2.getX();
         float f10 = bounds2.getY();
         float f11 = bounds2.getWidth();
         float f12 = bounds2.getHeight();
         float f13 = this.process12(element2);
         float f14 = f9 - f2;
         float f15 = f10 - f3;
         bounds2.setPosition(f14, f15);
         bounds2.setSize(f4, f13);
         element2.render(f, matrix4f);
         if (i < layoutGroup2.getList().size() - 1) {
            float f16 = (f4 - this.value5) / 2.0F;
            float f17 = f15 + f13 + (this.value - this.value2) / 2.0F;
            WexSideClient.getGuiRenderer().fillRectangle(matrix4f, f16, f17, this.value5, this.value2, n);
         }

         bounds2.setPosition(f9, f10);
         bounds2.setSize(f11, f12);
         if (element2 instanceof SettingRow settingRow2) {
            settingRow2.refreshLayout();
            settingRow2.updateLayoutState();
         }
      }
   }

   private List<PositionedLayoutGroup> process10(List<LayoutGroup> list) {
      ArrayList<PositionedLayoutGroup> arrayList = new ArrayList<>(list.size());
      float f = Math.max(0.0F, (this.getBounds().getWidth() - this.value3) / 2.0F);
      float f2 = f + this.value3;
      float f3 = 0.0F;
      float f4 = 0.0F;
      boolean bl = false;
      boolean bl2 = false;

      for(int i = 0; i < list.size(); ++i) {
         LayoutGroup layoutGroup2 = list.get(i);
         boolean bl3 = (i & 1) == 0;
         float f5 = bl3 ? 0.0F : f2;
         float f6 = bl3 ? f3 : f4;
         boolean bl4 = bl3 ? bl : bl2;
         float f7 = f6 + this.process18(layoutGroup2, bl4);
         this.process15(layoutGroup2, f5, f7, f);
         arrayList.add(new PositionedLayoutGroup(layoutGroup2, f5, f6, f, bl4));
         float f8 = this.process17(layoutGroup2, bl4);
         if (bl3) {
            f3 += f8;
            bl = true;
         } else {
            f4 += f8;
            bl2 = true;
         }
      }

      return arrayList;
   }

   private void closeNestedPopups(GuiElement element2) {
      PopupOwner popupProvider;
      if (element2 instanceof PopupOwner && (popupProvider = (PopupOwner)element2).getPopup() != null) {
         popupProvider.getPopup().setBooleanType(false);
      }

      FloatingPanelProvider panelProvider;
      if (element2 instanceof FloatingPanelProvider && (panelProvider = (FloatingPanelProvider)element2).getFloatingPanel() != null) {
         panelProvider.getFloatingPanel().setBooleanType(false);
      }

      for(GuiElement element3 : element2.getChildren()) {
         this.closeNestedPopups(element3);
      }
   }

   private boolean process11(LayoutGroup layoutGroup2) {
      if (layoutGroup2.getList().isEmpty()) {
         return false;
      } else {
         GuiElement element2 = layoutGroup2.getList().getFirst();
         if (!(element2 instanceof SettingRow)) {
            return true;
         } else {
            return layoutGroup2.isActive() && layoutGroup2.getFloatType() >= 1.0F;
         }
      }
   }

   private float process12(GuiElement element2) {
      return element2 instanceof SettingRow settingRow2 ? settingRow2.getFloatType2() : Math.max(11.0F, element2.getBounds().getHeight());
   }

   private void resetHiddenElement(GuiElement element2) {
      element2.update2();
      element2.getBounds().setPosition(0.0F, 0.0F);
      element2.getBounds().setSize(0.0F, 0.0F);
      this.closeNestedPopups(element2);
   }

   private boolean process13(GuiElement element2, VisibilityCondition visibilityCondition) {
      if (!(element2 instanceof SettingRow)) {
         return false;
      } else {
         SettingRow settingRow2 = (SettingRow)element2;
         VisibilityCondition visibilityCondition2 = settingRow2.getSetting().getVisibilityCondition();
         return visibilityCondition2 == null ? false : visibilityCondition.getString().equals(visibilityCondition2.getString());
      }
   }

   private List<PositionedLayoutGroup> process14(List<LayoutGroup> list) {
      ArrayList<PositionedLayoutGroup> arrayList = new ArrayList<>(list.size());
      float f = this.getBounds().getWidth();
      float f2 = 0.0F;

      for(int i = 0; i < list.size(); ++i) {
         LayoutGroup layoutGroup2 = list.get(i);
         boolean bl = i > 0;
         float f3 = f2 + this.process18(layoutGroup2, bl);
         this.process15(layoutGroup2, 0.0F, f3, f);
         arrayList.add(new PositionedLayoutGroup(layoutGroup2, 0.0F, f2, f, bl));
         f2 += this.process17(layoutGroup2, bl);
      }

      return arrayList;
   }

   private List<LayoutGroup> buildLayoutGroups() {
      ArrayList<LayoutGroup> arrayList = new ArrayList<>();
      int n = 0;

      while(n < this.children.size()) {
         GuiElement element2 = this.children.get(n);
         if (!(element2 instanceof SettingRow)) {
            arrayList.add(new LayoutGroup(List.of(element2), 1.0F, true, false));
            ++n;
         } else {
            SettingRow iiIllIIlII2 = (SettingRow)element2;
            VisibilityCondition visibilityCondition = iiIllIIlII2.getSetting().getVisibilityCondition();
            if (visibilityCondition == null) {
               LayoutGroup object2 = this.process16(element2, iiIllIIlII2);
               if (object2 != null) {
                  arrayList.add(object2);
               }

               ++n;
            } else {
               ArrayList<GuiElement> groupedRows = new ArrayList<>();

               int n2;
               for(n2 = n; n2 < this.children.size() && this.process13(this.children.get(n2), visibilityCondition); ++n2) {
                  groupedRows.add(this.children.get(n2));
               }

               if (groupedRows.size() <= 1) {
                  LayoutGroup layoutGroup2 = this.process16(element2, iiIllIIlII2);
                  if (layoutGroup2 != null) {
                     arrayList.add(layoutGroup2);
                  }

                  ++n;
               } else {
                  boolean bl = false;

                  for(GuiElement groupedRow : groupedRows) {
                     SettingRow settingRow = (SettingRow)groupedRow;
                     if (!settingRow.isTargetVisible()) {
                        this.closeNestedPopups(groupedRow);
                     } else {
                        bl = true;
                     }
                  }

                  float f = this.process7(visibilityCondition.getString(), bl);
                  if (!bl && !(f > 0.0F)) {
                     for(GuiElement groupedRow : groupedRows) {
                        this.resetHiddenElement(groupedRow);
                     }
                  } else {
                     arrayList.add(new LayoutGroup(List.copyOf(groupedRows), f, bl, true));
                  }

                  n = n2;
               }
            }
         }
      }

      return arrayList;
   }

   private void process15(LayoutGroup layoutGroup2, float f, float f2, float f3) {
      float f4 = f2;

      for(int i = 0; i < layoutGroup2.getList().size(); ++i) {
         GuiElement element2 = layoutGroup2.getList().get(i);
         float f5 = this.process12(element2);
         element2.getBounds().setPosition(f, f4);
         element2.getBounds().setSize(f3, f5);
         if (element2 instanceof SettingRow settingRow2) {
            settingRow2.updateLayoutState();
         }

         f4 += f5;
         if (i < layoutGroup2.getList().size() - 1) {
            f4 += this.value;
         }
      }
   }

   private List<PositionedLayoutGroup> getList2() {
      List<LayoutGroup> list = this.buildLayoutGroups();
      if (list.isEmpty()) {
         return List.of();
      } else {
         return this.settingsColumnLayout == SettingsColumnLayout.TWO_COLUMNS ? this.process10(list) : this.process14(list);
      }
   }

   private LayoutGroup process16(GuiElement element2, SettingRow<?> settingRow2) {
      if (!settingRow2.isTargetVisible()) {
         this.closeNestedPopups(element2);
      }

      if (!settingRow2.shouldRemainInLayout()) {
         this.resetHiddenElement(element2);
         return null;
      } else {
         return new LayoutGroup(List.of(element2), settingRow2.visibilityProgress(), settingRow2.isTargetVisible(), false);
      }
   }

   private float process17(LayoutGroup layoutGroup2, boolean bl) {
      return this.process8(layoutGroup2, bl) * layoutGroup2.getFloatType();
   }

   private float process18(LayoutGroup layoutGroup2, boolean bl) {
      return bl ? this.value * layoutGroup2.getFloatType() : 0.0F;
   }
}
