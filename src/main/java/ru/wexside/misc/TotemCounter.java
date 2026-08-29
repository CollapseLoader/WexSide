package ru.wexside.misc;

import java.util.ArrayList;
import java.util.function.BooleanSupplier;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1935;
import net.minecraft.class_310;
import net.minecraft.class_746;
import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.misc.BakedIconEntry;
import ru.wexside.misc.FontRegistry;
import ru.wexside.render.BakedItemIcon;
import ru.wexside.render.ItemIconCache;
import ru.wexside.util.AbstractHudElement;
import ru.wexside.util.GuiDrawApi;

public final class TotemCounter
extends AbstractHudElement {
    static final int slot = -1;
    static final float value3 = 12.0f;
    private BakedItemIcon bakedItemIcon;
    static final float value4 = 16.0f;
    private int slot2;
    static final int slot3 = -3670016;
    static final float value5 = 10.0f;
    static final float value6 = 7.0f;
    private final ItemIconCache itemIconCache = new ItemIconCache();
    private final class_1799 stack = new class_1799((class_1935)class_1802.field_8288);

    public TotemCounter(BooleanSupplier booleanSupplier) {
        super("Totem Counter", booleanSupplier);
    }

    @Override
    protected float getWidth() {
        return 16.0f;
    }

    @Override
    protected void updateLayout() {
        this.itemIconCache.update3();
        this.slot2 = this.countTotems();
        this.itemIconCache.update2();
        this.bakedItemIcon = this.itemIconCache.process(this.stack);
        float f = class_310.method_1551().method_22683().method_4495();
        ArrayList<BakedIconEntry> arrayList = new ArrayList<BakedIconEntry>();
        this.itemIconCache.process2(f, arrayList);
        if (!arrayList.isEmpty()) {
            WexSideClient.getRenderPipeline2().setList(arrayList);
        }
    }

    @Override
    protected void renderContent(GuiDrawApi drawApi, Matrix4f matrix4f, float f, float f2, float f3, float f4, float f5) {
        if (this.bakedItemIcon == null) {
            return;
        }
        this.itemIconCache.process3(drawApi, matrix4f, this.bakedItemIcon, f, f2, 16.0f * f5);
        FontRegistry.font5.process2(matrix4f, drawApi, String.valueOf(this.slot2), f + 10.0f * f5, f2 + 12.0f * f5, 7.0f * f5, this.slot2 == 0 ? -3670016 : -1);
        this.itemIconCache.update();
    }

    @Override
    protected float getHeight() {
        return 16.0f;
    }

    private int countTotems() {
        class_746 player = class_310.method_1551().field_1724;
        if (player == null) {
            return 0;
        }
        int count = player.method_6079().method_31574(class_1802.field_8288) ? player.method_6079().method_7947() : 0;
        for (int index = 0; index < player.method_31548().method_5439(); ++index) {
            class_1799 item = player.method_31548().method_5438(index);
            if (!item.method_31574(class_1802.field_8288)) continue;
            count += item.method_7947();
        }
        return count;
    }
}

