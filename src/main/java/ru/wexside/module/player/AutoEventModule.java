package ru.wexside.module.player;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.class_2561;
import net.minecraft.class_2596;
import net.minecraft.class_310;
import net.minecraft.class_634;
import net.minecraft.class_7439;
import ru.wexside.WexSideClient;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.event.IncomingPacketEvent;
import ru.wexside.event.WorldSessionEvent;
import ru.wexside.misc.ElapsedTimer;
import ru.wexside.misc.Waypoint;
import ru.wexside.misc.WaypointStore;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.server.FunTimeServerContext;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.NumberSetting;
import ru.wexside.setting.NumberSettingBuilder;

public final class AutoEventModule extends Module implements ConfigSerializable {
   private static final String EVENT_DELAY_COMMAND = "event delay";
   private static final Pattern COORD_PATTERN = Pattern.compile("-?\\d+");
   private static final List<String> EVENT_NAMES = List.of("аирдроп", "airdrop", "танк", "метеорит", "схрон", "босс", "страж", "дракон", "ивент");
   private final BooleanSetting enabledSetting;
   private final BooleanSetting checkEvents;
   private final NumberSetting commandDelay;
   private final ConcurrentLinkedQueue<Waypoint> waypoints = new ConcurrentLinkedQueue<>();
   private final ElapsedTimer commandTimer = new ElapsedTimer();
   private volatile String pendingEvent;
   private volatile boolean sendDelayOnJoin;

   public AutoEventModule(EventBus eventBus) {
      super(eventBus, "auto_event", "Auto Event", "Автоматически отмечает ивенты FT вейпоинтами", ModuleCategory.valueOf("PLAYER"));
      this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Enabled")
            .id("enabled")
            .description("Автоматически отмечает ивенты вейпоинтами")
            .withKeybind()
            .toggle())
         .build();
      this.registerSetting(this.enabledSetting);
      this.checkEvents = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Check Events")
            .id("check_events")
            .description("Периодически отправлять /event delay")
            .aliases("check events", "проверка ивентов"))
         .build();
      this.registerSetting(this.checkEvents);
      this.commandDelay = ((NumberSettingBuilder)NumberSetting.builder()
            .range(1.0, 10.0)
            .defaultValue(3.0)
            .multiplier(1.0)
            .precision(0)
            .animationSpeed(20.0F)
            .name("Command Delay")
            .id("command_delay")
            .description("Задержка между /event delay (в минутах)")
            .aliases("command delay", "задержка")
            .visibleWhen(this.checkEvents::isEnabled))
         .build();
      this.registerSetting(this.commandDelay);
   }

   @Override
   protected void initialize() {
      this.listen(IncomingPacketEvent.class, this::onIncomingPacket);
      this.listen(ClientTickEvent.class, this::onTick);
      this.listen(WorldSessionEvent.class, event -> this.sendDelayOnJoin = true);
   }

   private void onIncomingPacket(IncomingPacketEvent event) {
      if (this.enabledSetting.isEnabled()) {
         class_2596 content = event.getPacket();
         if (content instanceof class_7439 chat && !chat.comp_906()) {
            class_2561 contentx = chat.comp_763();
            if (contentx == null) {
               return;
            }

            String message = contentx.getString().toLowerCase(Locale.ROOT);
            String eventName = this.matchEventName(message);
            if (eventName != null) {
               this.pendingEvent = eventName;
            }

            if (this.pendingEvent != null && this.hasCoordinates(message)) {
               int[] xyz = this.parseCoordinates(message);
               if (xyz != null) {
                  this.waypoints.add(new Waypoint(this.pendingEvent, xyz[0], xyz[1], xyz[2]));
               }

               this.pendingEvent = null;
            }

            return;
         }
      }
   }

   private void onTick(ClientTickEvent event) {
      WaypointStore waypointsStore = WexSideClient.getWaypointStore();
      Waypoint waypoint;
      if (waypointsStore != null) {
         while((waypoint = this.waypoints.poll()) != null) {
            waypointsStore.add(waypoint);
         }
      }

      if (!this.enabledSetting.isEnabled()) {
         this.sendDelayOnJoin = false;
      } else {
         class_634 network = class_310.method_1551().method_1562();
         if (network != null) {
            if (this.sendDelayOnJoin) {
               this.sendDelayOnJoin = false;
               if (FunTimeServerContext.isConnected()) {
                  network.method_45730("event delay");
               }
            }

            if (this.checkEvents.isEnabled() && FunTimeServerContext.isConnected() && this.commandTimer.process((long)this.commandDelay.getIntValue() * 60000L)
               )
             {
               network.method_45730("event delay");
               this.commandTimer.update();
            }
         }
      }
   }

   private String matchEventName(String message) {
      for(String name : EVENT_NAMES) {
         if (message.contains(name.toLowerCase(Locale.ROOT))) {
            return name;
         }
      }

      return null;
   }

   private int[] parseCoordinates(String message) {
      Matcher matcher = COORD_PATTERN.matcher(message);
      int regionStart = message.indexOf("координат");
      if (regionStart >= 0) {
         matcher.region(regionStart, message.length());
      }

      int[] xyz = new int[3];

      int found;
      for(found = 0; matcher.find() && found < 3; ++found) {
         try {
            xyz[found] = Integer.parseInt(matcher.group());
         } catch (NumberFormatException var7) {
            return null;
         }
      }

      return found == 3 ? xyz : null;
   }

   private boolean hasCoordinates(String message) {
      return message.contains("координаты") || message.contains("на координатах");
   }
}
