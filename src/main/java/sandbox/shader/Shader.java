package sandbox.shader;

import org.lwjgl.opengl.GL20;

public class Shader {
    private final int shaderId;
    private final String name;
    private final ShaderType type;

    public Shader(String name, ShaderType type) {
        this.name = name;
        this.type = type;

        shaderId = GL20.glCreateShader(type.getGlType());
        if (shaderId == 0) {
            throw new RuntimeException("Failed to create shader: " + name + " of type: " + type + "!");
        }
    }

    public Shader buildShader(String shaderSource) {
        GL20.glShaderSource(shaderId, shaderSource);
        GL20.glCompileShader(shaderId);

        if (GL20.glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS) == 0) {
            throw new RuntimeException(
                "Error compiling shader: " + name + " of type: " + type + "!\n" + GL20.glGetShaderInfoLog(shaderId, 1024)
            );
        }

        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Shader) {
            return name.equals(((Shader) o).name) && type == ((Shader) o).type;
        }
        return false;
    }

    public int getShaderId() {
        return shaderId;
    }

    public String getName() {
        return name;
    }

    public ShaderType getType() {
        return type;
    }    

    public void destroy() {
        GL20.glDeleteShader(shaderId);
    }
}
