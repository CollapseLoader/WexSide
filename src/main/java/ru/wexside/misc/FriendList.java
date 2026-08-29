package ru.wexside.misc;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

public final class FriendList {
   private final FriendListStore store;

   public FriendList(FriendListStore store) {
      this.store = store;
   }

   public boolean contains(String name) {
      return name != null && this.store.getSet().contains(normalize(name));
   }

   public boolean add(String name) {
      if (name != null && !name.isBlank()) {
         boolean added = this.store.getSet().add(normalize(name));
         if (added) {
            this.save();
         }

         return added;
      } else {
         return false;
      }
   }

   public boolean remove(String name) {
      if (name == null) {
         return false;
      } else {
         boolean removed = this.store.getSet().remove(normalize(name));
         if (removed) {
            this.save();
         }

         return removed;
      }
   }

   public void clear() {
      if (!this.store.getSet().isEmpty()) {
         this.store.getSet().clear();
         this.save();
      }
   }

   public Collection<String> getNames() {
      return Collections.unmodifiableCollection(this.store.getSet());
   }

   private void save() {
      try {
         this.store.save();
      } catch (IOException var2) {
         throw new IllegalStateException("Failed to save friend list", var2);
      }
   }

   private static String normalize(String name) {
      return name.trim().toLowerCase(Locale.ROOT);
   }
}
