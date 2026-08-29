package ru.wexside.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import net.minecraft.class_1297;
import net.minecraft.class_1304;
import net.minecraft.class_1308;
import net.minecraft.class_1309;
import net.minecraft.class_1429;
import net.minecraft.class_1657;
import net.minecraft.class_1675;
import net.minecraft.class_1799;
import net.minecraft.class_1839;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_266;
import net.minecraft.class_268;
import net.minecraft.class_269;
import net.minecraft.class_310;
import net.minecraft.class_3966;
import net.minecraft.class_3988;
import net.minecraft.class_408;
import net.minecraft.class_742;
import net.minecraft.class_746;
import net.minecraft.class_8646;
import net.minecraft.class_8685;
import net.minecraft.class_9013;
import net.minecraft.class_9015;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import ru.wexside.WexSideClient;
import ru.wexside.misc.BakedIconEntry;
import ru.wexside.misc.FontRegistry;
import ru.wexside.misc.FrameInterpolator;
import ru.wexside.misc.PreparedLayer;
import ru.wexside.misc.TextureHandle;
import ru.wexside.misc.ThemeColors;
import ru.wexside.module.Module;
import ru.wexside.module.combat.AttackAuraModule;
import ru.wexside.render.HudIconRenderer;
import ru.wexside.render.IconAtlasEntry;
import ru.wexside.render.ItemIconCache;
import ru.wexside.render.PlayerHeadRenderCache;
import ru.wexside.render.RenderFrameClock;
import ru.wexside.util.AbstractHudElement;
import ru.wexside.util.ColorUtils;
import ru.wexside.util.GuiDrawApi;

