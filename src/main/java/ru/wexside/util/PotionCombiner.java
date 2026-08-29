package ru.wexside.util;

import java.util.ArrayList;
import java.util.List;
import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.misc.ActionButton;
import ru.wexside.misc.BoundsProvider;
import ru.wexside.misc.CharacterInputHandler;
import ru.wexside.misc.FrameInterpolator;
import ru.wexside.misc.GuiRenderable;
import ru.wexside.misc.KeyPressHandler;
import ru.wexside.misc.LabeledTextField;
import ru.wexside.misc.LayoutUpdater;
import ru.wexside.misc.MouseButtonHandler;
import ru.wexside.misc.MouseScrollHandler;
import ru.wexside.misc.MovablePanel;
import ru.wexside.misc.PotionBindEditor;
import ru.wexside.misc.PotionCatalogEntry;
import ru.wexside.misc.PotionEditorState;
import ru.wexside.misc.PotionPresetDraft;
import ru.wexside.misc.PotionPresetList;
import ru.wexside.misc.PotionSelectorPopup;
import ru.wexside.misc.PotionSlotButton;
import ru.wexside.misc.SectionHeader;
import ru.wexside.misc.SelectedPotionPanel;
import ru.wexside.misc.StyledActionButton;
import ru.wexside.misc.ThemeColors;
import ru.wexside.render.ItemIconRenderer;
import ru.wexside.ui.GuiBounds;

