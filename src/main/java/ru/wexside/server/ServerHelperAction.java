package ru.wexside.server;

import java.util.List;
import java.util.Objects;
import net.minecraft.class_1792;

public record ServerHelperAction(
   String id,
   String displayName,
   String bindLabel,
   class_1792 icon,
   int color,
   String generalServerTag,
   List<String> alternateServerTags,
   boolean donationItem,
   boolean splashPotion,
   float activationDistance,
   boolean debuff,
   boolean matchByItem
) {
   public ServerHelperAction(
      String id,
      String displayName,
      String bindLabel,
      class_1792 icon,
      int color,
      String generalServerTag,
      List<String> alternateServerTags,
      boolean donationItem,
      boolean splashPotion,
      float activationDistance,
      boolean debuff,
      boolean matchByItem
   ) {
      Objects.requireNonNull(id, "id");
      Objects.requireNonNull(displayName, "displayName");
      Objects.requireNonNull(bindLabel, "bindLabel");
      Objects.requireNonNull(icon, "icon");
      generalServerTag = generalServerTag == null ? "" : generalServerTag;
      alternateServerTags = List.copyOf(alternateServerTags == null ? List.of() : alternateServerTags);
      this.id = id;
      this.displayName = displayName;
      this.bindLabel = bindLabel;
      this.icon = icon;
      this.color = color;
      this.generalServerTag = generalServerTag;
      this.alternateServerTags = alternateServerTags;
      this.donationItem = donationItem;
      this.splashPotion = splashPotion;
      this.activationDistance = activationDistance;
      this.debuff = debuff;
      this.matchByItem = matchByItem;
   }

   public String selectorLabel() {
      return this.alternateServerTags.isEmpty() ? this.displayName : this.alternateServerTags.getFirst();
   }
}
