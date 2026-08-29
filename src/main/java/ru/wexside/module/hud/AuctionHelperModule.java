package ru.wexside.module.hud;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ru.wexside.WexSideClient;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.EventBus;
import ru.wexside.event.OutgoingChatEvent;
import ru.wexside.misc.AuctionHighlightSettings;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.server.FunTimeServerContext;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.ColorSetting;
import ru.wexside.setting.ColorSettingBuilder;
import ru.wexside.setting.NumberSetting;
import ru.wexside.setting.NumberSettingBuilder;
import ru.wexside.util.AuctionSlotHighlighter;

public final class AuctionHelperModule extends Module implements ConfigSerializable {
   private final NumberSetting tintSpeed;
   private final BooleanSetting affordable;
   private final BooleanSetting tint;
   private final BooleanSetting onlyWhole;
   private final BooleanSetting calculator;
   private final ColorSetting color;
   private final Pattern pattern;
   private final NumberSetting slotsCount;
   private final BooleanSetting enabledSetting;

   public boolean isEnabled() {
      return this.enabledSetting.isEnabled();
   }

   @Override
   protected void initialize() {
      WexSideClient.getSlotHighlightRegistry().setCallback56(new AuctionSlotHighlighter(this::isEnabled, this::getAuctionHighlightSettings));
      this.listen(OutgoingChatEvent.class, this::onOutgoingChatEvent);
   }

   public BooleanSetting getBooleanSetting() {
      return this.enabledSetting;
   }

   public ColorSetting getColorSetting() {
      return this.color;
   }

   public NumberSetting getNumberSetting() {
      return this.slotsCount;
   }

   public Pattern getPattern() {
      return this.pattern;
   }

   public BooleanSetting getBooleanSetting2() {
      return this.affordable;
   }

   private AuctionHighlightSettings getAuctionHighlightSettings() {
      int n = this.affordable.isEnabled() ? FunTimeServerContext.getBalance() : -1;
      return new AuctionHighlightSettings(
         this.onlyWhole.isEnabled(),
         this.affordable.isEnabled(),
         n,
         this.slotsCount.getIntValue(),
         this.color.getColor(),
         this.tint.isEnabled(),
         this.tintSpeed.getIntValue()
      );
   }

   private static Long compute(String string) {
      try {
         if (string.contains("*")) {
            String[] stringArray = string.split("\\*");
            return Long.parseLong(stringArray[0].trim()) * Long.parseLong(stringArray[1].trim());
         }

         if (string.contains("/")) {
            String[] stringArray = string.split("/");
            long l = Long.parseLong(stringArray[1].trim());
            if (l == 0L) {
               l = 1L;
            }

            return Long.parseLong(stringArray[0].trim()) / l;
         }

         if (string.contains("+")) {
            String[] stringArray = string.split("\\+");
            return Long.parseLong(stringArray[0].trim()) + Long.parseLong(stringArray[1].trim());
         }

         if (string.contains("-")) {
            String[] stringArray = string.split("-");
            return Long.parseLong(stringArray[0].trim()) - Long.parseLong(stringArray[1].trim());
         }
      } catch (Exception var4) {
      }

      return null;
   }

   public BooleanSetting getBooleanSetting3() {
      return this.calculator;
   }

   public BooleanSetting getBooleanSetting4() {
      return this.onlyWhole;
   }

   private String compute2(String string) {
      Matcher matcher = this.pattern.matcher(string);
      StringBuilder stringBuilder = new StringBuilder();

      while(matcher.find()) {
         double d = Double.parseDouble(matcher.group(1).replace(',', '.'));
         long l = 1L;

         for(int i = 0; i < matcher.group(2).length(); ++i) {
            l *= 1000L;
         }

         long l2 = (long)Math.floor(d * (double)l);
         matcher.appendReplacement(stringBuilder, Long.toString(l2));
      }

      matcher.appendTail(stringBuilder);
      return stringBuilder.toString();
   }

