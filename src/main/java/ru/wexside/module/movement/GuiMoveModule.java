package ru.wexside.module.movement;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.class_1041;
import net.minecraft.class_1713;
import net.minecraft.class_2596;
import net.minecraft.class_2645;
import net.minecraft.class_2649;
import net.minecraft.class_2653;
import net.minecraft.class_2813;
import net.minecraft.class_2815;
import net.minecraft.class_304;
import net.minecraft.class_310;
import net.minecraft.class_315;
import net.minecraft.class_3675;
import net.minecraft.class_408;
import net.minecraft.class_437;
import net.minecraft.class_473;
import net.minecraft.class_490;
import net.minecraft.class_746;
import net.minecraft.class_7743;
import net.minecraft.class_3675.class_306;
import net.minecraft.class_3675.class_307;
import ru.wexside.WexSideClient;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.event.IncomingPacketEvent;
import ru.wexside.event.OutgoingPacketEvent;
import ru.wexside.misc.ElapsedTimer;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.ModeSetting;
import ru.wexside.setting.ModeSettingBuilder;
import ru.wexside.ui.WexsideScreen;
import ru.wexside.util.InventoryController;

public class GuiMoveModule extends Module implements ConfigSerializable {
   private volatile boolean waitingForConfirm;
   private volatile boolean flushingFunTime;
   private volatile boolean sendingOwnPackets;
   private volatile int funTimeFlushTicks;
   private final ModeSetting bypass;
   private volatile boolean delayingClicks;
   private volatile int clickDelayTicks;
   private final Queue<class_2813> clickQueue = new ConcurrentLinkedQueue();
   private final ElapsedTimer confirmTimer = new ElapsedTimer();
   private final Queue<class_2596<?>> funTimeQueue = new ConcurrentLinkedQueue();
   private volatile class_2815 pendingClose;
   private volatile int closeDelayTicks;
   private final BooleanSetting enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
         .value(false)
         .defaultValue(false)
         .name("Enabled")
         .id("enabled")
         .description("")
         .withKeybind()
         .toggle())
      .build();
   private volatile boolean movingKeysDown;
   private volatile boolean wasDelayingClicks;

   public GuiMoveModule(EventBus eventBus) {
      super(eventBus, "gui_move", "Gui Move", "Позволяет двигаться при открытом GUI", ModuleCategory.valueOf("MOVEMENT"));
      this.registerSetting(this.enabledSetting);
      this.bypass = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder()
            .options("Off", "FT", "Spooky-T")
            .defaultOption("Off")
            .name("Обход")
            .id("bypass")
            .description("Режим обхода кликов по слотам"))
         .build();
      this.registerSetting(this.bypass);
   }

   @Override
   protected void initialize() {
      this.listen(ClientTickEvent.class, this::onClientTick);
      this.listen(OutgoingPacketEvent.class, this::onOutgoingPacket);
      this.listen(IncomingPacketEvent.class, this::onIncomingPacket);
   }

   private void onClientTick(ClientTickEvent event) {
      boolean busy = false;
      if (this.enabledSetting.isEnabled() && this.spookyTimeMode()) {
         this.tickSpookyTime();
         busy = this.delayingClicks || !this.clickQueue.isEmpty() || this.pendingClose != null;
      } else {
         this.resetSpookyTime();
      }

      if (this.enabledSetting.isEnabled() && this.funTimeMode()) {
         this.tickFunTime();
         busy = busy || this.flushingFunTime;
      } else {
         this.resetFunTime();
      }

      this.setInventoryBusy(busy);
      class_437 screen = class_310.method_1551().field_1755;
      if (this.enabledSetting.isEnabled() && screen != null && !this.compute2(screen)) {
         this.pressMovementKeys();
      } else {
         if (screen != null) {
            this.releaseMovementKeys();
         }
      }
   }

   private void onOutgoingPacket(OutgoingPacketEvent event) {
      if (this.enabledSetting.isEnabled() && !this.sendingOwnPackets) {
         if (this.funTimeMode()) {
            this.onFunTimeOutgoing(event);
         } else if (this.spookyTimeMode()) {
            this.onSpookyTimeOutgoing(event);
         }
      }
   }

   private void onIncomingPacket(IncomingPacketEvent event) {
      if (this.enabledSetting.isEnabled()) {
         if (this.funTimeMode()) {
            class_2596<?> packet = event.getPacket();
            class_2645 close;
            if (packet instanceof class_2645 && (close = (class_2645)packet).method_36148() == 0) {
               event.update();
            }
         } else if (this.spookyTimeMode() && this.waitingForConfirm && !this.confirmTimer.process(2000L)) {
            class_2596<?> packet = event.getPacket();
            if (packet instanceof class_2653 || packet instanceof class_2649) {
               this.waitingForConfirm = false;
               event.update();
            }
         }
      }
   }

   private void resetFunTime() {
      if (!this.funTimeQueue.isEmpty()) {
         this.funTimeQueue.clear();
      }

      this.flushingFunTime = false;
      this.funTimeFlushTicks = 0;
   }

   private void pressMovementKeys() {
      class_304[] keys = this.movementKeys();
      if (keys.length != 0) {
         class_1041 window = class_310.method_1551().method_22683();

         for(class_304 key : keys) {
            class_306 bound = class_3675.method_15981(key.method_1428());
            boolean pressed = bound.method_1442() != class_307.field_1672 && class_3675.method_15987(window, bound.method_1444());
            key.method_23481(pressed);
         }
      }
   }

   private boolean spookyTimeMode() {
      return "Spooky-T".equals(this.bypass.getSelectedOption());
   }

   private void onSpookyTimeOutgoing(OutgoingPacketEvent event) {
      class_2596<?> packet = event.getPacket();
      if (packet instanceof class_2815 close) {
         if (this.shouldHoldClose()) {
            this.pendingClose = close;
            this.closeDelayTicks = 0;
            event.update();
         }
      } else if (packet instanceof class_2813) {
         class_2813 click = (class_2813)packet;
         if (this.clickBusy() && click.comp_3844() != -1) {
            this.armClickDelay(click.comp_3846());
            if (!this.delayingClicks && this.queueClick(click)) {
               this.waitingForConfirm = true;
               this.confirmTimer.update();
               event.update();
            }
         }
      }
   }

   private void resetSpookyTime() {
      if (!this.clickQueue.isEmpty()) {
         this.clickQueue.clear();
      }

      this.clickDelayTicks = 0;
      this.delayingClicks = false;
      this.wasDelayingClicks = false;
      this.waitingForConfirm = false;
      this.movingKeysDown = false;
      this.pendingClose = null;
      this.closeDelayTicks = 0;
   }

   private boolean isKeyDown(class_1041 window, class_304 key) {
      class_306 bound = class_3675.method_15981(key.method_1428());
      return bound.method_1442() != class_307.field_1672 && class_3675.method_15987(window, bound.method_1444());
   }

   private void tickFunTime() {
      class_746 player = class_310.method_1551().field_1724;
      if (player == null) {
         this.resetFunTime();
      } else {
         this.updateMovingKeys();
         if (!this.flushingFunTime) {
            this.funTimeFlushTicks = 0;
         } else if (this.funTimeFlushTicks <= 2) {
            ++this.funTimeFlushTicks;
         } else {
            this.sendingOwnPackets = true;

            class_2596<?> packet;
            try {
               while((packet = (class_2596)this.funTimeQueue.poll()) != null) {
                  player.field_3944.method_52787(packet);
               }
            } finally {
               this.sendingOwnPackets = false;
            }

            this.flushingFunTime = false;
            this.funTimeFlushTicks = 0;
         }
      }
   }

   private boolean compute2(class_437 screen) {
      return screen instanceof class_408 || screen instanceof class_7743 || screen instanceof class_473 || screen instanceof WexsideScreen;
   }

   private boolean shouldHoldClose() {
      if (!this.clickBusy() && this.clickQueue.isEmpty()) {
         class_746 player = class_310.method_1551().field_1724;
         return player != null && player.method_5624();
      } else {
         return true;
      }
   }

   private boolean holdingItemInInventory(class_746 player) {
      if (!(class_310.method_1551().field_1755 instanceof class_490)) {
         return false;
      } else if (!this.clickBusy()) {
         return false;
      } else {
         return !player.field_7512.method_34255().method_7960();
      }
   }

   private boolean queueClick(class_2813 packet) {
      return this.clickQueue.contains(packet) ? false : this.clickQueue.add(packet);
   }

   private void flushPendingClose(class_746 player) {
      if (this.pendingClose != null) {
         if (this.clickQueue.isEmpty() && this.closeDelayTicks > 1) {
            this.sendingOwnPackets = true;

            try {
               player.field_3944.method_52787(this.pendingClose);
            } finally {
               this.sendingOwnPackets = false;
            }

            this.pendingClose = null;
            this.closeDelayTicks = 0;
         } else {
            ++this.closeDelayTicks;
         }
      }
   }

   private int delayFor(class_1713 actionType) {
      return actionType == class_1713.field_7790 ? 1 : (this.clickDelayTicks > 1 ? 2 : 3);
   }

   private void setInventoryBusy(boolean busy) {
      InventoryController member6090 = WexSideClient.getInventoryController();
      if (member6090 != null) {
         if (busy) {
            member6090.update();
         } else {
            member6090.update2();
         }
      }
   }

   private void onFunTimeOutgoing(OutgoingPacketEvent event) {
      class_2596<?> packet = event.getPacket();
      if (packet instanceof class_2813 click) {
         if (class_310.method_1551().field_1755 instanceof class_490 && this.movingKeysDown && click.comp_3844() != -1) {
            this.funTimeQueue.add(click);
            event.update();
         }
      } else if (packet instanceof class_2815 close && !this.funTimeQueue.isEmpty()) {
         this.funTimeQueue.add(close);
         this.flushingFunTime = true;
         event.update();
      }
   }

   private class_304[] movementKeys() {
      class_315 options = class_310.method_1551().field_1690;
      return options == null
         ? new class_304[0]
         : new class_304[]{options.field_1894, options.field_1913, options.field_1881, options.field_1849, options.field_1903, options.field_1867};
   }

   private void flushClickQueue(class_746 player) {
      if (!this.clickQueue.isEmpty()) {
         this.sendingOwnPackets = true;

         class_2813 click;
         try {
            while((click = (class_2813)this.clickQueue.poll()) != null) {
               player.field_3944.method_52787(click);
            }
         } finally {
            this.sendingOwnPackets = false;
         }
      }
   }

   private boolean funTimeMode() {
      return "FT".equals(this.bypass.getSelectedOption());
   }

   private boolean clickBusy() {
      return this.movingKeysDown || this.delayingClicks;
   }

   private void releaseMovementKeys() {
      for(class_304 key : this.movementKeys()) {
         key.method_23481(false);
      }
   }

   private void armClickDelay(class_1713 actionType) {
      this.clickDelayTicks = this.delayFor(actionType == null ? class_1713.field_7790 : actionType) + 1;
   }

   private void updateMovingKeys() {
      class_315 options = class_310.method_1551().field_1690;
      if (options == null) {
         this.movingKeysDown = false;
      } else {
         class_1041 window = class_310.method_1551().method_22683();
         this.movingKeysDown = this.isKeyDown(window, options.field_1894)
            || this.isKeyDown(window, options.field_1881)
            || this.isKeyDown(window, options.field_1913)
            || this.isKeyDown(window, options.field_1849);
      }
   }

   private void tickSpookyTime() {
      class_746 player = class_310.method_1551().field_1724;
      if (player == null) {
         this.resetSpookyTime();
      } else {
         this.updateMovingKeys();
         if (this.wasDelayingClicks && this.delayingClicks && this.clickDelayTicks > 0) {
            this.flushClickQueue(player);
         }

         this.wasDelayingClicks = this.delayingClicks;
         if (this.holdingItemInInventory(player)) {
            this.clickDelayTicks = this.delayFor(class_1713.field_7790) + 1;
         } else if (this.clickDelayTicks > 0) {
            --this.clickDelayTicks;
         }

         this.delayingClicks = this.clickDelayTicks > 0;
         this.flushPendingClose(player);
      }
   }
}
