package ru.wexside.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.WatchEvent.Kind;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.LineEvent.Type;
import ru.wexside.misc.DirectoryWatchRegistrar;
import ru.wexside.misc.WavFileVisitor;

public final class CustomSoundLibrary implements AutoCloseable {
   private final Map<String, byte[]> map2 = new ConcurrentHashMap<>();
   private final AtomicBoolean atomicBoolean;
   private static final float value = 16.0F;
   private final Map<String, List<String>> map3 = new ConcurrentHashMap<>();
   private final Path path;
   private static final float value2 = 1.0F;
   private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2, runnable -> {
      Thread thread = new Thread(runnable, "wexside-custom-sounds");
      thread.setDaemon(true);
      return thread;
   });
   private static final float value3 = 1.0F;
   private WatchService watchService;

   public CustomSoundLibrary(Path path) throws IOException {
      this.atomicBoolean = new AtomicBoolean(false);
      this.path = path;
      Files.createDirectories(path);
      this.update2();
      this.update3();
   }

   @Override
   public void close() {
      this.atomicBoolean.set(false);

      try {
         if (this.watchService != null) {
            this.watchService.close();
         }
      } catch (IOException var2) {
      }

      this.scheduledExecutorService.shutdownNow();
      this.map2.clear();
      this.map3.clear();
   }

   public void process(String string, float f, long l) {
      byte[] byArray = (byte[])this.map2.get(string);
      if (byArray != null && !(f <= 0.0F)) {
         this.process8(() -> this.process10(byArray, f), l);
      }
   }

   public boolean process2(String string) {
      List<String> list = this.map3.get(string);
      return list != null && !list.isEmpty();
   }

   private static float process3(float f) {
      return Math.max(0.0F, Math.min(1.0F, f));
   }

   private float process4(float f) {
      f = Math.max(0.0F, f);
      if (f <= 1.0F) {
         return 1.0F;
      } else if (f >= 16.0F) {
         return 0.0F;
      } else {
         float f22 = 1.0F / (1.0F + 1.0F * (f - 1.0F));
         float f3 = 1.0F - (f - 1.0F) / 15.0F;
         return process3(f22 * f3);
      }
   }

   public List<String> process5(String string) {
      ArrayList<String> arrayList = new ArrayList<>(this.map3.getOrDefault(string, Collections.emptyList()));
      Collections.sort(arrayList);
      return arrayList;
   }

   public void process6(String string, double d, double d2, double d3, double d4, double d5, double d6, float f, long l) {
      byte[] byArray = (byte[])this.map2.get(string);
      if (byArray != null) {
         double d7 = d - d4;
         double d8 = d2 - d5;
         double d9 = d3 - d6;
         float f2 = (float)Math.sqrt(d7 * d7 + d8 * d8 + d9 * d9);
         float f3 = f * this.process4(f2);
         if (!(f3 <= 0.0F)) {
            this.process8(() -> this.process10(byArray, f3), l);
         }
      }
   }

   public boolean process7(String string) {
      return this.map2.containsKey(string);
   }

   public void update() {
      synchronized(this) {
         try {
            this.update2();
         } catch (IOException var6) {
         }
      }
   }

   public List<String> getList() {
      ArrayList<String> arrayList = new ArrayList<>(this.map3.keySet());
      Collections.sort(arrayList);
      return arrayList;
   }

   private void setPath(Path path) {
      String string = this.process12(path);
      this.map2.remove(string);
      String string2 = process13(string);
      List<String> list = this.map3.get(string2);
      if (list != null) {
         list.remove(string);
         if (list.isEmpty()) {
            this.map3.remove(string2);
         }
      }
   }

   private void process8(Runnable runnable, long l) {
      if (l > 0L) {
         this.scheduledExecutorService.schedule(runnable, l, TimeUnit.MILLISECONDS);
      } else {
         this.scheduledExecutorService.execute(runnable);
      }
   }

   public void setPath2(Path path) {
      try {
         String string2 = this.process12(path);
         this.map2.put(string2, Files.readAllBytes(path));
         String string3 = process13(string2);
         List list = this.map3.computeIfAbsent(string3, string -> new CopyOnWriteArrayList());
         if (!list.contains(string2)) {
            list.add(string2);
         }
      } catch (IOException var5) {
      }
   }

   public static boolean process9(Path path) {
      return path.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".wav");
   }

   private void setPath3(Path path) throws IOException {
      Files.walkFileTree(path, new DirectoryWatchRegistrar(this));
   }

   public void registerDirectory(Path directory) throws IOException {
      directory.register(this.watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
   }

   private void update2() throws IOException {
      this.map2.clear();
      this.map3.clear();
      Files.walkFileTree(this.path, new WavFileVisitor(this));
   }

   private void process10(byte[] byArray, float f) {
      try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(new ByteArrayInputStream(byArray)))) {
         AudioFormat audioFormat = audioInputStream.getFormat();
         AudioFormat audioFormat2 = process11(audioFormat);
         AudioInputStream audioInputStream2 = audioFormat.matches(audioFormat2)
            ? audioInputStream
            : AudioSystem.getAudioInputStream(audioFormat2, audioInputStream);
         Clip clip = (Clip)AudioSystem.getLine(new Info(Clip.class, audioFormat2));
         clip.addLineListener(lineEvent -> {
            if (lineEvent.getType() == Type.STOP || lineEvent.getType() == Type.CLOSE) {
               lineEvent.getLine().close();
            }
         });
         clip.open(audioInputStream2);
         if (clip.isControlSupported(javax.sound.sampled.FloatControl.Type.MASTER_GAIN)) {
            FloatControl floatControl = (FloatControl)clip.getControl(javax.sound.sampled.FloatControl.Type.MASTER_GAIN);
            float f2 = (float)(20.0 * Math.log10((double)Math.max(1.0E-4F, f)));
            floatControl.setValue(Math.max(floatControl.getMinimum(), Math.min(floatControl.getMaximum(), f2)));
         }

         clip.start();
      } catch (Exception var12) {
      }
   }

   private static AudioFormat process11(AudioFormat audioFormat) {
      return audioFormat.getEncoding() == Encoding.PCM_SIGNED
         ? audioFormat
         : new AudioFormat(
            Encoding.PCM_SIGNED, audioFormat.getSampleRate(), 16, audioFormat.getChannels(), audioFormat.getChannels() * 2, audioFormat.getSampleRate(), false
         );
   }

   private void update3() throws IOException {
      if (this.atomicBoolean.compareAndSet(false, true)) {
         this.watchService = FileSystems.getDefault().newWatchService();
         this.setPath3(this.path);
         Thread thread = new Thread(() -> {
            while(this.atomicBoolean.get()) {
               try {
                  WatchKey watchKey = this.watchService.take();
                  Path path = (Path)watchKey.watchable();

                  for(WatchEvent<?> watchEvent : watchKey.pollEvents()) {
                     Kind<?> kind = watchEvent.kind();
                     if (kind == StandardWatchEventKinds.OVERFLOW) {
                        this.setPath4(path);
                     } else {
                        Path path2 = path.resolve((Path)watchEvent.context());
                        if (Files.isDirectory(path2) && kind == StandardWatchEventKinds.ENTRY_CREATE) {
                           this.setPath3(path2);
                           this.setPath4(path2);
                        } else if (process9(path2)) {
                           if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                              this.setPath(path2);
                           } else {
                              this.setPath2(path2);
                           }
                        }
                     }
                  }

                  if (!watchKey.reset()) {
                     break;
                  }
               } catch (InterruptedException var7) {
                  Thread.currentThread().interrupt();
                  return;
               } catch (Exception var8) {
               }
            }
         }, "wexside-custom-sounds-watch");
         thread.setDaemon(true);
         thread.start();
      }
   }

   private String process12(Path path) {
      Path path2 = this.path.relativize(path).normalize();
      return path2.toString().replace('\\', '/').replaceAll("\\.wav$", "");
   }

   private void setPath4(Path path) {
      try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path)) {
         for(Path path2 : directoryStream) {
            if (!Files.isDirectory(path2) && process9(path2)) {
               this.setPath2(path2);
            }
         }
      } catch (IOException var7) {
      }
   }

   private static String process13(String string) {
      int n = string.indexOf(47);
      return n < 0 ? string : string.substring(0, n);
   }
}
