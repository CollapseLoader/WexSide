package ru.wexside.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1661;
import net.minecraft.class_1799;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_4604;
import net.minecraft.class_638;
import net.minecraft.class_746;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import ru.wexside.WexSideClient;
import ru.wexside.event.EventBus;
import ru.wexside.event.HudRenderEvent;
import ru.wexside.event.WorldSessionEvent;
import ru.wexside.misc.BakedIconEntry;
import ru.wexside.misc.BoxEspSettings;
import ru.wexside.misc.FontRegistry;
import ru.wexside.misc.FrameInterpolator;
import ru.wexside.misc.ServerKind;
import ru.wexside.model.esp.EspTargetClassifier;
import ru.wexside.model.esp.EspTargetType;
import ru.wexside.module.Module;
import ru.wexside.module.player.ServerHelperModule;
import ru.wexside.render.BakedItemIcon;
import ru.wexside.render.ItemIconCache;
import ru.wexside.render.RenderProjection;
import ru.wexside.server.ServerHelperAction;
import ru.wexside.server.ServerHelperActions;
import ru.wexside.server.ServerItemMatcher;

public final class ServerItemCooldownOverlay {
   private static final long longType = 200L;
   private long getTextureId;
   private static final float value2 = 10.0F;
   private final Map<UUID, ServerItemCooldownOverlay.EntityCooldownState> map2;
   private static final int slot = -1342177280;
   private static final int slot2 = 40;
   private final class_310 mc = class_310.method_1551();
   private final Map<ServerHelperAction, class_1799> map3;
   private static final float value3 = 12.0F;
   private static final float value4 = 12.0F;
   private static final float value5 = 6.0F;
   private static final int slot3 = Integer.MIN_VALUE;
   private final EspFeatureRegistry espFeatures;
   private static final float value6 = 50.0F;
   private ServerHelperModule serverHelperModule;
   private static final MsdfFontRenderer font5 = FontRegistry.font2;
   private static final long longType3 = 250L;
   private int process2;
   private final ItemIconCache itemIconCache;

   public ServerItemCooldownOverlay(EventBus eventBus, EspFeatureRegistry espFeatures) {
      this.espFeatures = espFeatures;
      this.map2 = new HashMap<>();
      this.map3 = new HashMap<>();
      this.itemIconCache = new ItemIconCache();
      eventBus.subscribe(HudRenderEvent.class, this::setHudRenderEvent, -100);
      eventBus.subscribe(WorldSessionEvent.class, this::slot3);
   }

   private void process(GuiDrawApi drawApi, Matrix4f matrix4f, float f, float f2, float f3, float f4, int n, int n2) {
      EspBoxBorderRenderer.process2(drawApi, matrix4f, f, f2, f3, f4, n, n2);
   }

   private void slot3(WorldSessionEvent gameEvent7) {
      this.itemIconCache.update3();
      this.map3.clear();
      this.map2.clear();
   }

   private float process3(ServerItemCooldownOverlay.EntityCooldownState entityCooldownState) {
      float f = 0.0F;

      for(ServerItemCooldownOverlay.CooldownDisplayEntry enabled2 : entityCooldownState.activeEntries) {
         f = Math.max(f, font5.process3(enabled2.label, 6.0F));
      }

      return Math.max(15.0F, f + 4.0F);
   }

   private void process4(class_746 player2, ServerKind serverKind2) {
      this.map3.clear();
      class_1661 inv = player2.method_31548();
      if (inv != null) {
         for(ServerHelperAction action : ServerHelperActions.COOLDOWN_TRACKED) {
            class_1799 stack = ServerItemMatcher.findStack(inv, action, serverKind2);
            if (!stack.method_7960()) {
               this.map3.put(action, stack);
            }
         }
      }
   }

   private void process5(GuiDrawApi drawApi, Matrix4f matrix4f, float f, float f2, float f3, float f4, int n, int n2, int n3, int n4) {
      EspBoxBorderRenderer.process(drawApi, matrix4f, f, f2, f3, f4, n, n2, n3, n4);
   }

