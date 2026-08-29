package ru.wexside.util;

import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.minecraft.class_124;
import net.minecraft.class_1297;
import net.minecraft.class_1304;
import net.minecraft.class_1309;
import net.minecraft.class_1528;
import net.minecraft.class_1542;
import net.minecraft.class_1548;
import net.minecraft.class_1560;
import net.minecraft.class_1588;
import net.minecraft.class_1613;
import net.minecraft.class_1627;
import net.minecraft.class_1628;
import net.minecraft.class_1642;
import net.minecraft.class_1747;
import net.minecraft.class_1753;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1887;
import net.minecraft.class_1890;
import net.minecraft.class_1893;
import net.minecraft.class_2190;
import net.minecraft.class_2561;
import net.minecraft.class_2583;
import net.minecraft.class_2588;
import net.minecraft.class_266;
import net.minecraft.class_269;
import net.minecraft.class_310;
import net.minecraft.class_3489;
import net.minecraft.class_4604;
import net.minecraft.class_5250;
import net.minecraft.class_5251;
import net.minecraft.class_5321;
import net.minecraft.class_6880;
import net.minecraft.class_8646;
import net.minecraft.class_9013;
import net.minecraft.class_9304;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import ru.wexside.WexSideClient;
import ru.wexside.event.EventBus;
import ru.wexside.event.HudRenderEvent;
import ru.wexside.event.WorldSessionEvent;
import ru.wexside.item.ItemBadge;
import ru.wexside.item.ItemBadgeCategory;
import ru.wexside.misc.BakedIconEntry;
import ru.wexside.misc.FontRegistry;
import ru.wexside.misc.FriendList;
import ru.wexside.misc.NameTagSettings;
import ru.wexside.model.esp.EspTargetClassifier;
import ru.wexside.model.esp.EspTargetType;
import ru.wexside.module.player.NameProtectModule;
import ru.wexside.render.ItemIconCache;
import ru.wexside.render.NameplateLayout;
import ru.wexside.render.RenderCamera;
import ru.wexside.render.RenderProjection;

public final class NameplateRenderer {
   private static final float value5 = 1.15F;
   private static final float value6 = 11.0F;
   private final int backgroundColor;
   private static final float value7 = 6.25F;
   private final int specialItemBackgroundColor;
   private static final float value8 = 5.0F;
   private static final int slot3 = 16733525;
   private static final int process19 = 16776960;
   static volatile NameplateRenderer fire2;
   private static final float value9 = 2.0F;
   private static final int slot4 = -1;
   private static final MsdfFontRenderer enchantmentFont = FontRegistry.icons;
   private static final class_1304[] ARMOR_SLOTS = new class_1304[]{class_1304.field_6169, class_1304.field_6174, class_1304.field_6172, class_1304.field_6166};
   private static final int slot5 = 5635925;
   private final class_310 mc = class_310.method_1551();
   private static final float value10 = 2.0F;
   private final class_2561 friendSuffix;
   private static final int slot6 = 11184810;
   private static final float value11 = 1.15F;
   private final class_2561 separator;
   private static final MsdfFontRenderer nameFont = FontRegistry.regularText;
   private final EspFeatureRegistry espFeatures;
   private final ItemIconCache itemIcons;
   private final int friendBackgroundColor;
   private static final int slot8 = 8421504;

   public NameplateRenderer(EventBus eventBus, EspFeatureRegistry espFeatures) {
      this.espFeatures = espFeatures;
      this.itemIcons = new ItemIconCache();
      this.backgroundColor = -871362544;
      this.specialItemBackgroundColor = -869654000;
      this.friendBackgroundColor = -871355880;
      this.separator = class_2561.method_43470(" ");
      this.friendSuffix = class_2561.method_43470("[Friend]").method_54663(5635925);
      eventBus.subscribe(HudRenderEvent.class, this::setHudRenderEvent, -100);
      eventBus.subscribe(WorldSessionEvent.class, this::onWorldChanged);
      fire2 = this;
   }

   private void onWorldChanged(WorldSessionEvent event) {
      this.itemIcons.close();
   }

