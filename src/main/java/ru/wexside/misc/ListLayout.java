package ru.wexside.misc;

import org.joml.Matrix4f;

public interface ListLayout {
   float process(int var1);

   int getIntType();

   float process2(int var1, Matrix4f var2, float var3, float var4, float var5);
}
