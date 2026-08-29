package ru.wexside.misc;

import java.util.function.IntConsumer;
import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.render.BakedItemIcon;
import ru.wexside.render.ItemIconRenderer;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.GuiElement;
import ru.wexside.util.ColorUtils;
import ru.wexside.util.GuiDrawApi;
import ru.wexside.util.PotionPresetController;

public final class PotionSlotButton
   extends GuiElement
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider,
   MouseHitTest {
   private final int slot;
   private final IntConsumer intConsumer;
   private float value3;
   private final ItemIconRenderer itemIconRenderer;
   private final String string2 = "Я";
   private final PotionEditorState potionEditorState;
   private float value7;
   private final PotionPresetController potionPresetController2;
   private float value9;
   private final String string3 = "С";

   public PotionSlotButton(
      int n, PotionEditorState potionEditorState, PotionPresetController potionPresetController2, ItemIconRenderer itemIconRenderer, IntConsumer intConsumer
   ) {
      super(new GuiBounds(0.0F, 0.0F, 44.0F, 30.0F));
      this.slot = n;
      this.potionEditorState = potionEditorState;
      this.potionPresetController2 = potionPresetController2;
      this.itemIconRenderer = itemIconRenderer;
      this.intConsumer = intConsumer;
   }

   @Override
   public void onMouseScroll(int n, int n2, double d) {
   }

   @Override
   public void update() {
   }

   @Override
   public boolean onMousePressed(int n, int n2, int n3) {
      if (n3 == 0 && this.getBounds().contains((float)n, (float)n2)) {
         this.intConsumer.accept(this.slot);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public float render(float f, Matrix4f matrix4f) {
      GuiBounds bounds2 = this.getBounds();
      GuiDrawApi drawApi = WexSideClient.getGuiRenderer();
      GuiInteractionState guiInteractionState = GuiInteractionState.getInstance();
      boolean bl = this.potionEditorState.getSelectorSlot() == this.slot;
      boolean bl2 = this.potionEditorState.getSelectedSlot() == this.slot;
      this.value9 = FrameInterpolator.lerpTowards(
         this.value9, this.process13(guiInteractionState.getScaledMouseX(), guiInteractionState.getScaledMouseY()) ? 1.0F : 0.0F, 20.0F
      );
      this.value3 = FrameInterpolator.lerpTowards(this.value3, !bl && !bl2 ? 0.0F : 1.0F, 25.0F);
      this.value7 = FrameInterpolator.lerpTowards(this.value7, bl2 ? 1.0F : 0.0F, 25.0F);
      int n = ColorUtils.lerp(
         ColorUtils.lerp(ThemeColors.borderPrimary(), ThemeColors.borderStrong(), (double)this.value9), ThemeColors.accent(), (double)this.value3
      );
      int n2 = ColorUtils.multiplyAlpha(ThemeColors.accentTint(), this.value7);
      drawApi.drawRoundedRectangleOutlined(matrix4f, bounds2.getX(), bounds2.getY(), 44.0F, 30.0F, 16.0F, 1.0F, n2, n);
      PotionCatalogEntry potionCatalogEntry = this.getPotionCatalogEntry();
      if (potionCatalogEntry == null) {
         String string = bl ? "С" : "Я";
         int n3 = ColorUtils.lerp(ThemeColors.textPlaceholder(), ThemeColors.accent(), (double)this.value3);
         FontRegistry.font3
            .process5(
               matrix4f,
               drawApi,
               string,
               bounds2.getX() + 22.0F - FontRegistry.font3.process13(string.charAt(0), 0.0F, 10.0F),
               bounds2.getY() + 15.0F - FontRegistry.font3.process14(string.charAt(0), 0.0F, 10.0F),
               10.0F,
               n3
            );
         return bounds2.getY() + 30.0F;
      } else {
         BakedItemIcon iiIlilllII2 = this.itemIconRenderer.process(this.potionPresetController2.resolveStack(potionCatalogEntry));
         this.itemIconRenderer.process2(drawApi, matrix4f, iiIlilllII2, bounds2.getX() + 12.0F, bounds2.getY() + 5.0F, 20.0F, -1);
         return bounds2.getY() + 30.0F;
      }
   }

   @Override
   public void update2() {
      PotionCatalogEntry potionCatalogEntry = this.getPotionCatalogEntry();
      if (potionCatalogEntry != null) {
         this.itemIconRenderer.process(this.potionPresetController2.resolveStack(potionCatalogEntry));
      }
   }

   public PotionCatalogEntry getPotionCatalogEntry() {
      return PotionCatalog.findById(this.potionEditorState.getWorkingCopy().getPotionId(this.slot));
   }

   @Override
   public boolean process13(int n, int n2) {
      float f = this.getAbsoluteX();
      float f2 = this.getAbsoluteY();
      return (float)n >= f && (float)n <= f + 44.0F && (float)n2 >= f2 && (float)n2 <= f2 + 30.0F;
   }
}
