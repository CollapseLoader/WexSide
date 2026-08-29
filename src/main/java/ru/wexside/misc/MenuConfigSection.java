package ru.wexside.misc;

import ru.wexside.config.ConfigSerializable;

public abstract class MenuConfigSection implements ConfigSerializable {
   private final String id;

   protected MenuConfigSection(String string) {
      this.id = this.sanitizeId(string);
   }

   @Override
   public final String getConfigId() {
      String string = this.id;
      return "menu." + string;
   }

   private String sanitizeId(String string) {
      if (string != null && !string.isBlank()) {
         StringBuilder stringBuilder = new StringBuilder();
         boolean bl = false;

         for(int i = 0; i < string.length(); ++i) {
            char c = Character.toLowerCase(string.charAt(i));
            if (Character.isLetterOrDigit(c)) {
               stringBuilder.append(c);
               bl = false;
            } else if (!bl) {
               stringBuilder.append('_');
               bl = true;
            }
         }

         String string2 = stringBuilder.toString().replaceAll("^_+|_+$", "");
         if (!string2.isBlank()) {
            return string2;
         } else {
            throw new IllegalArgumentException("Menu settings id must not be blank");
         }
      } else {
         throw new IllegalArgumentException("Menu settings id must not be blank");
      }
   }

   public String getId() {
      return this.id;
   }
}