public final class PotionCombiner
   extends MovablePanel
   implements MouseScrollHandler,
   GuiRenderable,
   BoundsProvider,
   MouseButtonHandler,
   CharacterInputHandler,
   LayoutUpdater,
   KeyPressHandler {
   static final float value = 8.0F;
   static final float value2 = 210.0F;
   private final ItemIconRenderer itemIconRenderer;
   static final float value3 = 188.0F;
   static final float value4 = 56.0F;
   private final ActionButton actionButton;
   static final float value5 = 96.0F;
   private final int slot;
   private float value6;
   private final String string2;
   static final float value7 = 5.0F;
   private final PotionPresetList potionPresetList;
   private final PotionPresetController potionPresetController2;
   static final float value8 = 11.5F;
   private final PotionSelectorPopup potionSelectorPopup;
   private final float value9;
   private final ActionButton actionButton2;
   static final float value10 = 6.0F;
   private float value11;
   private float value12;
   static final float value13 = 16.0F;
   static final float value14 = 100.0F;
   private float value15;
   private final float value16;
   private final SelectedPotionPanel selectedPotionPanel;
   private final List<PotionSlotButton> potionSlotButtons;
   private final PotionEditorState potionEditorState = new PotionEditorState();
   private final LabeledTextField labeledTextField;
   static final float value17 = 142.0F;
   private float value18;
   private final SectionHeader sectionHeader;
   private final String string3;
   private final SectionHeader sectionHeader2;
   static final float value19 = 218.0F;
   static final float value20 = 34.5F;
   static final float value21 = 194.0F;
   static final float value22 = 368.0F;
   private final PotionBindEditor potionBindEditor;
   static final float value23 = 8.0F;
   private final StyledActionButton styledActionButton;
   static final float value24 = 2.0F;

   public PotionCombiner(PotionPresetController potionPresetController2) {
      super(0, 0, 368, 188);
      this.value9 = 20.0F;
      this.slot = 3;
      this.value16 = 56.0F;
      this.string2 = "Создание пресета";
      this.string3 = "Редактирование пресета";
      this.itemIconRenderer = new ItemIconRenderer();
      this.potionSlotButtons = new ArrayList<>();
      this.value12 = Float.NaN;
      this.potionPresetController2 = potionPresetController2;
      this.sectionHeader = new SectionHeader(
         "Создание пресета", "щ", "Potion Combiner", "Выбрасывает указанные зелья из инвентаря по нажатию указанной пользователем клавиши", 194.0F
      );
      this.sectionHeader2 = new SectionHeader("Настройки пресетов", "Й", "Ваши пресеты", "Созданные вами ранее пресеты для Potion Combiner", 142.0F);
      this.labeledTextField = new LabeledTextField("Имя пресета", "Уникальное имя данного пресета", 32, 194.0F);

      for(int i = 0; i < 4; ++i) {
         this.potionSlotButtons.add(new PotionSlotButton(i, this.potionEditorState, potionPresetController2, this.itemIconRenderer, this::selectPotionSlot));
      }

      this.selectedPotionPanel = new SelectedPotionPanel(
         this.potionEditorState, 194.0F, () -> this.openPotionSelector(this.potionEditorState.getSelectedSlot()), this::update5
      );
      this.potionBindEditor = new PotionBindEditor(this.potionEditorState, 194.0F);
      this.styledActionButton = new StyledActionButton("Сохранить пресет", this::getButtonBackgroundColor, this::getButtonTextColor, this::savePreset);
      this.actionButton = new ActionButton("Удалить пресет", "ь", 96.0F, 16.0F, this::update6);
      this.actionButton2 = new ActionButton("Выйти", "m", 34.5F, 11.5F, this::update9);
      this.potionPresetList = new PotionPresetList(
         new GuiBounds(218.0F, 56.0F, 142.0F, 0.0F), potionPresetController2, this.potionEditorState, this.itemIconRenderer, this::setPotionPresetDraft
      );
      this.potionSelectorPopup = new PotionSelectorPopup(potionPresetController2, this.potionEditorState, this.itemIconRenderer, this::setPotionCatalogEntry);
      this.sectionHeader.getBounds().setPosition(8.0F, 8.0F);
      this.sectionHeader2.getBounds().setPosition(218.0F, 8.0F);
      this.labeledTextField.getBounds().setPosition(8.0F, 56.0F);
      this.styledActionButton.getBounds().setSize(194.0F, 16.0F);
      this.addChild(this.sectionHeader);
      this.addChild(this.sectionHeader2);
      this.addChild(this.labeledTextField);

      for(PotionSlotButton potionSlotButton : this.potionSlotButtons) {
         this.addChild(potionSlotButton);
      }

      this.addChild(this.selectedPotionPanel);
      this.addChild(this.potionBindEditor);
      this.addChild(this.styledActionButton);
      this.addChild(this.actionButton);
      this.addChild(this.actionButton2);
      this.addChild(this.potionPresetList);
      this.addChild(this.potionSelectorPopup);
   }

   @Override
   public void onMouseScroll(int n, int n2, double d) {
      int n3 = n - (int)this.getBounds().getX();
      int n4 = n2 - (int)this.getBounds().getY();
      if (this.potionSelectorPopup.isActive2() && this.potionSelectorPopup.getBounds().contains((float)n3, (float)n4)) {
         this.potionSelectorPopup.onMouseScroll(n3, n4, d);
      } else {
         this.potionPresetList.onMouseScroll(n3, n4, d);
      }
   }

   @Override
   public void update() {
      this.potionPresetController2.refreshInventoryIndex();
      this.itemIconRenderer.update3();

      for(PotionSlotButton potionSlotButton : this.potionSlotButtons) {
         potionSlotButton.update2();
      }

      this.potionPresetList.update4();
      this.potionSelectorPopup.update4();
      this.itemIconRenderer.update();
      this.itemIconRenderer.update2();

      for(PotionSlotButton potionSlotButton : this.potionSlotButtons) {
         potionSlotButton.update();
      }
   }

   @Override
   public boolean onMousePressed(int n, int n2, int n3) {
      int n5 = n - (int)this.getBounds().getX();
      int n4;
      if (this.potionSelectorPopup.onMousePressed(n5, n4 = n2 - (int)this.getBounds().getY(), n3)) {
         return true;
      } else if (!this.potionEditorState.isActive() || !this.actionButton2.onMousePressed(n5, n4, n3) && !this.actionButton.onMousePressed(n5, n4, n3)) {
         if (this.potionEditorState.getSelectedSlot() >= 0 && this.selectedPotionPanel.onMousePressed(n5, n4, n3)) {
            return true;
         } else {
            for(PotionSlotButton potionSlotButton : this.potionSlotButtons) {
               if (potionSlotButton.onMousePressed(n5, n4, n3)) {
                  return true;
               }
            }

            return this.labeledTextField.onMousePressed(n5, n4, n3)
               || this.potionBindEditor.onMousePressed(n5, n4, n3)
               || this.styledActionButton.onMousePressed(n5, n4, n3)
               || this.potionPresetList.onMousePressed(n5, n4, n3);
         }
      } else {
         return true;
      }
   }

   @Override
   public float render(float f, Matrix4f matrix4f) {
      GuiDrawApi drawApi = WexSideClient.getGuiRenderer();
      this.updateLayout();
      Matrix4f matrix4f2 = Math.abs(this.value15) < 0.001F ? matrix4f : new Matrix4f(matrix4f).translate(0.0F, this.value15, 0.0F);
      drawApi.begin();
      this.process7(drawApi, matrix4f2);
      drawApi.beginStencil(3);
      drawApi.drawRoundedRectangle(matrix4f2, 0.0F, 0.0F, this.getBounds().getWidth(), this.getBounds().getHeight(), 10.5F, ColorUtils.withAlpha(-1, 0.0F));
      drawApi.applyStencilMask(3);
      this.sectionHeader.render(f, matrix4f2);
      this.sectionHeader2.render(f, matrix4f2);
      this.labeledTextField.render(f, matrix4f2);

      for(PotionSlotButton potionSlotButton : this.potionSlotButtons) {
         potionSlotButton.render(f, matrix4f2);
      }

      this.process6(drawApi, f, matrix4f2);
      this.potionBindEditor.render(f, matrix4f2);
      this.styledActionButton.render(f, matrix4f2);
      if (this.potionEditorState.isActive()) {
         this.actionButton.render(f, matrix4f2);
         this.actionButton2.render(f, matrix4f2);
      }

      this.potionPresetList.render(f, matrix4f2);
      drawApi.endStencil();
      this.potionSelectorPopup.render(f, matrix4f2);
      drawApi.end();
      return 0.0F;
   }

   @Override
   public void onMouseReleased(int n, int n2, int n3) {
      int n4 = n - (int)this.getBounds().getX();
      int n5 = n2 - (int)this.getBounds().getY();
      if (this.potionSelectorPopup.isActive2() && !this.potionSelectorPopup.getBounds().contains((float)n4, (float)n5)) {
         this.update8();
      }

      super.onMouseReleased(n4, n5, n3);
   }

   @Override
   public boolean onCharTyped(char c) {
      return this.potionSelectorPopup.onCharTyped(c) || this.labeledTextField.onCharTyped(c);
   }

   @Override
   public void update2() {
      this.update8();
      super.update2();
   }

   @Override
   public boolean onKeyPressed(int n) {
      return this.potionSelectorPopup.onKeyPressed(n)
         || this.labeledTextField.onKeyPressed(n)
         || this.potionBindEditor.onKeyPressed(n)
         || this.potionPresetList.onKeyPressed(n);
   }

   @Override
   public GuiBounds getVisibleBounds() {
      GuiBounds bounds2 = super.getVisibleBounds();
      if (!this.potionSelectorPopup.isActive()) {
         return bounds2;
      } else {
         GuiBounds bounds3 = this.potionSelectorPopup.getBounds();
         float f = Math.min(bounds2.getX(), bounds3.getX());
         float f2 = Math.min(bounds2.getY(), bounds3.getY());
         float f3 = Math.max(bounds2.getX() + bounds2.getWidth(), bounds3.getX() + bounds3.getWidth());
         float f4 = Math.max(bounds2.getY() + bounds2.getHeight(), bounds3.getY() + bounds3.getHeight());
         return new GuiBounds(f, f2, f3 - f, f4 - f2);
      }
   }

   @Override
   public void update4() {
      this.setBooleanType3(true);
   }

   private void process7(GuiDrawApi drawApi, Matrix4f matrix4f) {
      float f = this.getBounds().getWidth();
      float f2 = this.getBounds().getHeight();
      drawApi.drawRoundedRectangle(matrix4f, 0.0F, 0.0F, f, f2, 10.5F, ThemeColors.backgroundPrimary());
      drawApi.drawRoundedRectangle(matrix4f, 0.0F, 0.0F, f, f2, 10.5F, ThemeColors.backgroundPrimary());
      drawApi.fillRectangle(matrix4f, 210.0F, 0.0F, 0.5F, f2, ThemeColors.borderPrimary());
   }

   private void updateLayout() {
      this.value18 = FrameInterpolator.lerpTowards(this.value18, this.canSavePreset() ? 1.0F : 0.0F, 20.0F);
      this.value6 = FrameInterpolator.lerpTowards(this.value6, this.potionEditorState.getSelectedSlot() >= 0 ? 1.0F : 0.0F, 20.0F);
      float f = 130.0F;
      float f2 = 41.5F;
      float f3 = f + f2 * this.value6;
      this.selectedPotionPanel.getBounds().setPosition(8.0F, f + 5.0F);
      this.potionBindEditor.getBounds().setPosition(8.0F, f3 + 8.0F);
      float f4 = f3 + 8.0F + 18.0F + 8.0F;
      float f5 = f4 + 16.0F + 8.0F;
      this.setFloatType(f5);

      for(int i = 0; i < this.potionSlotButtons.size(); ++i) {
         this.potionSlotButtons.get(i).getBounds().setPosition(8.0F + (float)i * 50.0F, 100.0F);
      }

      if (this.potionEditorState.isActive()) {
         this.actionButton.getBounds().setPosition(8.0F, f4);
         this.styledActionButton.getBounds().setPosition(106.0F, f4);
         this.styledActionButton.getBounds().setSize(96.0F, 16.0F);
         this.actionButton2.getBounds().setPosition(167.5F, 8.0F);
      } else {
         this.styledActionButton.getBounds().setPosition(8.0F, f4);
         this.styledActionButton.getBounds().setSize(194.0F, 16.0F);
      }

      this.sectionHeader.setString(this.potionEditorState.isActive() ? "Редактирование пресета" : "Создание пресета");
      this.potionPresetList.getBounds().setSize(142.0F, this.getBounds().getHeight() - 56.0F - 8.0F);
   }

   private int getButtonTextColor() {
      return ColorUtils.lerp(ThemeColors.textDisabled(), -1, (double)this.value18);
   }

   private void selectPotionSlot(int n) {
      if (this.potionEditorState.getWorkingCopy().getPotionId(n) == null) {
         this.openPotionSelector(n);
      } else {
         this.update8();
         this.potionEditorState.setSelectedSlot(this.potionEditorState.getSelectedSlot() == n ? -1 : n);
      }
   }

   private void setPotionPresetDraft(PotionPresetDraft potionPresetDraft) {
      this.update8();
      this.potionEditorState.beginEditing(potionPresetDraft);
      this.labeledTextField.setString(potionPresetDraft.getName());
   }

   private void setPotionCatalogEntry(PotionCatalogEntry potionCatalogEntry) {
      int n = this.potionEditorState.getSelectorSlot();
      if (n >= 0) {
         this.potionEditorState.getWorkingCopy().setPotionId(n, potionCatalogEntry.getId());
      }

      this.update8();
   }

   private void savePreset() {
      if (this.canSavePreset()) {
         String string = this.labeledTextField.getString().trim();
         this.potionEditorState.getWorkingCopy().setName(string);
         PotionPresetDraft potionPresetDraft = this.potionEditorState.isActive()
            ? this.potionEditorState.getOriginalPreset()
            : this.potionPresetController2.createPreset(string);
         this.potionEditorState.applyWorkingCopyTo(potionPresetDraft);
         this.potionPresetController2.savePresets();
         this.update7();
      }
   }

   private int getButtonBackgroundColor() {
      return ColorUtils.lerp(ThemeColors.controlFill(), ThemeColors.accent(), (double)this.value18);
   }

   private void setFloatType(float f) {
      GuiBounds bounds2 = this.getBounds();
      if (Float.isNaN(this.value12) || Math.abs(bounds2.getY() - this.value11) > 0.5F) {
         this.value12 = bounds2.getY() + bounds2.getHeight() / 2.0F;
      }

      float f2 = this.value12 - f / 2.0F;
      this.value15 = f2 - bounds2.getY();
      bounds2.setSize(368.0F, f);
      float f3 = (float)Math.round(f2);
      if (Math.abs(f3 - bounds2.getY()) > 0.01F) {
         bounds2.setPosition(bounds2.getX(), f3);
      }

      this.value11 = f3;
   }

   private void process6(GuiDrawApi drawApi, float f, Matrix4f matrix4f2) {
      if (!(this.value6 <= 0.01F)) {
         GuiBounds bounds2 = this.selectedPotionPanel.getBounds();
         float f2 = bounds2.getX();
         float f3 = bounds2.getY();
         bounds2.setPosition(0.0F, 0.0F);
         ClippedLayerRenderer.process(
            drawApi,
            matrix4f2,
            f2,
            f3,
            bounds2.getWidth(),
            36.5F * this.value6,
            0.0F,
            this.value6 < 0.99F,
            ColorUtils.withAlpha(-1, 255.0F * this.value6),
            matrix4f -> this.selectedPotionPanel.render(f, matrix4f)
         );
         bounds2.setPosition(f2, f3);
      }
   }

   private void update5() {
      int n = this.potionEditorState.getSelectedSlot();
      if (n >= 0) {
         this.potionEditorState.getWorkingCopy().setPotionId(n, null);
      }

      this.potionEditorState.setSelectedSlot(-1);
   }

   private void update6() {
      this.potionPresetController2.deletePreset(this.potionEditorState.getOriginalPreset());
      this.update7();
   }

   private void update7() {
      this.update8();
      this.potionEditorState.reset();
      this.labeledTextField.update4();
   }

   private void openPotionSelector(int n) {
      if (n >= 0) {
         this.potionEditorState.setSelectorSlot(n);
         this.potionEditorState.setSearchQuery("");
         this.potionPresetController2.refreshInventoryIndex();
         this.potionSelectorPopup.setBounds(this.potionSlotButtons.get(n).getBounds());
         this.potionSelectorPopup.update3();
         this.potionSelectorPopup.setBooleanType(true);
      }
   }

   private void update8() {
      this.potionSelectorPopup.setBooleanType(false);
      this.potionEditorState.setSelectorSlot(-1);
   }

   private boolean canSavePreset() {
      return !this.labeledTextField.getString().trim().isEmpty() && !this.potionEditorState.getWorkingCopy().isEmpty();
   }

   private void update9() {
      this.update7();
   }
}
