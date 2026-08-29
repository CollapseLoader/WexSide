package ru.wexside.setting;

import ru.wexside.util.RussianPluralForms;

public enum NumberUnit {
   BLOCKS("блок", "блока", "блоков"),
   PIXELS("пиксель", "пикселя", "пикселей"),
   PERCENT("%", "%", "%");

   private final RussianPluralForms formatter;

   private NumberUnit(String singular, String paucal, String plural) {
      this.formatter = new RussianPluralForms(singular, paucal, plural);
   }

   public RussianPluralForms getFormatter() {
      return this.formatter;
   }
}
