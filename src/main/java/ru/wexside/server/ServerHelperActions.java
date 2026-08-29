package ru.wexside.server;

import java.util.List;
import net.minecraft.class_1792;
import net.minecraft.class_1802;

public final class ServerHelperActions {
   public static final ServerHelperAction GODS_AURA = action(
      "GodsAura", "Божья аура", class_1802.field_8137, -732322, "Божья аура", List.of("Божья аура", "God's Aura"), false, false, 0.0F, false, false
   );
   public static final ServerHelperAction TRAP = action(
      "Trap", "Трапка", class_1802.field_8786, -2631721, "Трапка", List.of("Трапка", "Trap"), false, false, 4.0F, true, false
   );
   public static final ServerHelperAction PLAST = action(
      "Plast", "Пласт", class_1802.field_8777, -8921737, "Пласт", List.of("Пласт", "Plast"), false, false, 4.0F, true, false
   );
   public static final ServerHelperAction DISORIENTATION = action(
      "Disorientation",
      "Дезориентация",
      class_1802.field_8449,
      -6595099,
      "Дезориентация",
      List.of("Дезориентация", "Disorientation"),
      false,
      false,
      5.0F,
      true,
      false
   );
   public static final ServerHelperAction VISIBLE_DUST = action(
      "VisibleDust", "Явная пыль", class_1802.field_8601, -11930, "Явная пыль", List.of("Явная пыль", "Visible Dust"), false, false, 5.0F, true, false
   );
   public static final ServerHelperAction FREEZE_BALL = action(
      "FreezeBall",
      "Снежок заморозки",
      class_1802.field_8543,
      -8333057,
      "Снежок заморозки",
      List.of("Снежок заморозки", "Freeze Ball"),
      false,
      false,
      5.0F,
      true,
      false
   );
   public static final ServerHelperAction FIERY_TORNADO = action(
      "FieryTornado",
      "Огненный смерч",
      class_1802.field_8814,
      -38091,
      "Огненный смерч",
      List.of("Огненный смерч", "Fiery Tornado"),
      false,
      false,
      6.0F,
      true,
      false
   );
   public static final ServerHelperAction WIND_CHARGE = action(
      "WindCharge", "Wind Charge", class_1802.field_49098, -1509377, "Wind Charge", List.of("Wind Charge", "Заряд ветра"), false, false, 6.0F, false, true
   );
   public static final ServerHelperAction ASSASSIN_POTION = potion("PotionAssassin", "Зелье Ассасина", -9675545, false);
   public static final ServerHelperAction HOLY_WATER = potion("PotionHolyWater", "Святая Вода", -9127425, false);
   public static final ServerHelperAction RAGE_POTION = potion("PotionRage", "Зелье Гнева", -2740175, false);
   public static final ServerHelperAction PALADIN_POTION = potion("PotionPaladin", "Зелье Палладина", -144530, false);
   public static final ServerHelperAction POPPER = potion("PotionPopper", "Хлопушка", -35211, true);
   public static final ServerHelperAction RADIATION_POTION = potion("PotionRadiation", "Зелье Радиации", -11145276, true);
   public static final ServerHelperAction DROWSINESS_POTION = potion("PotionDrowsiness", "Зелье Снотворного", -6120450, true);
   public static final List<ServerHelperAction> ALL = List.of(
      GODS_AURA,
      TRAP,
      PLAST,
      DISORIENTATION,
      VISIBLE_DUST,
      FREEZE_BALL,
      FIERY_TORNADO,
      WIND_CHARGE,
      ASSASSIN_POTION,
      HOLY_WATER,
      RAGE_POTION,
      PALADIN_POTION,
      POPPER,
      RADIATION_POTION,
      DROWSINESS_POTION
   );
   public static final List<ServerHelperAction> COOLDOWN_TRACKED = ALL.stream()
      .filter(action -> action.activationDistance() > 0.0F && !action.donationItem())
      .toList();

   private ServerHelperActions() {
   }

   public static ServerHelperAction byId(String id) {
      return id == null ? null : ALL.stream().filter(action -> action.id().equals(id)).findFirst().orElse(null);
   }

   private static ServerHelperAction action(
      String id,
      String displayName,
      class_1792 icon,
      int color,
      String generalTag,
      List<String> alternateTags,
      boolean donationItem,
      boolean splashPotion,
      float distance,
      boolean debuff,
      boolean matchByItem
   ) {
      return new ServerHelperAction(
         id, displayName, displayName, icon, color, generalTag, alternateTags, donationItem, splashPotion, distance, debuff, matchByItem
      );
   }

   private static ServerHelperAction potion(String id, String name, int color, boolean debuff) {
      return action(id, name, class_1802.field_8436, color, name, List.of(name), true, true, 5.0F, debuff, false);
   }
}
