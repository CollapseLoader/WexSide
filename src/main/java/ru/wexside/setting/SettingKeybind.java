package ru.wexside.setting;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.Arrays;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.input.BindInput;
import ru.wexside.misc.BindActivationMode;
import ru.wexside.misc.KeybindBinding;

public final class SettingKeybind extends KeybindBinding implements ConfigSerializable {
   private BindActivationMode activationMode;
   private boolean active;
   private byte[] previousPayload = new byte[0];
   private boolean editorInitialized;
   private byte[] activationPayload;
   private boolean shownInHud = true;

   public SettingKeybind(Setting setting) {
      super(setting, BindInput.unbound());
      this.activationMode = BindActivationMode.TOGGLE;
      this.captureCurrentValue();
   }

   @Override
   public String getConfigId() {
      String string = this.getSetting().getConfigId();
      if (string != null && !string.isBlank()) {
         return string + ".$binding";
      } else {
         String string3 = this.getSetting().getDisplayName();
         return string3 + ".$binding";
      }
   }

   @Override
   public void writeConfig(DataOutputStream dataOutputStream) throws IOException {
      dataOutputStream.writeInt(this.getLegacyCode());
      dataOutputStream.writeInt(this.activationPayload.length);
      dataOutputStream.write(this.activationPayload);
      dataOutputStream.writeBoolean(this.shownInHud);
      dataOutputStream.writeInt(this.previousPayload.length);
      dataOutputStream.write(this.previousPayload);
      dataOutputStream.writeBoolean(this.active);
      dataOutputStream.writeInt(this.activationMode.ordinal());
   }

   @Override
   public void readConfig(DataInputStream input) throws IOException {
      int payloadLength;
      int storedInput;
      try {
         storedInput = input.readInt();
         payloadLength = input.readInt();
      } catch (EOFException var9) {
         return;
      }

      this.setLegacyCode(storedInput);
      if (payloadLength <= 0) {
         this.captureCurrentValue();
      } else if (payloadLength > input.available()) {
         throw new IOException("Stored value of " + this.getSetting().getDisplayName() + " claims " + payloadLength + " bytes the config does not carry");
      } else {
         byte[] payload = new byte[payloadLength];
         input.readFully(payload);
         this.setActivationPayload(payload);

         try {
            this.shownInHud = input.readBoolean();
         } catch (EOFException var8) {
            this.shownInHud = true;
            this.previousPayload = new byte[0];
            this.active = false;
            return;
         }

         try {
            int previousLength = input.readInt();
            if (previousLength > input.available()) {
               throw new IOException(
                  "Previous value of " + this.getSetting().getDisplayName() + " claims " + previousLength + " bytes the config does not carry"
               );
            }

            if (previousLength > 0) {
               this.previousPayload = new byte[previousLength];
               input.readFully(this.previousPayload);
            } else {
               this.previousPayload = new byte[0];
            }

            this.active = input.readBoolean();
         } catch (EOFException var11) {
            this.previousPayload = new byte[0];
            this.active = false;
            return;
         }

         try {
            int ordinal = input.readInt();
            BindActivationMode[] modes = BindActivationMode.values();
            this.activationMode = ordinal >= 0 && ordinal < modes.length ? modes[ordinal] : BindActivationMode.TOGGLE;
         } catch (EOFException var10) {
            this.activationMode = BindActivationMode.TOGGLE;
            return;
         }

         try {
            input.readBoolean();
         } catch (EOFException var7) {
         }
      }
   }

   public void refreshToggleState() {
      this.synchronizeActiveState();
   }

   @Override
   public void onReleased() {
      if (this.activationMode == BindActivationMode.HOLD) {
         this.deactivate();
      }
   }

   @Override
   public void onPressed() {
      switch(this.activationMode) {
         case TOGGLE:
            this.synchronizeActiveState();
            if (this.active) {
               this.deactivate();
            } else {
               this.activate();
            }
            break;
         case HOLD:
            this.activate();
      }
   }

   private void activate() {
      if (!this.active) {
         this.previousPayload = this.getSetting().copyPayload();
         this.applyActivationValue();
         this.active = true;
      }
   }

   public byte[] getActivationPayload() {
      return (byte[])this.activationPayload.clone();
   }

   public void setBindActivationMode(BindActivationMode bindActivationMode) {
      this.activationMode = bindActivationMode == null ? BindActivationMode.TOGGLE : bindActivationMode;
   }

   public void markEditorInitialized() {
      this.editorInitialized = true;
   }

   public void setActivationPayload(byte[] payload) {
      if (payload != null && payload.length != 0) {
         this.activationPayload = (byte[])payload.clone();
      } else {
         this.activationPayload = this.getSetting().copyPayload();
      }
   }

   public BindActivationMode getBindActivationMode() {
      return this.activationMode;
   }

   public boolean isEditorInitialized() {
      return this.editorInitialized;
   }

   private void restorePreviousValue() {
      if (this.previousPayload != null && this.previousPayload.length != 0) {
         this.getSetting().restorePayload(this.previousPayload);
      }
   }

   public boolean isShownInHud() {
      return this.shownInHud;
   }

   public void resetBindingState() {
      this.deactivate();
      this.clear();
      this.previousPayload = new byte[0];
      this.active = false;
      this.editorInitialized = false;
      this.activationMode = BindActivationMode.TOGGLE;
      this.shownInHud = true;
      this.captureCurrentValue();
   }

   public void captureCurrentValue() {
      this.setActivationPayload(this.getSetting().copyPayload());
   }

   private void synchronizeActiveState() {
      boolean valueApplied = Arrays.equals(this.getSetting().copyPayload(), this.activationPayload);
      if (valueApplied != this.active) {
         if (valueApplied) {
            this.previousPayload = this.getSetting().togglePayload();
            this.active = true;
         } else {
            this.previousPayload = new byte[0];
            this.active = false;
         }
      }
   }

   public void applyActivationValue() {
      this.getSetting().restorePayload(this.activationPayload);
   }

   public void setShownInHud(boolean shownInHud) {
      this.shownInHud = shownInHud;
   }

   private void deactivate() {
      if (this.active) {
         this.restorePreviousValue();
         this.previousPayload = new byte[0];
         this.active = false;
      }
   }

   public boolean isActive() {
      return this.active;
   }
}
