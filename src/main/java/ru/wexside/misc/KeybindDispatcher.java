package ru.wexside.misc;

import java.util.List;
import ru.wexside.input.BindInput;

public final class KeybindDispatcher {
   private final List<KeybindBinding> bindings;

   public KeybindDispatcher(List<KeybindBinding> bindings) {
      this.bindings = bindings;
   }

   public void onMouseReleased(int button) {
      this.dispatchReleased(BindInput.mouse(button));
   }

   public void onKeyPressed(int keyCode) {
      this.dispatchPressed(BindInput.keyboard(keyCode));
   }

   private void dispatchPressed(BindInput input) {
      for(KeybindBinding binding : this.bindings) {
         if (binding.matches(input)) {
            binding.onPressed();
         }
      }
   }

   public void onMousePressed(int button) {
      this.dispatchPressed(BindInput.mouse(button));
   }

   private void dispatchReleased(BindInput input) {
      for(KeybindBinding binding : this.bindings) {
         if (binding.matches(input)) {
            binding.onReleased();
         }
      }
   }

   public void onKeyReleased(int keyCode) {
      this.dispatchReleased(BindInput.keyboard(keyCode));
   }
}
