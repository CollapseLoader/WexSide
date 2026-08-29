package ru.wexside.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import net.minecraft.class_310;
import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.misc.BakedIconEntry;
import ru.wexside.misc.ItemBindBox;
import ru.wexside.util.AbstractHudElement;
import ru.wexside.util.GuiDrawApi;
import ru.wexside.util.ItemStatusTile;

public final class ItemStatusHudElement
extends AbstractHudElement {
    private final float value;
    private final int slot;
    private final float value2;
    private final List<ItemStatusTile> itemTiles = new ArrayList<ItemStatusTile>();

    public ItemStatusHudElement(String string, BooleanSupplier booleanSupplier, List<ItemBindBox> list, BooleanSupplier booleanSupplier2) {
        super(string, booleanSupplier);
        this.value2 = 21.0f;
        this.value = 3.0f;
        this.slot = 6;
        for (ItemBindBox itemBindBox : list) {
            this.itemTiles.add(new ItemStatusTile(itemBindBox, booleanSupplier2));
        }
    }

    @Override
    protected float getWidth() {
        int n = this.getIntType();
        int n2 = Math.min(n, 6);
        if (n2 == 0) {
            return 0.0f;
        }
        return (float)n2 * 21.0f + (float)(n2 - 1) * 3.0f;
    }

    private int getIntType() {
        int n = 0;
        for (ItemStatusTile itemTile : this.itemTiles) {
            if (!itemTile.isActive()) continue;
            ++n;
        }
        return n;
    }

    @Override
    protected void updateLayout() {
        for (ItemStatusTile itemTile : this.itemTiles) {
            itemTile.update3();
            itemTile.update();
        }
        this.update2();
    }

    @Override
    protected void renderContent(GuiDrawApi drawApi, Matrix4f matrix4f, float f, float f2, float f3, float f4, float f5) {
        float f6 = 24.0f * f5;
        int n = 0;
        for (ItemStatusTile itemTile : this.itemTiles) {
            if (!itemTile.isActive()) continue;
            int n2 = n % 6;
            int n3 = n / 6;
            itemTile.process(drawApi, matrix4f, f + (float)n2 * f6, f2 + (float)n3 * f6, f5);
            ++n;
        }
    }

    private void update2() {
        float f = class_310.method_1551().method_22683().method_4495();
        ArrayList<BakedIconEntry> arrayList = new ArrayList<BakedIconEntry>();
        for (ItemStatusTile itemTile : this.itemTiles) {
            BakedIconEntry bakedIconEntry = itemTile.process2(f);
            if (bakedIconEntry == null) continue;
            arrayList.add(bakedIconEntry);
        }
        if (!arrayList.isEmpty()) {
            WexSideClient.getRenderPipeline2().setList(arrayList);
        }
    }

    @Override
    protected float getHeight() {
        int n = (this.getIntType() + 6 - 1) / 6;
        if (n == 0) {
            return 0.0f;
        }
        return (float)n * 21.0f + (float)(n - 1) * 3.0f;
    }

    @Override
    protected boolean isContentVisible() {
        for (ItemStatusTile itemTile : this.itemTiles) {
            if (!itemTile.isActive()) continue;
            return true;
        }
        return false;
    }
}

