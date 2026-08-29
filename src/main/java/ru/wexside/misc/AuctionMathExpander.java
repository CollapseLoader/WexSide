package ru.wexside.misc;

import java.util.function.BooleanSupplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ru.wexside.event.OutgoingChatEvent;

public class AuctionMathExpander {
   private final BooleanSupplier field12;
   private final String[] field16;
   private final Pattern field20;

   public AuctionMathExpander() {
      this(() -> true);
   }

   public AuctionMathExpander(BooleanSupplier enabled) {
      this.field12 = enabled == null ? () -> false : enabled;
      this.field16 = new String[]{"/ah", "/ah ", "ah "};
      this.field20 = Pattern.compile("(\\d+)\\s*[*x×]\\s*(\\d+)");
   }

   private String process(String string) {
      Matcher matcher = this.field20.matcher(string);
      StringBuilder stringBuilder = new StringBuilder();

      while(matcher.find()) {
         try {
            long l = Long.parseLong(matcher.group(1));
            long l2 = Long.parseLong(matcher.group(2));
            long l3 = l * l2;
            matcher.appendReplacement(stringBuilder, Long.toString(l3));
         } catch (NumberFormatException var10) {
            matcher.appendReplacement(stringBuilder, Matcher.quoteReplacement(matcher.group(0)));
         }
      }

      matcher.appendTail(stringBuilder);
      return stringBuilder.toString();
   }

   private boolean process2(String string) {
      for(String string2 : this.field16) {
         if (string.startsWith(string2)) {
            return true;
         }
      }

      return false;
   }

   public void onOutgoingChat(OutgoingChatEvent gameEvent15) {
      if (this.field12.getAsBoolean()) {
         String string = gameEvent15.getString();
         if (string != null && this.process2(string)) {
            String string2 = this.process(string);
            if (!string2.equals(string)) {
               gameEvent15.setString(string2);
            }
         }
      }
   }
}