   private boolean process(class_1799 stack) {
      return stack.method_31573(class_3489.field_42611);
   }

   private class_5250 buildLabel(class_1297 entity, NameTagSettings settings) {
      boolean localPlayer = entity == this.mc.field_1724;
      FriendList friends = WexSideClient.getFriends();
      boolean friend = friends != null && friends.contains(entity.method_5477().getString());
      class_2561 text;
      if (entity instanceof class_1542 item) {
         text = item.method_6983().method_7964();
      } else {
         text = entity.method_5476();
      }

      class_5250 label = text != null ? text.method_27661() : entity.method_5477().method_27661();
      if (text != null && text.method_10851() instanceof class_2588) {
         label = text.method_27661().method_27696(class_2583.field_24360.method_36139(8421504));
      }

      if (friend && !localPlayer && NameProtectModule.isEnabled()) {
         label = class_2561.method_43470(NameProtectModule.getString());
      }

      if (settings.isHealthVisible() && entity instanceof class_1309 living) {
         label.method_10852(this.separator).method_10852(this.bracketedText(16733525, String.valueOf(this.getHealth(entity, living))));
      }

      int reward = settings.isMoneyVisible() && entity instanceof class_1588 ? this.getMobReward(entity) : 0;
      if (reward != 0) {
         label.method_10852(this.separator).method_10852(this.bracketedText(16776960, "~" + reward + "$"));
      }

      if (friend && !localPlayer) {
         label.method_10852(this.separator).method_10852(this.friendSuffix);
      }

      return label;
   }

   private float process3(NameplateLayout nameplateLayout, int n) {
      return nameplateLayout.centerX + ((float)n - (float)(nameplateLayout.equipment.size() - 1) / 2.0F) * 11.0F;
   }

   private static int enchantmentLevel(class_1799 stack, class_5321<class_1887> enchantment) {
      class_9304 espFeatures = class_1890.method_57532(stack);

      for(Entry entry : espFeatures.method_57539()) {
         if (((class_6880)entry.getKey()).method_40225(enchantment)) {
            return entry.getIntValue();
         }
      }

      return 0;
   }

   private float process5(GuiDrawApi drawApi, Matrix4f matrix4f, StringBuilder stringBuilder, float f, float f2, float f3, int n) {
      String string = stringBuilder.toString();
      stringBuilder.setLength(0);
      nameFont.process2(matrix4f, drawApi, string, f, f2, f3, n);
      return nameFont.process3(string, f3);
   }

