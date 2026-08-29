package ru.wexside.render;

import net.minecraft.class_10799;
import net.minecraft.class_1799;
import net.minecraft.class_332;
import net.minecraft.class_8685;
import org.joml.Matrix3x2fStack;

public final class HudIconRenderer {
    private static final float VANILLA_ITEM_SIZE = 16.0f;

    private HudIconRenderer() {
    }

    public static void drawItem(class_332 context, class_1799 stack, int x, int y, int size) {
        if (context == null || stack == null || stack.method_7960() || size <= 0) {
            return;
        }
        Matrix3x2fStack matrices = context.method_51448();
        matrices.pushMatrix();
        matrices.translate((float)x, (float)y);
        float scale = (float)size / 16.0f;
        matrices.scale(scale, scale);
        context.method_51427(stack, 0, 0);
        matrices.popMatrix();
    }

    public static void drawPlayerHead(class_332 context, class_8685 skin, int x, int y, int size) {
        if (context == null || skin == null || skin.comp_1626() == null || size <= 0) {
            return;
        }
        context.method_25302(class_10799.field_56883, skin.comp_1626().comp_3627(), x, y, 8.0f, 8.0f, size, size, 8, 8, 64, 64);
        context.method_25302(class_10799.field_56883, skin.comp_1626().comp_3627(), x, y, 40.0f, 8.0f, size, size, 8, 8, 64, 64);
    }
}

