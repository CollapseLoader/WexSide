package ru.wexside.misc;

import java.util.function.BooleanSupplier;

public final class InventoryTask {
   private final InventoryAction action;
   private final String owner;
   private final TaskFlag flag;
   private final ClickPolicy policy;
   private final TaskPriority priority;
   private final int delayTicks;
   private final BooleanSupplier condition;
   private final boolean blocking;

   private InventoryTask(
      InventoryAction action,
      String owner,
      TaskFlag flag,
      ClickPolicy policy,
      TaskPriority priority,
      int delayTicks,
      BooleanSupplier condition,
      boolean blocking
   ) {
      this.action = action;
      this.owner = owner;
      this.flag = flag;
      this.policy = policy;
      this.priority = priority;
      this.delayTicks = Math.max(0, delayTicks);
      this.condition = condition;
      this.blocking = blocking;
   }

   public static InventoryTask.Builder builder() {
      return new InventoryTask.Builder();
   }

   public InventoryAction action() {
      return this.action;
   }

   public String owner() {
      return this.owner;
   }

   public ClickPolicy clickPolicy() {
      return this.policy;
   }

   public TaskPriority priority() {
      return this.priority;
   }

   public TaskFlag conflictPolicy() {
      return this.flag;
   }

   public int delayTicks() {
      return this.delayTicks;
   }

   public boolean isBlocking() {
      return this.blocking;
   }

   public BooleanSupplier condition() {
      return this.condition;
   }

   public static final class Builder {
      private InventoryAction action;
      private String owner = "";
      private TaskFlag flag = TaskFlag.DEFAULT;
      private ClickPolicy policy = ClickPolicy.SILENT;
      private TaskPriority priority = TaskPriority.NORMAL;
      private int delayTicks;
      private BooleanSupplier condition = () -> true;
      private boolean blocking;

      public InventoryTask.Builder action(InventoryAction action) {
         this.action = action;
         return this;
      }

      public InventoryTask.Builder owner(String owner) {
         this.owner = owner;
         return this;
      }

      public InventoryTask.Builder flag(TaskFlag flag) {
         this.flag = flag;
         return this;
      }

      public InventoryTask.Builder flag2(TaskFlag flag) {
         return this.flag(flag);
      }

      public InventoryTask.Builder policy(ClickPolicy policy) {
         this.policy = policy;
         return this;
      }

      public InventoryTask.Builder priority(TaskPriority priority) {
         this.priority = priority;
         return this;
      }

      public InventoryTask.Builder delay(int delayTicks) {
         this.delayTicks = delayTicks;
         return this;
      }

      public InventoryTask.Builder condition(BooleanSupplier condition) {
         this.condition = condition == null ? () -> true : condition;
         return this;
      }

      public InventoryTask.Builder blocking(boolean blocking) {
         this.blocking = blocking;
         return this;
      }

      public InventoryTask build() {
         return new InventoryTask(this.action, this.owner, this.flag, this.policy, this.priority, this.delayTicks, this.condition, this.blocking);
      }
   }
}
