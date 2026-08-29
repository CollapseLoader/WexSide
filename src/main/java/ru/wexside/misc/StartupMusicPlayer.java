package ru.wexside.misc;

import java.util.function.Consumer;
import net.minecraft.class_1109;
import net.minecraft.class_310;
import net.minecraft.class_3414;
import net.minecraft.class_1140.class_11518;

public final class StartupMusicPlayer {
   private final class_3414 music;
   private final Consumer<String> statusSink;
   private int readyTicks;
   private boolean started;

   public StartupMusicPlayer(class_3414 music, Consumer<String> statusSink) {
      this.music = music;
      this.statusSink = statusSink;
   }

   public void tick(class_310 client) {
      if (!this.started && client.method_1483().method_4869(this.music.comp_3319()) != null) {
         if (++this.readyTicks >= 10) {
            class_11518 result = client.method_1483().method_4873(class_1109.method_4757(this.music, 1.0F, 0.8F));
            if (result == class_11518.field_60956) {
               this.readyTicks = 0;
            } else {
               this.started = true;
               this.statusSink.accept("Startup music playback: " + result);
            }
         }
      }
   }
}
