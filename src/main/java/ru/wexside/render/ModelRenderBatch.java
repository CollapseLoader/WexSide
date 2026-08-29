package ru.wexside.render;

import java.util.List;
import org.joml.Matrix4f;
import ru.wexside.util.InlineMesh;
import ru.wexside.util.ModelRenderOptions;

public record ModelRenderBatch(
   List<InlineMesh> meshes, Matrix4f[] transforms, int color, int outlineColor, double x, double y, double z, double radius, ModelRenderOptions options
) {
}