   private void process6(ServerItemCooldownOverlay.EntityCooldownState entityCooldownState, float f) {
      for(ServerHelperAction action : ServerHelperActions.COOLDOWN_TRACKED) {
         String string2 = action.id();
         float f2 = action.activationDistance();
         class_1799 stack = (class_1799)this.map3.get(action);
         boolean bl = f2 > 0.0F && !action.donationItem() && f <= f2 && stack != null && !stack.method_7960();
         if (bl) {
            float f3 = class_3532.method_15363((f2 - f) / f2, 0.0F, 1.0F);
            float f4 = 1.0F - (1.0F - f3) * (1.0F - f3);
            int n = Math.round(10.0F + f4 * 90.0F);
            int n2 = (int)(255.0F * (1.0F - f4));
            int n3 = (int)(255.0F * f4);
            ServerItemCooldownOverlay.CooldownDisplayEntry enabled2 = entityCooldownState.entriesById
               .computeIfAbsent(string2, string -> new ServerItemCooldownOverlay.CooldownDisplayEntry());
            enabled2.stack = stack;
            enabled2.label = n + "%";
            enabled2.color = n2 << 16 | n3 << 8;
            enabled2.setVisible(true);
         } else {
            ServerItemCooldownOverlay.CooldownDisplayEntry enabled2 = entityCooldownState.entriesById.get(string2);
            if (enabled2 != null) {
               enabled2.setVisible(false);
            }
         }
      }
   }

   private void process7(GuiDrawApi drawApi, Matrix4f matrix4f, float f, float f2, float f3, float f4, int n, int n2, int n3, int n4) {
      EspBoxBorderRenderer.process4(drawApi, matrix4f, f, f2, f3, f4, n, n2, n3, n4);
   }

   private static int process8(int n, float f) {
      int n2 = (int)(class_3532.method_15363(f, 0.0F, 1.0F) * 255.0F);
      return n2 << 24 | n & 16777215;
   }

   private void process9(List<class_1297> list, class_746 player2, Matrix4f matrix4f, float f) {
      ServerKind serverKind2 = this.getServerKind();
      long l = System.currentTimeMillis();
      if (l - this.getTextureId >= 200L) {
         this.process4(player2, serverKind2);
         this.getTextureId = l;
      }

      this.itemIconCache.update2();
      ++this.process2;

      for(class_1297 object22 : list) {
         if (object22 != player2 && object22 instanceof class_1309 && object22.method_5805()) {
            Vector2f screenPosition = RenderProjection.projectEntityCenter(object22, matrix4f);
            if (screenPosition != null) {
               ServerItemCooldownOverlay.EntityCooldownState entityState = this.map2
                  .computeIfAbsent(object22.method_5667(), uUID -> new ServerItemCooldownOverlay.EntityCooldownState());
               entityState.lastSeenFrame = this.process2;
               entityState.screenX = screenPosition.x;
               entityState.screenY = screenPosition.y;
               entityState.markVisible(true);
               this.process6(entityState, player2.method_5739(object22));
            }
         }
      }

      ArrayList<ServerItemCooldownOverlay.EntityCooldownState> arrayList = new ArrayList<>();
      Iterator<ServerItemCooldownOverlay.EntityCooldownState> iterator2 = this.map2.values().iterator();

      while(iterator2.hasNext()) {
         ServerItemCooldownOverlay.EntityCooldownState state = iterator2.next();
         if (this.process2 - state.lastSeenFrame > 40) {
            iterator2.remove();
         } else if (state.lastSeenFrame == this.process2) {
            state.update(this.itemIconCache);
            if (!state.activeEntries.isEmpty()) {
               arrayList.add(state);
            }
         }
      }

      if (arrayList.isEmpty()) {
         this.itemIconCache.update();
      } else {
         ArrayList<BakedIconEntry> arrayList2 = new ArrayList<>();
         this.itemIconCache.process2(f, arrayList2);
         if (!arrayList2.isEmpty()) {
            WexSideClient.getRenderPipeline2().setList(arrayList2);
         }

         GuiDrawApi renderer = WexSideClient.getHudRenderer();
         Matrix4f scaledMatrix = new Matrix4f().scale(f);
         renderer.begin();

         try {
            for(ServerItemCooldownOverlay.EntityCooldownState entityCooldownState : arrayList) {
               this.process10(renderer, scaledMatrix, entityCooldownState);
            }
         } finally {
            renderer.end();
         }

         renderer.begin();

         try {
            for(ServerItemCooldownOverlay.EntityCooldownState entityCooldownState : arrayList) {
               this.process13(renderer, scaledMatrix, entityCooldownState);
            }
         } finally {
            renderer.end();
         }

         this.itemIconCache.update();
      }
   }

