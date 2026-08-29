package ru.wexside.module.misc;

import java.util.List;
import java.util.function.Consumer;
import net.minecraft.class_2561;
import net.minecraft.class_437;
import ru.wexside.misc.PotionPresetDraft;
import ru.wexside.misc.ServerKind;
import ru.wexside.setting.BindSetting;
import ru.wexside.util.PotionPresetController;

public final class PotionCombinerRadialScreen extends class_437 {
   private final BindSetting bind;
   private final PotionPresetController potionCombiner;
   private final Consumer<PotionPresetDraft> onSelect;

   public PotionCombinerRadialScreen(BindSetting bind, PotionPresetController potionCombiner, Consumer<PotionPresetDraft> onSelect) {
      super(class_2561.method_43473());
      this.bind = bind;
      this.potionCombiner = potionCombiner;
      this.onSelect = onSelect;
   }

   public BindSetting bind() {
      return this.bind;
   }

   public List<PotionPresetDraft> presets() {
      return this.potionCombiner.getFavoritePresets();
   }

   public ServerKind serverMode() {
      return PotionCombinerModule.getServerKind();
   }

   public void method_25393() {
      if (this.bind == null || !this.bind.isPressed()) {
         this.method_25419();
      }
   }

   public boolean method_25421() {
      return false;
   }

   public void select(PotionPresetDraft preset) {
      if (preset != null && this.onSelect != null) {
         this.onSelect.accept(preset);
      }

      this.method_25419();
   }
}