   private List<String> process6(class_1799 stack) {
      ArrayList<String> arrayList = new ArrayList<>();
      int n = enchantmentLevel(stack, class_1893.field_9111);
      int n2 = enchantmentLevel(stack, class_1893.field_9097);
      int n3 = enchantmentLevel(stack, class_1893.field_9119);
      int n4 = enchantmentLevel(stack, class_1893.field_9101);
      int n5 = enchantmentLevel(stack, class_1893.field_9129);
      int n6 = enchantmentLevel(stack, class_1893.field_9128);
      int n7 = enchantmentLevel(stack, class_1893.field_9118);
      int n8 = enchantmentLevel(stack, class_1893.field_9110);
      int n9 = enchantmentLevel(stack, class_1893.field_9125);
      int n10 = enchantmentLevel(stack, class_1893.field_9103);
      int n11 = enchantmentLevel(stack, class_1893.field_9116);
      int n12 = enchantmentLevel(stack, class_1893.field_9126);
      int n13 = enchantmentLevel(stack, class_1893.field_9121);
      int n14 = enchantmentLevel(stack, class_1893.field_9124);
      int n15 = enchantmentLevel(stack, class_1893.field_9131);
      int n16 = enchantmentLevel(stack, class_1893.field_9099);
      int n17 = enchantmentLevel(stack, class_1893.field_9130);
      int n18 = enchantmentLevel(stack, class_1893.field_9095);
      int n19 = enchantmentLevel(stack, class_1893.field_9107);
      if (this.process35(stack)) {
         if (n7 > 0) {
            arrayList.add("Shr" + n7);
         }

         if (n15 > 0) {
            arrayList.add("Eff" + n15);
         }

         if (n3 > 0) {
            arrayList.add("Unb" + n3);
         }
      }

      if (this.process11(stack)) {
         if (n18 > 0) {
            arrayList.add("Fire" + n18);
         }

         if (n19 > 0) {
            arrayList.add("Bla" + n19);
         }

         if (n6 > 0) {
            arrayList.add("Dep" + n6);
         }

         if (n5 > 0) {
            arrayList.add("Fea" + n5);
         }

         if (n > 0) {
            arrayList.add("Pro" + n);
         }

         if (n2 > 0) {
            arrayList.add("Thr" + n2);
         }

         if (n4 > 0) {
            arrayList.add("Mn");
         }

         if (n3 > 0) {
            arrayList.add("Unb" + n3);
         }
      }

      if (this.process28(stack)) {
         if (n9 > 0) {
            arrayList.add("Inf" + n9);
         }

         if (n10 > 0) {
            arrayList.add("Pow" + n10);
         }

         if (n11 > 0) {
            arrayList.add("Pun" + n11);
         }

         if (n4 > 0) {
            arrayList.add("Mn");
         }

         if (n12 > 0) {
            arrayList.add("Fla" + n12);
         }

         if (n3 > 0) {
            arrayList.add("Unb" + n3);
         }
      }

      if (this.process(stack)) {
         if (n8 > 0) {
            arrayList.add("L" + n8);
         }

         if (n7 > 0) {
            arrayList.add("Shr" + n7);
         }

         if (n13 > 0) {
            arrayList.add("Kno" + n13);
         }

         if (n14 > 0) {
            arrayList.add("Fir" + n14);
         }

         if (n3 > 0) {
            arrayList.add("Unb" + n3);
         }

         if (n4 > 0) {
            arrayList.add("Mn");
         }
      }

      if (this.process23(stack)) {
         if (n3 > 0) {
            arrayList.add("Unb" + n3);
         }

         if (n4 > 0) {
            arrayList.add("Mn");
         }

         if (n15 > 0) {
            arrayList.add("Eff" + n15);
         }

         if (n16 > 0) {
            arrayList.add("Sil" + n16);
         }

         if (n17 > 0) {
            arrayList.add("For" + n17);
         }
      }

      return arrayList;
   }

   private void process7(GuiDrawApi drawApi, Matrix4f matrix4f, NameplateLayout nameplateLayout) {
      class_2561 iIiilliIII2 = nameplateLayout.badge.label();
      float f = this.process13(iIiilliIII2, 6.25F);
      this.process38(drawApi, matrix4f, iIiilliIII2, nameplateLayout.centerX - f / 2.0F, this.process17(nameplateLayout), 6.25F);
   }

   private void process8(GuiDrawApi drawApi, Matrix4f matrix4f, NameplateLayout nameplateLayout) {
      float f = nameFont.process4(nameplateLayout.label.getString(), nameplateLayout.fontSize);
      float f2 = nameplateLayout.labelWidth;
      float f3 = nameplateLayout.centerX - f2 / 2.0F;
      float f4 = f3 - 1.5F;
      float f5 = nameplateLayout.baselineY - f - 2.5F;
      float f6 = f3 + f2 + 1.5F - f4;
      float f7 = nameplateLayout.baselineY - 2.0F - f5;
      drawApi.fillRectangle(matrix4f, f4, f5, f6, f7, nameplateLayout.friend ? this.friendBackgroundColor : this.backgroundColor);
   }

   private boolean process11(class_1799 stack) {
      return stack.method_31573(class_3489.field_48303);
   }

   private boolean process12(class_1297 entity2, class_4604 frustum2) {
      EspTargetType espTargetType = EspTargetClassifier.targetType(entity2, this.mc.field_1724);
      NameTagSettings talisman = this.espFeatures.getNameTagSettings(espTargetType, EspTargetClassifier.relation(entity2));
      return espTargetType != null
         && talisman != null
         && talisman.isEnabled()
         && entity2.method_5805()
         && RenderProjection.isVisible(entity2, frustum2)
         && (entity2 != this.mc.field_1724 || !this.mc.field_1690.method_31044().method_31034());
   }

