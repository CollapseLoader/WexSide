package ru.wexside.render;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.minecraft.class_10789;
import net.minecraft.class_10799;
import net.minecraft.class_290;
import net.minecraft.class_2960;

public final class ClientRenderPipelines {
    private static final VertexFormatElement POSITION_2F = ClientRenderPipelines.element(7, VertexFormatElement.Type.FLOAT, 2);
    private static final VertexFormatElement MESH_POSITION_2F = ClientRenderPipelines.element(8, VertexFormatElement.Type.FLOAT, 2);
    private static final VertexFormatElement MESH_SIZE_2F = ClientRenderPipelines.element(9, VertexFormatElement.Type.FLOAT, 2);
    private static final VertexFormatElement TEXTURE_COORDINATE_2F = ClientRenderPipelines.element(10, VertexFormatElement.Type.FLOAT, 2);
    private static final VertexFormatElement CORNER_RADII_4F = ClientRenderPipelines.element(11, VertexFormatElement.Type.FLOAT, 4);
    private static final VertexFormatElement FILL_COLOR_4UB = ClientRenderPipelines.element(12, VertexFormatElement.Type.UBYTE, 4);
    private static final VertexFormatElement OUTLINE_COLOR_4UB = ClientRenderPipelines.element(13, VertexFormatElement.Type.UBYTE, 4);
    private static final VertexFormatElement OUTLINE_THICKNESS_1F = ClientRenderPipelines.element(14, VertexFormatElement.Type.FLOAT, 1);
    private static final VertexFormatElement EDGE_SOFTNESS_1F = ClientRenderPipelines.element(15, VertexFormatElement.Type.FLOAT, 1);
    private static final VertexFormatElement MSDF_EDGE_1F = ClientRenderPipelines.element(16, VertexFormatElement.Type.FLOAT, 1);
    private static final VertexFormatElement MSDF_TEXT_SIZE_1F = ClientRenderPipelines.element(17, VertexFormatElement.Type.FLOAT, 1);
    private static final VertexFormatElement MSDF_RANGE_1F = ClientRenderPipelines.element(18, VertexFormatElement.Type.FLOAT, 1);
    private static final VertexFormatElement MSDF_ATLAS_SIZE_2F = ClientRenderPipelines.element(19, VertexFormatElement.Type.FLOAT, 2);
    private static final VertexFormatElement TEXTURE_INDEX_1B = ClientRenderPipelines.element(20, VertexFormatElement.Type.BYTE, 1);
    private static final VertexFormatElement DRAW_MODE_1B = ClientRenderPipelines.element(21, VertexFormatElement.Type.BYTE, 1);
    public static final VertexFormat GUI_VERTEX_FORMAT = VertexFormat.builder().add("aPosition", POSITION_2F).add("aMeshPos", MESH_POSITION_2F).add("aMeshSize", MESH_SIZE_2F).add("aTexCoord", TEXTURE_COORDINATE_2F).add("aRadius", CORNER_RADII_4F).add("aColor", FILL_COLOR_4UB).add("aOutlineColor", OUTLINE_COLOR_4UB).add("aThickness", OUTLINE_THICKNESS_1F).add("aSoftness", EDGE_SOFTNESS_1F).add("aMsdfEdge", MSDF_EDGE_1F).add("aMsdfTextSize", MSDF_TEXT_SIZE_1F).add("aMsdfRange", MSDF_RANGE_1F).add("aMsdfAtlasSize", MSDF_ATLAS_SIZE_2F).add("aTexIndex", TEXTURE_INDEX_1B).add("aDrawMode", DRAW_MODE_1B).build();
    public static final RenderPipeline OVERLAY_PRESENT = class_10799.method_67887((RenderPipeline)ClientRenderPipelines.fullscreenPipeline("overlay_present", "dc/present").withSampler("OverlaySampler").withBlend(BlendFunction.TRANSLUCENT).build());
    public static final RenderPipeline MASK_CLEAR = class_10799.method_67887((RenderPipeline)ClientRenderPipelines.fullscreenPipeline("mask_clear", "dc/clear").withoutBlend().build());
    public static final RenderPipeline KAWASE_DOWNSAMPLE = class_10799.method_67887((RenderPipeline)ClientRenderPipelines.fullscreenPipeline("kawase_downsample", "dc/kawase").withFragmentShader(ClientRenderPipelines.id("dc/kawase_down")).withSampler("InSampler").withUniform("KawaseData", class_10789.field_60031).withoutBlend().build());
    public static final RenderPipeline KAWASE_UPSAMPLE = class_10799.method_67887((RenderPipeline)ClientRenderPipelines.fullscreenPipeline("kawase_upsample", "dc/kawase").withFragmentShader(ClientRenderPipelines.id("dc/kawase_up")).withSampler("InSampler").withUniform("KawaseData", class_10789.field_60031).withoutBlend().build());
    public static final RenderPipeline MASK_WRITER = class_10799.method_67887((RenderPipeline)ClientRenderPipelines.guiPipeline("mask_writer").withoutBlend().build());
    public static final RenderPipeline GUI_BATCH = class_10799.method_67887((RenderPipeline)ClientRenderPipelines.guiPipeline("gui_batch").withBlend(BlendFunction.TRANSLUCENT).build());
    public static final RenderPipeline POTION_DECAL = class_10799.method_67887((RenderPipeline)ClientRenderPipelines.fullscreenPipeline("potion_decal", "prediction/potion_decal").withSampler("DepthSampler").withUniform("PotionDecal", class_10789.field_60031).withBlend(BlendFunction.TRANSLUCENT).build());

    private ClientRenderPipelines() {
    }

    private static VertexFormatElement element(int id, VertexFormatElement.Type type, int count) {
        return VertexFormatElement.register((int)id, (int)0, (VertexFormatElement.Type)type, (VertexFormatElement.Usage)VertexFormatElement.Usage.GENERIC, (int)count);
    }

    private static RenderPipeline.Builder fullscreenPipeline(String name, String shader) {
        return RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[0]).withLocation(ClientRenderPipelines.id("pipeline/" + name)).withVertexShader(ClientRenderPipelines.id(shader)).withFragmentShader(ClientRenderPipelines.id(shader)).withVertexFormat(class_290.field_1592, VertexFormat.class_5596.field_27382).withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withDepthWrite(false).withCull(false);
    }

    private static RenderPipeline.Builder guiPipeline(String name) {
        RenderPipeline.Builder builder = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[0]).withLocation(ClientRenderPipelines.id("pipeline/" + name)).withVertexShader(ClientRenderPipelines.id("dc/core_dc")).withFragmentShader(ClientRenderPipelines.id("dc/core_dc")).withUniform("WexGlobals", class_10789.field_60031).withSampler("MaskTex").withVertexFormat(GUI_VERTEX_FORMAT, VertexFormat.class_5596.field_27382).withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withDepthWrite(false).withCull(false);
        for (int textureIndex = 0; textureIndex < 16; ++textureIndex) {
            builder.withSampler("textureSampler[" + textureIndex + "]");
        }
        return builder;
    }

    private static class_2960 id(String path) {
        return class_2960.method_60655((String)"wexside", (String)path);
    }
}

