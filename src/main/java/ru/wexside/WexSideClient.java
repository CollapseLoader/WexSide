package ru.wexside;

import com.google.gson.Gson;
import com.twelvemonkeys.imageio.plugins.webp.WebPImageReaderSpi;
import java.nio.file.Path;
import java.util.EnumSet;
import javax.imageio.spi.IIORegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_310;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.wexside.animation.AnimationQueryRegistry;
import ru.wexside.config.LocalConfigCatalog;
import ru.wexside.event.EventBus;
import ru.wexside.event.EventBusImpl;
import ru.wexside.event.KeyPressedEvent;
import ru.wexside.misc.BlockEspConfigStore;
import ru.wexside.misc.BlockEspStore;
import ru.wexside.misc.BlockedSoundList;
import ru.wexside.misc.BlockedSoundStore;
import ru.wexside.misc.ClasspathResource;
import ru.wexside.misc.ClientProfile;
import ru.wexside.misc.ClientRole;
import ru.wexside.misc.CommandManager;
import ru.wexside.misc.ConfigManager;
import ru.wexside.misc.ConfigRegistry;
import ru.wexside.misc.ContainerDisplaySettings;
import ru.wexside.misc.FriendList;
import ru.wexside.misc.FriendListStore;
import ru.wexside.misc.InventoryHudRegistry;
import ru.wexside.misc.KeybindRegistry;
import ru.wexside.misc.MacroConfigStore;
import ru.wexside.misc.MacroManager;
import ru.wexside.misc.PasswordConfigStore;
import ru.wexside.misc.PasswordStore;
import ru.wexside.misc.PotionPresetStore;
import ru.wexside.misc.ResourceResolver;
import ru.wexside.misc.ServerTickRate;
import ru.wexside.misc.SlotHighlightRegistry;
import ru.wexside.misc.SpookyTest;
import ru.wexside.misc.StaffNameConfigStore;
import ru.wexside.misc.StaffNameStore;
import ru.wexside.misc.TextureResource;
import ru.wexside.misc.ThemeColors;
import ru.wexside.misc.TickOverride;
import ru.wexside.misc.WaypointConfigStore;
import ru.wexside.misc.WaypointStore;
import ru.wexside.module.ModuleManager;
import ru.wexside.module.ModuleState;
import ru.wexside.module.hud.HUDModule;
import ru.wexside.notification.NotificationCenter;
import ru.wexside.render.ClientEventBridge;
import ru.wexside.render.ClientRenderPipelines;
import ru.wexside.render.GlShaderProgram;
import ru.wexside.render.GuiRenderPipeline;
import ru.wexside.render.OffscreenRenderManager;
import ru.wexside.render.WexGlobals;
import ru.wexside.schedule.EventScheduleRegistry;
import ru.wexside.ui.HudLayoutManager;
import ru.wexside.ui.WexsideScreen;
import ru.wexside.util.ClickGuiPanel;
import ru.wexside.util.Easing;
import ru.wexside.util.EspFeatureRegistry;
import ru.wexside.util.EspRenderCoordinator;
import ru.wexside.util.GuiDrawApi;
import ru.wexside.util.GuiPhotoBanner;
import ru.wexside.util.GuiRenderBackend;
import ru.wexside.util.HotbarSlotLock;
import ru.wexside.util.InventoryController;
import ru.wexside.util.PotionCombiner;
import ru.wexside.util.PotionPresetController;
import ru.wexside.util.RotationController;
import ru.wexside.util.entity.NpcDetector;

