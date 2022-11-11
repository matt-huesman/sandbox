package sandbox;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

public class Model {
    private final int vao;
    private final List<Integer> vbos;

    public Model(MeshData meshData) {
        MemoryStack stack = MemoryStack.stackPush();

        // Generates a new VAO for holding an array of VBOs
        vao = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vao);

        vbos = new ArrayList<Integer>();

        vbos.add(generateVBO(0, 3, new float[] { 0.0f, 0.0f, 0.0f }));
    }

    private int generateVBO(int index, int size, float[] data) {
        // Create a new VBO
        int vbo = GL15.glGenBuffers();
        
        try (MemoryStack stack = MemoryStack.stackPush()) {
            // Fetch vertex position values and store them in a float buffer on the stack
            FloatBuffer dataBuffer = stack.callocFloat(data.length);
            dataBuffer.put(0, data);
            // Bind VBO in preperation for inserting data
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
            // Insert data into VBO
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vbo, GL15.GL_STATIC_DRAW);
            // Prepare VAO at index 0
            GL30.glEnableVertexAttribArray(index);
            // Insert current VBO into VAO at index 0
            GL30.glVertexAttribPointer(index, size, GL11.GL_FLOAT, false, 0, 0);
            // Disable VAO and then the VBO
            GL30.glDisableVertexAttribArray(index);
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        } catch(Exception e) {
            e.printStackTrace();
        }

        return vbo;
    }
}
