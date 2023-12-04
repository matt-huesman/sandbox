package sandbox.shader;

import org.joml.*;
import org.lwjgl.opengl.GL20;

public class Uniform {
    private final int uniformLocation;
    private final UniformType type;

    public Uniform(int uniformLocation, UniformType type) {
        this.uniformLocation = uniformLocation;
        this.type = type;
    }

    public <T> void setValue(T t) {
        // Check if the uniform location is valid
        if (uniformLocation == -1) {
            return;
        }

        switch (type) {
            case FLOAT:
                GL20.glUniform1f(uniformLocation, (Float) t);
                break;
            case VEC2:
                GL20.glUniform2f(uniformLocation, ((Vector2f) t).x, ((Vector2f) t).y);
                break;
            case VEC3:
                GL20.glUniform3f(uniformLocation, ((Vector3f) t).x, ((Vector3f) t).y, ((Vector3f) t).z);
                break;
            case VEC4:
                GL20.glUniform4f(uniformLocation, ((Vector4f) t).x, ((Vector4f) t).y, ((Vector4f) t).z, ((Vector4f) t).w);
                break;
            case MAT3:
                GL20.glUniformMatrix3fv(uniformLocation, false, ((Matrix3f) t).get(new float[9]));
                break;
            case MAT4:
                GL20.glUniformMatrix4fv(uniformLocation, false, ((Matrix4f) t).get(new float[16]));
                break;
        }
    }

    public enum UniformType {
        FLOAT(GL20.GL_FLOAT),
        VEC2(GL20.GL_FLOAT_VEC2),
        VEC3(GL20.GL_FLOAT_VEC3),
        VEC4(GL20.GL_FLOAT_VEC4),
        MAT3(GL20.GL_FLOAT_MAT3),
        MAT4(GL20.GL_FLOAT_MAT4);

        private final int glType;

        UniformType(int glType) {
            this.glType = glType;
        }

        public static UniformType fromGLType(int glType) {
            for (UniformType type : values()) {
                if (type.glType == glType) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Invalid uniform type: " + glType);
        }
    }
}
