package ru.wexside.misc;

import java.io.IOException;

public interface ConfigStore {
   void load();

   void save() throws IOException;
}
