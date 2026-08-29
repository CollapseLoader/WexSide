package ru.wexside.misc;

import java.util.HashMap;
import java.util.Map;
import ru.wexside.util.RandomizedDirectRotationStrategy;
import ru.wexside.util.SpookyRotationStrategy;

public final class SpookyTest implements RotationStrategyFactory {
   private final Map<String, RotationStrategy> field12 = new HashMap<>();

   public void update() {
      this.field12.put("Simple", new SmoothRotationStrategy());
      this.field12.put("FT Snap", new HumanizedRotationStrategy());
      this.field12.put("RW", new RandomizedRotationStrategy());
      this.field12.put("Spooky", new RandomizedDirectRotationStrategy());
      this.field12.put("Spooky Test", new SpookyRotationStrategy());
   }

   @Override
   public RotationStrategy process(String string) {
      return this.field12.getOrDefault(string, this.field12.get("Simple"));
   }
}