   private void process10(GuiDrawApi drawApi, Matrix4f matrix4f, ServerItemCooldownOverlay.EntityCooldownState entityCooldownState) {
      int n = entityCooldownState.activeEntries.size();
      float f = this.process3(entityCooldownState);
      float f2 = entityCooldownState.screenX - (float)n * f / 2.0F;
      float f3 = entityCooldownState.screenY + 4.0F;
      float f4 = entityCooldownState.alpha();
      float f5 = 1.0F;

      for(int i = 0; i < n; ++i) {
         ServerItemCooldownOverlay.CooldownDisplayEntry enabled2 = entityCooldownState.activeEntries.get(i);
         float f6 = f4 * enabled2.alpha();
         if (!(f6 <= 0.0F)) {
            float f7 = f2 + (float)i * f + f / 2.0F;
            float f8 = f7 - 6.0F;
            drawApi.drawRoundedRectangleBordered(matrix4f, f8, f3, 12.0F, 12.0F, 12.0F, 0.0F, process15(-1342177280, f6));
            this.itemIconCache.process4(drawApi, matrix4f, enabled2.icon, f7 - 5.0F, f3 + f5, 10.0F, process8(16777215, f6));
         }
      }
   }

   private float[] process11(class_1297 entity2, Matrix4f matrix4f) {
      float f = RenderProjection.tickProgress();
      class_243 vec = entity2.method_30950(f);
      class_238 box = entity2.method_5829()
         .method_989(vec.field_1352 - entity2.method_23317(), vec.field_1351 - entity2.method_23318(), vec.field_1350 - entity2.method_23321());
      float f2 = Float.MAX_VALUE;
      float f3 = Float.MAX_VALUE;
      float f4 = -Float.MAX_VALUE;
      float f5 = -Float.MAX_VALUE;
      int n = 0;

      for(int i = 0; i < 8; ++i) {
         double d3 = (i & 1) == 0 ? box.field_1323 : box.field_1320;
         Vector2f vector2f = RenderProjection.project(
            d3, (i & 2) == 0 ? box.field_1322 : box.field_1325, (i & 4) == 0 ? box.field_1321 : box.field_1324, matrix4f
         );
         if (vector2f != null) {
            ++n;
            f2 = Math.min(f2, vector2f.x);
            f3 = Math.min(f3, vector2f.y);
            f4 = Math.max(f4, vector2f.x);
            f5 = Math.max(f5, vector2f.y);
         }
      }

      return n == 0 ? null : new float[]{f2, f3, f4, f5};
   }

   private void process12(GuiDrawApi drawApi, Matrix4f matrix4f, float f, float f2, float f3, float f4, int n) {
      EspBoxBorderRenderer.process5(drawApi, matrix4f, f, f2, f3, f4, n);
   }

   private ServerKind getServerKind() {
      if (this.serverHelperModule == null) {
         for(Module module : WexSideClient.getInstance().getModuleManager().getModules()) {
            if (module instanceof ServerHelperModule) {
               ServerHelperModule serverHelperModule;
               this.serverHelperModule = serverHelperModule = (ServerHelperModule)module;
               break;
            }
         }
      }

      return this.serverHelperModule != null ? this.serverHelperModule.getServerKind() : ServerKind.GENERAL;
   }

