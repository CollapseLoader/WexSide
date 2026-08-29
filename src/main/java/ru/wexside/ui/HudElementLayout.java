package ru.wexside.ui;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.function.BooleanSupplier;
import net.minecraft.class_310;
import ru.wexside.config.ConfigSerializable;

public final class HudElementLayout
implements ConfigSerializable {
    private final String name;
    private BooleanSupplier visibility = () -> true;
    private boolean enabled = true;
    private float anchorX = 0.5f;
    private float anchorY = 0.5f;
    private float x;
    private float y;
    private float width;
    private float height;
    private float scale = 1.0f;

    public HudElementLayout(String name) {
        this.name = name;
    }

    @Override
    public String getConfigId() {
        return "draggable/" + this.name;
    }

    @Override
    public void writeConfig(DataOutputStream output) throws IOException {
        output.writeFloat(this.anchorX);
        output.writeFloat(this.anchorY);
        output.writeBoolean(false);
    }

    @Override
    public void readConfig(DataInputStream input) throws IOException {
        this.anchorX = HudElementLayout.clamp01(input.readFloat());
        this.anchorY = HudElementLayout.clamp01(input.readFloat());
        if (input.readBoolean()) {
            input.readUTF();
            input.readUTF();
            input.readFloat();
            input.readFloat();
            input.readFloat();
        }
    }

    public String getName() {
        return this.name;
    }

    public boolean isVisible() {
        return this.enabled && this.visibility.getAsBoolean();
    }

    public HudElementLayout visibleWhen(BooleanSupplier visibility) {
        this.visibility = visibility == null ? () -> true : visibility;
        return this;
    }

    public HudElementLayout setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public void setAnchor(float anchorX, float anchorY) {
        this.anchorX = HudElementLayout.clamp01(anchorX);
        this.anchorY = HudElementLayout.clamp01(anchorY);
        this.updateScreenPosition();
    }

    public void setAnchorX(float anchorX) {
        this.setAnchor(anchorX, this.anchorY);
    }

    public void setAnchorY(float anchorY) {
        this.setAnchor(this.anchorX, anchorY);
    }

    public void setScale(float scale) {
        this.scale = Math.max(0.01f, scale);
    }

    public void setSize(float width, float height) {
        this.width = Math.max(0.0f, width * this.scale);
        this.height = Math.max(0.0f, height * this.scale);
        this.updateScreenPosition();
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        class_310 client = class_310.method_1551();
        if (client != null && client.method_22683() != null) {
            float availableWidth = Math.max(0.0f, (float)client.method_22683().method_4486() - this.width);
            float availableHeight = Math.max(0.0f, (float)client.method_22683().method_4502() - this.height);
            this.anchorX = availableWidth == 0.0f ? 0.0f : HudElementLayout.clamp01(x / availableWidth);
            this.anchorY = availableHeight == 0.0f ? 0.0f : HudElementLayout.clamp01(y / availableHeight);
        }
    }

    private void updateScreenPosition() {
        class_310 client = class_310.method_1551();
        if (client == null || client.method_22683() == null) {
            return;
        }
        this.x = ((float)client.method_22683().method_4486() - this.width) * this.anchorX;
        this.y = ((float)client.method_22683().method_4502() - this.height) * this.anchorY;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public float getWidth() {
        return this.width;
    }

    public float getHeight() {
        return this.height;
    }

    public float getScale() {
        return this.scale;
    }

    private static float clamp01(float value) {
        return Math.max(0.0f, Math.min(1.0f, value));
    }
}

