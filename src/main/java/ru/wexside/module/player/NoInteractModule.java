package ru.wexside.module.player;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_3481;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.BlockInteractEvent;
import ru.wexside.event.EventBus;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.MultiSelectSetting;
import ru.wexside.setting.MultiSelectSettingBuilder;

public final class NoInteractModule extends Module implements ConfigSerializable {
   private static final List<NoInteractModule.BlockFilter> FILTERS = List.of(
      new NoInteractModule.BlockFilter("Craft-Table", block -> block == class_2246.field_9980),
      new NoInteractModule.BlockFilter("Brewing-Stand", block -> block == class_2246.field_10333),
      new NoInteractModule.BlockFilter("Door", block -> block.method_9564().method_26164(class_3481.field_15495)),
      new NoInteractModule.BlockFilter("Hopper", block -> block == class_2246.field_10312),
      new NoInteractModule.BlockFilter("Button", block -> block.method_9564().method_26164(class_3481.field_15493)),
      new NoInteractModule.BlockFilter("Note-Block", block -> block == class_2246.field_10179),
      new NoInteractModule.BlockFilter("Trap-Door", block -> block.method_9564().method_26164(class_3481.field_15487)),
      new NoInteractModule.BlockFilter("Furnace", block -> block == class_2246.field_10181),
      new NoInteractModule.BlockFilter("Chest", block -> block == class_2246.field_10034),
      new NoInteractModule.BlockFilter("Trapped-Chest", block -> block == class_2246.field_10380),
      new NoInteractModule.BlockFilter("Ender-Chest", block -> block == class_2246.field_10443),
      new NoInteractModule.BlockFilter("Gate", block -> block.method_9564().method_26164(class_3481.field_25147)),
      new NoInteractModule.BlockFilter("Anvil", block -> block.method_9564().method_26164(class_3481.field_15486)),
      new NoInteractModule.BlockFilter("Dispenser", block -> block == class_2246.field_10200),
      new NoInteractModule.BlockFilter("Lever", block -> block == class_2246.field_10363),
      new NoInteractModule.BlockFilter("Sign", block -> block.method_9564().method_26164(class_3481.field_41282)),
      new NoInteractModule.BlockFilter("Shulker-Box", block -> block.method_9564().method_26164(class_3481.field_21490)),
      new NoInteractModule.BlockFilter("Barrel", block -> block == class_2246.field_16328),
      new NoInteractModule.BlockFilter("Smoker", block -> block == class_2246.field_16334),
      new NoInteractModule.BlockFilter("Dropper", block -> block == class_2246.field_10228),
      new NoInteractModule.BlockFilter("Crafter", block -> block == class_2246.field_46797),
      new NoInteractModule.BlockFilter("Beacon", block -> block == class_2246.field_10327),
      new NoInteractModule.BlockFilter("Enchanting-Table", block -> block == class_2246.field_10485),
      new NoInteractModule.BlockFilter("Grindstone", block -> block == class_2246.field_16337),
      new NoInteractModule.BlockFilter("Stonecutter", block -> block == class_2246.field_16335),
      new NoInteractModule.BlockFilter("Loom", block -> block == class_2246.field_10083),
      new NoInteractModule.BlockFilter("Cartography-Table", block -> block == class_2246.field_16336),
      new NoInteractModule.BlockFilter("Smithing-Table", block -> block == class_2246.field_16329),
      new NoInteractModule.BlockFilter("Lectern", block -> block == class_2246.field_16330),
      new NoInteractModule.BlockFilter("Chiseled-Bookshelf", block -> block == class_2246.field_40276),
      new NoInteractModule.BlockFilter("Decorated-Pot", block -> block == class_2246.field_42752),
      new NoInteractModule.BlockFilter("Comparator", block -> block == class_2246.field_10377),
      new NoInteractModule.BlockFilter("Repeater", block -> block == class_2246.field_10450),
      new NoInteractModule.BlockFilter("Jukebox", block -> block == class_2246.field_10223),
      new NoInteractModule.BlockFilter("Bell", block -> block == class_2246.field_16332),
      new NoInteractModule.BlockFilter("Composter", block -> block == class_2246.field_17563),
      new NoInteractModule.BlockFilter("Respawn-Anchor", block -> block == class_2246.field_23152),
      new NoInteractModule.BlockFilter("Cauldron", block -> block.method_9564().method_26164(class_3481.field_26985))
   );
   private final BooleanSetting enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
         .value(false)
         .defaultValue(false)
         .name("Enabled")
         .id("enabled")
         .description("Блокировка взаимодействия с блоками")
         .withKeybind()
         .toggle())
      .build();
   private final BooleanSetting allBlocks;
   private final MultiSelectSetting ignore;

   public NoInteractModule(EventBus eventBus) {
      super(eventBus, "no_interact", "No Interact", "Блокировка взаимодействия с блоками", ModuleCategory.valueOf("PLAYER"));
      this.registerSetting(this.enabledSetting);
      this.allBlocks = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("All Blocks")
            .id("all_blocks")
            .description("Блокировка всех блоков из списка"))
         .build();
      this.registerSetting(this.allBlocks);
      MultiSelectSetting ignoreSetting = ((MultiSelectSettingBuilder)MultiSelectSetting.getMultiSelectSettingBuilder()
            .options(
               "Craft-Table",
               "Brewing-Stand",
               "Door",
               "Hopper",
               "Button",
               "Note-Block",
               "Trap-Door",
               "Furnace",
               "Chest",
               "Trapped-Chest",
               "Ender-Chest",
               "Gate",
               "Anvil",
               "Dispenser",
               "Lever",
               "Sign",
               "Shulker-Box",
               "Barrel",
               "Smoker",
               "Dropper",
               "Crafter",
               "Beacon",
               "Enchanting-Table",
               "Grindstone",
               "Stonecutter",
               "Loom",
               "Cartography-Table",
               "Smithing-Table",
               "Lectern",
               "Chiseled-Bookshelf",
               "Decorated-Pot",
               "Comparator",
               "Repeater",
               "Jukebox",
               "Bell",
               "Composter",
               "Respawn-Anchor",
               "Cauldron"
            )
            .selectAll(false)
            .optionListEnabled(false)
            .name("Ignore")
            .id("ignore")
            .description("Блоки, взаимодействие с которыми блокируется")
            .visibleWhen(() -> !this.allBlocks.isEnabled()))
         .build();
      this.ignore = ignoreSetting;
      this.registerSetting(ignoreSetting);
   }

   @Override
   protected void initialize() {
      this.listen(BlockInteractEvent.class, this::onBlockInteract);
   }

   private void onBlockInteract(BlockInteractEvent event) {
      if (this.enabledSetting.isEnabled()) {
         if (this.shouldBlock(event.getBlock())) {
            event.cancel();
         }
      }
   }

   private boolean shouldBlock(class_2248 block) {
      boolean all = this.allBlocks.isEnabled();
      List<String> selected = this.ignore.getSelectedOptions();

      for(NoInteractModule.BlockFilter filter : FILTERS) {
         if ((all || selected.contains(filter.name)) && filter.matches.test(block)) {
            return true;
         }
      }

      return false;
   }

   private static record BlockFilter(String name, Predicate<class_2248> matches) {
   }
}