   private void process13(GuiDrawApi drawApi, Matrix4f matrix4f, ServerItemCooldownOverlay.EntityCooldownState entityCooldownState) {
      int n = entityCooldownState.activeEntries.size();
      float f = this.process3(entityCooldownState);
      float f2 = entityCooldownState.screenX - (float)n * f / 2.0F;
      float f3 = entityCooldownState.screenY + 4.0F;
      float f4 = f3 + 12.0F + 3.0F;
      float f5 = font5.process4("0%", 6.0F);
      float f6 = entityCooldownState.alpha();

      for(int i = 0; i < n; ++i) {
         ServerItemCooldownOverlay.CooldownDisplayEntry enabled2 = entityCooldownState.activeEntries.get(i);
         float f7 = f6 * enabled2.alpha();
         if (!(f7 <= 0.0F) && enabled2.label != null && !enabled2.label.isEmpty()) {
            float f8 = f2 + (float)i * f + f / 2.0F;
            float f9 = font5.process3(enabled2.label, 6.0F);
            float f10 = f8 - f9 / 2.0F;
            drawApi.drawRoundedRectangle(matrix4f, f10 - 1.5F, f4 - 1.0F, f9 + 3.0F, f5 + 2.0F, 4.0F, process15(-1342177280, f7));
            font5.process2(matrix4f, drawApi, enabled2.label, f10, f4, 6.0F, process8(enabled2.color, f7));
         }
      }
   }

   private void process14(GuiDrawApi drawApi, Matrix4f matrix4f, float f, float f2, float f3, float f4, int n, int n2) {
      EspBoxBorderRenderer.process3(drawApi, matrix4f, f, f2, f3, f4, n, n2);
   }

   private static int process15(int n, float f) {
      int n2 = (int)((float)(n >>> 24 & 0xFF) * class_3532.method_15363(f, 0.0F, 1.0F));
      return n2 << 24 | n & 16777215;
   }

   private void setHudRenderEvent(HudRenderEvent gameEvent20) {
      if (!this.espFeatures.hasEnabledBox2d()) {
         this.itemIconCache.update3();
         this.map2.clear();
      } else {
         class_746 player2 = this.mc.field_1724;
         class_638 world2 = this.mc.field_1687;
         if (player2 != null && world2 != null) {
            class_4604 frustum2 = RenderProjection.frustum();
            Matrix4f matrix4f = RenderProjection.viewProjectionMatrix();
            boolean bl = this.mc.field_1690 != null && this.mc.field_1690.method_31044().method_31034();
            ArrayList<class_1297> arrayList2 = new ArrayList();

            for(class_1297 entity2 : world2.method_18112()) {
               EspTargetType targetType = EspTargetClassifier.targetType(entity2, player2);
               BoxEspSettings rectangle2 = this.espFeatures.getBox2dSettings(targetType, EspTargetClassifier.relation(entity2));
               if (targetType != null
                  && rectangle2 != null
                  && rectangle2.isEnabled()
                  && entity2.method_5805()
                  && (entity2 != player2 || !bl)
                  && RenderProjection.isVisible(entity2, frustum2)) {
                  arrayList2.add(entity2);
               }
            }

            if (!arrayList2.isEmpty()) {
               arrayList2.sort(Comparator.comparingDouble(entity -> player2.method_73189().method_1025(entity.method_73189())));
               float f = (float)this.mc.method_22683().method_4495();
               Matrix4f guiMatrix = new Matrix4f().scale(f);
               GuiDrawApi renderer = WexSideClient.getHudRenderer();
               renderer.begin();

               try {
                  for(class_1297 entity3 : arrayList2) {
                     BoxEspSettings rectangle = this.espFeatures
                        .getBox2dSettings(EspTargetClassifier.targetType(entity3, player2), EspTargetClassifier.relation(entity3));
                     float[] bounds = this.process11(entity3, matrix4f);
                     if (rectangle != null && bounds != null) {
                        float f3 = bounds[0];
                        float f4 = bounds[1];
                        float right = bounds[2];
                        float bottom = bounds[3];
                        class_1309 value;
                        class_1309 living = entity3 instanceof class_1309 ? (value = (class_1309)entity3) : null;
                        boolean bl2 = rectangle.isBoxEnabled();
                        boolean bl3 = rectangle.isCornerStyle();
                        int n = rectangle.getBoxColor(0.0F);
                        int n2 = rectangle.getBoxColor(0.25F);
                        int n3 = rectangle.getBoxColor(0.5F);
                        int n4 = rectangle.getBoxColor(0.75F);
                        boolean bl4 = rectangle.isHealthBarEnabled();
                        int n5 = rectangle.getHealthColor(0.0F);
                        int n6 = rectangle.getHealthColor(0.5F);
                        boolean bl5 = rectangle.isArmorBarEnabled();
                        int n7 = rectangle.getArmorColor(0.25F);
                        int n8 = rectangle.getArmorColor(0.75F);
                        if (bl2) {
                           boolean bl6 = bl3 && player2.method_5739(entity3) < 50.0F;
                           if (bl6) {
                              this.process7(renderer, guiMatrix, f3, f4, right, bottom, n, n2, n3, n4);
                           } else {
                              this.process5(renderer, guiMatrix, f3, f4, right, bottom, n, n2, n3, n4);
                           }
                        }

                        float f2;
                        if (bl5 && living != null && (f2 = class_3532.method_15363(living.method_6032() / living.method_6063(), 0.0F, 1.0F)) != 0.0F) {
                           this.process12(renderer, guiMatrix, f3, bottom + 0.5F, right, bottom + 2.5F, Integer.MIN_VALUE);
                           this.process14(renderer, guiMatrix, f3 + 0.5F, bottom + 1.0F, f3 + 0.5F + (right - 0.5F - (f3 + 0.5F)) * f2, bottom + 2.0F, n7, n8);
                        }

                        if (bl4 && living != null && !(living.method_6067() <= 0.0F)) {
                           float f5 = class_3532.method_15363(living.method_6067() / Math.max(1.0F, living.method_6063()), 0.0F, 1.0F);
                           this.process12(renderer, guiMatrix, f3 - 2.5F, f4, f3 - 0.5F, bottom, Integer.MIN_VALUE);
                           this.process(renderer, guiMatrix, f3 - 2.0F, f4 + 0.5F + (bottom - 1.0F - f4) * (1.0F - f5), f3 - 1.0F, bottom - 0.5F, n5, n6);
                        }
                     }
                  }
               } finally {
                  renderer.end();
               }

               ArrayList var44 = new ArrayList();

               for(class_1297 entity2 : arrayList2) {
                  BoxEspSettings rectangle = this.espFeatures
                     .getBox2dSettings(EspTargetClassifier.targetType(entity2, player2), EspTargetClassifier.relation(entity2));
                  if (rectangle != null && rectangle.isPartnerItemsEnabled()) {
                     var44.add(entity2);
                  }
               }

               if (!var44.isEmpty()) {
                  this.process9(var44, player2, matrix4f, f);
               } else if (!this.map2.isEmpty()) {
                  this.itemIconCache.update3();
                  this.map2.clear();
               }
            }
         }
      }
   }