public class WexSideClient implements ClientModInitializer {
   public static final String MOD_ID = "wexside";
   public static final String VERSION_LABEL = "Wexside 1.21.11 (CL)";
   @SuppressWarnings("unused")
   private static final String collapseloader = "has been here";
   static SlotHighlightRegistry slotHighlightRegistry;
   static FriendList friendList;
   static OffscreenRenderManager entityRenderPipeline;
   static RotationController rotationController;
   static GuiDrawApi legacyGuiRenderer;
   static EspRenderCoordinator espRenderCoordinator;
   static ServerTickRate serverTickRate;
   private EventScheduleRegistry eventSchedules;
   private Path configDirectory;
   private CommandManager commandManager;
   private ClickGuiPanel miscellaneous;
   private ContainerDisplaySettings containerDisplaySettings;
   private ClientProfile clientProfile;
   static BlockedSoundList blockedSoundList;
   private PotionPresetController potionPresetController;
   private ConfigRegistry configRegistry;
   private PotionCombiner potionCombiner;
   private StaffNameStore staffNameStore;
   static InventoryHudRegistry inventoryHudRegistry;
   static HotbarSlotLock hotbarSlotLock;
   static MacroManager macroManager;
   static GuiDrawApi gpuGuiRenderer;
   static NotificationCenter notificationCenter;
   static EspFeatureRegistry espFeatures;
   static EventBus eventBus;
   static InventoryController inventory;
   static TickOverride tickOverride;
   private Logger logger;
   static WaypointStore waypointStore;
   private LocalConfigCatalog localConfigCatalog;
   static NpcDetector npcDetector;
   static HudLayoutManager hudLayoutManager;
   static GuiDrawApi hudRenderer;
   private KeybindRegistry keybindRegistry;
   static PasswordStore passwordStore;
   static ConfigManager configManager;
   static BlockEspStore blockEspStore;
   private static GuiDrawApi guiRendererOverride;
   private ModuleManager moduleManager;
   static WexSideClient instance;

   public static ServerTickRate getServerTickRate() {
      return serverTickRate;
   }

   public static InventoryController getInventoryController() {
      return inventory;
   }

   public static WexSideClient getInstance() {
      return instance;
   }

   public void onInitializeClient() {
      this.bootstrap();
   }

   public ModuleManager getModuleManager() {
      return this.moduleManager;
   }

   public static RotationController getRotationController() {
      return rotationController;
   }

   public static GuiDrawApi getHudRenderer() {
      if (hudRenderer == null) {
         initializeHudRenderer();
      }

      return hudRenderer;
   }

   public static OffscreenRenderManager getRenderPipeline2() {
      if (entityRenderPipeline == null) {
         entityRenderPipeline = new OffscreenRenderManager();
      }

      return entityRenderPipeline;
   }

   public static NpcDetector getNpcDetector() {
      return npcDetector;
   }

   public static FriendList getFriends() {
      return friendList;
   }

   public static HotbarSlotLock getHotbarSlotLock() {
      return hotbarSlotLock;
   }

   public Logger getLogger() {
      return this.logger;
   }

   public ClientProfile getClientProfile() {
      return this.clientProfile;
   }

   public static BlockEspStore getBlockEspStore() {
      return blockEspStore;
   }

   public static WaypointStore getWaypointStore() {
      return waypointStore;
   }

   public StaffNameStore getStaffNameStore() {
      return this.staffNameStore;
   }

   public static BlockedSoundList getBlockedSoundList() {
      return blockedSoundList;
   }

   public static EventBus getEventBus() {
      return eventBus;
   }

   public static MacroManager getMacroManager() {
      return macroManager;
   }

   public static ConfigManager getConfigManager() {
      return configManager;
   }

