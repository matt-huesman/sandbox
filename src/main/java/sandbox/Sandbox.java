package sandbox;

import java.io.IOException;
import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import sandbox.shader.ShaderManager;
import sandbox.shader.ShaderProgram;

public class Sandbox {
    private final Window window;

    private final ShaderManager shaderManager;

    private boolean running = true;

    /**
     * Render engine:
     * 
     * Render manager holds all object renderers
     *  - Terrain renderer (static objects)
     *  - Entity renderer (dynamic objects)
     *  - Player renderer (HUD & player model)
     * 
     * Game Loading Phase
     *  1. Load shaders, textures, models, and material / entity metadata
     *  2. Associate textures, models, and metadata into materials / entities
     * 
     * SuperChunk Background Loading
     *  3. Load world file containing metadata, chunk changes, saved entities, and chunk changes
     *  4. Build chunk instances from chunk data within the specified chunk loading range
     * 
     * 
     * 8. World filters loaded chunks by visibility and builds verticies to vbo and vao
     * 9. World renderer takes vao objects from world and renders them with generic shader
     */

    private final float[] vertices = {
        -0.5f, -0.5f, 0.0f,
         0.5f, -0.5f, 0.0f,
         0.0f,  0.5f, 0.0f
    };

    // Used for passing application arguments and instantiating
    public Sandbox(String[] args) {
        window = new Window(800, 600, "Hello World!");

        shaderManager = new ShaderManager();
    }

    // Used for initial program logic
    public Sandbox init() {
        try {
            window.init();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            stop();
        }

        try {
            shaderManager.loadShaderProgram("basic");
        } catch (Exception e) {
            e.printStackTrace();
        }

        gameLoop();

        return this;
    }

    private void gameLoop() {
        int vao = GL30.glGenVertexArrays();
        int vbo = GL15.glGenBuffers();

        GL30.glBindVertexArray(vao);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        FloatBuffer verticesBuffer = MemoryUtil.memAllocFloat(vertices.length);
        verticesBuffer.put(vertices).flip();
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_STATIC_DRAW);

        GL30.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);
        GL30.glEnableVertexAttribArray(0);  

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);

        MemoryUtil.memFree(verticesBuffer);

        while (running) {
            // Clear the current framebuffer
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
            // Swap color buffers

            shaderManager.useShaderProgram("basic");

            GL30.glBindVertexArray(vao);

            GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 3);

            update();
        }

        GL15.glDeleteBuffers(vbo);
        GL30.glDeleteVertexArrays(vao);

        stop();
    }

    private void update() {
        window.update();

        running = !window.closeRequested();
    }

    private void stop() {
        window.destroy();
        shaderManager.cleanUp();
    }
}
