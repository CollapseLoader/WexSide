package ru.wexside.misc;

import java.util.List;

public record PotionPreset(String name, int keyCode, boolean favorite, List<String> potionIds) {
   public PotionPreset(String name, int keyCode, boolean favorite, List<String> potionIds) {
      name = name == null ? "" : name;
      potionIds = potionIds == null ? List.of() : List.copyOf(potionIds);
      this.name = name;
      this.keyCode = keyCode;
      this.favorite = favorite;
      this.potionIds = potionIds;
   }
}
