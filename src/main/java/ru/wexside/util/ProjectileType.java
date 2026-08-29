package ru.wexside.util;

import net.minecraft.class_1297;
import net.minecraft.class_1542;
import net.minecraft.class_1665;
import net.minecraft.class_1680;
import net.minecraft.class_1681;
import net.minecraft.class_1683;
import net.minecraft.class_1684;
import net.minecraft.class_1685;
import net.minecraft.class_1686;
import net.minecraft.class_1753;
import net.minecraft.class_1764;
import net.minecraft.class_1771;
import net.minecraft.class_1776;
import net.minecraft.class_1779;
import net.minecraft.class_1792;
import net.minecraft.class_1812;
import net.minecraft.class_1823;
import net.minecraft.class_1835;

public enum ProjectileType {
   TRIDENT,
   PEARL,
   ARROW,
   CROSSBOW,
   POTION,
   ITEM;

   public static ProjectileType fromItem(class_1792 item) {
      if (item instanceof class_1835) {
         return TRIDENT;
      } else if (item instanceof class_1776 || item instanceof class_1823 || item instanceof class_1771) {
         return PEARL;
      } else if (item instanceof class_1753) {
         return ARROW;
      } else if (item instanceof class_1764) {
         return CROSSBOW;
      } else {
         return !(item instanceof class_1812) && !(item instanceof class_1779) ? null : POTION;
      }
   }

   public static ProjectileType fromEntity(class_1297 entity) {
      if (entity instanceof class_1685) {
         return TRIDENT;
      } else if (entity instanceof class_1684 || entity instanceof class_1680 || entity instanceof class_1681) {
         return PEARL;
      } else if (entity instanceof class_1686 || entity instanceof class_1683) {
         return POTION;
      } else if (entity instanceof class_1665) {
         return ARROW;
      } else {
         return entity instanceof class_1542 ? ITEM : null;
      }
   }
}
