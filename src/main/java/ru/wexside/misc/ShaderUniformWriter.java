package ru.wexside.misc;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.function.IntConsumer;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL33;

public class ShaderUniformWriter {
    private int location;
    private static final FloatBuffer MATRIX_BUFFER = BufferUtils.createFloatBuffer((int)16);
    private final String name;

    public ShaderUniformWriter(String name) {
        this.name = name;
    }

    public void set(float x, float y, float z, float w) {
        GL33.glUniform4f((int)this.location, (float)x, (float)y, (float)z, (float)w);
    }

    public IntConsumer getIntConsumer() {
        return this::resolveLocation;
    }

    public void set(float value) {
        GL33.glUniform1f((int)this.location, (float)value);
    }

    public void set(int value) {
        GL33.glUniform1i((int)this.location, (int)value);
    }

    public void set(IntBuffer values) {
        GL33.glUniform1iv((int)this.location, (IntBuffer)values);
    }

    public void set(Matrix4f matrix) {
        GL33.glUniformMatrix4fv((int)this.location, (boolean)false, (FloatBuffer)matrix.get(MATRIX_BUFFER));
    }

    public void set(float x, float y) {
        GL33.glUniform2f((int)this.location, (float)x, (float)y);
    }

    public void set(FloatBuffer values) {
        GL33.glUniform1fv((int)this.location, (FloatBuffer)values);
    }

    public void set(float x, float y, float z) {
        GL33.glUniform3f((int)this.location, (float)x, (float)y, (float)z);
    }

    private void resolveLocation(int programId) {
        this.location = GL33.glGetUniformLocation((int)programId, (CharSequence)this.name);
    }
}

