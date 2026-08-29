package ru.wexside.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import net.minecraft.class_1799;
import net.minecraft.class_310;
import net.minecraft.class_746;
import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.misc.BakedIconEntry;
import ru.wexside.misc.FontRegistry;
import ru.wexside.misc.ItemStackRenderer;
import ru.wexside.misc.ThemeColors;
import ru.wexside.util.AbstractHudElement;
import ru.wexside.util.GuiDrawApi;

public final class InventoryHUD
extends AbstractHudElement {
    private static final int COLUMNS = 9;
    private static final int ROWS = 3;
    private static final float WIDTH = 210.0f;
    private static final float HEIGHT = 90.0f;
    private final List<ItemStackRenderer> slots = new ArrayList<ItemStackRenderer>(27);

    public InventoryHUD(BooleanSupplier visible) {
        super("Inventory HUD", visible);
        for (int index = 0; index < 27; ++index) {
            this.slots.add(new ItemStackRenderer());
        }
    }

    @Override
    protected float getWidth() {
        return 210.0f;
    }

    @Override
    protected float getHeight() {
        return 90.0f;
    }

    @Override
    protected void updateLayout() {
        class_746 player = class_310.method_1551().field_1724;
        for (int index = 0; index < this.slots.size(); ++index) {
            class_1799 stack = player == null ? class_1799.field_8037 : player.method_31548().method_5438(9 + index);
            this.slots.get(index).setStack(stack);
        }
        this.collectIconBakes();
    }

    @Override
    protected void renderContent(GuiDrawApi renderer, Matrix4f matrix, float x, float y, float width, float height, float scale) {
        int color = ThemeColors.hudTextPrimary();
        this.renderPanelSurface(renderer, matrix, x, y, width, height, 8.0f, scale);
        renderer.beginStencil(1);
        renderer.drawRoundedRectangle(matrix, x, y, width, height, 8.0f * scale, -1);
        renderer.applyStencilMask(1);
        FontRegistry.font7.process2(matrix, renderer, "Inventory", x + 8.0f * scale, y + 4.5f * scale, 8.0f * scale, color);
        FontRegistry.font3.process5(matrix, renderer, "\u0419", x + width - 19.0f * scale, y + 6.0f * scale, 8.0f * scale, color);
        float slotWidth = this.slots.get(0).getFloatType2();
        float slotHeight = this.slots.get(0).getFloatType();
        float columnStep = (slotWidth + 3.06f) * scale;
        float rowStep = (slotHeight + 3.5f) * scale;
        for (int index = 0; index < this.slots.size(); ++index) {
            float slotX = x + 5.0f * scale + (float)(index % 9) * columnStep;
            float slotY = y + 21.0f * scale + (float)(index / 9) * rowStep;
            this.slots.get(index).process(renderer, matrix, slotX, slotY, scale);
        }
        renderer.endStencil();
    }

    private void collectIconBakes() {
        float framebufferScale = class_310.method_1551().method_22683().method_4495();
        ArrayList<BakedIconEntry> bakes = new ArrayList<BakedIconEntry>();
        for (ItemStackRenderer slot : this.slots) {
            BakedIconEntry bake = slot.process3(framebufferScale);
            if (bake == null) continue;
            bakes.add(bake);
        }
        if (!bakes.isEmpty()) {
            WexSideClient.getRenderPipeline2().setList(bakes);
        }
    }
}

