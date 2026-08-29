package ru.wexside.schedule;

import java.util.Arrays;

public record ScheduledEvent(String name, int[] minutesOfDay) {
   public ScheduledEvent(String name, int[] minutesOfDay) {
      minutesOfDay = (int[])minutesOfDay.clone();
      Arrays.sort(minutesOfDay);
      this.name = name;
      this.minutesOfDay = minutesOfDay;
   }

   public int[] minutesOfDay() {
      return (int[])this.minutesOfDay.clone();
   }
}
