package ru.wexside.input;

import java.util.Objects;

public final class BindInput {
   private static final BindInput UNBOUND = new BindInput(BindDevice.NONE, 0);
   private final BindDevice device;
   private final int code;

   public BindInput(BindDevice device, int code) {
      this.device = Objects.requireNonNull(device, "device");
      this.code = device == BindDevice.NONE ? 0 : code;
   }

   public static BindInput unbound() {
      return UNBOUND;
   }

   public static BindInput fromLegacyCode(int code) {
      if (code == 0) {
         return UNBOUND;
      } else {
         return code < 0 ? mouse(code + 100) : keyboard(code);
      }
   }

   public static BindInput keyboard(int keyCode) {
      return new BindInput(BindDevice.KEYBOARD, keyCode);
   }

   public static BindInput mouse(int buttonCode) {
      return new BindInput(BindDevice.MOUSE, buttonCode);
   }

   public int toLegacyCode() {
      return switch(this.device) {
         case NONE -> 0;
         case KEYBOARD -> this.code;
         case MOUSE -> this.code - 100;
         default -> throw new MatchException(null, null);
      };
   }

   public boolean matchesKeyboard(int keyCode) {
      return this.device == BindDevice.KEYBOARD && this.code == keyCode;
   }

   public boolean matchesMouse(int buttonCode) {
      return this.device == BindDevice.MOUSE && this.code == buttonCode;
   }

   public boolean isUnbound() {
      return this.device == BindDevice.NONE;
   }

   public boolean isKeyboard() {
      return this.device == BindDevice.KEYBOARD;
   }

   public boolean isMouse() {
      return this.device == BindDevice.MOUSE;
   }

   public BindDevice device() {
      return this.device;
   }

   public int code() {
      return this.code;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else {
         if (object instanceof BindInput other && this.device == other.device && this.code == other.code) {
            return true;
         }

         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.device, this.code);
   }

   @Override
   public String toString() {
      return "BindInput[device=" + this.device + ", code=" + this.code + "]";
   }
}
