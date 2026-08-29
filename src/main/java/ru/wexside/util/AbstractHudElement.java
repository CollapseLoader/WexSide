package ru.wexside.util;

import java.util.function.BooleanSupplier;
import net.minecraft.class_310;
import net.minecraft.class_408;
import net.minecraft.class_490;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector4f;
import ru.wexside.WexSideClient;
import ru.wexside.misc.FadeAnimation;
import ru.wexside.misc.HudElementConfig;
import ru.wexside.misc.PreparedLayer;
import ru.wexside.misc.TextureHandle;
import ru.wexside.misc.ThemeColors;
import ru.wexside.misc.ThemeManager;
import ru.wexside.ui.HudElementLayout;
import ru.wexside.util.ColorUtils;
import ru.wexside.util.GuiDrawApi;

public abstract class AbstractHudElement {
    private static final float OPAQUE_THRESHOLD = 0.98f;
    private static final float HIDDEN_THRESHOLD = 0.02f;
    private static final float MAXIMUM_BLUR_RADIUS = 16.0f;
    private static final float LAYER_PADDING = 32.0f;
    protected final String name;
    protected final HudElementConfig hudElementConfig;
    protected final FadeAnimation fadeAnimation = new FadeAnimation(15.0f);
    protected final HudElementLayout layout;

    protected AbstractHudElement(String name, BooleanSupplier visibility) {
        this.name = name;
        this.layout = WexSideClient.getHudLayoutManager().register(name).visibleWhen(visibility);
        this.hudElementConfig = new HudElementConfig(name);
        WexSideClient.getInstance().getConfigRegistry().register(this.hudElementConfig);
        WexSideClient.getInventoryHudRegistry().register(this);
        this.configureDefaultAnchor(name);
    }

    public HudElementConfig getHudElementConfig() {
        return this.hudElementConfig;
    }

    public final void renderFrame() {
        boolean visible = this.layout.isVisible() && (this.isContentVisible() || this.isEditorScreen());
        this.fadeAnimation.updateTarget(visible);
        float alpha = this.resolveVisibilityAlpha(visible);
        if (alpha <= 0.0f) {
            return;
        }
        this.hudElementConfig.updateScaleAnimation();
        this.layout.setScale(this.hudElementConfig.getScale());
        this.updateLayout();
        this.layout.setSize(this.getWidth(), this.getHeight());
        float scale = this.layout.getScale();
        float width = this.layout.getWidth();
        float height = this.layout.getHeight();
        GuiDrawApi renderer = WexSideClient.getHudRenderer();
        float pixelScale = 2.0f;
        float x = (float)Math.round(this.layout.getX() * pixelScale) / pixelScale;
        float y = (float)Math.round(this.layout.getY() * pixelScale) / pixelScale;
        Matrix4f matrix = new Matrix4f().scale(pixelScale).translate(x, y, 0.0f);
        if (alpha >= 1.0f) {
            renderer.begin();
            this.renderContent(renderer, matrix, 0.0f, 0.0f, width, height, scale);
            renderer.end();
            return;
        }
        renderer.begin();
        PreparedLayer layer = renderer.prepareLayer(matrix, 0.0f, 0.0f, width, height, 32.0f);
        Vector4f transformedBottomLeft = matrix.transform(new Vector4f(-32.0f, height + 32.0f, 0.0f, 1.0f));
        float textureBottomPadding = (float)layer.getTexture().getHeight() * (1.0f - layer.maxV());
        float layerX = renderer.getLayerOffsetX() + transformedBottomLeft.x;
        float layerY = renderer.getLayerOffsetY() + ((float)renderer.getFramebufferHeight() - transformedBottomLeft.y) - textureBottomPadding;
        renderer.beginLayerFrame(layer.getTexture(), layerX, layerY);
        Matrix4f contentMatrix = new Matrix4f((Matrix4fc)layer.getContentMatrix()).translate(layer.contentX(), layer.contentY(), 0.0f);
        this.renderContent(renderer, contentMatrix, 0.0f, 0.0f, width, height, scale);
        renderer.endLayerFrame();
        TextureHandle texture = renderer.blurTexture(layer.getTexture(), 16.0f * (1.0f - alpha));
        int color = ColorUtils.withAlpha(-1, 255.0f * alpha);
        renderer.drawLayerTexture(matrix, texture, layer.drawX(), layer.drawY(), layer.drawWidth(), layer.drawHeight(), 0.0f, 1.0f, layer.maxU(), 1.0f - layer.maxV(), color);
        renderer.end();
    }

