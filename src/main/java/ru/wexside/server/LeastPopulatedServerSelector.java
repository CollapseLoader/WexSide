package ru.wexside.server;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.class_1703;
import net.minecraft.class_1713;
import net.minecraft.class_1735;
import net.minecraft.class_1799;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_465;
import net.minecraft.class_746;
import net.minecraft.class_9290;
import net.minecraft.class_9334;
import ru.wexside.misc.ClientChat;
import ru.wexside.misc.ServerPopulationEntry;

public final class LeastPopulatedServerSelector {
   private static final int TIMEOUT_TICKS = 1600;
   private static final int MENU_SETTLE_TICKS = 6;
   private static final int PAGE_SETTLE_TICKS = 30;
   private static final int MENU_CHANGE_TIMEOUT_TICKS = 60;
   private static final int MAX_PAGES = 12;
   private static final int[] COMMAND_CATEGORIES = new int[]{1, 2, 3, 5, 10};
   private final Pattern populationPattern = Pattern.compile("Онлайн режима:\\s*(\\d+)");
   private final Pattern numberPattern = Pattern.compile("(\\d+)");
   private final List<ServerPopulationEntry> discoveredServers = new ArrayList<>();
   private final Set<String> discoveredNames = new HashSet<>();
   private LeastPopulatedServerSelector.State state = LeastPopulatedServerSelector.State.IDLE;
   private LeastPopulatedServerSelector.State stateAfterMenuChange;
   private ServerPopulationEntry selectedServer;
   private int elapsedTicks;
   private int menuTicks;
   private int page;
   private int categoryIndex;
   private int expectedSyncId = -1;
   private int menuChangeTicks;

   public void tick() {
      if (this.state != LeastPopulatedServerSelector.State.IDLE) {
         if (++this.elapsedTicks > 1600) {
            this.fail("таймаут");
         } else {
            class_310 client = class_310.method_1551();
            if (client.field_1724 != null && client.field_1755 instanceof class_465) {
               switch(this.state.ordinal()) {
                  case 1:
                     this.openVersionOrCategory();
                     break;
                  case 2:
                     this.awaitMenuChange();
                     break;
                  case 3:
                     this.scanCurrentCategory(false);
                     break;
                  case 4:
                     this.scanCurrentCategory(true);
               }
            } else {
               this.fail("меню закрыто");
            }
         }
      }
   }

   public void start() {
      if (this.state == LeastPopulatedServerSelector.State.IDLE) {
         class_310 client = class_310.method_1551();
         if (client.field_1724 != null && client.field_1755 instanceof class_465) {
            this.resetProgress();
            this.state = LeastPopulatedServerSelector.State.OPEN_VERSION;
            ClientChat.send("Ищем самую пустую анархию...");
         }
      }
   }

   private void openVersionOrCategory() {
      String title = this.currentTitle();
      if (title.contains("Выберите сервер") || title.contains("тип режима")) {
         int categorySlot = this.findCategorySlot(COMMAND_CATEGORIES[0]);
         if (categorySlot < 0) {
            this.fail("нет вкладки команды");
            return;
         }

         this.categoryIndex = 0;
         this.page = 0;
         this.clickAndWait(categorySlot, LeastPopulatedServerSelector.State.SCAN_SERVERS);
      } else if (title.contains("Выберите режим")) {
         int versionSlot = this.findSlotContaining("1.21.11");
         if (versionSlot < 0) {
            this.fail("нет типа 1.21.11");
            return;
         }

         this.clickAndWait(versionSlot, LeastPopulatedServerSelector.State.OPEN_VERSION);
      } else {
         this.fail("неожиданный экран: " + title);
      }
   }

   private void scanCurrentCategory(boolean locatingSelectedServer) {
      if (!this.currentTitle().contains("Выберите сервер")) {
         this.fail("ожидал экран серверов");
      } else {
         ++this.menuTicks;
         if (this.menuTicks >= 30 || this.menuTicks >= 6 && this.containsAnarchyServers()) {
            if (locatingSelectedServer) {
               int serverSlot = this.findExactSlot(this.selectedServer.name());
               if (serverSlot >= 0) {
                  this.clickSlot(serverSlot);
                  ClientChat.send("Заходим на " + this.selectedServer.name() + " (онлайн " + this.selectedServer.onlinePlayers() + ")");
                  this.finish();
                  return;
               }
            } else {
               this.collectCurrentPage();
            }

            int nextPageSlot = this.findSlotContaining("Следующая страница");
            if (nextPageSlot >= 0 && this.page < 12) {
               ++this.page;
               this.clickAndWait(
                  nextPageSlot,
                  locatingSelectedServer ? LeastPopulatedServerSelector.State.FIND_SELECTED_SERVER : LeastPopulatedServerSelector.State.SCAN_SERVERS
               );
            } else if (locatingSelectedServer) {
               this.fail("не нашёл сервер " + this.selectedServer.name());
            } else {
               ++this.categoryIndex;
               this.page = 0;
               if (this.categoryIndex < COMMAND_CATEGORIES.length) {
                  int categorySlot = this.findCategorySlot(COMMAND_CATEGORIES[this.categoryIndex]);
                  if (categorySlot < 0) {
                     this.fail("нет вкладки команды " + COMMAND_CATEGORIES[this.categoryIndex]);
                  } else {
                     this.clickAndWait(categorySlot, LeastPopulatedServerSelector.State.SCAN_SERVERS);
                  }
               } else {
                  this.selectedServer = this.findLeastPopulatedServer();
                  if (this.selectedServer == null) {
                     this.fail("серверов не найдено");
                  } else {
                     int selectedCategorySlot = this.findCategorySlot(COMMAND_CATEGORIES[this.selectedServer.categoryIndex()]);
                     if (selectedCategorySlot < 0) {
                        this.fail("нет вкладки команды для захода");
                     } else {
                        this.clickAndWait(selectedCategorySlot, LeastPopulatedServerSelector.State.FIND_SELECTED_SERVER);
                     }
                  }
               }
            }
         }
      }
   }

