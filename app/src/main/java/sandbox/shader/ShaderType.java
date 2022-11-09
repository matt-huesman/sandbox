package sandbox.shader;

import org.lwjgl.opengl.GL20;

public enum ShaderType {
    VERTEX_SHADER("vertex", "vsh", GL20.GL_VERTEX_SHADER),
    FRAGMENT_SHADER("fragment", "fsh", GL20.GL_FRAGMENT_SHADER);

    private final String type;
    private final String extension;
    private final int glType;

    private ShaderType(String type, String extension, int glType) {
        this.type = type;
        this.extension = extension;
        this.glType = glType;
    }

    public String getType() {
        return type;
    }

    public String getExtension() {
        return extension;
    }

    public int getGlType() {
        return glType;
    }
}