    protected abstract float getWidth();

    protected boolean isEditorScreen() {
        return class_310.method_1551().field_1755 instanceof class_408 || class_310.method_1551().field_1755 instanceof class_490;
    }

    protected void updateLayout() {
    }

    protected abstract void renderContent(GuiDrawApi var1, Matrix4f var2, float var3, float var4, float var5, float var6, float var7);

    protected final void renderPanelSurface(GuiDrawApi renderer, Matrix4f matrix, float x, float y, float width, float height, float radius, float scale) {
        float scaledRadius = radius * scale;
        if (ThemeManager.getThemeManager().isHudBlurEnabled()) {
            renderer.drawBlurredRoundedRectangle(matrix, x, y, width, height, scaledRadius);
        }
        renderer.drawRoundedRectangle(matrix, x, y, width, height, scaledRadius, ThemeColors.hudBackground());
        renderer.drawRoundedOutline(matrix, x, y, width, height, scaledRadius, scale, ThemeColors.withHoverOverlay(ThemeColors.notificationOutline()));
    }

    public String getName() {
        return this.name;
    }

    protected abstract float getHeight();

    private void configureDefaultAnchor(String name) {
        float anchorX;
        float anchorY = switch (name) {
            case "Watermark" -> {
                anchorX = 0.0f;
                yield 0.0f;
            }
            case "Keybinds" -> {
                anchorX = 1.0f;
                yield 0.0f;
            }
            case "Cooldowns" -> {
                anchorX = 0.5f;
                yield 0.02f;
            }
            case "Effects" -> {
                anchorX = 1.0f;
                yield 0.2f;
            }
            case "Schedules" -> {
                anchorX = 1.0f;
                yield 0.55f;
            }
            case "Staff List" -> {
                anchorX = 0.0f;
                yield 0.38f;
            }
            case "Target HUD" -> {
                anchorX = 0.5f;
                yield 0.28f;
            }
            case "Armor HUD" -> {
                anchorX = 0.5f;
                yield 0.82f;
            }
            case "Inventory HUD" -> {
                anchorX = 0.0f;
                yield 1.0f;
            }
            case "Server Helper" -> {
                anchorX = 0.0f;
                yield 0.66f;
            }
            case "Totem Counter" -> {
                anchorX = 0.5f;
                yield 0.78f;
            }
            default -> {
                anchorX = 0.5f;
                yield 0.5f;
            }
        };
        this.layout.setAnchor(anchorX, anchorY);
    }

    protected boolean isContentVisible() {
        return true;
    }

    protected float centerVertically(float y, float scale, float contentHeight) {
        return this.centerWithinHeight(y, scale, contentHeight, this.getHeight());
    }

    public float getMaximumBlurRadius() {
        return 16.0f;
    }

    public FadeAnimation getFadeAnimation() {
        return this.fadeAnimation;
    }

    private float resolveVisibilityAlpha(boolean visible) {
        float progress = this.fadeAnimation.getProgress();
        if (visible) {
            if (progress < 0.98f) {
                return progress;
            }
            this.fadeAnimation.setProgress(1.0f);
            return 1.0f;
        }
        if (progress > 0.02f) {
            return progress;
        }
        this.fadeAnimation.setProgress(0.0f);
        return 0.0f;
    }

    protected float centerWithinHeight(float y, float scale, float contentHeight, float containerHeight) {
        return y + (containerHeight - contentHeight) / 2.0f * scale;
    }

    public HudElementLayout getLayout() {
        return this.layout;
    }

    public float getLayerPadding() {
        return 32.0f;
    }
}

