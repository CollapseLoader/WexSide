package ru.wexside.render;

public record GlStateSnapshot(
   boolean blendEnabled,
   boolean cullEnabled,
   boolean depthTestEnabled,
   boolean depthWriteEnabled,
   boolean scissorEnabled,
   boolean customModeEnabled,
   boolean multisampleEnabled,
   boolean polygonSmoothEnabled,
   boolean lineSmoothEnabled,
   boolean sampleAlphaToCoverageEnabled,
   int blendSourceRgb,
   int blendDestinationRgb,
   int blendSourceAlpha,
   int blendDestinationAlpha,
   float lineWidth,
   int lineSmoothHint,
   int scissorX,
   int scissorY,
   int scissorWidth,
   int scissorHeight
) {
}
