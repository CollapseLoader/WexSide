package ru.wexside.module.player;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.class_124;
import net.minecraft.class_1263;
import net.minecraft.class_1703;
import net.minecraft.class_1707;
import net.minecraft.class_1713;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_442;
import net.minecraft.class_636;
import net.minecraft.class_746;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.misc.ClientChat;
import ru.wexside.misc.ElapsedTimer;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.server.FunTimeServerContext;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.NumberSetting;
import ru.wexside.setting.NumberSettingBuilder;

public class ChestStealerModule extends Module implements ConfigSerializable {
   private static final int MISS_PERIOD = 30;
   private static final int MISS_JITTER = 15;
   private static final double MISS_CHANCE = 0.1;
   private final BooleanSetting enabledSetting;
   private final BooleanSetting random;
   private final BooleanSetting missSlots;
   private final BooleanSetting closeIfEmpty;
   private final BooleanSetting leaveAfterLoot;
   private final NumberSetting delay;
   private final ElapsedTimer delayTimer = new ElapsedTimer();
   private int tickCounter;

   public ChestStealerModule(EventBus eventBus) {
      super(eventBus, "chest_stealer", "Chest Stealer", "Автоматически забирает предметы из открытых сундуков", ModuleCategory.valueOf("PLAYER"));
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
      this.random = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Random")
            .id("random")
            .description("Случайный порядок и джиттер задержки")
            .aliases("random", "рандом"))
         .build();
      this.registerSetting(this.random);
      this.missSlots = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Miss Slots")
            .id("miss_slots")
            .description("Иногда промахиваться по пустым слотам")
            .aliases("miss slots", "промахи"))
         .build();
      this.registerSetting(this.missSlots);
      this.closeIfEmpty = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Close If Empty")
            .id("close_if_empty")
            .description("Закрывать сундук когда он пуст")
            .aliases("close if empty", "закрывать пустой"))
         .build();
      this.registerSetting(this.closeIfEmpty);
      this.leaveAfterLoot = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Leave After Loot")
            .id("leave_after_loot")
            .description("Выходить с сервера после лута")
            .aliases("leave after loot", "выходить после лута"))
         .build();
      this.registerSetting(this.leaveAfterLoot);
      this.delay = ((NumberSettingBuilder)NumberSetting.builder()
            .range(0.0, 500.0)
            .defaultValue(50.0)
            .multiplier(1.0)
            .precision(0)
            .animationSpeed(20.0F)
            .name("Delay")
            .id("delay")
            .description("Задержка между предметами (мс)")
            .aliases("delay", "задержка"))
         .build();
      this.registerSetting(this.delay);
   }

   @Override
   protected void initialize() {
      this.listen(ClientTickEvent.class, event -> this.onTick());
   }

   private void onTick() {
      class_310 client = class_310.method_1551();
      class_746 player = client.field_1724;
      if (this.enabledSetting.isEnabled() && player != null && client.field_1687 != null) {
         class_1703 handler = player.field_7512;
         if (handler instanceof class_1707) {
            class_1707 container = (class_1707)handler;
            class_1263 chest = container.method_7629();
            this.tickCounter = (this.tickCounter + 1) % 600;
            if (player.method_31548().method_7376() == -1) {
               ClientChat.send(class_2561.method_43470("Сундук закрыт: инвентарь полон.").method_27692(class_124.field_1061));
               this.enabledSetting.setEnabled(false);
               player.method_7346();
            } else if (chest.method_5442()) {
               if (this.closeIfEmpty.isEnabled()) {
                  player.method_7346();
               }

               if (this.leaveAfterLoot.isEnabled() && !FunTimeServerContext.isPvpLocked()) {
                  this.leaveServer();
               }
            } else {
               long wait = (long)this.delay.getIntValue();
               if (this.random.isEnabled()) {
                  wait += (long)ThreadLocalRandom.current().nextInt(15);
               }

               int slot;
               if (this.delayTimer.process(wait) && (slot = this.nextFilledSlot(chest)) != -1) {
                  this.click(container.field_7763, slot, class_1713.field_7794, player);
                  this.delayTimer.update();
               }

               if (this.missSlots.isEnabled()) {
                  this.maybeMiss(container, chest, player);
               }
            }
         }
      }
   }

   private void leaveServer() {
      this.enabledSetting.setEnabled(false);
      class_310 client = class_310.method_1551();
      client.method_72099();
      client.method_1507(new class_442());
   }

   private int nextFilledSlot(class_1263 chest) {
      int size = chest.method_5439();
      if (!this.random.isEnabled()) {
         for(int i = 0; i < size; ++i) {
            if (!chest.method_5438(i).method_7960()) {
               return i;
            }
         }

         return -1;
      } else {
         ArrayList<Integer> filled = new ArrayList<>();

         for(int i = 0; i < size; ++i) {
            if (!chest.method_5438(i).method_7960()) {
               filled.add(i);
            }
         }

         return filled.isEmpty() ? -1 : filled.get(ThreadLocalRandom.current().nextInt(filled.size()));
      }
   }

   private void click(int member9959, int slot, class_1713 action, class_746 player) {
      class_636 interactions = class_310.method_1551().field_1761;
      if (interactions != null) {
         interactions.method_2906(member9959, slot, 0, action, player);
      }
   }

   private void maybeMiss(class_1707 container, class_1263 chest, class_746 player) {
      if (this.tickCounter % 30 == 0) {
         int size = chest.method_5439();

         for(int i = 0; i < size; ++i) {
            if (chest.method_5438(i).method_7960() && !(ThreadLocalRandom.current().nextDouble() >= 0.1)) {
               this.click(container.field_7763, i, class_1713.field_7790, player);
               return;
            }
         }
      }
   }
}
