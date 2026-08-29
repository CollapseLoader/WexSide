package ru.wexside.misc;

public final class HotbarSelectAction implements InventoryAction {
   public final int slot;
   public final boolean use;

   public HotbarSelectAction(int slot, boolean use) {
      this.slot = slot;
      this.use = use;
   }

   public int slot() {
      return this.slot;
   }

   public boolean useAfterSelect() {
      return this.use;
   }
}
