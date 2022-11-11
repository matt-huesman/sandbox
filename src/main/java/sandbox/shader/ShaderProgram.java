package sandbox.shader;

import org.lwjgl.opengl.GL20;

public class ShaderProgram {
    private final int programId;
    private final String name;

    public ShaderProgram(String name) throws Exception {
        this.name = name;

        programId = GL20.glCreateProgram();
        if (programId == 0) {
            throw new Exception("Failed to create shader program!");
        }
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

    public ShaderProgram link() throws Exception {
        GL20.glLinkProgram(programId);
        if (GL20.glGetProgrami(programId, GL20.GL_LINK_STATUS) == 0) {
            throw new RuntimeException("Error linking Shaders: " + GL20.glGetProgramInfoLog(programId, 1024));
        }
        return this;
    }

    public void bind() {
        GL20.glUseProgram(programId);
    }

    public static void unbind() {
        GL20.glUseProgram(0);
    }

    public String getProgramName() {
        return name;
    }
}