   private float process13(class_2561 iIiilliIII2, float f) {
      float[] fArray = new float[]{0.0F};
      StringBuilder stringBuilder = new StringBuilder(16);
      iIiilliIII2.method_27658((style, string) -> {
         String string2 = string;
         stringBuilder.setLength(0);

         for(int i = 0; i < string2.length(); ++i) {
            char c = string2.charAt(i);
            if ((c == 167 || c == '&') && i + 1 < string2.length() && class_124.method_544(Character.toLowerCase(string2.charAt(i + 1))) != null) {
               ++i;
            } else {
               stringBuilder.append(c);
            }
         }

         if (stringBuilder.length() > 0) {
            fArray[0] += nameFont.process3(stringBuilder.toString(), f);
         }

         return Optional.empty();
      }, class_2583.field_24360);
      return fArray[0];
   }

   private float process14(float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8) {
      float f10 = Math.max(0.0F, Math.min(f + f3, f5 + f7) - Math.max(f, f5));
      float f11 = f10 * Math.max(0.0F, Math.min(f2 + f4, f6 + f8) - Math.max(f2, f6));
      return f11 == 0.0F ? 0.0F : f11 / Math.min(f3 * f4, f7 * f8);
   }

   private float process15(NameplateLayout nameplateLayout) {
      return nameplateLayout.badge != null
         ? this.process17(nameplateLayout) - nameFont.process4("Ag", 6.25F) - 2.0F
         : nameplateLayout.baselineY - 23.0F - 10.0F;
   }

   public static boolean process16(class_1297 entity2) {
      NameplateRenderer fire = fire2;
      if (fire == null) {
         return false;
      } else {
         class_310 mc2 = class_310.method_1551();
         EspTargetType espTargetType = EspTargetClassifier.targetType(entity2, mc2.field_1724);
         NameTagSettings talisman = fire.espFeatures.getNameTagSettings(espTargetType, EspTargetClassifier.relation(entity2));
         return espTargetType != null
            && talisman != null
            && talisman.isEnabled()
            && (entity2 != mc2.field_1724 || !mc2.field_1690.method_31044().method_31034());
      }
   }

   private float process17(NameplateLayout nameplateLayout) {
      return nameplateLayout.baselineY - 23.0F - 9.0F;
   }

   private int getHealth(class_1297 entity, class_1309 living) {
      class_269 scoreboard = this.mc.field_1687.method_8428();
      class_266 objective = scoreboard.method_1189(class_8646.field_45158);
      class_9013 score = objective == null ? null : scoreboard.method_55430(entity, objective);
      return score != null ? score.method_55397() : Math.round(living.method_6032());
   }

   private List<class_1799> process20(class_1309 entity3) {
      ArrayList<class_1799> arrayList = new ArrayList();
      arrayList.add(entity3.method_6047());
      arrayList.add(entity3.method_6079());

      for(class_1304 iliiIIiliI2 : ARMOR_SLOTS) {
         arrayList.add(entity3.method_6118(iliiIIiliI2));
      }

      arrayList.removeIf(class_1799::method_7960);
      return arrayList;
   }

   private void process22(GuiDrawApi drawApi, Matrix4f matrix4f, NameplateLayout nameplateLayout) {
      float f = nameplateLayout.baselineY - 23.0F;

      for(int i = 0; i < nameplateLayout.equipment.size(); ++i) {
         float f2 = this.process3(nameplateLayout, i);
         boolean bl = this.process29((class_1799)nameplateLayout.equipment.get(i));
         drawApi.fillRectangle(matrix4f, f2 - 5.0F, f, 10.0F, 10.0F, bl ? this.specialItemBackgroundColor : this.backgroundColor);
      }
   }

   private boolean process23(class_1799 stack) {
      return stack.method_31573(class_3489.field_42614) || stack.method_31573(class_3489.field_42615) || stack.method_31573(class_3489.field_42613);
   }

