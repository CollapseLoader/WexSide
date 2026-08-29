package ru.wexside.misc;

import java.util.Objects;

public final class MacroDefinition {
   private final int keyCode;
   private final String message;
   private final String name;
   private final MacroType type;

   public int getKeyCode() {
      return this.keyCode;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (!(object instanceof MacroDefinition)) {
         return false;
      } else {
         MacroDefinition macroDefinition = (MacroDefinition)object;
         return this.keyCode == macroDefinition.keyCode
            && this.type == macroDefinition.type
            && Objects.equals(this.name, macroDefinition.name)
            && Objects.equals(this.message, macroDefinition.message);
      }
   }

   @Override
   public String toString() {
      String string = String.valueOf(this.type);
      int n = this.keyCode;
      String string2 = this.message;
      String string3 = this.name;
      return "Macro[name=" + string3 + ", message=" + string2 + ", key=" + n + ", type=" + string + "]";
   }

   public String getMessage() {
      return this.message;
   }

   public String getName() {
      return this.name;
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.name, this.message, this.keyCode, this.type);
   }

   public MacroType getType() {
      return this.type;
   }

   public MacroDefinition(String name, String message, int keyCode, MacroType macroType) {
      this.name = name;
      this.message = message;
      this.keyCode = keyCode;
      this.type = macroType == null ? MacroType.CHAT : macroType;
   }
}
