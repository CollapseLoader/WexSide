package ru.wexside.module.misc;

import java.util.Locale;
import net.minecraft.class_124;
import net.minecraft.class_1713;
import net.minecraft.class_1735;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2561;
import net.minecraft.class_2596;
import net.minecraft.class_2678;
import net.minecraft.class_310;
import net.minecraft.class_437;
import net.minecraft.class_465;
import net.minecraft.class_636;
import net.minecraft.class_7439;
import net.minecraft.class_746;
import ru.wexside.WexSideClient;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.event.IncomingPacketEvent;
import ru.wexside.event.WorldSessionEvent;
import ru.wexside.misc.ClickPolicy;
import ru.wexside.misc.ClickSlotAction;
import ru.wexside.misc.ElapsedTimer;
import ru.wexside.misc.HotbarSelectAction;
import ru.wexside.misc.Inventories;
import ru.wexside.misc.InventoryTask;
import ru.wexside.misc.TaskFlag;
import ru.wexside.misc.TaskPriority;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.ModeSetting;
import ru.wexside.setting.ModeSettingBuilder;
import ru.wexside.setting.NumberSetting;
import ru.wexside.setting.NumberSettingBuilder;
import ru.wexside.util.InventoryController;

public final class ServerJoinerModule extends Module implements ConfigSerializable {
   private static final String SERVER_SPOOKY_TIME = "SpookyTime";
   private static final String SERVER_REALLY_WORLD = "ReallyWorld";
   private static final String MODULE_OWNER = "server_joiner";
   private static final long GRIEF_CLICK_DELAY_MS = 150L;
   private static final long REALLY_WORLD_RETRY_MS = 500L;
   private static final long REALLY_WORLD_QUEUE_MS = 5000L;
   private final ElapsedTimer clickTimer = new ElapsedTimer();
   private final BooleanSetting enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
         .value(false)
         .defaultValue(false)
         .name("Enabled")
         .id("enabled")
         .description("")
         .withKeybind()
         .toggle())
      .build();
   private final ModeSetting server;
   private final NumberSetting griefNumber;
   private volatile long reallyWorldRetryAtMs;
   private boolean firstTick = true;

   public ServerJoinerModule(EventBus eventBus) {
      super(
         eventBus,
         "server_joiner",
         "Server Joiner",
         "Автоматический вход на выбранный сервер: SpookyTime дуэли / ReallyWorld гриф",
         ModuleCategory.valueOf("MISC")
      );
      this.registerSetting(this.enabledSetting);
      this.server = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder()
            .options("SpookyTime", "ReallyWorld")
            .defaultOption("SpookyTime")
            .name("Сервер")
            .id("server")
            .description("Сервер для автоматического входа"))
         .build();
      this.registerSetting(this.server);
      this.griefNumber = ((NumberSettingBuilder)NumberSetting.builder()
            .range(1.0, 42.0)
            .defaultValue(1.0)
            .multiplier(1.0)
            .precision(0)
            .animationSpeed(20.0F)
            .snapTo(1.0)
            .name("Номер грифа")
            .id("grief_number")
            .description("Номер гриф-сервера ReallyWorld")
            .visibleWhen(() -> "ReallyWorld".equals(this.server.getSelectedOption())))
         .build();
      this.registerSetting(this.griefNumber);
   }

   @Override
   protected void initialize() {
      this.listen(ClientTickEvent.class, this::onClientTick);
      this.listen(IncomingPacketEvent.class, this::onIncomingPacket);
      this.listen(WorldSessionEvent.class, this::onWorldChange);
   }

   private void onIncomingPacket(IncomingPacketEvent event) {
      if (this.enabledSetting.isEnabled() && "ReallyWorld".equals(this.server.getSelectedOption())) {
         class_2596<?> packet = event.getPacket();
         if (packet instanceof class_2678) {
            this.enabledSetting.setEnabled(false);
         } else {
            String message = this.extractMessage(packet);
            if (message != null) {
               boolean waitMessage = message.contains("Подождите");
               boolean retryMessage = waitMessage
                  || message.contains("К сожалению сервер переполнен")
                  || message.contains("большой поток игроков")
                  || message.contains("Сервер перезагружается");
               if (retryMessage) {
                  this.reallyWorldRetryAtMs = waitMessage ? System.currentTimeMillis() + 5000L : System.currentTimeMillis();
               }
            }
         }
      }
   }

   private void onClientTick(ClientTickEvent event) {
      if (!this.enabledSetting.isEnabled()) {
         this.firstTick = true;
         this.reallyWorldRetryAtMs = 0L;
      } else {
         class_310 client = class_310.method_1551();
         if (client.field_1724 != null) {
            boolean initialTick = this.firstTick;
            this.firstTick = false;
            if ("ReallyWorld".equals(this.server.getSelectedOption())) {
               this.handleReallyWorld(initialTick);
            } else {
               this.handleSpookyTime();
            }
         }
      }
   }

   private void handleSpookyTime() {
      class_310 client = class_310.method_1551();
      if (client.field_1755 == null) {
         this.selectHotbarItem("выбор сервера", true);
      }

      class_437 screen = client.field_1755;
      class_465 handledScreen;
      if (client.field_1755 instanceof class_465 && (handledScreen = (class_465)screen).method_25440().getString().contains("Выберите режим:")) {
         this.clickModeSelectorSlot();
      }

      if (this.findHotbarSlot("вход в очередь") != -1) {
         this.enabledSetting.setEnabled(false);
      }
   }

   private void handleReallyWorld(boolean initialTick) {
      if (initialTick) {
         this.selectCompassSlot();
         this.clickTimer.update();
      } else if (this.reallyWorldRetryAtMs != 0L && System.currentTimeMillis() >= this.reallyWorldRetryAtMs) {
         this.reallyWorldRetryAtMs = 0L;
         this.selectCompassSlot();
         this.clickTimer.update();
      } else {
         class_310 client = class_310.method_1551();
         class_746 player = client.field_1724;
         if (player != null) {
            if (client.field_1755 == null) {
               if (player.field_6012 < 5 && this.clickTimer.process(500L)) {
                  this.selectCurrentHotbarSlot();
                  this.clickTimer.update();
               }
            } else {
               if (client.field_1755 instanceof class_465) {
                  this.clickMatchingGriefSlot(player);
               }
            }
         }
      }
   }

   private void clickMatchingGriefSlot(class_746 player) {
      int griefNumber = this.griefNumber.getIntValue();

      for(int slotIndex = 0; slotIndex < player.field_7512.field_7761.size(); ++slotIndex) {
         class_1735 slot = (class_1735)player.field_7512.field_7761.get(slotIndex);
         class_1799 stack = slot.method_7677();
         String name;
         if (!stack.method_7960()
            && ((name = stack.method_7964().getString()).contains("ГРИФЕРСКОЕ ВЫЖИВАНИЕ") || this.matchesGriefNumber(name, griefNumber))
            && this.clickTimer.process(150L)) {
            this.clickContainerSlot(slotIndex);
            this.clickTimer.update();
         }
      }
   }

   private boolean matchesGriefNumber(String name, int griefNumber) {
      String marker = "ГРИФ #" + griefNumber;
      int index = name.indexOf(marker);
      if (index == -1) {
         return false;
      } else {
         int afterMarker = index + marker.length();
         if (afterMarker >= name.length()) {
            return true;
         } else {
            return !Character.isDigit(name.charAt(afterMarker));
         }
      }
   }

   private void clickModeSelectorSlot() {
      class_746 player = class_310.method_1551().field_1724;
      class_636 interactionManager = class_310.method_1551().field_1761;
      if (player != null && interactionManager != null) {
         interactionManager.method_2906(player.field_7512.field_7763, 13, 0, class_1713.field_7790, player);
      }
   }

   private String extractMessage(class_2596<?> packet) {
      class_2561 text = null;
      if (packet instanceof class_7439 chatPacket) {
         if (chatPacket.comp_906()) {
            return null;
         }

         text = chatPacket.comp_763();
      }

      return text == null ? null : class_124.method_539(text.getString());
   }

   private void selectCompassSlot() {
      class_746 player = class_310.method_1551().field_1724;
      if (player != null) {
         int slot = Inventories.findHotbarSlot(class_1802.field_8251);
         if (slot == -1) {
            slot = player.method_31548().method_67532();
         }

         this.selectHotbarSlot(slot, false);
      }
   }

   private void selectCurrentHotbarSlot() {
      class_746 player = class_310.method_1551().field_1724;
      if (player != null) {
         this.selectHotbarSlot(player.method_31548().method_67532(), false);
      }
   }

   private void selectHotbarItem(String namePart, boolean rightClick) {
      int slot = this.findHotbarSlot(namePart);
      if (slot != -1) {
         this.selectHotbarSlot(slot, rightClick);
      }
   }

   private void selectHotbarSlot(int slot, boolean rightClick) {
      InventoryController member6090 = WexSideClient.getInventoryController();
      if (member6090 != null) {
         member6090.submit(
            InventoryTask.builder()
               .action(new HotbarSelectAction(slot, rightClick))
               .owner("server_joiner")
               .flag2(TaskFlag.DEFAULT)
               .policy(ClickPolicy.SILENT)
               .priority(TaskPriority.NORMAL)
               .build()
         );
      }
   }

   private void clickContainerSlot(int slot) {
      InventoryController member6090 = WexSideClient.getInventoryController();
      if (member6090 != null) {
         member6090.submit(
            InventoryTask.builder()
               .action(new ClickSlotAction(slot, 0))
               .owner("server_joiner")
               .flag2(TaskFlag.DEFAULT)
               .policy(ClickPolicy.SILENT)
               .priority(TaskPriority.NORMAL)
               .build()
         );
      }
   }

   private int findHotbarSlot(String namePart) {
      class_746 player = class_310.method_1551().field_1724;
      if (player == null) {
         return -1;
      } else {
         for(int slot = 0; slot < 9; ++slot) {
            class_1799 stack = player.method_31548().method_5438(slot);
            if (!stack.method_7960() && stack.method_7964().getString().toLowerCase(Locale.ROOT).contains(namePart)) {
               return slot;
            }
         }

         return -1;
      }
   }

   private void onWorldChange(WorldSessionEvent event) {
      if (this.enabledSetting.isEnabled()) {
         class_310 client = class_310.method_1551();
         if (client.field_1687 != null && !"ReallyWorld".equals(this.server.getSelectedOption())) {
            if (client.field_1687.method_27983().method_29177().toString().contains(":spawn")) {
               this.enabledSetting.setEnabled(false);
            }
         }
      }
   }
}