   private String compute3(String string) {
      String string2 = this.compute2(string);
      Long l = compute(string2);
      if (l != null) {
         return Long.toString(l);
      } else {
         return string2.matches("-?\\d+") ? string2 : null;
      }
   }

   public BooleanSetting getBooleanSetting5() {
      return this.tint;
   }

   public NumberSetting getNumberSetting2() {
      return this.tintSpeed;
   }

   private void onOutgoingChatEvent(OutgoingChatEvent gameEvent15) {
      if (this.isEnabled() && this.calculator.isEnabled()) {
         String string = gameEvent15.getString();
         if (string != null && string.startsWith("/ah sell ")) {
            String string2 = string.substring("/ah sell ".length()).trim();
            String string3 = this.compute3(string2);
            if (string3 != null && !string3.equals(string2)) {
               gameEvent15.setString("/ah sell " + string3);
            }
         }
      }
   }

   public AuctionHelperModule(EventBus eventBus) {
      super(eventBus, "auction_helper", "Auction Helper", "Помощник на аукционе", ModuleCategory.valueOf("DISPLAY"));
      BooleanSetting toggle4;
      this.enabledSetting = toggle4 = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Enabled")
            .id("enabled")
            .description("")
            .withKeybind()
            .toggle())
         .build();
      this.registerSetting(toggle4);
      NumberSetting number;
      this.slotsCount = number = ((NumberSettingBuilder)NumberSetting.builder()
            .range(1.0, 5.0)
            .defaultValue(1.0)
            .multiplier(1.0)
            .precision(0)
            .animationSpeed(20.0F)
            .name("Slots Count")
            .id("slots_count")
            .description("Количество подсвечиваемых самых дешёвых слотов"))
         .build();
      this.registerSetting(number);
      BooleanSetting toggle3;
      this.onlyWhole = toggle3 = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Whole Items Only")
            .id("only_whole")
            .description("Только целые предметы"))
         .build();
      this.registerSetting(toggle3);
      BooleanSetting toggle2;
      this.affordable = toggle2 = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Balance Filter")
            .id("affordable")
            .description("Учитывать баланс"))
         .build();
      this.registerSetting(toggle2);
      ColorSetting colorSetting = ((ColorSettingBuilder)ColorSetting.builder()
            .selectedIndex(0)
            .name("Cheapest Color")
            .id("color")
            .description("Цвет подсветки самых дешёвых лотов"))
         .build();
      colorSetting.setPrimaryColor(0, -11753627);
      colorSetting.setPrimaryColor(1, -1543135);
      colorSetting.setPrimaryColor(2, -9279489);
      colorSetting.setPrimaryColor(3, -46001);
      colorSetting.setPrimaryColor(4, -13218);
      colorSetting.setPrimaryColor(5, -10582785);
      colorSetting.setPrimaryColor(6, -2732032);
      this.color = colorSetting;
      this.registerSetting(colorSetting);
      BooleanSetting toggle;
      this.tint = toggle = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Tint")
            .id("tint")
            .description("Мигание подсветки"))
         .build();
      this.registerSetting(toggle);
      NumberSetting numberSetting;
      this.tintSpeed = numberSetting = ((NumberSettingBuilder)NumberSetting.builder()
            .range(1.0, 10.0)
            .defaultValue(5.0)
            .multiplier(1.0)
            .precision(0)
            .animationSpeed(20.0F)
            .name("Tint Speed")
            .id("tint_speed")
            .description("Скорость мигания")
            .visibleWhen(toggle::isEnabled))
         .build();
      this.registerSetting(numberSetting);
      BooleanSetting booleanSetting;
      this.calculator = booleanSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Calculator")
            .id("calculator")
            .description("В /ah sell разворачивает 1kk → 1000000 и считает * / + -"))
         .build();
      this.registerSetting(booleanSetting);
      this.pattern = Pattern.compile("(\\d+(?:[.,]\\d+)?)([kKкК]+)");
   }
}
