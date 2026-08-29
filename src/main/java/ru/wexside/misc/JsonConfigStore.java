package ru.wexside.misc;

import com.google.gson.Gson;
import java.io.File;

public abstract class JsonConfigStore extends FileBackedStore implements ConfigStore {
   public final Gson gson2;

   public JsonConfigStore(File file, Gson gson3) {
      super(file);
      this.gson2 = gson3;
   }
}
