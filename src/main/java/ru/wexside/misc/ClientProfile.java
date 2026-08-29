package ru.wexside.misc;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Objects;
import ru.wexside.module.ModuleState;

public final class ClientProfile {
   private final ClientRole role;
   private final String username;
   private final String expirationDate;
   private final int uid;
   private final EnumSet<ModuleState> tiers;
   private final TextureResource avatarTexture;
   private final byte[] avatarBytes;

   public ClientProfile(
      ClientRole role, String username, String expirationDate, int uid, EnumSet<ModuleState> tiers, TextureResource avatarTexture, byte[] avatarBytes
   ) {
      this.role = Objects.requireNonNull(role, "role");
      this.username = Objects.requireNonNull(username, "username");
      this.expirationDate = Objects.requireNonNull(expirationDate, "expirationDate");
      this.uid = uid;
      this.tiers = tiers.isEmpty() ? EnumSet.noneOf(ModuleState.class) : EnumSet.copyOf(tiers);
      this.avatarTexture = avatarTexture;
      this.avatarBytes = avatarBytes == null ? new byte[0] : (byte[])avatarBytes.clone();
   }

   public static ClientProfile fromSerializedFields(
      String username, String roleName, String expirationDate, String uid, boolean enabledTier, boolean alwaysTier, byte[] avatarBytes
   ) {
      EnumSet<ModuleState> tiers = EnumSet.of(ModuleState.DISABLED);
      if (enabledTier) {
         tiers.add(ModuleState.ENABLED);
      }

      if (alwaysTier) {
         tiers.add(ModuleState.ALWAYS);
      }

      return new ClientProfile(
         ClientRole.valueOf(roleName.toUpperCase()), username, expirationDate, Integer.parseInt(uid), tiers, decodeAvatar(avatarBytes), avatarBytes
      );
   }

   private static TextureResource decodeAvatar(byte[] bytes) {
      if (bytes != null && bytes.length != 0) {
         ByteBuffer buffer = ByteBuffer.allocateDirect(bytes.length);
         buffer.put(bytes).flip();
         return new TextureResource(buffer);
      } else {
         return null;
      }
   }

   public ClientRole getRole() {
      return this.role;
   }

   public String getUsername() {
      return this.username;
   }

   public String getExpirationDate() {
      return this.expirationDate;
   }

   public int getUid() {
      return this.uid;
   }

   public EnumSet<ModuleState> getTiers() {
      return this.tiers.isEmpty() ? EnumSet.noneOf(ModuleState.class) : EnumSet.copyOf(this.tiers);
   }

   public boolean hasTier(ModuleState tier) {
      return this.tiers.contains(tier);
   }

   public TextureResource getAvatarTexture() {
      return this.avatarTexture;
   }

   public byte[] getAvatarBytes() {
      return (byte[])this.avatarBytes.clone();
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (!(object instanceof ClientProfile)) {
         return false;
      } else {
         ClientProfile other = (ClientProfile)object;
         return this.uid == other.uid
            && this.role == other.role
            && this.username.equals(other.username)
            && this.expirationDate.equals(other.expirationDate)
            && this.tiers.equals(other.tiers)
            && Objects.equals(this.avatarTexture, other.avatarTexture)
            && Arrays.equals(this.avatarBytes, other.avatarBytes);
      }
   }

   @Override
   public int hashCode() {
      return 31 * Objects.hash(this.role, this.username, this.expirationDate, this.uid, this.tiers, this.avatarTexture) + Arrays.hashCode(this.avatarBytes);
   }

   @Override
   public String toString() {
      return "ClientProfile[role="
         + this.role
         + ", username="
         + this.username
         + ", expirationDate="
         + this.expirationDate
         + ", uid="
         + this.uid
         + ", tiers="
         + this.tiers
         + "]";
   }
}
