package ru.wexside.misc;

import java.util.function.Consumer;
import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.model.esp.EspRelation;
import ru.wexside.util.ClippedLayerRenderer;
import ru.wexside.util.ColorUtils;

public final class EspRelationSelector {
   private final SelectableButton selectableButton;
   private static final float value = 33.0F;
   private static final float value2 = 34.5F;
   private static final String string = "Д";
   private EspRelation espRelation = EspRelation.DEFAULT;
   private final SelectableButton selectableButton2;
   private static final float value3 = 3.0F;
   private static final String string2 = "х";
   private Consumer<EspRelation> consumer = espRelation -> {
   };
   public static final float value4 = 11.5F;

   public EspRelationSelector() {
      this.selectableButton = new SelectableButton("Д", EspRelation.FRIEND.getTitle(), 33.0F, 11.5F, () -> this.setEspRelation2(EspRelation.FRIEND));
      this.selectableButton2 = new SelectableButton("х", EspRelation.DEFAULT.getTitle(), 34.5F, 11.5F, () -> this.setEspRelation2(EspRelation.DEFAULT));
      this.update();
   }

   public void setEspRelation(EspRelation espRelation) {
      this.setEspRelation2(espRelation);
   }

   public float enabled() {
      return 70.5F;
   }

   public boolean process(int n, int n2, int n3) {
      return this.selectableButton.onMousePressed(n, n2, n3) || this.selectableButton2.onMousePressed(n, n2, n3);
   }

   public void setConsumer(Consumer<EspRelation> consumer) {
      this.consumer = consumer == null ? espRelation -> {
      } : consumer;
   }

   private void setEspRelation2(EspRelation espRelation) {
      if (this.espRelation != espRelation) {
         this.espRelation = espRelation;
         this.update();
         this.consumer.accept(espRelation);
      }
   }

   private void update() {
      this.selectableButton.setBooleanType(this.espRelation == EspRelation.FRIEND);
      this.selectableButton2.setBooleanType(this.espRelation == EspRelation.DEFAULT);
   }

   public void process2(float f, Matrix4f matrix4f2, float f2) {
      if (!(f2 <= 0.01F)) {
         float f3 = this.selectableButton.getBounds().getX();
         float f4 = this.selectableButton.getBounds().getY();
         ClippedLayerRenderer.process(
            WexSideClient.getGuiRenderer(),
            matrix4f2,
            f3,
            f4,
            this.enabled(),
            11.5F,
            0.0F,
            f2 < 0.99F,
            ColorUtils.withAlpha(-1, 255.0F * f2),
            contentMatrix -> {
               Matrix4f translatedMatrix = new Matrix4f(contentMatrix).translate(-f3, -f4, 0.0F);
               this.selectableButton.render(f, translatedMatrix);
               this.selectableButton2.render(f, translatedMatrix);
            }
         );
      }
   }

   public void process3(float f, float f2) {
      this.selectableButton.getBounds().setPosition(f, f2);
      this.selectableButton2.getBounds().setPosition(f + 33.0F + 3.0F, f2);
   }

   public EspRelation getEspRelation() {
      return this.espRelation;
   }
}
