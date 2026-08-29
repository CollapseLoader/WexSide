package ru.wexside.market;

import java.util.List;
import java.util.Locale;
import net.minecraft.class_1799;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_437;

public final class AuctionPriceParser {
   private static final String PURCHASE_MARKER = "чтобы купить";

   public AuctionPrice parse(class_1799 stack, class_310 client) {
      if (stack != null && !stack.method_7960() && client != null) {
         List<class_2561> tooltip = class_437.method_25408(client, stack);
         boolean auctionItem = false;
         Integer totalPrice = null;
         Integer unitPrice = null;
         Integer dollarPrice = null;

         for(class_2561 line : tooltip) {
            String text = line.getString();
            String lowerCase = text.toLowerCase(Locale.ROOT);
            auctionItem |= lowerCase.contains("чтобы купить");
            if (lowerCase.contains("за 1 ед")) {
               unitPrice = parseAfterColon(text);
            } else if (lowerCase.contains("цена")) {
               totalPrice = parseAfterColon(text);
            } else {
               int dollarIndex = text.indexOf(36);
               if (dollarIndex >= 0) {
                  dollarPrice = parseDigits(text.substring(dollarIndex + 1));
               }
            }
         }

         if (!auctionItem) {
            return null;
         } else {
            if (totalPrice == null) {
               totalPrice = dollarPrice;
            }

            int count = Math.max(1, stack.method_7947());
            if (unitPrice == null && totalPrice != null) {
               unitPrice = totalPrice / count;
            }

            if (unitPrice == null) {
               return null;
            } else {
               if (totalPrice == null) {
                  totalPrice = (int)Math.min((long)unitPrice.intValue() * (long)count, 2147483647L);
               }

               return new AuctionPrice(totalPrice, unitPrice);
            }
         }
      } else {
         return null;
      }
   }

   public boolean isAuctionItem(class_1799 stack, class_310 client) {
      return stack != null && !stack.method_7960() && client != null
         ? class_437.method_25408(client, stack)
            .stream()
            .<String>map(class_2561::getString)
            .map(text -> text.toLowerCase(Locale.ROOT))
            .anyMatch(text -> text.contains("чтобы купить"))
         : false;
   }

   private static Integer parseAfterColon(String text) {
      int colonIndex = text.lastIndexOf(58);
      return parseDigits(colonIndex >= 0 ? text.substring(colonIndex + 1) : text);
   }

   private static Integer parseDigits(String text) {
      String digits = text.replaceAll("\\D", "");
      if (digits.isEmpty()) {
         return null;
      } else {
         try {
            return (int)Math.min(Long.parseLong(digits), 2147483647L);
         } catch (NumberFormatException var3) {
            return null;
         }
      }
   }
}
