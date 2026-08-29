package ru.wexside.module.player;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.class_1799;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_437;
import ru.wexside.WexSideClient;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.event.HudRenderEvent;
import ru.wexside.event.OutgoingChatEvent;
import ru.wexside.misc.AuctionMathExpander;
import ru.wexside.misc.ItemBindBox;
import ru.wexside.misc.ServerHelperShulker;
import ru.wexside.misc.ServerKind;
import ru.wexside.misc.SwapTiming;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.server.LeastPopulatedServerSelector;
import ru.wexside.server.ServerHelperAction;
import ru.wexside.server.ServerHelperActions;
import ru.wexside.setting.BindSetting;
import ru.wexside.setting.BindSettingBuilder;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.ColorSetting;
import ru.wexside.setting.ColorSettingBuilder;
import ru.wexside.setting.ModeSetting;
import ru.wexside.setting.ModeSettingBuilder;
import ru.wexside.setting.MultiSelectSetting;
import ru.wexside.setting.MultiSelectSettingBuilder;
import ru.wexside.setting.NumberSetting;
import ru.wexside.setting.NumberSettingBuilder;
import ru.wexside.util.CheapestAuctionHighlighter;
import ru.wexside.util.DonMarketHighlighter;
import ru.wexside.util.FTSnap;
import ru.wexside.util.ItemStatusHudElement;

public class ServerHelperModule extends Module implements ConfigSerializable {
   private static final String[] ACTION_NAMES = new String[]{
      "Божья аура",
      "Трапка",
      "Пласт",
      "Дезориентация",
      "Явная пыль",
      "Снежок заморозки",
      "Огненный смерч",
      "Wind Charge",
      "Зелье Ассасина",
      "Святая Вода",
      "Зелье Гнева",
      "Зелье Палладина",
      "Хлопушка",
      "Зелье Радиации",
      "Зелье Снотворного"
   };
   static volatile ServerHelperModule serverHelperModule2;
   private final BooleanSetting enabledSetting;
   private final ModeSetting serverMode;
   private final ModeSetting swapMode;
   private final BooleanSetting fromBundle;
   private final BooleanSetting ftMode;
   private final BooleanSetting noLeftHandPlace;
   private final BindSetting openShulker;
   private final BindSetting radialSelector;
   private final MultiSelectSetting selectorItems;
   private final ModeSetting windChargeMode;
   private final BooleanSetting windChargeAutoJump;
   private final ModeSetting buffThrowMode;
   private final BooleanSetting auctionCalculator;
   private final BooleanSetting visualizer;
   private final BooleanSetting visualizerAll;
   private final MultiSelectSetting visualizerItems;
   private final BooleanSetting highlightEmpty;
   private final BooleanSetting visualizePotions;
   private final BooleanSetting highlightCheapest;
   private final ColorSetting cheapestColor;
   private final NumberSetting cheapestCount;
   private final BooleanSetting highlightBestOffer;
   private final ColorSetting bestOfferColor;
   private final NumberSetting bestOfferCount;
   private final Map<String, BindSetting> actionBinds = new LinkedHashMap<>();
   private final LeastPopulatedServerSelector serverSelector = new LeastPopulatedServerSelector();
   private ItemStatusHudElement inventoryHUD2Impl;
   private ServerHelperShulker serverHelperShulker;
   private AuctionMathExpander auctionMathExpander;
   private FTSnap fTSnap;

