package ru.wexside.misc;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.SecureRandom;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import ru.wexside.util.BinaryCodec;

public final class EncryptedConfigIO {
   private static final String ENCRYPTED_VALUES_PROPERTY = "sec";
   private static final int AUTHENTICATION_TAG_BITS = 128;
   private static final int NONCE_LENGTH = 12;
   private static final SecureRandom RANDOM = new SecureRandom();
   private static final byte[] ENCRYPTION_KEY = createEncryptionKey();

   private EncryptedConfigIO() {
   }

   public static void writeConfig(File file, JsonObject config, Set<String> publicProperties, Gson gson) throws IOException {
      Files.createDirectories(file.toPath().getParent());
      JsonObject publicValues = new JsonObject();
      JsonObject privateValues = new JsonObject();

      for(Entry<String, JsonElement> entry : config.entrySet()) {
         (publicProperties.contains(entry.getKey()) ? publicValues : privateValues).add(entry.getKey(), (JsonElement)entry.getValue());
      }

      byte[] encryptedValues = encrypt(gson.toJson(privateValues).getBytes(StandardCharsets.UTF_8), serializeAssociatedData(publicValues));
      publicValues.addProperty("sec", BinaryCodec.encodeBase64(encryptedValues));
      File temporaryFile = new File(file.getParentFile(), file.getName() + ".tmp");
      Files.writeString(temporaryFile.toPath(), gson.toJson(publicValues), StandardCharsets.UTF_8);

      try {
         Files.move(temporaryFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
      } catch (IOException var11) {
         try {
            Files.move(temporaryFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
         } catch (IOException var10) {
            var10.addSuppressed(var11);
            throw var10;
         }
      }
   }

   public static ConfigReadResult readConfig(File file, Gson gson) {
      if (!file.exists()) {
         return new ConfigReadResult(new JsonObject(), false);
      } else {
         try {
            JsonObject storedValues = (JsonObject)gson.fromJson(Files.readString(file.toPath(), StandardCharsets.UTF_8), JsonObject.class);
            if (storedValues == null) {
               throw new IllegalStateException("Config file is empty: " + file);
            } else if (!storedValues.has("sec")) {
               return new ConfigReadResult(storedValues, true);
            } else {
               JsonObject publicValues = storedValues.deepCopy();
               publicValues.remove("sec");
               byte[] decryptedValues = decrypt(BinaryCodec.decodeBase64(storedValues.get("sec").getAsString()), serializeAssociatedData(publicValues));
               JsonObject privateValues = (JsonObject)gson.fromJson(new String(decryptedValues, StandardCharsets.UTF_8), JsonObject.class);
               JsonObject mergedValues = publicValues.deepCopy();

               for(Entry<String, JsonElement> entry : privateValues.entrySet()) {
                  mergedValues.add(entry.getKey(), (JsonElement)entry.getValue());
               }

               return new ConfigReadResult(mergedValues, false);
            }
         } catch (Exception var9) {
            throw new IllegalStateException("Failed to read config " + file, var9);
         }
      }
   }

   private static byte[] serializeAssociatedData(JsonObject values) {
      TreeMap<String, String> sortedValues = new TreeMap<>();

      for(Entry<String, JsonElement> entry : values.entrySet()) {
         sortedValues.put(entry.getKey(), ((JsonElement)entry.getValue()).toString());
      }

      StringBuilder result = new StringBuilder();
      sortedValues.forEach((key, value) -> result.append(key).append('=').append(value).append('\n'));
      return result.toString().getBytes(StandardCharsets.UTF_8);
   }

   private static byte[] decrypt(byte[] encryptedData, byte[] associatedData) throws Exception {
      Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
      byte[] nonce = BinaryCodec.slice(encryptedData, 0, 12);
      cipher.init(2, new SecretKeySpec(ENCRYPTION_KEY, "AES"), new GCMParameterSpec(128, nonce));
      cipher.updateAAD(associatedData);
      return cipher.doFinal(BinaryCodec.slice(encryptedData, 12, encryptedData.length - 12));
   }

   private static byte[] encrypt(byte[] plainData, byte[] associatedData) throws IOException {
      try {
         byte[] nonce = new byte[12];
         RANDOM.nextBytes(nonce);
         Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
         cipher.init(1, new SecretKeySpec(ENCRYPTION_KEY, "AES"), new GCMParameterSpec(128, nonce));
         cipher.updateAAD(associatedData);
         return BinaryCodec.concatenate(nonce, cipher.doFinal(plainData));
      } catch (Exception var4) {
         throw new IOException("Failed to encrypt local configuration", var4);
      }
   }

   private static byte[] createEncryptionKey() {
      byte[] seed = new byte[]{119, 88, 33, -102, 60, -15, 5, -66, 100, 45, -113, -64};
      return BinaryCodec.sha256(
         BinaryCodec.concatenate(BinaryCodec.littleEndianLong(-7046029254386353131L), seed, BinaryCodec.littleEndianLong(-3335678366873096957L))
      );
   }
}