   private float process25(NameplateLayout nameplateLayout) {
      float f = 0.0F;

      for(class_1799 stack : nameplateLayout.equipment) {
         for(String string : this.process6(stack)) {
            f = Math.max(f, enchantmentFont.process3(string, 5.0F));
         }
      }

      float f2 = 10.5F;
      return f > f2 ? 5.0F * f2 / f : 5.0F;
   }

   private boolean process26(class_1297 entity2) {
      if (entity2 == this.mc.field_1724) {
         return false;
      } else {
         FriendList friendList2 = WexSideClient.getFriends();
         return friendList2 != null && friendList2.contains(entity2.method_5477().getString());
      }
   }

   private int getMobReward(class_1297 entity2) {
      if (entity2 instanceof class_1628) {
         return 700;
      } else if (entity2 instanceof class_1548) {
         return 2000;
      } else if (entity2 instanceof class_1627) {
         return 4000;
      } else if (entity2 instanceof class_1613 || entity2 instanceof class_1642) {
         return 600;
      } else if (entity2 instanceof class_1560) {
         return 1000;
      } else {
         return entity2 instanceof class_1528 ? 10000 : 0;
      }
   }

   private boolean process28(class_1799 stack) {
      return stack.method_7909() instanceof class_1753;
   }

   private void setHudRenderEvent(HudRenderEvent gameEvent20) {
      if (!this.espFeatures.hasEnabledNameTags()) {
         this.itemIcons.close();
      } else if (this.mc.field_1687 != null && this.mc.field_1724 != null && RenderCamera.position() != null) {
         class_4604 frustum = RenderProjection.frustum();
         Matrix4f viewProjection = RenderProjection.viewProjectionMatrix();
         ArrayList<class_1297> visibleEntities = new ArrayList();

         for(class_1297 entity2 : this.mc.field_1687.method_18112()) {
            if (this.process12(entity2, frustum)) {
               visibleEntities.add(entity2);
            }
         }

         if (!visibleEntities.isEmpty()) {
            visibleEntities.sort(Comparator.comparingDouble(entity -> this.mc.field_1724.method_73189().method_1025(entity.method_73189())));
            ArrayList<NameplateLayout> nameplates = new ArrayList<>();
            ArrayList<float[]> occupiedBounds = new ArrayList<>();
            Iterator scale = visibleEntities.iterator();

            while(true) {
               class_1297 entity2;
               float f4;
               NameTagSettings settings;
               Vector2f projected;
               class_5250 label;
               float f;
               while(true) {
                  if (!scale.hasNext()) {
                     if (nameplates.isEmpty()) {
                        return;
                     }

                     float scalex = (float)this.mc.method_22683().method_4495();
                     this.itemIcons.beginFrame();

                     for(NameplateLayout nameplate : nameplates) {
                        if (nameplate.equipment != null) {
                           nameplate.equipmentIcons = new ArrayList<>(nameplate.equipment.size());

                           for(class_1799 stack : nameplate.equipment) {
                              nameplate.equipmentIcons.add(this.itemIcons.get(stack));
                           }
                        }
                     }

                     ArrayList<BakedIconEntry> arrayList = new ArrayList<>();
                     this.itemIcons.collectBakeEntries(scalex, arrayList);
                     if (!arrayList.isEmpty()) {
                        WexSideClient.getRenderPipeline2().setList(arrayList);
                     }

                     GuiDrawApi renderer = WexSideClient.getHudRenderer();
                     Matrix4f guiMatrix = new Matrix4f().scale(scalex);
                     renderer.begin();

                     try {
                        for(NameplateLayout nameplate : nameplates) {
                           this.process8(renderer, guiMatrix, nameplate);
                           if (nameplate.badge != null) {
                              this.process31(renderer, guiMatrix, nameplate);
                           }

                           if (nameplate.equipment != null) {
                              this.process22(renderer, guiMatrix, nameplate);
                           }
                        }

                        for(NameplateLayout nameplate : nameplates) {
                           if (nameplate.equipment != null) {
                              this.process33(renderer, guiMatrix, nameplate);
                           }
                        }

                        for(NameplateLayout nameplate : nameplates) {
                           this.process37(renderer, guiMatrix, nameplate);
                           if (nameplate.badge != null) {
                              this.process7(renderer, guiMatrix, nameplate);
                           }
                        }
                     } finally {
                        renderer.end();
                     }

                     renderer.begin();

                     try {
                        for(NameplateLayout nameplate : nameplates) {
                           if (nameplate.equipment != null && nameplate.showEnchantments) {
                              this.process30(renderer, guiMatrix, nameplate);
                           }
                        }
                     } finally {
                        renderer.end();
                     }

                     this.itemIcons.evictUnused();
                     return;
                  }

                  entity2 = (class_1297)scale.next();
                  settings = this.espFeatures
                     .getNameTagSettings(EspTargetClassifier.targetType(entity2, this.mc.field_1724), EspTargetClassifier.relation(entity2));
                  projected = RenderProjection.projectEntityLabel(entity2, viewProjection);
                  if (settings != null && projected != null) {
                     label = this.buildLabel(entity2, settings);
                     String string = label.getString();
                     f = string.contains("★") ? 9.0F : 7.0F;
                     float f5 = f4 = this.process13(label, f);
                     float f6 = nameFont.process4(string, f) + 4.0F;
                     float f7 = projected.x - f5 / 2.0F;
                     float f8 = projected.y - f6;
                     if (!settings.shouldPreventOverlap()) {
                        break;
                     }

                     boolean overlaps = false;

                     for(float[] bounds : occupiedBounds) {
                        if ((double)this.process14(f7, f8, f5, f6, bounds[0], bounds[1], bounds[2], bounds[3]) > settings.getOverlapThreshold()) {
                           overlaps = true;
                           break;
                        }
                     }

                     if (!overlaps) {
                        occupiedBounds.add(new float[]{f7, f8, f5, f6});
                        break;
                     }
                  }
               }

               NameplateLayout nameplate = new NameplateLayout(projected.x, projected.y, label, f, f4);
               nameplate.friend = this.process26(entity2);
               nameplate.showEnchantments = settings.shouldShowEnchantments();
               class_1309 living;
               List<class_1799> equipment;
               if (settings.areItemsVisible() && entity2 instanceof class_1309 && !(equipment = this.process20(living = (class_1309)entity2)).isEmpty()) {
                  nameplate.equipment = equipment;
               }

               if (entity2 instanceof class_1309 livingEntity) {
                  nameplate.badge = process40(livingEntity, settings.isTalismanVisible(), settings.isSphereVisible());
               }

               nameplates.add(nameplate);
            }
         }
      }
   }

