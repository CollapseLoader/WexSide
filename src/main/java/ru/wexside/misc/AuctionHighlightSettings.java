package ru.wexside.misc;

public final class AuctionHighlightSettings {
   public final int slot;
   public final int slot2;
   public final boolean enabled;
   public final int slot3;
   public final boolean enabled2;
   public final boolean enabled3;
   public final int slot4;

   public AuctionHighlightSettings(boolean bl, boolean bl2, int n, int n2, int n3, boolean bl3, int n4) {
      this.enabled3 = bl;
      this.enabled = bl2;
      this.slot2 = n;
      this.slot4 = n2;
      this.slot = n3;
      this.enabled2 = bl3;
      this.slot3 = n4;
   }
}
