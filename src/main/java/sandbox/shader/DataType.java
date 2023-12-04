package sandbox.shader;

import org.lwjgl.opengl.GL15;

public enum DataType {
    FLOAT(4, GL15.GL_FLOAT),
    INT(4, GL15.GL_INT),
    UNSIGNED_INT(4, GL15.GL_UNSIGNED_INT),
    SHORT(2, GL15.GL_SHORT),
    UNSIGNED_SHORT(2, GL15.GL_UNSIGNED_SHORT),
    BYTE(1, GL15.GL_BYTE),
    UNSIGNED_BYTE(1, GL15.GL_UNSIGNED_BYTE);

    private final int size;
    private final int glType;

    DataType(int size, int glType) {
        this.size = size;
        this.glType = glType;
    }

    public int getSize() {
        return size;
    }

    public int getGLType() {
        return glType;
    }
}
