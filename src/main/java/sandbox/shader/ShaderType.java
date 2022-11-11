package sandbox.shader;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL40;

public enum ShaderType {
    VERTEX_SHADER("vertex", "vsh", GL20.GL_VERTEX_SHADER),
    GEOMETRY_SHADER("geometry", "gsh", GL40.GL_GEOMETRY_SHADER),
    FRAGMENT_SHADER("fragment", "fsh", GL20.GL_FRAGMENT_SHADER);

    private final String type;
    private final String extension;
    private final int glType;

    /*
     * Because we don't want to reload shared shaders across seperate shader programs
     * shaders are stored in for reuse in their type's list
     */

    private ShaderType(String type, String extension, int glType) {
        this.type = type;
        this.extension = extension;
        this.glType = glType;
    }

    public String getExtension() {
        return extension;
    }

    public int getGlType() {
        return glType;
    }

    @Override
    public String toString() {
        return type;
    }
}
