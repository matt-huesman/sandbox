package sandbox.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class VertexDataBuffer {
    private final int vaoId;

    public VertexDataBuffer(float[] vertices, int[] indices) {
        vaoId = GL30.glGenVertexArrays();
        int vboId = GL15.glGenBuffers();
        int eboId = GL15.glGenBuffers();

        bind();

        // Store the vertices array in a float buffer
        FloatBuffer verticesBuffer = MemoryUtil.memAllocFloat(vertices.length);
        verticesBuffer.put(vertices).flip();
        // Push the vertices buffer to the VBO and set as STATIC_DRAW
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_STATIC_DRAW);

        // Store the indices array in an int buffer
        IntBuffer indicesBuffer = MemoryUtil.memAllocInt(indices.length);
        indicesBuffer.put(indices).flip();
        // Push the indices buffer to the EBO and set as STATIC_DRAW
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, eboId);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);

        // Set the vertex attribute pointer and enable it
        GL30.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);
        GL30.glEnableVertexAttribArray(0);

        // Unbind the VAO
        unbind();

        // Unbind the VBO, EBO
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

        // Free unused memory
        MemoryUtil.memFree(verticesBuffer);
        MemoryUtil.memFree(indicesBuffer);

        GL15.glDeleteBuffers(vboId);
        GL15.glDeleteBuffers(eboId);
    }

    public void bind() {
        GL30.glBindVertexArray(vaoId);
    }

    public void unbind() {
        GL30.glBindVertexArray(0);
    }

    public void destroy() {
        GL30.glDeleteVertexArrays(vaoId);
    }
}
