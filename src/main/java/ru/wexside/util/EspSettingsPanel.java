package ru.wexside.util;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.misc.BoundsProvider;
import ru.wexside.misc.CharacterInputHandler;
import ru.wexside.misc.ContainerDisplay;
import ru.wexside.misc.EspRelationSelector;
import ru.wexside.misc.FontRegistry;
import ru.wexside.misc.FrameInterpolator;
import ru.wexside.misc.GuiRenderable;
import ru.wexside.misc.KeyPressHandler;
import ru.wexside.misc.LayoutUpdater;
import ru.wexside.misc.MouseButtonHandler;
import ru.wexside.misc.MouseScrollHandler;
import ru.wexside.misc.ThemeColors;
import ru.wexside.model.esp.EspRelation;
import ru.wexside.model.esp.EspTargetType;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleManager;
import ru.wexside.render.EspPreviewRenderer;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.GuiElement;

public final class EspSettingsPanel
   extends GuiElement
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider {
   private final String string2;
   private final String string3;
   private final ModuleManager moduleManager;
   private final GuiBounds bounds3;
   private float value11 = 1.0F;
   private final EnumMap<EspTargetType, EnumMap<EspRelation, List<ModuleCard>>> enumMap;
   private final SegmentedControl segmentedControl;
   private final EspPreviewRenderer entity;
   private EspTargetType espTargetType;
   private final ScrollController scrollController;
   private final EspRelationSelector espRelationSelector;
   private final int slot;
   private final ContainerDisplay containerDisplay = new ContainerDisplay();
   private final GuiBounds bounds4;
   private Module pendingModuleScroll;

   public EspSettingsPanel(GuiBounds bounds3, String string, ModuleManager moduleManager, ContainerDisplay containerDisplay) {
      super(bounds3);
      this.scrollController = new ScrollController(18.0F, 30.0F);
      this.entity = new EspPreviewRenderer();
      this.espRelationSelector = new EspRelationSelector();
      this.string2 = "Вращайте модель зажав на ней ЛКМ";
      this.slot = ColorUtils.rgba(167, 168, 174, 255);
      this.bounds3 = new GuiBounds(0.0F, 0.0F, 0.0F, 0.0F);
      this.bounds4 = new GuiBounds(0.0F, 0.0F, 0.0F, 0.0F);
      this.enumMap = new EnumMap<>(EspTargetType.class);
      this.espTargetType = EspTargetType.PLAYERS;
      this.string3 = string;
      this.moduleManager = moduleManager;
      SegmentedControlStyle segmentedControlStyle = new SegmentedControlStyle()
         .process2(180.0F / (float)EspTargetType.values().length)
         .process11(12.0F)
         .process12(8.0F)
         .process7(6.0F)
         .process9(6.5F)
         .process5(2.5F);
      this.segmentedControl = new SegmentedControl(new GuiBounds(0.0F, 0.0F, 180.0F, 12.0F), segmentedControlStyle);

      for(EspTargetType espTargetType : EspTargetType.values()) {
         this.segmentedControl.process4(espTargetType.getTitle(), espTargetType.getIcon());
      }

      this.segmentedControl.setIntConsumer(this::setIntType);
      this.segmentedControl.setIntType2(0);
      this.espRelationSelector.setConsumer(espRelation -> this.update4());
      this.update3();
   }

   @Override
   public void onMouseScroll(int n, int n2, double d) {
      if (this.bounds4.contains((float)n, (float)n2) && this.getModuleCards() != null) {
         this.scrollController.scrollByWheel(d, this.bounds4.getHeight());
      }
   }

   @Override
   public void update() {
      this.segmentedControl.update();
      List<ModuleCard> list = this.getModuleCards();
      if (list != null) {
         for(ModuleCard moduleCard : list) {
            moduleCard.update();
         }
      }
   }

   @Override
   public boolean onMousePressed(int n, int n2, int n3) {
      if (n3 != 0 || !this.getBounds().contains((float)n, (float)n2)) {
         return false;
      } else if (this.segmentedControl.onMousePressed(n, n2, n3)) {
         return true;
      } else if (this.isActive() && this.espRelationSelector.process(n, n2, n3)) {
         return true;
      } else if (this.bounds3.contains((float)n, (float)n2)) {
         this.entity.beginRotation(n, n2, this.bounds3);
         return true;
      } else {
         List<ModuleCard> list = this.getModuleCards();
         if (list != null && this.bounds4.contains((float)n, (float)n2)) {
            for(ModuleCard moduleCard : list) {
               if (moduleCard.onMousePressed(n, n2, n3)) {
                  return true;
               }
            }
         }

         return this.getBounds().contains((float)n, (float)n2);
      }
   }

   @Override
   public float render(float f, Matrix4f matrix4f) {
      GuiBounds bounds3 = this.getBounds();
      GuiDrawApi drawApi = WexSideClient.getGuiRenderer();
      float f5 = bounds3.getX() + 8.0F;
      float f6 = bounds3.getY() + 8.0F;
      float f7 = 162.5F;
      float f8 = f5 + f7 + 6.5F;
      float f9 = Math.max(0.0F, bounds3.getX() + bounds3.getWidth() - 8.0F - f8);
      float f11 = bounds3.getY() + bounds3.getHeight() - 8.0F;
      this.bounds3.setPosition(f8 + 6.0F, f6 + 24.0F);
      this.bounds3.setSize(f9 - 12.0F, f11 - f6 - 30.0F);
      this.entity.updatePreviewRotation();
      float f12 = Math.min(this.bounds3.getWidth(), this.bounds3.getHeight());
      this.entity.requestPreviewRender(this.espTargetType, this.espRelationSelector.getEspRelation(), f12 * Math.max(1.0F, Math.abs(matrix4f.m00())));
      float f13 = Math.min(f7, 180.0F);
      this.segmentedControl.getSegmentedControlStyle().process2(f13 / (float)EspTargetType.values().length);
      this.segmentedControl.getBounds().setPosition(f5, f6);
      this.segmentedControl.getBounds().setSize(f13, 12.0F);
      this.segmentedControl.render(f, matrix4f);
      float f14 = f6 + 12.0F + 6.0F;
      float f15 = bounds3.getY() + bounds3.getHeight() - f14;
      this.bounds4.setPosition(f5, f14);
      this.bounds4.setSize(f7, Math.max(0.0F, f15));
      List<ModuleCard> list = this.getModuleCards();
      if (list != null && !list.isEmpty() && f15 > 0.0F) {
         if (this.pendingModuleScroll != null) {
            Module pending = this.pendingModuleScroll;
            this.pendingModuleScroll = null;
            this.process8(pending);
         }
         float f4 = this.process6(list);
         this.scrollController.update(f15, f4);
         drawApi.beginStencil(1);
         drawApi.drawRoundedRectangle(matrix4f, f5, f14, f7, f15, 0.0F, ColorUtils.rgba(0, 0, 0, 0));
         drawApi.applyStencilMask(1);
         float f3 = this.scrollController.getOffset();
         float f2;
         float f16 = f2 = f14 + f3;
         float visibleTop = f14 - 1.0F;
         float visibleBottom = f14 + f15 + 1.0F;

         for(ModuleCard moduleCard : list) {
            moduleCard.getBounds().setPosition(f5, f2);
            moduleCard.getBounds().setSize(f7, moduleCard.getBounds().getHeight());
            float cardTop = moduleCard.getBounds().getY();
            float cardBottom = cardTop + moduleCard.getBounds().getHeight();
            if (cardBottom >= visibleTop && cardTop <= visibleBottom) {
               f16 = moduleCard.render(f, matrix4f);
            } else {
               f16 = cardBottom;
            }
            f2 = f16 + 4.0F;
         }

         drawApi.endStencil();
         float f17 = f16 - f3 - f14 + 8.0F;
         this.scrollController.setContentHeight(f15, Math.max(0.0F, f17));
      }

      drawApi.drawRoundedRectangleOutlined(matrix4f, f8, f6, f9, f11 - f6, 8.0F, 0.75F, ColorUtils.rgba(0, 0, 0, 0), ThemeColors.borderPrimary());
      float f4 = f6 + 13.25F;
      float f3 = FontRegistry.font2.process4("ESP Preview", 6.75F);
      FontRegistry.font2.process2(matrix4f, drawApi, "ESP Preview", f8 + 7.5F, f4 - f3 / 2.0F, 6.75F, ThemeColors.textSecondary());
      this.entity.render(drawApi, matrix4f, this.bounds3);
      this.value11 = FrameInterpolator.lerpTowards(this.value11, this.isActive() ? 1.0F : 0.0F, 20.0F);
      this.espRelationSelector.process3(f8 + f9 - 7.0F - this.espRelationSelector.enabled(), f4 - 5.75F);
      this.espRelationSelector.process2(f, matrix4f, this.value11);
      float f2 = FontRegistry.font2.process4("Вращайте модель зажав на ней ЛКМ", 6.1F);
      FontRegistry.font2.process2(matrix4f, drawApi, "Вращайте модель зажав на ней ЛКМ", f8 + 7.5F, f11 - 10.25F - f2 / 2.0F, 6.1F, this.slot);
      return bounds3.getY() + bounds3.getHeight();
   }

   @Override
   public void onMouseReleased(int n, int n2, int n3) {
      List<ModuleCard> list = this.getModuleCards();
      if (list != null) {
         for(ModuleCard moduleCard : list) {
            moduleCard.onMouseReleased(n, n2, n3);
         }
      }
   }

   @Override
   public boolean onCharTyped(char c) {
      List<ModuleCard> list = this.getModuleCards();
      if (list != null) {
         for(ModuleCard moduleCard : list) {
            if (moduleCard.onCharTyped(c)) {
               return true;
            }
         }
      }

      return false;
   }

   @Override
   public void update2() {
      this.scrollController.scrollToTop();
   }

   public void resetPreview() {
      super.update2();
      this.entity.close();
   }

   @Override
   public boolean onKeyPressed(int n) {
      List<ModuleCard> list = this.getModuleCards();
      if (list != null) {
         for(ModuleCard moduleCard : list) {
            if (moduleCard.onKeyPressed(n)) {
               return true;
            }
         }
      }

      return false;
   }

   private void update3() {
      EspFeatureRegistry espFeatures = WexSideClient.getEspFeatureRegistry();
      if (espFeatures != null) {
         for(EspTargetType espTargetType : EspTargetType.values()) {
            EnumMap<EspRelation, List<ModuleCard>> enumMap = new EnumMap<>(EspRelation.class);
            if (espTargetType == EspTargetType.PLAYERS) {
               for(EspRelation espRelation : EspRelation.values()) {
                  enumMap.put(espRelation, this.process7(espFeatures.getModules(espTargetType, espRelation)));
               }
            } else {
               List<ModuleCard> list = this.process7(espFeatures.getModules(espTargetType, EspRelation.DEFAULT));

               for(EspRelation espRelation : EspRelation.values()) {
                  enumMap.put(espRelation, list);
               }
            }

            this.enumMap.put(espTargetType, enumMap);
         }
      }
   }

   private List<ModuleCard> getModuleCards() {
      EnumMap<EspRelation, List<ModuleCard>> enumMap = this.enumMap.get(this.espTargetType);
      return enumMap == null ? null : enumMap.get(this.espRelationSelector.getEspRelation());
   }

   private void setEspTargetType(EspTargetType espTargetType) {
      EnumMap<EspRelation, List<ModuleCard>> enumMap = this.enumMap.get(espTargetType);
      if (enumMap != null) {
         for(List<ModuleCard> list : enumMap.values()) {
            for(ModuleCard moduleCard : list) {
               moduleCard.update2();
            }
         }
      }

      this.scrollController.scrollToTop();
   }

   private float process6(List<ModuleCard> list) {
      float f = 0.0F;

      for(ModuleCard moduleCard : list) {
         f += moduleCard.getBounds().getHeight() + 4.0F;
      }

      return Math.max(0.0F, f - 4.0F) + 8.0F;
   }

   private boolean isActive() {
      return this.espTargetType == EspTargetType.PLAYERS;
   }

   private void setIntType(int n) {
      EspTargetType espTargetType = this.espTargetType;
      EspTargetType[] cls0277Array = EspTargetType.values();
      if (n >= 0 && n < cls0277Array.length) {
         this.espTargetType = cls0277Array[n];
      }

      if (!this.isActive()) {
         this.espRelationSelector.setEspRelation(EspRelation.DEFAULT);
      }

      this.setEspTargetType(espTargetType);
   }

   private List<ModuleCard> process7(List<Module> list) {
      ArrayList<ModuleCard> arrayList = new ArrayList<>();

      for(Module module : list) {
         ModuleCard moduleCard = new ModuleCard(new GuiBounds(0.0F, 0.0F, 0.0F, 0.0F), module, this.containerDisplay);
         arrayList.add(moduleCard);
         this.addChild(moduleCard);
      }

      return arrayList;
   }

   private void update4() {
      this.setEspTargetType(this.espTargetType);
      this.scrollController.scrollToTop();
   }

   public void setModule(Module module) {
      EspFeatureRegistry espFeatures = WexSideClient.getEspFeatureRegistry();
      if (espFeatures != null && module != null) {
         EspTargetType espTargetType = espFeatures.getTargetType(module);
         if (espTargetType != null) {
            this.segmentedControl.setIntType2(espTargetType.ordinal());
            this.espTargetType = espTargetType;
            this.espRelationSelector.setEspRelation(this.isActive() ? espFeatures.getRelation(module) : EspRelation.DEFAULT);
            this.setEspTargetType(espTargetType);
            this.scrollController.scrollToTop();
            this.pendingModuleScroll = module;
         }
      }
   }

   private void process8(Module module) {
      if (module == null) {
         return;
      }
      List<ModuleCard> list = this.getModuleCards();
      if (list == null) {
         return;
      }
      float f2 = 0.0F;
      for(ModuleCard moduleCard : list) {
         if (moduleCard.getModule() == module) {
            moduleCard.setBooleanType(true);
            this.scrollController.scrollTo(-f2, this.bounds4.getHeight());
            return;
         }
         f2 += moduleCard.getBounds().getHeight() + 4.0F;
      }
   }
}
