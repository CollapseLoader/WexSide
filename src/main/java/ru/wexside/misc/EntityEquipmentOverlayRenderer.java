package ru.wexside.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.class_1747;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2190;
import net.minecraft.class_2561;
import net.minecraft.class_2583;
import net.minecraft.class_5251;
import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.item.ItemBadge;
import ru.wexside.render.BakedItemIcon;
import ru.wexside.render.ItemIconCache;
import ru.wexside.render.OffscreenRenderManager;
import ru.wexside.ui.ColoredTextSegment;
import ru.wexside.ui.GuiBounds;
import ru.wexside.util.GuiDrawApi;
import ru.wexside.util.MsdfFontRenderer;

final class EntityEquipmentOverlayRenderer {
   private static final int slot = -1;
   private static final int slot2 = -5592406;
   private static final int slot3 = -1728053248;
   private static final MsdfFontRenderer font5 = FontRegistry.font2;
   private final List<BakedItemIcon> itemIcons;
   private static final float value = 9.0F;
   private static final float value2 = 11.0F;
   private static final int slot4 = -872415232;
   private final ItemIconCache itemIconCache = new ItemIconCache();
   private static final int slot5 = -43691;
   private static final float value4 = 6.25F;
   private static final float value5 = 10.0F;
   private static final int slot6 = -256;
   private static final float value6 = 7.0F;
   private List<class_1799> values3;

   void member5307() {
      this.itemIconCache.update3();
      this.itemIcons.clear();
      this.values3 = List.of();
   }

   EntityEquipmentOverlayRenderer() {
      this.itemIcons = new ArrayList<>();
      this.values3 = List.of();
   }

   private static boolean JsonElement(class_1799 stack) {
      class_1792 iiIilIIilI2 = stack.method_7909();
      class_1747 blockItem2;
      boolean bl = iiIilIIilI2 instanceof class_1747 && (blockItem2 = (class_1747)iiIilIIilI2).method_7711() instanceof class_2190;
      return bl || stack.method_7909() == class_1802.field_8288;
   }

   void member11505(List<class_1799> list, float f) {
      this.values3 = list == null ? List.of() : list;
      this.itemIcons.clear();
      if (!this.values3.isEmpty()) {
         this.itemIconCache.update2();

         for(class_1799 object2 : this.values3) {
            this.itemIcons.add(this.itemIconCache.process(object2));
         }

         ArrayList<BakedIconEntry> arrayList = new ArrayList<>();
         this.itemIconCache.process2(f, arrayList);
         OffscreenRenderManager renderPipeline2;
         if (!arrayList.isEmpty() && (renderPipeline2 = WexSideClient.getRenderPipeline2()) != null) {
            renderPipeline2.setList(arrayList);
         }
      }
   }

   private void process(GuiDrawApi drawApi, Matrix4f matrix4f, float f, float f2, ItemBadge itemBadge) {
      class_2561 iIiilliIII2 = itemBadge.label();
      float f3 = process3(iIiilliIII2, 6.25F);
      float f4 = font5.process4(iIiilliIII2.getString(), 6.25F);
      float f5 = f2 - 2.0F - f4;
      float f6 = f - f3 * 0.5F;
      drawApi.fillRectangle(matrix4f, f6 - 1.5F, f5 - 1.0F, f3 + 3.0F, f4 + 2.0F, -1728053248);
      float[] fArray = new float[]{f6};
      iIiilliIII2.method_27658((lIiIilIIII2, string) -> {
         class_5251 illiiiIilI2 = lIiIilIIII2.method_10973();
         int n = 0xFF000000 | (illiiiIilI2 != null ? illiiiIilI2.method_27716() : 16777215);
         font5.process2(matrix4f, drawApi, string, fArray[0], f5, 6.25F, n);
         fArray[0] += font5.process3(string, 6.25F);
         return Optional.empty();
      }, class_2583.field_24360);
   }

   private void process2(GuiDrawApi drawApi, Matrix4f matrix4f, float f, float f2) {
      int n2 = this.itemIcons.size();

      for(int n = 0; n < n2; ++n) {
         float f3 = f + ((float)n - (float)(n2 - 1) / 2.0F) * 11.0F;
         boolean bl = JsonElement((class_1799)this.values3.get(n));
         drawApi.fillRectangle(matrix4f, f3 - 5.0F, f2, 10.0F, 10.0F, bl ? -872415232 : -1728053248);
      }

      for(int var10 = 0; var10 < n2; ++var10) {
         float f3 = f + ((float)var10 - (float)(n2 - 1) / 2.0F) * 11.0F;
         this.itemIconCache.process3(drawApi, matrix4f, this.itemIcons.get(var10), f3 - 4.5F, f2 + 0.5F, 9.0F);
      }
   }

   void member2669(
      GuiDrawApi drawApi, Matrix4f matrix4f, GuiBounds bounds2, NameTagSettings talisman, String string, int n, int n2, ItemBadge itemBadge, String string2
   ) {
      String string3 = string != null && !string.isEmpty() ? string : "?";
      ArrayList<ColoredTextSegment> arrayList = new ArrayList<>();
      arrayList.add(new ColoredTextSegment(string3, -1));
      if (talisman.isHealthVisible() && n >= 0) {
         arrayList.add(new ColoredTextSegment(" ", -1));
         appendSegment(arrayList, -43691, String.valueOf(n));
      }

      if (talisman.isSphereVisible() && string2 != null && !string2.isEmpty()) {
         arrayList.add(new ColoredTextSegment(" ", -1));
         appendSegment(arrayList, -43691, string2);
      }

      if (talisman.isMoneyVisible() && n2 != 0) {
         arrayList.add(new ColoredTextSegment(" ", -1));
         appendSegment(arrayList, -256, "~" + n2 + "$");
      }

      float f = 0.0F;

      for(ColoredTextSegment segment : arrayList) {
         f += font5.process3(segment.text(), 7.0F);
      }

      float f2 = font5.process4(string3, 7.0F);
      float f3 = bounds2.getX() + bounds2.getWidth() * 0.5F;
      float f4 = f3 - f * 0.5F;
      float f5 = bounds2.getY() - 4.0F - f2;
      drawApi.fillRectangle(matrix4f, f4 - 1.5F, f5 - 1.0F, f + 3.0F, f2 + 2.0F, -1728053248);
      float f6 = f4;

      for(ColoredTextSegment segment : arrayList) {
         font5.process2(matrix4f, drawApi, segment.text(), f6, f5, 7.0F, segment.color());
         f6 += font5.process3(segment.text(), 7.0F);
      }

      float f7 = f5;
      if (talisman.areItemsVisible() && !this.itemIcons.isEmpty()) {
         float f8 = f5 - 2.0F - 10.0F;
         this.process2(drawApi, matrix4f, f3, f8);
         f7 = f8;
      }

      if (talisman.isTalismanVisible() && itemBadge != null) {
         this.process(drawApi, matrix4f, f3, f7, itemBadge);
      }

      this.itemIconCache.update();
   }

   private static void appendSegment(List<ColoredTextSegment> list, int n, String string) {
      list.add(new ColoredTextSegment("[", -5592406));
      list.add(new ColoredTextSegment(string, n));
      list.add(new ColoredTextSegment("]", -5592406));
   }

   private static float process3(class_2561 iIiilliIII2, float f) {
      float[] fArray = new float[]{0.0F};
      iIiilliIII2.method_27658((lIiIilIIII2, string) -> {
         fArray[0] += font5.process3(string, f);
         return Optional.empty();
      }, class_2583.field_24360);
      return fArray[0];
   }
}
