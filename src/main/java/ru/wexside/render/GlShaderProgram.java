package ru.wexside.render;

import java.util.function.IntConsumer;
import org.lwjgl.opengl.GL33;
import ru.wexside.misc.BindableResource;
import ru.wexside.misc.ResourceData;
import ru.wexside.misc.ShaderIncludeProcessor;
import ru.wexside.misc.ShaderUniformWriter;

public class GlShaderProgram
implements BindableResource {
    private static final IntConsumer NO_OP_LINK_CALLBACK = ignored -> {};
    private Integer programId;
    private final ResourceData vertexShaderSource;
    private IntConsumer linkCallbacks = NO_OP_LINK_CALLBACK;
    private final ResourceData fragmentShaderSource;

    public GlShaderProgram(ResourceData fragmentShaderSource, ResourceData vertexShaderSource) {
        this.fragmentShaderSource = fragmentShaderSource;
        this.vertexShaderSource = vertexShaderSource;
    }

    @Override
    public void bind() {
        GL33.glUseProgram((int)this.getOrCreateProgram());
    }

    public ShaderUniformWriter registerUniform(String name) {
        ShaderUniformWriter uniform = new ShaderUniformWriter(name);
        this.addLinkCallback(uniform.getIntConsumer());
        return uniform;
    }

    private void addLinkCallback(IntConsumer callback) {
        IntConsumer previousCallbacks = this.linkCallbacks;
        this.linkCallbacks = program -> {
            previousCallbacks.accept(program);
            callback.accept(program);
        };
    }

    private int compileShader(int shaderType, ResourceData sourceResource) {
        int shaderId = GL33.glCreateShader((int)shaderType);
        String source = sourceResource.readUtf8();
        String includeDirectory = "/";
        String path = sourceResource.getPath();
        if (path != null) {
            int separator = path.lastIndexOf(47);
            includeDirectory = separator > 0 ? path.substring(0, separator) : "/";
        }
        String processedSource = ShaderIncludeProcessor.preprocess(source, includeDirectory, Thread.currentThread().getContextClassLoader());
        GL33.glShaderSource((int)shaderId, (CharSequence)processedSource);
        GL33.glCompileShader((int)shaderId);
        if (GL33.glGetShaderi((int)shaderId, (int)35713) == 0) {
            String log = GL33.glGetShaderInfoLog((int)shaderId);
            GL33.glDeleteShader((int)shaderId);
            throw new IllegalStateException("Couldn't compile shader: " + log);
        }
        return shaderId;
    }

    public void unbind() {
        GL33.glUseProgram((int)0);
    }

    public void close() {
        if (this.programId != null) {
            GL33.glDeleteProgram((int)this.programId);
            this.programId = null;
        }
    }

    private int getOrCreateProgram() {
        if (this.programId != null) {
            return this.programId;
        }
        this.programId = this.linkProgram();
        return this.programId;
    }

    private int linkProgram() {
        int program = GL33.glCreateProgram();
        int vertexShader = this.compileShader(35633, this.vertexShaderSource);
        int fragmentShader = this.compileShader(35632, this.fragmentShaderSource);
        GL33.glAttachShader((int)program, (int)vertexShader);
        GL33.glAttachShader((int)program, (int)fragmentShader);
        GL33.glLinkProgram((int)program);
        if (GL33.glGetProgrami((int)program, (int)35714) == 0) {
            String log = GL33.glGetProgramInfoLog((int)program);
            GL33.glDeleteShader((int)vertexShader);
            GL33.glDeleteShader((int)fragmentShader);
            GL33.glDeleteProgram((int)program);
            throw new IllegalStateException("Could not link program: " + log);
        }
        GL33.glDeleteShader((int)vertexShader);
        GL33.glDeleteShader((int)fragmentShader);
        this.notifyLinked(program);
        return program;
    }

    private void notifyLinked(int program) {
        this.linkCallbacks.accept(program);
    }

    public int getProgramId() {
        return this.getOrCreateProgram();
    }
}

