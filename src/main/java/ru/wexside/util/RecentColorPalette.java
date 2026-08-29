package ru.wexside.util;

public final class RecentColorPalette {
   private final int[] colors;

   public RecentColorPalette(int... colors) {
      this.colors = colors != null && colors.length != 0 ? (int[])colors.clone() : new int[]{-1};
   }

   public int[] getColors() {
      return this.colors;
   }

   public int size() {
      return this.colors.length;
   }

   public int getColor(int index) {
      return this.colors[Math.clamp((long)index, 0, this.colors.length - 1)];
   }

   public void addColor(int color) {
      for(int existing : this.colors) {
         if (existing == color) {
            return;
         }
      }

      System.arraycopy(this.colors, 0, this.colors, 1, this.colors.length - 1);
      this.colors[0] = color;
   }

   public RecentColorPalette copy() {
      return new RecentColorPalette(this.colors);
   }
}
