package ru.wexside.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import net.minecraft.class_1304;
import net.minecraft.class_310;
import net.minecraft.class_746;
import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.misc.ArmorSlotRenderer;
import ru.wexside.misc.BakedIconEntry;
import ru.wexside.util.AbstractHudElement;
import ru.wexside.util.GuiDrawApi;

public final class ArmorHUD
extends AbstractHudElement {
    private final float value;
    private final List<ArmorSlotRenderer> armorSlots = List.of(new ArmorSlotRenderer(class_1304.field_6169), new ArmorSlotRenderer(class_1304.field_6174), new ArmorSlotRenderer(class_1304.field_6172), new ArmorSlotRenderer(class_1304.field_6166));

    public ArmorHUD(BooleanSupplier booleanSupplier) {
        super("Armor HUD", booleanSupplier);
        this.value = 3.0f;
    }

    @Override
    protected float getWidth() {
        return (float)this.armorSlots.size() * this.armorSlots.get(0).getFloatType2() + (float)(this.armorSlots.size() - 1) * 3.0f;
    }

    @Override
    protected void updateLayout() {
        class_746 player2 = class_310.method_1551().field_1724;
        for (ArmorSlotRenderer armorSlotRenderer : this.armorSlots) {
            armorSlotRenderer.updateEquipment(player2);
        }
        this.update2();
    }

    @Override
    protected void renderContent(GuiDrawApi drawApi, Matrix4f matrix4f, float f, float f2, float f3, float f4, float f5) {
        float f6 = (this.armorSlots.get(0).getFloatType2() + 3.0f) * f5;
        for (int i = 0; i < this.armorSlots.size(); ++i) {
            this.armorSlots.get(i).process2(drawApi, matrix4f, f + (float)i * f6, f2, f5);
        }
    }

    private void update2() {
        float f = class_310.method_1551().method_22683().method_4495();
        ArrayList<BakedIconEntry> arrayList = new ArrayList<BakedIconEntry>();
        for (ArmorSlotRenderer armorSlotRenderer : this.armorSlots) {
            BakedIconEntry bakedIconEntry = armorSlotRenderer.process3(f);
            if (bakedIconEntry == null) continue;
            arrayList.add(bakedIconEntry);
        }
        if (!arrayList.isEmpty()) {
            WexSideClient.getRenderPipeline2().setList(arrayList);
        }
    }

    @Override
    protected float getHeight() {
        return this.armorSlots.get(0).getFloatType();
    }

    @Override
    protected boolean isContentVisible() {
        class_746 player2 = class_310.method_1551().field_1724;
        for (ArmorSlotRenderer armorSlotRenderer : this.armorSlots) {
            if (!armorSlotRenderer.process4(player2)) continue;
            return true;
        }
        return false;
    }
}

