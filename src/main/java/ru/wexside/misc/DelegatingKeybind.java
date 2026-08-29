package ru.wexside.misc;

import java.util.function.Consumer;
import java.util.function.Supplier;
import ru.wexside.input.BindInput;

public final class DelegatingKeybind implements KeybindDescriptor {
   private final String string3;
   private final Supplier<BindInput> supplier;
   private final Consumer<BindInput> consumer;
   private final String string4;

   public DelegatingKeybind(String string, String string2, Supplier<BindInput> supplier, Consumer<BindInput> consumer) {
      this.string4 = string;
      this.string3 = string2;
      this.supplier = supplier;
      this.consumer = consumer;
   }

   @Override
   public String getString() {
      return this.string4;
   }

   @Override
   public String getString2() {
      return this.string3;
   }

   @Override
   public void setBindInput(BindInput bindInput) {
      this.consumer.accept(bindInput == null ? BindInput.unbound() : bindInput);
   }

   @Override
   public BindInput getBindInput() {
      BindInput bindInput = this.supplier.get();
      return bindInput == null ? BindInput.unbound() : bindInput;
   }
}
