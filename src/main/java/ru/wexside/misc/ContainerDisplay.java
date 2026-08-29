package ru.wexside.misc;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import ru.wexside.config.ConfigSerializable;

public final class ContainerDisplay extends MenuConfigSection implements ConfigSerializable {
   private boolean enabled = true;
   private boolean enabled2 = true;
   private ContainerColumnLayout containerColumnLayout = ContainerColumnLayout.TWO_COLUMNS;

   public ContainerDisplay() {
      super("container_display");
   }

   @Override
   public void writeConfig(DataOutputStream dataOutputStream) throws IOException {
      dataOutputStream.writeInt(this.containerColumnLayout.ordinal());
      dataOutputStream.writeBoolean(this.enabled2);
      dataOutputStream.writeBoolean(this.enabled);
   }

   @Override
   public void readConfig(DataInputStream dataInputStream) throws IOException {
      int n = dataInputStream.readInt();
      ContainerColumnLayout[] cls0273Array = ContainerColumnLayout.values();
      this.containerColumnLayout = n >= 0 && n < cls0273Array.length ? cls0273Array[n] : ContainerColumnLayout.SINGLE_COLUMN;

      try {
         this.enabled2 = dataInputStream.readBoolean();
         this.enabled = dataInputStream.readBoolean();
      } catch (EOFException var5) {
         this.enabled2 = true;
         this.enabled = true;
      }
   }

   public boolean isActive() {
      return this.enabled2;
   }

   public boolean isActive2() {
      return this.enabled;
   }

   public ContainerColumnLayout getContainerColumnLayout() {
      return this.containerColumnLayout;
   }

   public void setBooleanType(boolean bl) {
      this.enabled = bl;
   }

   public void setBooleanType2(boolean bl) {
      this.enabled2 = bl;
   }

   public void setContainerColumnLayout(ContainerColumnLayout containerColumnLayout) {
      this.containerColumnLayout = containerColumnLayout;
   }

   public int getIntType() {
      int n = 0;
      if (this.enabled2) {
         ++n;
      }

      if (this.enabled) {
         ++n;
      }

      return n;
   }
}