   private static final class CooldownDisplayEntry {
      private String label = "";
      private class_1799 stack = class_1799.field_8037;
      private int color = 16777215;
      private boolean visible;
      private float visibility;
      private BakedItemIcon icon;

      private void setVisible(boolean visible) {
         this.visible = visible;
      }

      private void update(ItemIconCache iconCache) {
         this.visibility = FrameInterpolator.lerpTowards(this.visibility, this.visible ? 1.0F : 0.0F, 20.0F);
         if (!this.stack.method_7960()) {
            this.icon = iconCache.get(this.stack);
         }

         this.visible = false;
      }

      private float alpha() {
         return this.visibility;
      }
   }

   private static final class EntityCooldownState {
      private final Map<String, ServerItemCooldownOverlay.CooldownDisplayEntry> entriesById = new HashMap<>();
      private final List<ServerItemCooldownOverlay.CooldownDisplayEntry> activeEntries = new ArrayList<>();
      private int lastSeenFrame;
      private float screenX;
      private float screenY;
      private float alpha;

      private void markVisible(boolean visible) {
         if (!visible) {
            this.alpha = 0.0F;
         }
      }

      private void update(ItemIconCache iconCache) {
         this.activeEntries.clear();
         float targetAlpha = 0.0F;

         for(ServerItemCooldownOverlay.CooldownDisplayEntry entry2 : this.entriesById.values()) {
            entry2.update(iconCache);
            if (entry2.alpha() > 0.01F) {
               this.activeEntries.add(entry2);
               targetAlpha = Math.max(targetAlpha, entry2.alpha());
            }
         }

         this.activeEntries.sort(Comparator.comparing(entry -> entry.label));
         this.alpha = FrameInterpolator.lerpTowards(this.alpha, targetAlpha, 20.0F);
      }

      private float alpha() {
         return this.alpha;
      }
   }
}
