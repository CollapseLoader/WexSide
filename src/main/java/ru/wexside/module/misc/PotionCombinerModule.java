package ru.wexside.module.misc;

import net.minecraft.class_310;
import ru.wexside.WexSideClient;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.EventBus;
import ru.wexside.misc.ServerKind;
import ru.wexside.misc.SwapTiming;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BindSetting;
import ru.wexside.setting.BindSettingBuilder;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.ModeSetting;
import ru.wexside.setting.ModeSettingBuilder;
import ru.wexside.util.PotionPresetController;

public final class PotionCombinerModule extends Module implements ConfigSerializable {
   private static volatile PotionCombinerModule instance;
   private final BooleanSetting enabledSetting;
   private final ModeSetting serverMode;
   private final ModeSetting swapMode;
   private final BooleanSetting fromBundle;
   private final BooleanSetting ftMode;
   private final BindSetting menuKey;
   private final BindSetting radialSelector;

   public PotionCombinerModule(EventBus eventBus) {
      super(eventBus, "potion_combiner", "Potion Combiner", "Бросок набора зелий под себя по биндам пресетов", ModuleCategory.valueOf("MISC"));
      instance = this;
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
            .description("Доставать зелья из мешка если нет в инвентаре"))
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
      this.menuKey = ((BindSettingBuilder)BindSetting.getBindSettingBuilder()
            .keyboard(0)
            .name("Menu Key")
            .id("menu_key")
            .description("Клавиша открытия меню пресетов"))
         .build();
      this.registerSetting(this.menuKey);
      this.radialSelector = ((BindSettingBuilder)BindSetting.getBindSettingBuilder()
            .keyboard(0)
            .onReleased(this::openRadialMenu)
            .name("Radial Selector")
            .id("radial_selector")
            .description("Круговой селектор избранных пресетов"))
         .build();
      this.registerSetting(this.radialSelector);
   }

   @Override
   protected void initialize() {
   }

   private void openRadialMenu(BindSetting ignored) {
      if (this.enabledSetting.isEnabled()) {
         class_310 client = class_310.method_1551();
         WexSideClient wexSide = WexSideClient.getInstance();
         PotionPresetController combiner = wexSide == null ? null : wexSide.getPotionPresetController();
         if (client.field_1724 != null && client.field_1755 == null && combiner != null && !combiner.getFavoritePresets().isEmpty()) {
            client.method_1507(new PotionCombinerRadialScreen(this.radialSelector, combiner, combiner::queuePreset));
         }
      }
   }

   public static boolean isActive() {
      return isEnabled();
   }

   public static boolean isEnabled() {
      PotionCombinerModule module = instance;
      return module != null && module.enabledSetting.isEnabled();
   }

   public static boolean isActive2() {
      return isEnabled2();
   }

   public static boolean isEnabled2() {
      PotionCombinerModule module = instance;
      return module != null && module.fromBundle.isEnabled();
   }

   public static boolean isActive3() {
      return isEnabled3();
   }

   public static boolean isEnabled3() {
      PotionCombinerModule module = instance;
      return module != null && module.ftMode.isEnabled();
   }

   public static ServerKind getServerKind() {
      PotionCombinerModule module = instance;
      return module == null ? ServerKind.GENERAL : ServerKind.parse(module.serverMode.getSelectedOption());
   }

   public static SwapTiming getSwapTiming() {
      PotionCombinerModule module = instance;
      if (module == null) {
         return SwapTiming.DEFAULT;
      } else {
         String mode = module.swapMode.getSelectedOption();
         if (mode != null && mode.equalsIgnoreCase("Legit")) {
            return SwapTiming.LEGIT;
         } else {
            return mode != null && mode.equalsIgnoreCase("FS") ? SwapTiming.FUNTIME : SwapTiming.DEFAULT;
         }
      }
   }

   public static boolean process(int keyCode) {
      PotionCombinerModule module = instance;
      return module != null && module.enabledSetting.isEnabled() && module.menuKey.getBindInput().matchesKeyboard(keyCode);
   }
}
