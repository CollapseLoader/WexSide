package ru.wexside.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.class_1799;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_437;

public final class MarketTooltipParser {
   private static final Pattern NUMBER = Pattern.compile("\\d+");
   private static final String BUY_PROMPT = "нажмите, чтобы купить";

   public MarketTooltipParser.DonMarketOffer parseDonMarketOffer(class_1799 stack, class_310 client) {
      if (stack != null && !stack.method_7960() && client != null) {
         Integer balance = null;
         Integer exchangeRate = null;
         Integer price = null;
         boolean purchasable = false;

         for(class_2561 line : class_437.method_25408(client, stack)) {
            String text = line.getString().toLowerCase();
            if (text.contains("биржа баланс:")) {
               balance = firstNumber(text);
            } else if (text.contains("курс:")) {
               exchangeRate = firstNumber(text);
            } else if (text.contains("цена:")) {
               price = firstNumber(text);
            } else if (text.contains("нажмите, чтобы купить")) {
               purchasable = true;
            }
         }

         return purchasable && balance != null && exchangeRate != null && price != null && balance >= price
            ? new MarketTooltipParser.DonMarketOffer(balance, exchangeRate, price)
            : null;
      } else {
         return null;
      }
   }

   public Integer parseAuctionPrice(class_1799 stack, class_310 client) {
      if (stack != null && !stack.method_7960() && client != null) {
         boolean purchasable = false;
         Integer highestDollarValue = null;

         for(class_2561 line : class_437.method_25408(client, stack)) {
            String text = line.getString();
            if (text.toLowerCase().contains("нажмите, чтобы купить")) {
               purchasable = true;
            }

            Integer value;
            int dollar;
            if ((dollar = text.indexOf(36)) >= 0
               && (value = parseDigits(text.substring(dollar + 1))) != null
               && (highestDollarValue == null || value > highestDollarValue)) {
               highestDollarValue = value;
            }
         }

         return purchasable ? highestDollarValue : null;
      } else {
         return null;
      }
   }

   public Integer parseUnitAuctionPrice(class_1799 stack, class_310 client) {
      Integer total = this.parseAuctionPrice(stack, client);
      return total == null ? null : total / Math.max(1, stack.method_7947());
   }

   private static Integer firstNumber(String text) {
      Matcher matcher = NUMBER.matcher(text);
      return matcher.find() ? parseDigits(matcher.group()) : null;
   }

   private static Integer parseDigits(String text) {
      String digits = text.replaceAll("\\D", "");
      if (digits.isEmpty()) {
         return null;
      } else {
         try {
            return Integer.parseInt(digits);
         } catch (NumberFormatException var3) {
            return null;
         }
      }
   }

   public static record DonMarketOffer(int balance, int exchangeRate, int price) {
   }
}
