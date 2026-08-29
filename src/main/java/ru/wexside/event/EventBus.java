package ru.wexside.event;

public interface EventBus {
   <T extends Event> void subscribe(Class<T> var1, EventListener<? super T> var2, int var3);

   <T extends Event> void subscribe(Class<T> var1, EventListener<? super T> var2);

   <T extends Event> void post(T var1);
}
