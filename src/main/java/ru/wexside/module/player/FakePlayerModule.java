package ru.wexside.module.player;

import net.minecraft.class_1304;
import net.minecraft.class_1661;
import net.minecraft.class_310;
import net.minecraft.class_638;
import net.minecraft.class_745;
import net.minecraft.class_746;
import net.minecraft.class_1297.class_5529;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.event.WorldSessionEvent;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;

public class FakePlayerModule extends Module implements ConfigSerializable {
   private static final int FAKE_ID = -1338;
   private final BooleanSetting enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
         .value(false)
         .defaultValue(false)
         .name("Enabled")
         .id("enabled")
         .description("")
         .withKeybind()
         .toggle())
      .build();
   private class_638 trackedWorld;
   private class_745 fakePlayer;
   private boolean spawned;

   public FakePlayerModule(EventBus eventBus) {
      super(eventBus, "fake_player", "Fake Player", "Спавнит вашего двойника", ModuleCategory.valueOf("PLAYER"));
      this.registerSetting(this.enabledSetting);
   }

   @Override
   protected void initialize() {
      this.listen(ClientTickEvent.class, this::onTick);
      this.listen(WorldSessionEvent.class, event -> this.despawn());
   }

   private void onTick(ClientTickEvent event) {
      class_310 client = class_310.method_1551();
      class_638 world = client.field_1687;
      if (this.trackedWorld != world) {
         this.trackedWorld = world;
         if (this.spawned) {
            this.forceDespawn();
            return;
         }
      }

      if (!this.spawned || client.field_1724 != null && client.field_1724.method_5805()) {
         if (this.enabledSetting.isEnabled() && !this.spawned) {
            this.spawn();
         } else if (!this.enabledSetting.isEnabled() && this.spawned) {
            this.despawn();
         }
      } else {
         this.forceDespawn();
      }
   }

   private void spawn() {
      class_310 client = class_310.method_1551();
      class_746 player = client.field_1724;
      if (player != null && client.field_1687 != null && player.method_5805()) {
         this.trackedWorld = client.field_1687;
         this.copyFrom(player);
         this.spawned = true;
      } else {
         this.enabledSetting.setEnabled(false);
      }
   }

   private void despawn() {
      this.removeFake();
      this.spawned = false;
   }

   private void forceDespawn() {
      this.despawn();
      this.enabledSetting.setEnabled(false);
   }

   private void removeFake() {
      class_745 fake = this.fakePlayer;
      this.fakePlayer = null;
      if (fake != null) {
         try {
            fake.method_5650(class_5529.field_26999);
            fake.method_31472();
         } catch (Throwable var3) {
         }
      }
   }

   private void copyFrom(class_746 player) {
      class_310 client = class_310.method_1551();
      class_745 fake = new class_745(client.field_1687, player.method_7334());
      fake.method_5719(player);
      fake.method_5878(player);
      this.copyInventory(fake.method_31548(), player.method_31548());

      for(class_1304 slot : class_1304.values()) {
         fake.method_5673(slot, player.method_6118(slot).method_7972());
      }

      fake.method_5838(-1338);
      client.field_1687.method_53875(fake);
      this.fakePlayer = fake;
   }

   private void copyInventory(class_1661 dest, class_1661 source) {
      for(int i = 0; i < source.method_5439(); ++i) {
         dest.method_5447(i, source.method_5438(i).method_7972());
      }
   }
}
