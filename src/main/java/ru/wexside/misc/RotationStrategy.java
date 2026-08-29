package ru.wexside.misc;

import ru.wexside.util.RotationIntent;
import ru.wexside.util.RotationState;

public interface RotationStrategy {
   RotationApplyResult process(RotationState var1, RotationIntent var2);

   void onDeactivated(RotationState var1);

   void onActivated(RotationState var1);
}
