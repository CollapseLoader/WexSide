package ru.wexside.misc;

import ru.wexside.input.BindInput;
import ru.wexside.util.PotionPresetController;

public final class PotionPresetKeybind extends KeybindBinding {
   private final PotionPresetController potionCombiner;
   private final PotionPresetDraft preset;

   public PotionPresetKeybind(PotionPresetController potionCombiner, PotionPresetDraft preset) {
      super(null, preset.getBindInput());
      this.potionCombiner = potionCombiner;
      this.preset = preset;
   }

   @Override
   public void onReleased() {
      this.potionCombiner.queuePreset(this.preset);
   }

   @Override
   public void setBindInput(BindInput keybind) {
      super.setBindInput(keybind);
      this.preset.setBindInput(this.getBindInput());
   }
}