   private void initializeServices() {
      AnimationQueryRegistry.registerDefaults();
      eventBus = new EventBusImpl();
      this.keybindRegistry = new KeybindRegistry(eventBus);
      this.configRegistry = new ConfigRegistry();
      hudLayoutManager = new HudLayoutManager(this.configRegistry);
      tickOverride = new TickOverride(eventBus);
      serverTickRate = new ServerTickRate(eventBus);
      slotHighlightRegistry = new SlotHighlightRegistry();
      inventoryHudRegistry = new InventoryHudRegistry();
      inventory = new InventoryController(eventBus);
      hotbarSlotLock = new HotbarSlotLock(eventBus);
      SpookyTest rotationStrategies = new SpookyTest();
      rotationStrategies.update();
      rotationController = new RotationController(rotationStrategies, eventBus);
      configManager = new ConfigManager(this.configDirectory.toFile(), new Gson(), this.configRegistry, this.keybindRegistry);
      friendList = new FriendList(configManager.getStore(FriendListStore.class));
      this.staffNameStore = new StaffNameStore(configManager.getStore(StaffNameConfigStore.class));
      blockedSoundList = new BlockedSoundList(configManager.getStore(BlockedSoundStore.class));
      passwordStore = new PasswordStore(configManager.getStore(PasswordConfigStore.class));
      waypointStore = new WaypointStore(configManager.getStore(WaypointConfigStore.class));
      blockEspStore = new BlockEspStore(configManager.getStore(BlockEspConfigStore.class));
      macroManager = new MacroManager(configManager.getStore(MacroConfigStore.class), eventBus);
      npcDetector = new NpcDetector(eventBus);
      ModuleManager moduleManager = new ModuleManager();
      moduleManager.registerDefaults(eventBus);
      espFeatures = new EspFeatureRegistry(eventBus);
      moduleManager.getModules().addAll(espFeatures.getModules());
      this.moduleManager = moduleManager;
      moduleManager.registerKeybindSettings(this.keybindRegistry);
      moduleManager.registerConfigEntries(this.configRegistry);
      this.configRegistry.register(this.keybindRegistry);
      this.containerDisplaySettings = new ContainerDisplaySettings(this.configRegistry);
      this.localConfigCatalog = new LocalConfigCatalog(configManager);
      PotionPresetStore potionStore = configManager.getStore(PotionPresetStore.class);
      this.potionPresetController = new PotionPresetController(eventBus, this.keybindRegistry, potionStore);
      this.potionCombiner = new PotionCombiner(this.potionPresetController);
      espRenderCoordinator = new EspRenderCoordinator(eventBus, espFeatures);
      String cordName = System.getenv("WEXSIDE_USERNAME");
      if (cordName == null || cordName.isBlank()) {
         cordName = System.getProperty("wexside.username");
      }

      if (cordName == null || cordName.isBlank()) {
         cordName = "WexSide";
      }

      this.clientProfile = new ClientProfile(ClientRole.USER, cordName.trim(), "offline", 0, EnumSet.of(ModuleState.DISABLED), null, new byte[0]);
      notificationCenter = new NotificationCenter(moduleManager.getModule(HUDModule.class));
      GuiPhotoBanner photoBanner = new GuiPhotoBanner(
         new TextureResource(new ClasspathResource("/assets/wexside/textures/gui/startup_card.jpg")),
         System::nanoTime,
         Easing.EASE_OUT_CUBIC,
         Easing.EASE_IN_CUBIC,
         ThemeColors::backgroundPrimary,
         ThemeColors::borderStrong
      );
      this.miscellaneous = new ClickGuiPanel(moduleManager, this.containerDisplaySettings, this.localConfigCatalog, photoBanner);
      eventBus.subscribe(KeyPressedEvent.class, this::handleMenuKey);
      configManager.initialize();
      this.localConfigCatalog.refresh();
      ClientEventBridge.register(eventBus);
   }

   private void handleMenuKey(KeyPressedEvent event) {
      if (event.key() == 344) {
         class_310 client = class_310.method_1551();
         if (client.field_1755 instanceof WexsideScreen) {
            client.method_1507(null);
         } else if (client.field_1755 == null && this.miscellaneous != null) {
            client.method_1507(new WexsideScreen(this.miscellaneous));
         }
      }
   }

