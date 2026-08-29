package ru.wexside.misc;

import java.io.IOException;
import java.util.Locale;

public class PasswordStore {
   private final PasswordConfigStore configStore;

   public PasswordStore(PasswordConfigStore configStore) {
      this.configStore = configStore;
   }

   public void savePassword(String serverAddress, String username, String password) {
      String normalizedUsername = normalizeUsername(username);
      if (!normalizedUsername.isEmpty() && password != null && !password.isBlank()) {
         this.configStore.getPasswords().put(createAccountKey(serverAddress, username), password);
         this.persist();
      }
   }

   public String getPassword(String serverAddress, String username) {
      return normalizeUsername(username).isEmpty() ? null : this.configStore.getPasswords().get(createAccountKey(serverAddress, username));
   }

   private static String normalizeUsername(String username) {
      return username == null ? "" : username.trim().toLowerCase(Locale.ROOT);
   }

   private void persist() {
      try {
         this.configStore.save();
      } catch (IOException var2) {
      }
   }

   public static String normalizeServerAddress(String serverAddress) {
      if (serverAddress == null) {
         return "";
      } else {
         String normalizedAddress = serverAddress.trim().toLowerCase(Locale.ROOT);
         int portSeparator = normalizedAddress.indexOf(58);
         if (portSeparator >= 0) {
            normalizedAddress = normalizedAddress.substring(0, portSeparator);
         }

         if (!normalizedAddress.isEmpty() && !normalizedAddress.matches("[0-9.]+")) {
            if (normalizedAddress.split("\\.").length >= 3) {
               normalizedAddress = normalizedAddress.substring(normalizedAddress.indexOf(46) + 1);
            }

            return normalizedAddress;
         } else {
            return normalizedAddress;
         }
      }
   }

   private static String createAccountKey(String serverAddress, String username) {
      String normalizedAddress = normalizeServerAddress(serverAddress);
      String normalizedUsername = normalizeUsername(username);
      return normalizedAddress.isEmpty() ? normalizedUsername : normalizedAddress + "|" + normalizedUsername;
   }
}
