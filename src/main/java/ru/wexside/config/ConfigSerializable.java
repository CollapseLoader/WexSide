package ru.wexside.config;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface ConfigSerializable {
   String getConfigId();

   void writeConfig(DataOutputStream var1) throws IOException;

   void readConfig(DataInputStream var1) throws IOException;
}