   public ServerHelperModule(EventBus eventBus) {
      super(
         eventBus, "server_helper", "Server Helper", "Помощник серверов FT/Others: предметы, шалкер, калькулятор, селектор", ModuleCategory.valueOf("PLAYER")
      );
      serverHelperModule2 = this;
      this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Enabled")
            .id("enabled")
            .description("")
            .withKeybind()
            .toggle())
         .build();
      this.registerSetting(this.enabledSetting);
      this.serverMode = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder()
            .options("FT", "Others")
            .defaultOption("FT")
            .name("Server Mode")
            .id("server_mode")
            .description("Режим сервера"))
         .build();
      this.registerSetting(this.serverMode);
      this.swapMode = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder()
            .options("Default", "Legit", "FS")
            .defaultOption("Default")
            .name("Mode")
            .id("swap_mode")
            .description("Скорость свапа: Default - мгновенно, Legit - с задержкой"))
         .build();
      this.registerSetting(this.swapMode);
      this.fromBundle = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(true)
            .defaultValue(false)
            .name("Из мешков")
            .id("from_bundle")
            .description("Доставать предмет из мешка если нет в инвентаре"))
         .build();
      this.registerSetting(this.fromBundle);
      this.ftMode = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("FT-Mode")
            .id("ft_mode")
            .description("Поддержка мешков без лимита вместимости")
            .visibleWhen(this.fromBundle::isEnabled))
         .build();
      this.registerSetting(this.ftMode);
      this.noLeftHandPlace = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("No Left Hand Place")
            .id("no_left_hand_place")
            .description("Не ставить сферы (голову) из офф-хенда (левой руки)"))
         .build();
      this.registerSetting(this.noLeftHandPlace);
      this.openShulker = ((BindSettingBuilder)BindSetting.getBindSettingBuilder()
            .keyboard(0)
            .onReleased(ignored -> this.openShulker())
            .name("Open Shulker")
            .id("open_shulker")
            .description("Кнопка открытия шалкера в инвентаре"))
         .build();
      this.registerSetting(this.openShulker);
      this.radialSelector = ((BindSettingBuilder)BindSetting.getBindSettingBuilder()
            .keyboard(0)
            .onReleased(ignored -> this.openRadialSelector())
            .name("Radial Selector")
            .id("radial_selector")
            .description("Круговой селектор предметов"))
         .build();
      this.registerSetting(this.radialSelector);
      MultiSelectSetting selectorSetting = ((MultiSelectSettingBuilder)MultiSelectSetting.getMultiSelectSettingBuilder()
            .options(ACTION_NAMES)
            .selectAll(false)
            .optionListEnabled(false)
            .name("Selector Items")
            .id("selector_items")
            .description("Предметы, отобража-\nемые в круговом\nселекторе"))
         .build();
      this.selectorItems = selectorSetting;
      this.registerSetting(selectorSetting);
      this.registerAction("GodsAura", "action_gods_aura", "Использовать: Божья аура", 0);
      this.registerAction("Trap", "action_trap", "Использовать: Трапка", 1);
      this.registerAction("Plast", "action_plast", "Использовать: Пласт", 2);
      this.registerAction("Disorientation", "action_disorientation", "Использовать: Дезориентация", 3);
      this.registerAction("VisibleDust", "action_visible_dust", "Использовать: Явная пыль", 4);
      this.registerAction("FreezeBall", "action_freezeball", "Использовать: Снежок заморозки", 5);
      this.registerAction("FieryTornado", "action_fiery_tornado", "Использовать: Огненный смерч", 6);
      this.registerAction("WindCharge", "action_wind_charge", "Использовать: Wind Charge", 7);
      this.windChargeMode = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder()
            .options("Под себя", "По прицелу")
            .defaultOption("Под себя")
            .name("Wind Charge")
            .id("wind_charge_mode")
            .description("Направление броска снаряда ветра"))
         .build();
      this.registerSetting(this.windChargeMode);
      this.windChargeAutoJump = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Auto Jump")
            .id("wind_charge_auto_jump")
            .description("Подпрыгивание в момент броска под себя для большей высоты")
            .visibleWhen(() -> "Под себя".equals(this.windChargeMode.getSelectedOption())))
         .build();
      this.registerSetting(this.windChargeAutoJump);
      this.buffThrowMode = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder()
            .options("По прицелу", "Под себя")
            .defaultOption("По прицелу")
            .name("Куда бросать бафы")
            .id("buff_throw_mode")
            .description("Направление броска бафф-зелий (дебаффы всегда по прицелу)"))
         .build();
      this.registerSetting(this.buffThrowMode);
      this.registerAction("PotionAssassin", "action_potion_assassin", "Использовать: Зелье Ассасина", 8);
      this.registerAction("PotionHolyWater", "action_potion_holy_water", "Использовать: Святая Вода", 9);
      this.registerAction("PotionRage", "action_potion_rage", "Использовать: Зелье Гнева", 10);
      this.registerAction("PotionPaladin", "action_potion_paladin", "Использовать: Зелье Палладина", 11);
      this.registerAction("PotionPopper", "action_potion_popper", "Использовать: Хлопушка", 12);
      this.registerAction("PotionRadiation", "action_potion_radiation", "Использовать: Зелье Радиации", 13);
      this.registerAction("PotionDrowsiness", "action_potion_drowsiness", "Использовать: Зелье Снотворного", 14);
      this.auctionCalculator = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Auction Calculator")
            .id("auction_calculator")
            .description("В /ah заменяет N*M на результат"))
         .build();
      this.registerSetting(this.auctionCalculator);
      this.visualizer = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Visualizer")
            .id("visualizer")
            .description("Сетка предметов с биндами"))
         .build();
      this.registerSetting(this.visualizer);
      this.visualizerAll = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(true)
            .defaultValue(false)
            .name("Все предметы")
            .id("visualizer_all")
            .description("Показывать все предметы в сетке")
            .visibleWhen(this.visualizer::isEnabled))
         .build();
      this.registerSetting(this.visualizerAll);
      MultiSelectSetting visualizerItemsSetting = ((MultiSelectSettingBuilder)MultiSelectSetting.getMultiSelectSettingBuilder()
            .options(ACTION_NAMES)
            .selectAll(true)
            .optionListEnabled(false)
            .name("Предметы")
            .id("visualizer_items")
            .description("Предметы в сетке")
            .visibleWhen(() -> this.visualizer.isEnabled() && !this.visualizerAll.isEnabled()))
         .build();
      visualizerItemsSetting.setOptions(ACTION_NAMES);
      this.visualizerItems = visualizerItemsSetting;
      this.registerSetting(visualizerItemsSetting);
      this.highlightEmpty = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Highlight Empty")
            .id("highlight_empty")
            .description("Подсветка отсутствующих предметов")
            .visibleWhen(this.visualizer::isEnabled))
         .build();
      this.registerSetting(this.highlightEmpty);
      this.visualizePotions = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Highlight Potions")
            .id("visualize_potions")
            .description("Показывать дон-зелья даже без бинда")
            .visibleWhen(this.visualizer::isEnabled))
         .build();
      this.registerSetting(this.visualizePotions);
      this.highlightCheapest = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Highlight Cheapest")
            .id("highlight_cheapest")
            .description("Подсветка самых дешёвых лотов в /ah"))
         .build();
      this.registerSetting(this.highlightCheapest);
      ColorSetting cheapestColorSetting = ((ColorSettingBuilder)ColorSetting.builder()
            .selectedIndex(0)
            .name("Cheapest Color")
            .id("cheapest_color")
            .description("Цвет подсветки дешёвых лотов")
            .visibleWhen(this.highlightCheapest::isEnabled))
         .build();
      cheapestColorSetting.setPrimaryColor(0, -11753627);
      cheapestColorSetting.setPrimaryColor(1, -1543135);
      cheapestColorSetting.setPrimaryColor(2, -9279489);
      cheapestColorSetting.setPrimaryColor(3, -46001);
      cheapestColorSetting.setPrimaryColor(4, -13218);
      cheapestColorSetting.setPrimaryColor(5, -10582785);
      cheapestColorSetting.setPrimaryColor(6, -2732032);
      this.cheapestColor = cheapestColorSetting;
      this.registerSetting(cheapestColorSetting);
      this.cheapestCount = ((NumberSettingBuilder)NumberSetting.builder()
            .range(1.0, 5.0)
            .defaultValue(1.0)
            .multiplier(1.0)
            .precision(0)
            .animationSpeed(20.0F)
            .name("Cheapest Count")
            .id("cheapest_count")
            .description("Сколько лотов подсвечивать")
            .visibleWhen(this.highlightCheapest::isEnabled))
         .build();
      this.registerSetting(this.cheapestCount);
      this.highlightBestOffer = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Highlight Best Offer")
            .id("highlight_best_offer")
            .description("Подсветка лучших предложений в ДонМаркете"))
         .build();
      this.registerSetting(this.highlightBestOffer);
      ColorSetting bestOfferColorSetting = ((ColorSettingBuilder)ColorSetting.builder()
            .selectedIndex(0)
            .name("Best Offer Color")
            .id("best_offer_color")
            .description("Цвет подсветки лучших предложений")
            .visibleWhen(this.highlightBestOffer::isEnabled))
         .build();
      bestOfferColorSetting.setPrimaryColor(0, -11753627);
      bestOfferColorSetting.setPrimaryColor(1, -1543135);
      bestOfferColorSetting.setPrimaryColor(2, -9279489);
      bestOfferColorSetting.setPrimaryColor(3, -46001);
      bestOfferColorSetting.setPrimaryColor(4, -13218);
      bestOfferColorSetting.setPrimaryColor(5, -10582785);
      bestOfferColorSetting.setPrimaryColor(6, -2732032);
      this.bestOfferColor = bestOfferColorSetting;
      this.registerSetting(bestOfferColorSetting);
      this.bestOfferCount = ((NumberSettingBuilder)NumberSetting.builder()
            .range(1.0, 5.0)
            .defaultValue(1.0)
            .multiplier(1.0)
            .precision(0)
            .animationSpeed(20.0F)
            .name("Best Offer Count")
            .id("best_offer_count")
            .description("Сколько предложений подсвечивать")
            .visibleWhen(this.highlightBestOffer::isEnabled))
         .build();
      this.registerSetting(this.bestOfferCount);
      this.buildVisualizer();
      this.fTSnap = new FTSnap(
         this.getEventBus(),
         () -> ServerKind.parse(this.serverMode.getSelectedOption()),
         this::swapTiming,
         () -> "Под себя".equals(this.buffThrowMode.getSelectedOption()),
         this::throwAtFeet,
         () -> this.windChargeAutoJump.isEnabled(),
         () -> this.fromBundle.isEnabled(),
         () -> this.ftMode.isEnabled()
      );
      this.serverHelperShulker = new ServerHelperShulker();
      this.auctionMathExpander = new AuctionMathExpander(() -> this.auctionCalculator.isEnabled());
      WexSideClient.getSlotHighlightRegistry()
         .setCallback56(
            new CheapestAuctionHighlighter(
               () -> this.isEnabled() && this.highlightCheapest.isEnabled(), () -> this.cheapestCount.getIntValue(), () -> this.cheapestColor.getColor()
            )
         );
      WexSideClient.getSlotHighlightRegistry()
         .setCallback56(
            new DonMarketHighlighter(
               () -> this.isEnabled() && this.highlightBestOffer.isEnabled(), () -> this.bestOfferCount.getIntValue(), () -> this.bestOfferColor.getColor()
            )
         );
   }

   @Override
   protected void initialize() {
      this.listen(HudRenderEvent.class, event -> {
         if (this.inventoryHUD2Impl != null) {
            this.inventoryHUD2Impl.renderFrame();
         }
      });
      this.listen(ClientTickEvent.class, event -> this.serverSelector.tick());
      this.listen(OutgoingChatEvent.class, event -> {
         if (this.auctionMathExpander != null) {
            this.auctionMathExpander.onOutgoingChat(event);
         }
      });
   }

   public boolean isEnabled() {
      return this.enabledSetting.isEnabled();
   }

   public BooleanSetting getBooleanSetting() {
      return this.enabledSetting;
   }

   public ColorSetting getColorSetting() {
      return this.cheapestColor;
   }

   public ItemStatusHudElement getInventoryHUD2Impl() {
      return this.inventoryHUD2Impl;
   }

   public MultiSelectSetting getMultiSelectSetting() {
      return this.visualizerItems;
   }

   public BooleanSetting getBooleanSetting2() {
      return this.visualizerAll;
   }

   public FTSnap getFTSnap() {
      return this.fTSnap;
   }

   public Map<String, BindSetting> getMap() {
      return this.actionBinds;
   }

   public BooleanSetting getBooleanSetting3() {
      return this.windChargeAutoJump;
   }

   public BooleanSetting getBooleanSetting4() {
      return this.highlightEmpty;
   }

   public ModeSetting getModeSetting() {
      return this.serverMode;
   }

   public BooleanSetting getBooleanSetting5() {
      return this.highlightCheapest;
   }

   public BindSetting getBindSetting() {
      return this.radialSelector;
   }

   public ServerHelperShulker getServerHelperShulker() {
      return this.serverHelperShulker;
   }

   public static void tick2() {
      ServerHelperModule module = serverHelperModule2;
      if (module != null) {
         module.serverSelector.start();
      }
   }

   public BooleanSetting getBooleanSetting6() {
      return this.highlightBestOffer;
   }

   public BooleanSetting getBooleanSetting7() {
      return this.noLeftHandPlace;
   }

   public BooleanSetting getBooleanSetting8() {
      return this.fromBundle;
   }

   public BindSetting compute(String name) {
      return this.actionBinds.get(name);
   }

   public NumberSetting getNumberSetting() {
      return this.bestOfferCount;
   }

   public void queueAction(ServerHelperAction action) {
      if (this.fTSnap != null) {
         this.fTSnap.queueAction(action);
      }
   }

   public BooleanSetting getBooleanSetting9() {
      return this.visualizer;
   }

   public BindSetting getBindSetting2() {
      return this.openShulker;
   }

   public static String getString2() {
      ServerHelperModule module = serverHelperModule2;
      return module != null && module.isEnabled() && !module.openShulker.getBindInput().isUnbound() ? module.openShulker.getKeyDisplayName() : null;
   }

   public ColorSetting getColorSetting2() {
      return this.bestOfferColor;
   }

   public BooleanSetting getBooleanSetting10() {
      return this.auctionCalculator;
   }

   public BooleanSetting getBooleanSetting11() {
      return this.ftMode;
   }

   public BooleanSetting getBooleanSetting12() {
      return this.visualizePotions;
   }

   public LeastPopulatedServerSelector getServerSelector() {
      return this.serverSelector;
   }

   public ServerKind getServerKind() {
      return ServerKind.parse(this.serverMode.getSelectedOption());
   }

   public ModeSetting getModeSetting2() {
      return this.buffThrowMode;
   }

   public static boolean isEnabled3() {
      ServerHelperModule module = serverHelperModule2;
      return module != null && module.isEnabled() && module.noLeftHandPlace.isEnabled();
   }

   public ModeSetting getModeSetting3() {
      return this.swapMode;
   }

   public AuctionMathExpander getAuctionMathExpander() {
      return this.auctionMathExpander;
   }

   public List<ServerHelperAction> getList() {
      ArrayList<ServerHelperAction> selected = new ArrayList<>();

      for(ServerHelperAction action : ServerHelperActions.ALL) {
         if (this.selectorItems.getSelectedOptions().contains(action.selectorLabel())) {
            selected.add(action);
         }
      }

      return selected;
   }

   public ModeSetting getModeSetting4() {
      return this.windChargeMode;
   }

   public NumberSetting getNumberSetting2() {
      return this.cheapestCount;
   }

   public MultiSelectSetting getMultiSelectSetting2() {
      return this.selectorItems;
   }

   private SwapTiming swapTiming() {
      String mode = this.swapMode.getSelectedOption();
      if (mode != null && mode.equalsIgnoreCase("Legit")) {
         return SwapTiming.LEGIT;
      } else {
         return mode != null && mode.equalsIgnoreCase("FS") ? SwapTiming.FUNTIME : SwapTiming.DEFAULT;
      }
   }

   private boolean throwAtFeet() {
      return "Под себя".equals(this.windChargeMode.getSelectedOption());
   }

   private boolean isVisualizerItemEnabled(ServerHelperAction action) {
      return this.visualizerAll.isEnabled() || this.visualizerItems.getSelectedOptions().contains(action.selectorLabel());
   }

   private boolean isVisualizerEnabled() {
      return this.enabledSetting.isEnabled() && this.visualizer.isEnabled();
   }

   private void openShulker() {
      if (this.isEnabled() && this.serverHelperShulker != null) {
         this.serverHelperShulker.update();
      }
   }

   private void openRadialSelector() {
      if (this.isEnabled()) {
         class_310 client = class_310.method_1551();
         if (client.field_1724 != null && !this.getList().isEmpty()) {
            client.method_1507(new ServerHelperModule.RadialSelectorScreen(this.radialSelector, this::getList, this::getServerKind, this::queueAction));
         }
      }
   }

   private boolean matchesAction(ServerHelperAction action, class_1799 stack) {
      if (stack == null || stack.method_7960() || action == null) {
         return false;
      } else if (action.matchByItem()) {
         return stack.method_31574(action.icon());
      } else {
         String name = stack.method_7964().getString();
         if (this.getServerKind() == ServerKind.OTHERS) {
            for(String tag : action.alternateServerTags()) {
               if (name.contains(tag)) {
                  return true;
               }
            }

            return false;
         } else {
            String ftTag = action.generalServerTag();
            return ftTag != null && !ftTag.isEmpty() && name.contains(ftTag);
         }
      }
   }

   private void useAction(ServerHelperAction action) {
      if (this.isEnabled() && action != null) {
         this.queueAction(action);
      }
   }

   private ServerHelperAction actionAt(int index) {
      List<ServerHelperAction> actions = ServerHelperActions.ALL;
      return index >= 0 && index < actions.size() ? actions.get(index) : null;
   }

   private void registerAction(String name, String id, String description, int index) {
      ServerHelperAction action = this.actionAt(index);
      BindSetting bind = ((BindSettingBuilder)BindSetting.getBindSettingBuilder()
            .keyboard(0)
            .onReleased(ignored -> this.useAction(action))
            .name(name)
            .id(id)
            .description(description))
         .build();
      this.registerSetting(bind);
      if (action != null) {
         this.actionBinds.put(action.selectorLabel(), bind);
      } else if (index >= 0 && index < ACTION_NAMES.length) {
         this.actionBinds.put(ACTION_NAMES[index], bind);
      }
   }

   private void buildVisualizer() {
      ArrayList<ItemBindBox> boxes = new ArrayList<>();

      for(ServerHelperAction action : ServerHelperActions.ALL) {
         BindSetting bind = this.actionBinds.get(action.selectorLabel());
         if (bind != null) {
            boxes.add(new ItemBindBox(action.icon(), bind, stack -> this.matchesAction(action, stack), () -> this.isVisualizerItemEnabled(action)));
         }
      }

      this.inventoryHUD2Impl = new ItemStatusHudElement("Server Helper", this::isVisualizerEnabled, boxes, this.highlightEmpty::isEnabled);
   }

   public static class RadialSelectorScreen extends class_437 {
      private final BindSetting bind;
      private final Supplier<List<ServerHelperAction>> actions;
      private final Supplier<ServerKind> serverKind;
      private final Consumer<ServerHelperAction> onSelect;

      public RadialSelectorScreen(
         BindSetting bind, Supplier<List<ServerHelperAction>> actions, Supplier<ServerKind> serverKind, Consumer<ServerHelperAction> onSelect
      ) {
         super(class_2561.method_43473());
         this.bind = bind;
         this.actions = actions;
         this.serverKind = serverKind;
         this.onSelect = onSelect;
      }

      public BindSetting bind() {
         return this.bind;
      }

      public List<ServerHelperAction> actions() {
         return this.actions.get();
      }

      public ServerKind serverKind() {
         return this.serverKind.get();
      }

      public void method_25393() {
         if (this.bind == null || !this.bind.isPressed()) {
            this.method_25419();
         }
      }

      public boolean method_25421() {
         return false;
      }

      public void select(ServerHelperAction action) {
         if (action != null && this.onSelect != null) {
            this.onSelect.accept(action);
         }

         this.method_25419();
      }
   }
}
