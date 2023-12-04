package sandbox.shader;

import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.system.MemoryUtil.memAllocInt;

public class ShaderProgram {
    private final int programId;
    private final String name;

    // The last frame this shader program was used
    private long lastFrame;

    private final Map<String, Attribute> attributes;
    private final Map<String, Uniform> uniforms;

    public ShaderProgram(String name) throws Exception {
        this.name = name;

        programId = GL20.glCreateProgram();
        if (programId == 0) {
            throw new Exception("Failed to create shader program: " + name);
        }

        attributes = new HashMap<String, Attribute>();
        uniforms = new HashMap<String, Uniform>();
    }

    public ShaderProgram attach(Shader... shaders) {
        for (Shader shader : shaders) {
            GL20.glAttachShader(programId, shader.getShaderId());
        }
        return this;
    }

    public ShaderProgram attachIf(boolean condition, Shader... shaders) {
        if (condition) {
            attach(shaders);
        }
        return this;
    }

    public void link() {
        GL20.glLinkProgram(programId);
        if (GL20.glGetProgrami(programId, GL20.GL_LINK_STATUS) == 0) {
            throw new RuntimeException("Error linking Shaders in program " + name + ": " + GL20.glGetProgramInfoLog(programId, 1024));
        }
    }

    public void loadAttributes() {
        bind(0);

        IntBuffer size = memAllocInt(1);
        IntBuffer type = memAllocInt(1);

        int attribCount = GL20.glGetProgrami(programId, GL20.GL_ACTIVE_ATTRIBUTES);
        int totalSizeOffset = 0;
        Map<String, Integer> attributeOffsetList = new HashMap<String, Integer>();

        // Get the attribute name, location, type, and size and add it to the attributes
        for (int i = 0; i < attribCount; i++) {
            String name = GL20.glGetActiveAttrib(programId, i, 16, size, type);

            int attribLocation = GL20.glGetAttribLocation(programId, name);
            Attribute attrib = new Attribute(attribLocation, Attribute.AttribType.fromName(name));

            attributeOffsetList.put(name, totalSizeOffset);
            totalSizeOffset += attrib.getType().getTotalSize();

            attributes.put(name, attrib);
        }

        for (String name : attributes.keySet()) {
            Attribute attrib = attributes.get(name);
            GL20.glVertexAttribPointer(
                attrib.getLocation(),
                attrib.getType().getSize(),
                attrib.getType().getType().getGLType(),
                false,
                totalSizeOffset,
                attributeOffsetList.get(name)
            );
            GL20.glEnableVertexAttribArray(attrib.getLocation());
        }

        MemoryUtil.memFree(size);
        MemoryUtil.memFree(type);

        unbind();
    }

    public void loadUniforms() {
        bind(0);

        IntBuffer size = memAllocInt(1);
        IntBuffer type = memAllocInt(1);

        int uniformCount = GL20.glGetProgrami(programId, GL20.GL_ACTIVE_UNIFORMS);
        // Get the uniform name, location, type, and size and add it to the uniforms
        for (int i = 0; i < uniformCount; i++) {
            String name = GL20.glGetActiveUniform(programId, i, 16, size, type);
            uniforms.put(name, new Uniform(GL20.glGetUniformLocation(programId, name), Uniform.UniformType.fromGLType(type.get())));
        }

        MemoryUtil.memFree(size);
        MemoryUtil.memFree(type);

        unbind();
    }

    public void bind(long frame) {
        lastFrame = frame;
        GL20.glUseProgram(programId);
    }

    public static void unbind() {
        GL20.glUseProgram(0);
    }

    public void setUniform(String uniformName, Object value) {
        Uniform uniform = uniforms.get(uniformName);
        if (uniform == null) {
            throw new NullPointerException("Uniform doesn't exist in program " + name + ": " + uniformName);
        }
        uniform.setValue(value);
    }

    public long getLastFrame() {
        return lastFrame;
    }

    public void destroy() {
        GL20.glDeleteProgram(programId);
    }
}
