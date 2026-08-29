package ru.wexside.module.combat;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import net.minecraft.class_1661;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2487;
import net.minecraft.class_310;
import net.minecraft.class_746;
import net.minecraft.class_9276;
import net.minecraft.class_9279;
import net.minecraft.class_9334;
import ru.wexside.WexSideClient;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.event.KeyPressedEvent;
import ru.wexside.event.MousePressedEvent;
import ru.wexside.misc.Bundles;
import ru.wexside.misc.ClickPolicy;
import ru.wexside.misc.ClickSlotAction;
import ru.wexside.misc.InventoryTask;
import ru.wexside.misc.ItemAlerts;
import ru.wexside.misc.SwapIcon;
import ru.wexside.misc.SwapWheelScreen;
import ru.wexside.misc.TaskFlag;
import ru.wexside.misc.TaskPriority;
import ru.wexside.misc.VisibilityCondition;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.notification.ItemNotification;
import ru.wexside.notification.NotificationCenter;
import ru.wexside.setting.BindSetting;
import ru.wexside.setting.BindSettingBuilder;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.ModeSetting;
import ru.wexside.setting.ModeSettingBuilder;
import ru.wexside.setting.NumberSetting;
import ru.wexside.setting.NumberSettingBuilder;
import ru.wexside.util.InventoryController;

public class AutoSwapModule extends Module implements ConfigSerializable {
   private static final String OWNER = "auto_swap";
   private static final String BUKKIT_VALUES = "PublicBukkitValues";
   private static final String SESSION_KEY = "minecraft:s";
   private static final String DON_ITEM = "minecraft:don-item";
   private static final int OFFHAND_SLOT = 40;
   private static final int HOLD_TICKS_FOR_WHEEL = 4;
   private static final String SHORT_PRESS_WHEEL = "Круговой селектор";
   private final BooleanSetting enabledSetting;
   private final ModeSetting mode;
   private final ModeSetting serverMode;
   private final BooleanSetting separateBinds;
   private final BindSetting actionBind;
   private final BindSetting swapBind;
   private final BindSetting selectorBind;
   private final ModeSetting swapFrom;
   private final ModeSetting swapTo;
   private final ModeSetting shortPressMode;
   private final NumberSetting segments;
   private final BooleanSetting fromBundle;
   private final BooleanSetting ftMode;
   private String lastSwapFrom;
   private String lastSwapTo;
   private class_1799 lastWheelStack = class_1799.field_8037;
   private class_1799 previousWheelStack = class_1799.field_8037;
   private int holdTicks;
   private boolean actionHeld;
   private boolean wheelOpened;

