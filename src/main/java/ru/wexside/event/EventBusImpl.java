package ru.wexside.event;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventBusImpl implements EventBus {
   private static final Logger LOGGER = LoggerFactory.getLogger(EventBusImpl.class);
   private final Map<Class<?>, List<RegisteredListener<?>>> listenersByEvent = new ConcurrentHashMap<>();

   @Override
   public <T extends Event> void subscribe(Class<T> eventType, EventListener<? super T> listener, int priority) {
      List<RegisteredListener<?>> listeners = this.listenersByEvent.computeIfAbsent(eventType, ignored -> new CopyOnWriteArrayList());
      synchronized(listeners) {
         int index = 0;

         while(index < listeners.size() && listeners.get(index).priority() <= priority) {
            ++index;
         }

         listeners.add(index, new RegisteredListener<>(listener, priority));
      }
   }

   @Override
   public <T extends Event> void subscribe(Class<T> eventType, EventListener<? super T> listener) {
      this.subscribe(eventType, listener, 0);
   }

   @Override
   public <T extends Event> void post(T event) {
      for(RegisteredListener<?> listener : this.listenersByEvent.getOrDefault(event.getClass(), List.of())) {
         try {
            listener.dispatch(event);
         } catch (RuntimeException var5) {
            LOGGER.error("Event listener failed for {}", event.getClass().getName(), var5);
         }
      }
   }
}
