package sandbox;

import java.nio.IntBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import sandbox.resource.ResourceManager;

public class Sandbox {
    private final ResourceManager resourceManager;

    private final Window window;

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
        resourceManager = new ResourceManager();

        window = new Window(800, 600, "Hello World!");
    }

    // Used for initial program logic
    public Sandbox init() {
        try {
            window.init();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            stop();
        }

        gameLoop();

        // while (running) {
        //     update();
        // }

        return this;
    }

    private void gameLoop() {
        String vertexShaderSource = "#version 330 core\n" +
            "layout (location = 0) in vec3 aPos;\n" +
            "void main()\n" +
            "{\n" +
            "   gl_Position = vec4(aPos.x, aPos.y, aPos.z, 1.0);\n" +
            "}\0";

        String fragmentShaderSource = "#version 330 core\n" +
            "out vec4 FragColor;\n" +
            "void main()\n" +
            "{\n" +
            "    FragColor = vec4(1.0f, 0.5f, 0.2f, 1.0f);\n" +
            "}\0"; 

        int vertexShader = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);

        GL20.glShaderSource(vertexShader, vertexShaderSource);
        GL20.glCompileShader(vertexShader);

        if (GL20.glGetShaderi(vertexShader, GL20.GL_COMPILE_STATUS) == 0)
        {
            throw new RuntimeException("Error compiling Shader code: " + GL20.glGetShaderInfoLog(vertexShader, 1024));
        }

        int fragmentShader = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);

        GL20.glShaderSource(fragmentShader, fragmentShaderSource);
        GL20.glCompileShader(fragmentShader);

        if (GL20.glGetShaderi(fragmentShader, GL20.GL_COMPILE_STATUS) == 0)
        {
            throw new RuntimeException("Error compiling Shader code: " + GL20.glGetShaderInfoLog(fragmentShader, 1024));
        }

        int shaderProgram = GL20.glCreateProgram();
        GL20.glAttachShader(shaderProgram, vertexShader);
        GL20.glAttachShader(shaderProgram, fragmentShader);
        GL20.glLinkProgram(shaderProgram);

        if (GL20.glGetProgrami(shaderProgram, GL20.GL_LINK_STATUS) == 0) {
            throw new RuntimeException("Error linking Shaders: " + GL20.glGetProgramInfoLog(shaderProgram, 1024));
        }
        GL20.glDeleteShader(vertexShader);
        GL20.glDeleteShader(fragmentShader);

        int vao = GL30.glGenVertexArrays();
        int vbo = GL15.glGenBuffers();

        GL30.glBindVertexArray(vao);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertices, GL15.GL_STATIC_DRAW);

        GL30.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 3 * Float.SIZE, 0);
        GL30.glEnableVertexAttribArray(0);  

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);

        while (running) {
            // Clear the current framebuffer
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
            // Swap color buffers

            GL20.glUseProgram(shaderProgram);

            GL30.glBindVertexArray(vao);

            GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 3);

            update();
        }

        GL15.glDeleteBuffers(vbo);
        GL30.glDeleteVertexArrays(vao);
        GL20.glDeleteShader(vertexShader);

        stop();
    }

    private void update() {
        window.update();

        running = !window.closeRequested();
    }

    private void stop() {
        resourceManager.destroyAllResources();

        window.destroy();
    }
}
