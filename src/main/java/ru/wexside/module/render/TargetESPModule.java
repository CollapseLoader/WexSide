package ru.wexside.module.render;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_746;
import ru.wexside.WexSideClient;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.EntityAttackEvent;
import ru.wexside.event.EventBus;
import ru.wexside.event.WorldRenderEvent;
import ru.wexside.event.WorldSessionEvent;
import ru.wexside.misc.TargetEspEffect;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.module.combat.AttackAuraModule;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.ColorSetting;
import ru.wexside.setting.ColorSettingBuilder;
import ru.wexside.setting.ModeSetting;
import ru.wexside.setting.ModeSettingBuilder;
import ru.wexside.util.AuraTargetEspRenderer;
import ru.wexside.util.CylinderTargetEspRenderer;
import ru.wexside.util.MarkerTargetEspRenderer;
import ru.wexside.util.SkullTargetEspRenderer;
import ru.wexside.util.SwordTargetEspRenderer;

public final class TargetESPModule extends Module implements ConfigSerializable {
   private static final String SWORD = "Sword";
   private static volatile TargetESPModule instance;
   private final BooleanSetting enabledSetting;
   private final ModeSetting mode;
   private final ColorSetting color;
   private final BooleanSetting useAttackAuraTarget;
   private final Map<String, TargetEspEffect> renderers;
   private WorldRenderEvent pendingWorldRender;
   private AttackAuraModule attackAura;
   private class_1309 lastTarget;
   private String lastMode;