   private void bootstrap() {
      this.configDirectory = FabricLoader.getInstance().getConfigDir().resolve("wexside");
      this.logger = LoggerFactory.getLogger("wexside");
      instance = this;
      this.initializeServices();
      this.initializeCommands();
      this.registerWebPReader();
   }

   public static NotificationCenter getNotificationCenter() {
      return notificationCenter;
   }

   public static InventoryHudRegistry getInventoryHudRegistry() {
      return inventoryHudRegistry;
   }

   public CommandManager getCommandManager() {
      return this.commandManager;
   }

   public static GuiDrawApi getGpuGuiRenderer() {
      if (gpuGuiRenderer == null) {
         initializeGpuGuiRenderer();
      }

      return gpuGuiRenderer;
   }

   private static void initializeLegacyGuiRenderer() {
      ResourceResolver shaderResources = new ResourceResolver("/assets/wexside/shaders/", ClasspathResource::new);
      legacyGuiRenderer = new GuiDrawApi(
         new GuiRenderBackend(new GlShaderProgram(shaderResources.resolve("gl/core_gl.frag"), shaderResources.resolve("gl/core_gl.vert")))
      );
   }

   public LocalConfigCatalog getLocalConfigCatalog() {
      return this.localConfigCatalog;
   }

   public PotionCombiner getPotionCombiner() {
      return this.potionCombiner;
   }

   public static PasswordStore getPasswordStore() {
      return passwordStore;
   }

   public ConfigRegistry getConfigRegistry() {
      return this.configRegistry;
   }

   public ClickGuiPanel getMiscellaneous() {
      return this.miscellaneous;
   }

   private static void initializeHudRenderer() {
      if (legacyGuiRenderer == null) {
         initializeLegacyGuiRenderer();
      }

      GuiRenderBackend guiRenderBackend = (GuiRenderBackend)legacyGuiRenderer.getRenderDriver();
      WexGlobals wexGlobals = (WexGlobals)getGpuGuiRenderer().getRenderDriver();
      hudRenderer = new GuiDrawApi(new GuiRenderPipeline(guiRenderBackend, wexGlobals));
   }

   public PotionPresetController getPotionPresetController() {
      return this.potionPresetController;
   }

   public static TickOverride getTickOverride() {
      return tickOverride;
   }

   public EventScheduleRegistry getEventSchedules() {
      return this.eventSchedules;
   }

   public static HudLayoutManager getHudLayoutManager() {
      return hudLayoutManager;
   }

   public ContainerDisplaySettings getContainerDisplaySettings() {
      return this.containerDisplaySettings;
   }

   private void registerWebPReader() {
      try {
         IIORegistry.getDefaultInstance().registerServiceProvider(new WebPImageReaderSpi());
      } catch (Throwable var2) {
         this.logger.warn("WebP reader unavailable: {}", var2.toString());
      }
   }

   private static void initializeGpuGuiRenderer() {
      gpuGuiRenderer = new GuiDrawApi(new WexGlobals(ClientRenderPipelines.GUI_BATCH));
   }

   public static SlotHighlightRegistry getSlotHighlightRegistry() {
      return slotHighlightRegistry;
   }

   private void initializeCommands() {
      this.eventSchedules = new EventScheduleRegistry();
      CommandManager commandManager = new CommandManager();
      commandManager.initializeCommands();
      this.commandManager = commandManager;
   }

   public static EspFeatureRegistry getEspFeatureRegistry() {
      return espFeatures;
   }

   public static EspRenderCoordinator getEspRenderCoordinator() {
      return espRenderCoordinator;
   }

   public Path getConfigDirectory() {
      return this.configDirectory;
   }

   public static GuiDrawApi getGuiRenderer() {
      return guiRendererOverride != null ? guiRendererOverride : getHudRenderer();
   }

   public static void setGuiRendererOverride(GuiDrawApi renderer) {
      guiRendererOverride = renderer;
   }

   public KeybindRegistry getKeybindRegistry() {
      return this.keybindRegistry;
   }
}