   private Integer readPopulation(class_1799 stack) {
      class_9290 lore = (class_9290)stack.method_58694(class_9334.field_49632);
      if (lore == null) {
         return null;
      } else {
         for(class_2561 line : lore.comp_2400()) {
            Matcher matcher = this.populationPattern.matcher(line.getString());
            if (matcher.find()) {
               return Integer.parseInt(matcher.group(1));
            }
         }

         return null;
      }
   }

   private void collectCurrentPage() {
      class_1703 handler = this.currentHandler();
      if (handler != null) {
         for(class_1735 slot : handler.field_7761) {
            if (slot.method_7681()) {
               class_1799 stack;
               String name;
               Integer var10000 = (name = (stack = slot.method_7677()).method_7964().getString()).contains("Анархия-") ? this.readPopulation(stack) : null;
               Integer population = var10000;
               if (var10000 != null && this.discoveredNames.add(name)) {
                  this.discoveredServers.add(new ServerPopulationEntry(name, population, this.categoryIndex));
               }
            }
         }
      }
   }

   private boolean containsAnarchyServers() {
      class_1703 handler = this.currentHandler();
      if (handler == null) {
         return false;
      } else {
         for(class_1735 slot : handler.field_7761) {
            if (slot.method_7681() && slot.method_7677().method_7964().getString().contains("Анархия-") && this.readPopulation(slot.method_7677()) != null) {
               return true;
            }
         }

         return false;
      }
   }

   private int findExactSlot(String name) {
      class_1703 handler = this.currentHandler();
      if (handler == null) {
         return -1;
      } else {
         for(class_1735 slot : handler.field_7761) {
            if (slot.method_7681() && slot.method_7677().method_7964().getString().equals(name)) {
               return slot.field_7874;
            }
         }

         return -1;
      }
   }

   private int findSlotContaining(String text) {
      class_1703 handler = this.currentHandler();
      if (handler == null) {
         return -1;
      } else {
         for(class_1735 slot : handler.field_7761) {
            if (slot.method_7681() && slot.method_7677().method_7964().getString().contains(text)) {
               return slot.field_7874;
            }
         }

         return -1;
      }
   }

   private int findCategorySlot(int category) {
      class_1703 handler = this.currentHandler();
      if (handler == null) {
         return -1;
      } else {
         for(class_1735 slot : handler.field_7761) {
            String name;
            if (slot.method_7681() && (name = slot.method_7677().method_7964().getString()).contains("Команды") && this.lastNumber(name) == category) {
               return slot.field_7874;
            }
         }

         return -1;
      }
   }

   private int lastNumber(String text) {
      Matcher matcher = this.numberPattern.matcher(text);
      int value = -1;

      while(matcher.find()) {
         value = Integer.parseInt(matcher.group(1));
      }

      return value;
   }

   private void clickAndWait(int slot, LeastPopulatedServerSelector.State nextState) {
      class_1703 handler = this.currentHandler();
      this.expectedSyncId = handler == null ? -1 : handler.field_7763;
      this.stateAfterMenuChange = nextState;
      this.menuChangeTicks = 0;
      this.menuTicks = 0;
      this.clickSlot(slot);
      this.state = LeastPopulatedServerSelector.State.WAIT_FOR_MENU;
   }

   private void awaitMenuChange() {
      class_1703 handler = this.currentHandler();
      if (handler != null && handler.field_7763 != this.expectedSyncId) {
         this.state = this.stateAfterMenuChange;
         this.menuChangeTicks = 0;
         this.menuTicks = 0;
      } else {
         if (++this.menuChangeTicks > 60) {
            this.fail("окно не пришло");
         }
      }
   }

   private void clickSlot(int slot) {
      class_310 client = class_310.method_1551();
      class_746 player = client.field_1724;
      class_1703 handler = this.currentHandler();
      if (player != null && handler != null && client.field_1761 != null) {
         client.field_1761.method_2906(handler.field_7763, slot, 0, class_1713.field_7790, player);
      }
   }

   private ServerPopulationEntry findLeastPopulatedServer() {
      ServerPopulationEntry best = null;

      for(ServerPopulationEntry entry : this.discoveredServers) {
         if (best == null || entry.onlinePlayers() < best.onlinePlayers()) {
            best = entry;
         }
      }

      return best;
   }

   private String currentTitle() {
      class_310 client = class_310.method_1551();
      return client.field_1755 == null ? "" : client.field_1755.method_25440().getString();
   }

   private class_1703 currentHandler() {
      class_746 player = class_310.method_1551().field_1724;
      return player == null ? null : player.field_7512;
   }

   private void fail(String reason) {
      ClientChat.send("Пустая анархия: " + reason);
      this.finish();
   }

   private void finish() {
      this.state = LeastPopulatedServerSelector.State.IDLE;
      this.resetProgress();
   }

   private void resetProgress() {
      this.discoveredServers.clear();
      this.discoveredNames.clear();
      this.selectedServer = null;
      this.stateAfterMenuChange = null;
      this.elapsedTicks = 0;
      this.menuTicks = 0;
      this.page = 0;
      this.categoryIndex = 0;
      this.expectedSyncId = -1;
      this.menuChangeTicks = 0;
   }

   private static enum State {
      IDLE,
      OPEN_VERSION,
      WAIT_FOR_MENU,
      SCAN_SERVERS,
      FIND_SELECTED_SERVER;
   }
}