   public TargetESPModule(EventBus eventBus) {
      super(eventBus, "target_esp", "Target ESP", "Подсветка атакуемой цели", ModuleCategory.valueOf("RENDER"));
      instance = this;
      this.renderers = this.createRenderers();
      this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Enabled")
            .id("enabled")
            .description("Включить подсветку цели")
            .withKeybind()
            .toggle())
         .build();
      this.registerSetting(this.enabledSetting);
      this.mode = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder()
            .options("Sword", "Cylinder", "Marker", "Skull", "Aura")
            .defaultOption("Sword")
            .name("Mode")
            .id("mode")
            .description("Стиль подсветки"))
         .build();
      this.registerSetting(this.mode);
      ColorSetting colorSetting = ((ColorSettingBuilder)ColorSetting.builder().selectedIndex(0).name("Color").id("color").description("Цвет Target ESP"))
         .build();
      colorSetting.setPrimaryColor(0, -11753627);
      colorSetting.setPrimaryColor(1, -1543135);
      colorSetting.setPrimaryColor(2, -9279489);
      colorSetting.setPrimaryColor(3, -46001);
      colorSetting.setPrimaryColor(4, -13218);
      colorSetting.setPrimaryColor(5, -10582785);
      colorSetting.setPrimaryColor(6, -2732032);
      this.color = colorSetting;
      this.registerSetting(colorSetting);
      this.useAttackAuraTarget = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Attack Aura only")
            .id("use_attack_aura_target")
            .description("Отображать только при использовании Attack Aura"))
         .build();
      this.registerSetting(this.useAttackAuraTarget);
   }

   @Override
   protected void initialize() {
      this.listen(EntityAttackEvent.class, this::onAttack);
      this.listen(WorldRenderEvent.class, this::onWorldRender);
      this.listen(WorldSessionEvent.class, event -> this.resetRenderers());
   }

   public static void tick3() {
      TargetESPModule module = instance;
      if (module != null) {
         module.flushPendingRender();
      }
   }

   private void onAttack(EntityAttackEvent event) {
      if (this.enabledSetting.isEnabled()) {
         class_1297 var3 = event.getEntity();
         if (var3 instanceof class_1309 livingEntity) {
            this.lastTarget = livingEntity;
         }

         this.currentRenderer().setEntityAttackEvent(event);
      }
   }

   private void onWorldRender(WorldRenderEvent event) {
      if (!this.enabledSetting.isEnabled()) {
         this.resetRenderers();
         this.pendingWorldRender = null;
      } else {
         if (this.currentRenderer().isActive()) {
            this.pendingWorldRender = event;
         } else {
            this.pendingWorldRender = null;
            this.render(event);
         }
      }
   }

   private void flushPendingRender() {
      WorldRenderEvent event = this.pendingWorldRender;
      this.pendingWorldRender = null;
      if (event != null && this.enabledSetting.isEnabled()) {
         this.render(event);
      }
   }

   private void render(WorldRenderEvent event) {
      this.syncMode();
      this.auraTarget();
      this.currentRenderer().setWorldRenderEvent(event);
   }

   private void syncMode() {
      String selected = this.mode.getSelectedOption();
      if (selected != null) {
         if (this.lastMode == null) {
            this.lastMode = selected;
         } else {
            if (!selected.equals(this.lastMode)) {
               this.resetRenderers();
               this.lastMode = selected;
            }
         }
      }
   }

   private void resetRenderers() {
      this.renderers.values().forEach(TargetEspEffect::update);
      this.lastTarget = null;
   }

   private TargetEspEffect currentRenderer() {
      return this.renderers.getOrDefault(this.mode.getSelectedOption(), this.renderers.get("Sword"));
   }

   private Map<String, TargetEspEffect> createRenderers() {
      LinkedHashMap<String, TargetEspEffect> map = new LinkedHashMap<>();
      map.put("Sword", new SwordTargetEspRenderer());
      map.put("Cylinder", new CylinderTargetEspRenderer());
      map.put("Marker", new MarkerTargetEspRenderer());
      map.put("Skull", new SkullTargetEspRenderer());
      map.put("Aura", new AuraTargetEspRenderer());
      return map;
   }

   private AttackAuraModule attackAura() {
      if (this.attackAura != null) {
         return this.attackAura;
      } else if (WexSideClient.getInstance() != null && WexSideClient.getInstance().getModuleManager() != null) {
         this.attackAura = WexSideClient.getInstance().getModuleManager().getModule(AttackAuraModule.class);
         return this.attackAura;
      } else {
         return null;
      }
   }

   private class_1309 auraTarget() {
      if (!this.useAttackAuraTarget.isEnabled()) {
         return null;
      } else {
         AttackAuraModule module = this.attackAura();
         return module != null ? module.getLivingEntity() : null;
      }
   }

   public static TargetESPModule getInstance() {
      return instance;
   }

   public class_1309 getCurrentTarget() {
      class_1309 aura = this.auraTarget();
      class_1309 target = aura != null ? aura : this.lastTarget;
      if (target == null && !this.useAttackAuraTarget.isEnabled()) {
         target = this.findClosestTarget();
      }
      if (target == null || !target.method_5805() || !target.method_5732()) {
         this.lastTarget = null;
         return null;
      } else {
         return target;
      }
   }

   public int getPrimaryColor() {
      return this.color.getColor(0.0F);
   }

   public int getSecondaryColor() {
      return this.color.getColor(0.5F);
   }

   private class_1309 findClosestTarget() {
      class_310 mc = class_310.method_1551();
      if (mc == null || mc.field_1724 == null || mc.field_1687 == null) {
         return null;
      }

      class_746 player = mc.field_1724;
      class_243 playerPos = player.method_30950(1.0F);
      class_238 box = new class_238(
         playerPos.field_1352 - 6.0,
         playerPos.field_1351 - 6.0,
         playerPos.field_1350 - 6.0,
         playerPos.field_1352 + 6.0,
         playerPos.field_1351 + 6.0,
         playerPos.field_1350 + 6.0
      );
      List<class_1297> entities = mc.field_1687.method_8333(player, box, entity -> entity instanceof class_1309);
      double bestDistanceSq = 36.0;
      class_1309 bestTarget = null;

      for(class_1297 entity : entities) {
         if (!(entity instanceof class_1309 living)) {
            continue;
         }
         if (!living.method_5805() || !living.method_5732()) {
            continue;
         }

         class_243 entityPos = living.method_30950(1.0F);
         double d = playerPos.method_1028(entityPos.field_1352, entityPos.field_1351, entityPos.field_1350);
         if (d < bestDistanceSq) {
            bestDistanceSq = d;
            bestTarget = living;
         }
      }

      return bestTarget;
   }
}
