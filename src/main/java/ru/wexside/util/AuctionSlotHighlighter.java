package ru.wexside.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import net.minecraft.class_1703;
import net.minecraft.class_1735;
import net.minecraft.class_1799;
import net.minecraft.class_310;
import net.minecraft.class_465;
import ru.wexside.market.AuctionPrice;
import ru.wexside.market.AuctionPriceParser;
import ru.wexside.misc.AuctionHighlightSettings;
import ru.wexside.misc.SlotHighlight;
import ru.wexside.misc.SlotHighlightProvider;

public final class AuctionSlotHighlighter implements SlotHighlightProvider {
   private final Supplier<AuctionHighlightSettings> configSupplier;
   private final AuctionPriceParser priceParser = new AuctionPriceParser();
   private final BooleanSupplier enabledSupplier;

   public AuctionSlotHighlighter(BooleanSupplier enabledSupplier, Supplier<AuctionHighlightSettings> configSupplier) {
      this.enabledSupplier = enabledSupplier;
      this.configSupplier = configSupplier;
   }

   private static int withAlpha(int rgb, int alpha) {
      return alpha << 24 | rgb & 16777215;
   }

   private static int pulseAlpha(int speed) {
      double period = (double)Math.max(1, speed);
      float wave = 0.5F + 0.5F * (float)Math.sin((double)System.currentTimeMillis() / 1000.0 * period * Math.PI);
      return Math.round(50.0F + 180.0F * wave);
   }

   @Override
   public List<SlotHighlight> process4(class_465<?> screen) {
      if (!this.enabledSupplier.getAsBoolean()) {
         return List.of();
      } else {
         class_310 client = class_310.method_1551();
         class_1703 handler = screen.method_17577();
         if (handler != null && ContainerScreenHelper.isAuctionContainer(handler, screen)) {
            AuctionHighlightSettings config = this.configSupplier.get();
            ArrayList<AuctionSlotHighlighter.PricedSlot> pricedSlots = new ArrayList<>();

            for(int i = 0; i < handler.field_7761.size(); ++i) {
               class_1735 slot = (class_1735)handler.field_7761.get(i);
               class_1799 stack = slot.method_7677();
               if (!stack.method_7960() && (!config.enabled3 || stack.method_7919() <= 0)) {
                  AuctionPrice priceInfo = this.priceParser.parse(stack, client);
                  if (priceInfo != null && (!config.enabled || config.slot2 < 0 || priceInfo.unitPrice() <= config.slot2)) {
                     pricedSlots.add(new AuctionSlotHighlighter.PricedSlot(i, priceInfo.totalPrice()));
                  }
               }
            }

            if (pricedSlots.isEmpty()) {
               return List.of();
            } else {
               pricedSlots.sort(Comparator.comparingInt(AuctionSlotHighlighter.PricedSlot::price));
               int count = Math.min(Math.max(1, config.slot4), pricedSlots.size());
               int color = config.enabled2 ? withAlpha(config.slot, pulseAlpha(config.slot3)) : config.slot;
               ArrayList<SlotHighlight> highlights = new ArrayList<>(count);

               for(int i = 0; i < count; ++i) {
                  AuctionSlotHighlighter.PricedSlot pricedSlot = pricedSlots.get(i);
                  highlights.add(new SlotHighlight(pricedSlot.slotIndex(), color));
               }

               return highlights;
            }
         } else {
            return List.of();
         }
      }
   }

   private static record PricedSlot(int slotIndex, int price) {
   }
}