   private boolean process29(class_1799 stack) {
      class_1792 iiIilIIilI2 = stack.method_7909();
      class_1747 text2;
      boolean bl = iiIilIIilI2 instanceof class_1747 && (text2 = (class_1747)iiIilIIilI2).method_7711() instanceof class_2190;
      return bl || stack.method_7909() == class_1802.field_8288;
   }

   private void process30(GuiDrawApi drawApi, Matrix4f matrix4f, NameplateLayout nameplateLayout) {
      float f = this.process25(nameplateLayout);
      float f2 = enchantmentFont.process4("Ag", f) + 1.0F;
      float f3 = this.process15(nameplateLayout);

      for(int i = 0; i < nameplateLayout.equipment.size(); ++i) {
         float f4 = this.process3(nameplateLayout, i);
         List<String> list = this.process6((class_1799)nameplateLayout.equipment.get(i));

         for(int j = 0; j < list.size(); ++j) {
            String string = list.get(j);
            float f5 = enchantmentFont.process3(string, f);
            enchantmentFont.process2(matrix4f, drawApi, string, f4 - f5 / 2.0F, f3 - (float)j * f2, f, -1);
         }
      }
   }

   private void process31(GuiDrawApi drawApi, Matrix4f matrix4f, NameplateLayout nameplateLayout) {
      class_2561 iIiilliIII2 = nameplateLayout.badge.label();
      float f = this.process13(iIiilliIII2, 6.25F);
      float f2 = nameFont.process4(iIiilliIII2.getString(), 6.25F);
      float f3 = nameplateLayout.centerX - f / 2.0F;
      float f4 = this.process17(nameplateLayout);
      drawApi.fillRectangle(matrix4f, f3 - 1.5F, f4 - 1.0F, f + 3.0F, f2 + 2.0F, this.backgroundColor);
   }

