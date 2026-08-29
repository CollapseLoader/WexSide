package ru.wexside.misc;

import java.util.List;
import org.joml.Matrix4f;
import ru.wexside.util.ModuleKeybindGroup;

public final class SettingsListLayout implements ListLayout {
   private final boolean enabled;
   private final float[] value;
   private final float value2;
   private final List<ModuleKeybindGroup> groups;

   public SettingsListLayout(List<ModuleKeybindGroup> list, float f, boolean bl) {
      this.groups = list;
      this.value2 = f;
      this.enabled = bl;
      this.value = new float[list.size()];

      for(int i = 0; i < list.size(); ++i) {
         this.value[i] = list.get(i).process2(bl);
      }
   }

   @Override
   public float process(int n) {
      return this.value[n];
   }

   @Override
   public int getIntType() {
      return this.groups.size();
   }

   @Override
   public float process2(int n, Matrix4f matrix4f, float f, float f2, float f3) {
      ModuleKeybindGroup moduleKeybindGroup = this.groups.get(n);
      moduleKeybindGroup.getBounds().setPosition(f, f2);
      moduleKeybindGroup.getBounds().setSize(f3, this.value[n]);
      return moduleKeybindGroup.process4(this.value2, matrix4f, this.enabled);
   }
}