public final class TargetHUD
extends AbstractHudElement {
    private final int slot;
    private float value4;
    private final BooleanSupplier booleanSupplier;
    private final Map<UUID, IconAtlasEntry> map2;
    private AttackAuraModule attackAuraModule;
    private class_1309 entity2;
    private long longType;
    private int slot2 = -1;
    static final int slot3 = 64;
    private boolean enabled;
    private class_1309 entity3;
    private float value15;
    private final int slot4 = ColorUtils.rgba(0, 0, 0, 40);
    private class_1309 entity4;
    private int slot5 = -1;
    private float value18;
    private final ItemIconCache itemIconCache = new ItemIconCache();
    private float value25;
    private int slot6;
    private final int slot7;
    private final BooleanSupplier booleanSupplier2;

    public TargetHUD(BooleanSupplier booleanSupplier, BooleanSupplier booleanSupplier2, BooleanSupplier booleanSupplier3) {
        super("Target HUD", booleanSupplier);
        this.slot7 = 4;
        this.slot = 2;
        this.map2 = new PlayerHeadRenderCache(64);
        this.booleanSupplier = booleanSupplier2;
        this.booleanSupplier2 = booleanSupplier3;
    }

    public void clearCaches() {
        this.itemIconCache.update3();
        for (IconAtlasEntry renderPipeline10 : this.map2.values()) {
            renderPipeline10.update2();
        }
        this.map2.clear();
    }

    @Override
    protected float getWidth() {
        return 118.5f;
    }

    @Override
    protected void updateLayout() {
        class_1309 entity5 = this.getCurrentTarget();
        boolean bl = false;
        if (entity5 != null) {
            this.entity2 = entity5;
            this.value25 = this.value15;
            this.value15 = this.process5(entity5.field_6235);
            float f = TargetHUD.healthFraction(entity5);
            this.value18 = FrameInterpolator.lerpTowards(this.value18, f, 20.0f);
            bl = entity5.method_6115() && this.process17(entity5);
        }
        this.enabled = bl;
        this.value4 = FrameInterpolator.lerpTowards(this.value4, bl ? 1.0f : 0.0f, 15.0f);
        this.update2();
    }

    @Override
    protected void renderContent(GuiDrawApi drawApi, Matrix4f matrix4f, float f, float f2, float f3, float f4, float f5) {
        if (this.entity2 == null) {
            return;
        }
        this.renderPanelSurface(drawApi, matrix4f, f, f2, f3, f4, 8.0f, f5);
        drawApi.beginStencil(1);
        drawApi.drawRoundedRectangle(matrix4f, f, f2, f3, f4, 8.0f * f5, -1);
        drawApi.applyStencilMask(1);
        this.process9(drawApi, matrix4f, f, f2, f5);
        this.process8(drawApi, matrix4f, f, f2, f5);
        this.process4(drawApi, matrix4f, f, f2, f5);
        this.process18(drawApi, matrix4f, f, f2, f5);
        this.process11(drawApi, matrix4f, f, f2, f5);
        this.process16(drawApi, matrix4f, f, f2, f5);
        drawApi.endStencil();
    }

    private void update2() {
        class_742 player;
        IconAtlasEntry headTexture;
        if (this.entity2 == null) {
            return;
        }
        float f = class_310.method_1551().method_22683().method_4495();
        ArrayList<BakedIconEntry> arrayList = new ArrayList<BakedIconEntry>();
        class_1309 livingEntity = this.entity2;
        if (livingEntity instanceof class_742 && (headTexture = this.map2.computeIfAbsent((player = (class_742)livingEntity).method_5667(), ignored -> new IconAtlasEntry(true))).process(f)) {
            class_8685 skin = player.method_52814();
            arrayList.add(new BakedIconEntry(headTexture, (context, x, y, size) -> HudIconRenderer.drawPlayerHead(context, skin, x, y, size)));
        }
        this.itemIconCache.update2();
        for (class_1799 equipment : this.getList()) {
            if (equipment.method_7960()) continue;
            this.itemIconCache.process(equipment);
        }
        class_1799 activeItem = this.entity2.method_6030();
        if (this.enabled && !activeItem.method_7960()) {
            this.itemIconCache.process(activeItem);
        }
        this.itemIconCache.process2(f, arrayList);
        if (!arrayList.isEmpty()) {
            WexSideClient.getRenderPipeline2().setList(arrayList);
        }
        this.itemIconCache.update();
    }

    @Override
    protected float getHeight() {
        return 36.0f;
    }

    @Override
    protected boolean isContentVisible() {
        if (this.getCurrentTarget() == null) {
            this.slot2 = -1;
            return false;
        }
        int n = this.getIntType();
        if (this.slot2 < 0) {
            this.slot2 = n;
        }
        return n - this.slot2 >= 2;
    }

    private class_1309 getLivingEntity() {
        if (this.attackAuraModule == null) {
            for (Module module : WexSideClient.getInstance().getModuleManager().getModules()) {
                AttackAuraModule attackAuraModule;
                if (!(module instanceof AttackAuraModule)) continue;
                this.attackAuraModule = attackAuraModule = (AttackAuraModule)module;
                break;
            }
        }
        return this.attackAuraModule != null ? this.attackAuraModule.getLivingEntity() : null;
    }

    private IconAtlasEntry process2(class_1309 entity5) {
        if (!(entity5 instanceof class_1657)) {
            return null;
        }
        class_1657 player2 = (class_1657)entity5;
        return this.map2.get(player2.method_5667());
    }

    private class_1309 process3(float f) {
        class_1297 iIiiiilIiI2;
        class_238 box;
        class_243 vec;
        class_243 vec2;
        class_310 mc = class_310.method_1551();
        class_746 player3 = mc.field_1724;
        if (player3 == null || mc.field_1687 == null) {
            return null;
        }
        float f2 = mc.method_61966().method_60637(false);
        class_243 vec3 = player3.method_5836(f2);
        class_3966 iIIIllIilI2 = class_1675.method_18075((class_1297)player3, (class_243)vec3, (class_243)(vec2 = vec3.method_1019((vec = player3.method_5828(f2)).method_1021((double)f))), (class_238)(box = player3.method_5829().method_18804(vec.method_1021((double)f)).method_1014(1.0)), entity -> entity instanceof class_1309 && entity != player3 && entity.method_5805(), (double)(f * f));
        if (iIIIllIilI2 != null && (iIiiiilIiI2 = iIIIllIilI2.method_17782()) instanceof class_1309) {
            class_1309 entity5 = (class_1309)iIiiiilIiI2;
            return entity5;
        }
        return null;
    }

    private void process4(GuiDrawApi drawApi, Matrix4f matrix4f, float f, float f2, float f3) {
        String string2;
        int n = ThemeColors.accent();
        String string = !(this.entity2 instanceof class_1657) ? this.process10(this.entity2) : (TargetHUD.process12(string2 = this.process7(this.entity2)) ? string2 : "\u0418\u0433\u0440\u043e\u043a");
        FontRegistry.font6.process2(matrix4f, drawApi, string, f + 35.0f * f3, f2 + 6.25f * f3, 5.0f * f3, n);
    }

    private float process5(int n) {
        int n2 = 10 - n;
        if (n2 == 10) {
            return 0.0f;
        }
        float f = (float)n2 / 10.0f * 2.0f;
        if (f > 1.0f) {
            f = 2.0f - f;
        }
        return f;
    }

    private void process6(GuiDrawApi drawApi, Matrix4f matrix4f, float f, float f2, float f3, float f4, int n) {
        drawApi.drawRoundedShadow(matrix4f, f, f2, f3, f3, 6.0f * f4, 5.0f * f4, this.slot4);
        IconAtlasEntry renderPipeline10 = this.process2(this.entity2);
        if (renderPipeline10 != null && renderPipeline10.isActive()) {
            int n2 = drawApi.bindTexture(renderPipeline10.getIntType4(), renderPipeline10.getIntType(), renderPipeline10.getIntType());
            drawApi.drawRoundedTexture(matrix4f, f, f2, f3, f3, 0.0f, 1.0f, 1.0f, 0.0f, 6.0f * f4, n2, n);
        } else {
            drawApi.drawRoundedRectangle(matrix4f, f, f2, f3, f3, 6.0f * f4, n);
        }
    }

    private String process7(class_1309 entity5) {
        class_268 team2 = entity5.method_5781();
        if (team2 == null) {
            return "";
        }
        return team2.method_1144().getString().replaceAll("\u00a7.?", "").trim();
    }

    private void process8(GuiDrawApi drawApi, Matrix4f matrix4f, float f, float f2, float f3) {
        if (this.value4 < 0.001f || this.entity2 == null || !this.enabled) {
            return;
        }
        class_1799 stack = this.entity2.method_6030();
        if (stack.method_7960()) {
            return;
        }
        int n = (int)Math.clamp(this.value4 * 255.0f, 0.0f, 255.0f);
        int n2 = ColorUtils.rgba(255, 255, 255, n);
        float f4 = 12.0f * f3;
        float f5 = f + 12.0f * f3;
        float f6 = f2 + 8.0f * f3;
        this.itemIconCache.process4(drawApi, matrix4f, this.itemIconCache.process(stack), f5, f6, f4, n2);
        String string = String.format(Locale.ROOT, "%.2f\u0441", Float.valueOf(this.process15(stack)));
        float f7 = FontRegistry.font6.process3(string, 5.0f);
        float f8 = f + 18.0f * f3 - f7 * f3 / 2.0f;
        float f9 = f2 + 21.0f * f3;
        FontRegistry.font6.process2(matrix4f, drawApi, string, f8, f9, 5.0f * f3, n2);
    }

    private List<class_1799> getList() {
        ArrayList<class_1799> arrayList = new ArrayList<class_1799>(6);
        arrayList.add(this.entity2.method_6118(class_1304.field_6169));
        arrayList.add(this.entity2.method_6118(class_1304.field_6174));
        arrayList.add(this.entity2.method_6118(class_1304.field_6172));
        arrayList.add(this.entity2.method_6118(class_1304.field_6166));
        arrayList.add(this.entity2.method_6047());
        arrayList.add(this.entity2.method_6079());
        return arrayList;
    }

    private void process9(GuiDrawApi drawApi, Matrix4f matrix4f, float f, float f2, float f3) {
        float f4 = f + 5.0f * f3;
        float f5 = f2 + 5.0f * f3;
        float f6 = 26.0f * f3;
        float f7 = class_310.method_1551().method_61966().method_60637(false);
        float f8 = this.value25 + (this.value15 - this.value25) * f7;
        float f9 = this.booleanSupplier2.getAsBoolean() ? f8 * 0.5f : 0.0f;
        int n = Math.round((1.0f - f9) * 255.0f);
        int n2 = ColorUtils.rgba(255, n, n, 255);
        if (this.value4 < 0.001f) {
            this.process6(drawApi, matrix4f, f4, f5, f6, f3, n2);
        } else {
            this.process13(drawApi, matrix4f, f4, f5, f6, f3, n2);
        }
    }

    private String process10(class_1309 entity5) {
        if (entity5 instanceof class_3988) {
            return "\u0416\u0438\u0442\u0435\u043b\u044c";
        }
        if (entity5 instanceof class_1429) {
            return "\u0416\u0438\u0432\u043e\u0442\u043d\u043e\u0435";
        }
        if (entity5 instanceof class_1308) {
            return "\u041c\u043e\u0431";
        }
        return "\u0421\u0443\u0449\u0435\u0441\u0442\u0432\u043e";
    }

    private void process11(GuiDrawApi drawApi, Matrix4f matrix4f, float f, float f2, float f3) {
        List<class_1799> list = this.getList();
        float f4 = f + 35.0f * f3;
        float f5 = 6.0f * f3;
        float f6 = f2 + 23.25f * f3;
        for (int i = 0; i < list.size(); ++i) {
            class_1799 stack = list.get(i);
            if (!stack.method_7960()) {
                this.itemIconCache.process4(drawApi, matrix4f, this.itemIconCache.process(stack), f4, f6, f5, -1);
            } else if (i < 4) {
                drawApi.drawRoundedRectangle(matrix4f, f4, f6, f5, f5, 4.0f * f3, ThemeColors.visualizerSlot());
            }
            f4 += 8.5f * f3;
        }
    }

    private static boolean process12(String string) {
        for (int i = 0; i < string.length(); ++i) {
            if (!Character.isLetterOrDigit(string.charAt(i))) continue;
            return true;
        }
        return false;
    }

    private void process13(GuiDrawApi drawApi, Matrix4f matrix4f, float f, float f2, float f3, float f4, int n) {
        float f5 = 16.0f * f4;
        drawApi.begin();
        PreparedLayer preparedLayer = drawApi.prepareLayer(matrix4f, f, f2, f3, f3, f5);
        drawApi.beginLayerFrame(preparedLayer.getTexture());
        Matrix4f matrix4f2 = new Matrix4f((Matrix4fc)preparedLayer.getContentMatrix()).translate(preparedLayer.contentX(), preparedLayer.contentY(), 0.0f);
        this.process6(drawApi, matrix4f2, 0.0f, 0.0f, f3, f4, n);
        drawApi.endLayerFrame();
        TextureHandle texture = drawApi.blurTexture(preparedLayer.getTexture(), this.value4 * 4.0f);
        drawApi.drawLayerTexture(matrix4f, texture, preparedLayer.drawX(), preparedLayer.drawY(), preparedLayer.drawWidth(), preparedLayer.drawHeight(), 0.0f, 1.0f, preparedLayer.maxU(), 1.0f - preparedLayer.maxV(), -1);
        drawApi.end();
    }

    private String process14(String string, float f, float f2) {
        if (FontRegistry.font2.process3(string, f) <= f2) {
            return string;
        }
        String string2 = "\u2026";
        float f3 = FontRegistry.font2.process3(string2, f);
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < string.length(); ++i) {
            char c = string.charAt(i);
            String string3 = stringBuilder.toString();
            String string4 = string3 + c;
            if (FontRegistry.font2.process3(string4, f) + f3 > f2) break;
            stringBuilder.append(string.charAt(i));
        }
        String string5 = string2;
        String string6 = String.valueOf(stringBuilder);
        return string6 + string5;
    }

    private class_1309 resolveTarget() {
        class_1309 entity5 = this.getLivingEntity();
        if (entity5 != null) {
            return entity5;
        }
        class_1309 raycastTarget = this.booleanSupplier.getAsBoolean() ? this.process3(50.0f) : null;
        class_1309 livingEntity = raycastTarget;
        if (raycastTarget != null) {
            return raycastTarget;
        }
        class_310 mc = class_310.method_1551();
        if (mc.field_1755 instanceof class_408 && mc.field_1724 != null) {
            return mc.field_1724;
        }
        return null;
    }

    private float process15(class_1799 stack) {
        int n = this.entity2.method_6048();
        int n2 = switch (stack.method_7976()) {
            case class_1839.field_8953, class_1839.field_8951, class_1839.field_8947 -> this.entity2.method_6014();
            case class_1839.field_8950, class_1839.field_8946 -> 20 - n;
            case class_1839.field_8949 -> 25 - n;
            case class_1839.field_39058, class_1839.field_42717 -> 10 - n;
            default -> n;
        };
        return Math.max(0.0f, (float)n2 / 20.0f);
    }

    private void process16(GuiDrawApi drawApi, Matrix4f matrix4f, float f, float f2, float f3) {
        int n;
        float f4 = f + 0.5f + 100.5f * f3;
        float f5 = f2 + 0.5f + 18.0f * f3;
        float f6 = 10.0f * f3;
        float f7 = 2.5f * f3;
        drawApi.drawCircle(matrix4f, f4, f5, 0.0f, 360.0f, f7, f6, ThemeColors.separatorHover());
        float f8 = this.value18 * 360.0f;
        if (f8 > 0.0f) {
            drawApi.drawCircle(matrix4f, f4, f5, 0.0f, f8, f7, f6, ThemeColors.accent());
        }
        boolean bl = (n = TargetHUD.healthValue(this.entity2)) == 0 || n == 1000;
        String string = bl ? "?" : String.valueOf(n);
        float f9 = FontRegistry.font6.process3(string, 6.5f);
        float f10 = FontRegistry.font6.process4(string, 6.5f);
        FontRegistry.font6.process2(matrix4f, drawApi, string, f4 - f9 * f3 / 2.0f, f5 - f10 * f3 / 2.0f, 6.5f * f3, ThemeColors.hudTextPrimary());
    }

    private class_1309 getCurrentTarget() {
        int n = RenderFrameClock.currentFrame();
        if (n != this.slot5) {
            this.slot5 = n;
            this.entity3 = this.resolveTarget();
        }
        return this.entity3;
    }

    private boolean process17(class_1309 entity5) {
        int n = entity5.method_6014();
        long l = System.currentTimeMillis();
        if (entity5 != this.entity4 || n != this.slot6) {
            this.entity4 = entity5;
            this.slot6 = n;
            this.longType = l;
            return true;
        }
        return l - this.longType < 750L;
    }

    private void process18(GuiDrawApi drawApi, Matrix4f matrix4f, float f, float f2, float f3) {
        String string = this.entity2.method_5477().getString();
        float f4 = 55.5f;
        String string2 = this.process14(string, 7.5f, f4);
        FontRegistry.font2.process2(matrix4f, drawApi, string2, f + 35.0f * f3, f2 + 12.25f * f3, 7.5f * f3, ThemeColors.hudTextPrimary());
    }

    private int getIntType() {
        class_746 player3 = class_310.method_1551().field_1724;
        return player3 != null ? player3.field_6012 : 0;
    }

    private static int healthValue(class_1309 entity) {
        class_310 client = class_310.method_1551();
        if (client.field_1687 != null) {
            class_9013 score;
            class_269 scoreboard = client.field_1687.method_8428();
            class_266 objective = scoreboard.method_1189(class_8646.field_45158);
            class_9013 class_90132 = score = objective == null ? null : scoreboard.method_55430((class_9015)entity, objective);
            if (score != null) {
                return score.method_55397();
            }
        }
        return Math.round(entity.method_6032());
    }

    private static float healthFraction(class_1309 entity) {
        float maximum = entity.method_6063();
        if (maximum <= 0.0f) {
            return 0.0f;
        }
        return Math.clamp((float)TargetHUD.healthValue(entity) / maximum, 0.0f, 1.0f);
    }
}