   private void process33(GuiDrawApi drawApi, Matrix4f matrix4f, NameplateLayout nameplateLayout) {
      float f = nameplateLayout.baselineY - 23.0F;

      for(int i = 0; i < nameplateLayout.equipment.size(); ++i) {
         float f2 = this.process3(nameplateLayout, i);
         this.itemIcons.process3(drawApi, matrix4f, nameplateLayout.equipmentIcons.get(i), f2 - 4.5F, f + 0.5F, 9.0F);
      }
   }

   private boolean process35(class_1799 stack) {
      return stack.method_31573(class_3489.field_42612);
   }

   private class_5250 bracketedText(int color, String value) {
      return class_2561.method_43473()
         .method_10852(class_2561.method_43470("[").method_54663(11184810))
         .method_10852(class_2561.method_43470(value).method_54663(color))
         .method_10852(class_2561.method_43470("]").method_54663(11184810));
   }

   private void process37(GuiDrawApi drawApi, Matrix4f matrix4f, NameplateLayout nameplateLayout) {
      float f3 = nameFont.process4(nameplateLayout.label.getString(), nameplateLayout.fontSize);
      float f4 = nameplateLayout.labelWidth;
      float f5 = nameplateLayout.centerX - f4 / 2.0F;
      float f6 = nameplateLayout.baselineY - f3 - 2.5F;
      this.process38(drawApi, matrix4f, nameplateLayout.label, f5, f6, nameplateLayout.fontSize);
   }

   private void process38(GuiDrawApi drawApi, Matrix4f matrix4f, class_2561 iIiilliIII2, float f, float f2, float f3) {
      float[] fArray = new float[]{f};
      StringBuilder stringBuilder = new StringBuilder(16);
      iIiilliIII2.method_27658(
         (style, string) -> {
            String string2 = string;
            class_5251 textColor = style.method_10973();
            int baseColor;
            int currentColor = baseColor = 0xFF000000 | (textColor != null ? textColor.method_27716() : 16777215);
   
            for(int i = 0; i < string2.length(); ++i) {
               char c = string2.charAt(i);
               class_124 formatting;
               if ((c == 167 || c == '&')
                  && i + 1 < string2.length()
                  && (formatting = class_124.method_544(Character.toLowerCase(string2.charAt(i + 1)))) != null) {
                  int nextColor = formatting == class_124.field_1070
                     ? baseColor
                     : (formatting.method_532() != null ? 0xFF000000 | formatting.method_532() : currentColor);
                  if (nextColor != currentColor && stringBuilder.length() > 0) {
                     fArray[0] += this.process5(drawApi, matrix4f, stringBuilder, fArray[0], f2, f3, currentColor);
                  }
   
                  currentColor = nextColor;
                  ++i;
               } else {
                  stringBuilder.append(c);
               }
            }
   
            if (stringBuilder.length() > 0) {
               fArray[0] += this.process5(drawApi, matrix4f, stringBuilder, fArray[0], f2, f3, currentColor);
            }
   
            return Optional.empty();
         },
         class_2583.field_24360
      );
   }

   private static ItemBadge process40(class_1309 entity3, boolean bl, boolean bl2) {
      if (!bl && !bl2) {
         return null;
      } else {
         ItemBadge itemBadge = ItemBadge.fromStack(entity3.method_6079());
         if (itemBadge == null) {
            return null;
         } else {
            boolean enabledForCategory = itemBadge.category() == ItemBadgeCategory.TALISMAN ? bl : bl2;
            return enabledForCategory ? itemBadge : null;
         }
      }
   }
}
