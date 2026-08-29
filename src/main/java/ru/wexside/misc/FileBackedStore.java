package ru.wexside.misc;

import java.io.File;

public abstract class FileBackedStore implements ConfigStore {
   public final File file;

   public FileBackedStore(File file) {
      this.file = file;
   }
}