   public AutoSwapModule(EventBus eventBus) {
      super(eventBus, "auto_swap", "Auto Swap", "Меняет предметы при прожатии", ModuleCategory.valueOf("COMBAT"), "autoswap", "swap");
      this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Enabled")
            .id("enabled")
            .description("")
            .withKeybind()
            .toggle())
         .build();
      this.registerSetting(this.enabledSetting);
      this.mode = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder()
            .options("Default", "Legit")
            .defaultOption("Default")
            .name("Mode")
            .id("mode")
            .description("Default - мгновенный, Legit - с задержкой"))
         .build();
      this.registerSetting(this.mode);
      this.serverMode = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder()
            .options("FT", "Others")
            .defaultOption("FT")
            .name("Server Mode")
            .id("server_mode")
            .description("Режим сервера"))
         .build();
      this.registerSetting(this.serverMode);
      this.separateBinds = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(true)
            .defaultValue(false)
            .name("Раздельные бинды")
            .id("separate_binds")
            .description("Отдельные кнопки для свапа и селектора"))
         .build();
      this.registerSetting(this.separateBinds);
      this.actionBind = ((BindSettingBuilder)BindSetting.getBindSettingBuilder()
            .keyboard(0)
            .name("Action Button")
            .id("action_bind")
            .description("Нажать - свап, удерживать - селектор")
            .visibleWhen(() -> !this.separateBinds.isEnabled()))
         .build();
      this.registerSetting(this.actionBind);
      VisibilityCondition separateBindVisibility = VisibilityCondition.process("separate_binds", this.separateBinds::isEnabled);
      this.swapBind = ((BindSettingBuilder)BindSetting.getBindSettingBuilder()
            .keyboard(0)
            .onReleased(this::onSwapBind)
            .name("Свап")
            .id("swap_bind")
            .description("Замена предмета в оффхенде")
            .visibility(separateBindVisibility))
         .build();
      this.registerSetting(this.swapBind);
      this.selectorBind = ((BindSettingBuilder)BindSetting.getBindSettingBuilder()
            .keyboard(0)
            .onReleased(this::onSelectorBind)
            .name("Селектор")
            .id("selector_bind")
            .description("Круговой селектор предметов")
            .visibility(separateBindVisibility))
         .build();
      this.registerSetting(this.selectorBind);
      this.swapFrom = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder()
            .options("Shield", "Sphere", "Totem", "GApple", "Firework")
            .defaultOption("Shield")
            .name("Swap From")
            .id("swap_from")
            .description("Предмет, который будет заменён"))
         .build();
      this.registerSetting(this.swapFrom);
      this.swapTo = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder()
            .options("Shield", "Sphere", "Totem", "GApple", "Firework")
            .defaultOption("Totem")
            .name("Swap To")
            .id("swap_to")
            .description("Предмет, на который будет замена"))
         .build();
      this.registerSetting(this.swapTo);
      this.shortPressMode = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder()
            .options("Настроенные", "Круговой селектор")
            .defaultOption("Настроенные")
            .dynamicName(() -> this.separateBinds.isEnabled() ? "Что менять свапом" : "Переключать коротким нажатием")
            .id("short_press_mode")
            .description("Настроенные - пара из селектбоксов выше\nКруговой селектор - предметы колеса"))
         .build();
      this.registerSetting(this.shortPressMode);
      this.segments = ((NumberSettingBuilder)NumberSetting.builder()
            .range(2.0, 9.0)
            .defaultValue(3.0)
            .multiplier(1.0)
            .precision(0)
            .animationSpeed(20.0F)
            .snapTo(1.0)
            .name("Segments")
            .id("segments")
            .description("Количество сегментов селектора"))
         .build();
      this.registerSetting(this.segments);
      this.fromBundle = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(true)
            .defaultValue(false)
            .name("Из мешков")
            .id("from_bundle")
            .description("Доставать предмет из мешка если нет в инвентаре"))
         .build();
      this.registerSetting(this.fromBundle);
      this.ftMode = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("FT-Mode")
            .id("ft_mode")
            .description("Поддержка мешков без лимита вместимости")
            .visibleWhen(this.fromBundle::isEnabled))
         .build();
      this.registerSetting(this.ftMode);
   }

   @Override
   protected void initialize() {
      this.listen(ClientTickEvent.class, event -> this.onTick());
      this.listen(KeyPressedEvent.class, event -> this.onKey(event.key()));
      this.listen(MousePressedEvent.class, event -> this.onMouse(event.button()));
   }

   @Override
   public void readConfig(DataInputStream dataInputStream) throws IOException {
      super.readConfig(dataInputStream);
      if (this.swapBind.getBindInput().isUnbound() && this.selectorBind.getBindInput().isUnbound()) {
         this.swapBind.setBindInput(this.actionBind.getBindInput());
      }
   }

   private void onTick() {
      this.resetHistoryIfPairChanged();
      if (this.enabledSetting.isEnabled() && !this.separateBinds.isEnabled()) {
         class_310 client = class_310.method_1551();
         if (client.field_1755 != null && !(client.field_1755 instanceof SwapWheelScreen)) {
            this.resetHoldState();
         } else {
            boolean held = this.actionBind.isPressed();
            if (held) {
               if (!this.actionHeld) {
                  this.holdTicks = 0;
                  this.wheelOpened = false;
               }

               ++this.holdTicks;
               if (!this.wheelOpened && this.holdTicks >= 4 && client.field_1755 == null && client.field_1724 != null) {
                  client.method_1507(this.openWheel(this.actionBind));
                  this.wheelOpened = true;
               }
            } else {
               if (this.actionHeld && !this.wheelOpened && this.holdTicks < 4) {
                  this.swapOffhand();
               }

               this.holdTicks = 0;
            }

            this.actionHeld = held;
         }
      } else {
         this.resetHoldState();
      }
   }

   private void onSwapBind(BindSetting ignored) {
      this.swapFromSeparateBind();
   }

   private void onSelectorBind(BindSetting ignored) {
      this.openSelector();
   }

   public void forgetStack(class_1799 stack) {
      if (stack != null && !stack.method_7960()) {
         if (sameItem(this.lastWheelStack, stack)) {
            this.lastWheelStack = class_1799.field_8037;
         }

         if (sameItem(this.previousWheelStack, stack)) {
            this.previousWheelStack = class_1799.field_8037;
         }
      }
   }

   public void selectStack(class_1799 stack) {
      if (this.enabledSetting.isEnabled() && stack != null && !stack.method_7960()) {
         class_746 player = class_310.method_1551().field_1724;
         if (player != null) {
            class_1799 offhand = player.method_6079();
            if (!sameItem(offhand, stack)) {
               int slot = this.findInventorySlot(player.method_31548(), stack);
               if (slot == -1) {
                  if (this.useFromBundle(player, stack)) {
                     this.showHover(stack);
                     this.rememberWheelStack(stack);
                  } else {
                     ItemAlerts.warnMissing(stack, stack.method_7964().getString());
                  }
               } else if (this.swapToOffhand(slot)) {
                  this.showHover(stack);
                  this.rememberWheelStack(stack);
               }
            }
         }
      }
   }

   public SwapIcon iconFor(class_1799 stack) {
      if (stack != null && !stack.method_7960()) {
         class_746 player = class_310.method_1551().field_1724;
         if (player == null) {
            return SwapIcon.AVAILABLE;
         } else if (sameItem(player.method_6079(), stack)) {
            return SwapIcon.AVAILABLE;
         } else {
            class_1661 inventory = player.method_31548();

            for(int slot = 0; slot < 36; ++slot) {
               if (sameItem(inventory.method_5438(slot), stack)) {
                  return SwapIcon.AVAILABLE;
               }
            }

            return this.fromBundle.isEnabled() && Bundles.contains(inventory, candidate -> sameItem(candidate, stack)) ? SwapIcon.IN_BUNDLE : SwapIcon.MISSING;
         }
      } else {
         return SwapIcon.AVAILABLE;
      }
   }

   private class_1799 counterpart(class_746 player, class_1799 offhand) {
      String from = this.swapFrom.getSelectedOption();
      String to = this.swapTo.getSelectedOption();
      String wanted = this.predicate(from).test(offhand) ? to : from;
      return this.findByName(player.method_31548(), wanted);
   }

   private SwapWheelScreen openWheel(BindSetting bind) {
      return new SwapWheelScreen(bind, this.activeBinds(), this.segments.getIntValue(), this::selectStack, this::forgetStack, this::iconFor);
   }

   private static class_2487 stripSessionKeys(class_2487 nbt) {
      class_2487 copy = nbt.method_10553();
      class_2487 bukkit = (class_2487)copy.method_10562("PublicBukkitValues").orElse(null);
      if (bukkit != null) {
         bukkit.method_10551("minecraft:s");
         copy.method_10566("PublicBukkitValues", bukkit);
      }

      return copy;
   }

   private static boolean sameCustomData(class_2487 left, class_2487 right) {
      if (Objects.equals(left, right)) {
         return true;
      } else {
         return left != null && right != null ? Objects.equals(stripSessionKeys(left), stripSessionKeys(right)) : false;
      }
   }

   private static boolean sameItem(class_1799 left, class_1799 right) {
      if (!left.method_7960() && !right.method_7960()) {
         if (left.method_7909() != right.method_7909()) {
            return false;
         } else if (!Objects.equals(left.method_58694(class_9334.field_49631), right.method_58694(class_9334.field_49631))) {
            return false;
         } else {
            return !sameCustomData(customData(left), customData(right))
               ? false
               : Objects.equals(left.method_58694(class_9334.field_49632), right.method_58694(class_9334.field_49632));
         }
      } else {
         return false;
      }
   }

   private static class_2487 customData(class_1799 stack) {
      class_9279 component = (class_9279)stack.method_58694(class_9334.field_49628);
      return component == null ? null : component.method_57461();
   }

   private void swapFromSeparateBind() {
      if (this.enabledSetting.isEnabled() && this.separateBinds.isEnabled()) {
         if (class_310.method_1551().field_1755 == null) {
            this.swapOffhand();
         }
      }
   }

   private List<BindSetting> activeBinds() {
      return this.separateBinds.isEnabled() ? List.of(this.swapBind, this.selectorBind) : List.of(this.actionBind);
   }

   private boolean swapToOffhand(int slot) {
      if (AutoTotemModule.isActive()) {
         return false;
      } else {
         InventoryController inventory = WexSideClient.getInventoryController();
         if (inventory == null) {
            return false;
         } else {
            ClickPolicy policy = "Legit".equalsIgnoreCase(this.mode.getSelectedOption()) ? ClickPolicy.SWAP : ClickPolicy.VISIBLE;
            inventory.submit(
               InventoryTask.builder()
                  .action(new ClickSlotAction(slot, 40))
                  .owner("auto_swap")
                  .flag(TaskFlag.DEFAULT)
                  .policy(policy)
                  .priority(TaskPriority.NORMAL)
                  .build()
            );
            return true;
         }
      }
   }

   private class_1799 nextWheelStack(class_1799 offhand) {
      List<class_1799> items = SwapWheelScreen.items();
      if (items.isEmpty()) {
         return class_1799.field_8037;
      } else if (items.size() == 1) {
         return ((class_1799)items.get(0)).method_7972();
      } else if (items.size() == 2) {
         class_1799 first = (class_1799)items.get(0);
         class_1799 second = (class_1799)items.get(1);
         return sameItem(offhand, first) ? second.method_7972() : first.method_7972();
      } else {
         class_1799 last = this.listed(items, this.lastWheelStack) ? this.lastWheelStack : class_1799.field_8037;
         class_1799 previous = this.listed(items, this.previousWheelStack) ? this.previousWheelStack : class_1799.field_8037;
         if (last.method_7960() && previous.method_7960()) {
            return ((class_1799)items.get(0)).method_7972();
         } else if (last.method_7960()) {
            return previous.method_7972();
         } else if (previous.method_7960()) {
            return last.method_7972();
         } else if (sameItem(offhand, last)) {
            return previous.method_7972();
         } else {
            return sameItem(offhand, previous) ? last.method_7972() : last.method_7972();
         }
      }
   }

   private void onKey(int code) {
      if (this.enabledSetting.isEnabled() && class_310.method_1551().field_1755 == null) {
         for(BindSetting bind : this.activeBinds()) {
            if (bind.getBindInput().matchesKeyboard(code)) {
               return;
            }
         }

         class_1799 stack = SwapWheelScreen.stackAt(code);
         if (!stack.method_7960()) {
            this.selectStack(stack);
         }
      }
   }

   private void swapOffhand() {
      if (this.enabledSetting.isEnabled()) {
         class_746 player = class_310.method_1551().field_1724;
         if (player != null) {
            class_1799 offhand = player.method_6079();
            boolean fromWheel = "Круговой селектор".equals(this.shortPressMode.getSelectedOption());
            class_1799 wanted = fromWheel ? this.nextWheelStack(offhand) : this.counterpart(player, offhand);
            if (!wanted.method_7960() && !sameItem(offhand, wanted)) {
               int slot = this.findInventorySlot(player.method_31548(), wanted);
               if (slot == -1) {
                  if (this.useFromBundle(player, wanted)) {
                     this.showHover(wanted);
                     if (fromWheel) {
                        this.rememberWheelStack(wanted);
                     }
                  }
               } else if (this.swapToOffhand(slot)) {
                  this.showHover(wanted);
                  if (fromWheel) {
                     this.rememberWheelStack(wanted);
                  }
               }
            }
         }
      }
   }

   private int findInventorySlot(class_1661 inventory, class_1799 stack) {
      for(int slot = 0; slot < 36; ++slot) {
         if (sameItem(inventory.method_5438(slot), stack)) {
            return slot < 9 ? slot + 36 : slot;
         }
      }

      return -1;
   }

   private void resetHistoryIfPairChanged() {
      String from = this.swapFrom.getSelectedOption();
      String to = this.swapTo.getSelectedOption();
      if (!Objects.equals(from, this.lastSwapFrom) || !Objects.equals(to, this.lastSwapTo)) {
         this.lastSwapFrom = from;
         this.lastSwapTo = to;
         this.lastWheelStack = class_1799.field_8037;
         this.previousWheelStack = class_1799.field_8037;
      }
   }

   private class_1799 findMatching(class_1661 inventory, Predicate<class_1799> predicate) {
      for(int slot = 0; slot < 36; ++slot) {
         class_1799 stack = inventory.method_5438(slot);
         if (!stack.method_7960() && predicate.test(stack)) {
            return stack.method_7972();
         }
      }

      if (!this.fromBundle.isEnabled()) {
         return class_1799.field_8037;
      } else {
         int[] found = Bundles.findInBundle(inventory, predicate);
         if (found == null) {
            return class_1799.field_8037;
         } else {
            class_9276 contents = (class_9276)inventory.method_5438(found[0]).method_58694(class_9334.field_49650);
            return contents == null ? class_1799.field_8037 : contents.method_57422(found[1]).method_7972();
         }
      }
   }

   private Predicate<class_1799> predicate(String name) {
      if (name == null) {
         return stack -> false;
      } else {
         return switch(name) {
            case "Shield" -> stack -> stack.method_31574(class_1802.field_8255);
            case "Sphere" -> this::isSphere;
            case "Totem" -> stack -> stack.method_31574(class_1802.field_8288);
            case "GApple" -> stack -> stack.method_31574(class_1802.field_8463);
            case "Firework" -> stack -> stack.method_31574(class_1802.field_8639);
            default -> stack -> false;
         };
      }
   }

   private boolean hasDonItem(class_1799 stack) {
      class_2487 nbt = customData(stack);
      if (nbt == null) {
         return false;
      } else {
         class_2487 bukkit = (class_2487)nbt.method_10553().method_10562("PublicBukkitValues").orElse(null);
         return bukkit != null && bukkit.method_10545("minecraft:don-item");
      }
   }

   private boolean useFromBundle(class_746 player, class_1799 stack) {
      if (this.fromBundle.isEnabled() && !AutoTotemModule.isActive()) {
         InventoryController inventory = WexSideClient.getInventoryController();
         if (inventory != null && !inventory.isActive()) {
            int[] found = Bundles.findInBundle(player.method_31548(), candidate -> sameItem(candidate, stack));
            return found == null ? false : Bundles.useFromBundle(player, inventory, "auto_swap", found[0], found[1], 45, this.ftMode.isEnabled());
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private void rememberWheelStack(class_1799 stack) {
      if (!stack.method_7960()) {
         class_1799 copy = stack.method_7972();
         if (!sameItem(copy, this.lastWheelStack)) {
            if (sameItem(copy, this.previousWheelStack)) {
               class_1799 swap = this.lastWheelStack;
               this.lastWheelStack = copy;
               this.previousWheelStack = swap;
            } else {
               this.previousWheelStack = this.lastWheelStack;
               this.lastWheelStack = copy;
            }
         }
      }
   }

   private boolean listed(List<class_1799> items, class_1799 stack) {
      if (stack.method_7960()) {
         return false;
      } else {
         for(class_1799 candidate : items) {
            if (sameItem(candidate, stack)) {
               return true;
            }
         }

         return false;
      }
   }

   private void showHover(class_1799 stack) {
      if (stack != null && !stack.method_7960()) {
         NotificationCenter overlays = WexSideClient.getNotificationCenter();
         if (overlays != null) {
            overlays.push(new ItemNotification(stack));
         }
      }
   }

   private void onMouse(int button) {
      if (this.enabledSetting.isEnabled() && class_310.method_1551().field_1755 == null) {
         for(BindSetting bind : this.activeBinds()) {
            if (bind.getBindInput().matchesMouse(button)) {
               return;
            }
         }

         class_1799 stack = SwapWheelScreen.stackAt(SwapWheelScreen.indexAt(button));
         if (!stack.method_7960()) {
            this.selectStack(stack);
         }
      }
   }

   private void openSelector() {
      if (this.enabledSetting.isEnabled() && this.separateBinds.isEnabled()) {
         class_310 client = class_310.method_1551();
         if (client.field_1724 != null && client.field_1755 == null) {
            client.method_1507(this.openWheel(this.selectorBind));
         }
      }
   }

   private class_1799 findByName(class_1661 inventory, String name) {
      if ("Totem".equals(name)) {
         class_1799 enchanted = this.findMatching(inventory, stack -> stack.method_31574(class_1802.field_8288) && stack.method_7958());
         if (!enchanted.method_7960()) {
            return enchanted;
         }
      }

      return this.findMatching(inventory, this.predicate(name));
   }

   private boolean isSphere(class_1799 stack) {
      if (!stack.method_31574(class_1802.field_8575)) {
         return false;
      } else {
         return !"FT".equals(this.serverMode.getSelectedOption()) ? true : this.hasDonItem(stack);
      }
   }

   private void resetHoldState() {
      this.actionHeld = false;
      this.holdTicks = 0;
      this.wheelOpened = false;
   }
}
