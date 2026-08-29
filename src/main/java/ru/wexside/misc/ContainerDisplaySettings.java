package ru.wexside.misc;

public final class ContainerDisplaySettings {
   private final ContainerDisplay containerDisplay = new ContainerDisplay();

   public ContainerDisplaySettings(ConfigRegistry configRegistry) {
      configRegistry.register(this.containerDisplay);
   }

   public ContainerDisplay getContainerDisplay() {
      return this.containerDisplay;
   }
}
