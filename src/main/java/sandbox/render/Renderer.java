package sandbox.render;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import sandbox.Window;
import sandbox.shader.ShaderManager;
import sandbox.shader.ShaderProgram;

public class Renderer {
    private final Window window;

    public Renderer(Window window) {
        this.window = window;
    }

    public void init() {
        // Initialize OpenGL
        GL.createCapabilities();

        // Set the viewport to the window dimensions on window resize
        GL11.glViewport(0, 0, window.getWidth(), window.getHeight());
        window.onResize((window, width, height) -> {
            GL11.glViewport(0, 0, width, height);
        });

        resetState();
    }

    /**
     * Reset OpenGL state to the default state
     */
    public void resetState() {
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.f);

        // Enable depth testing
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthFunc(GL11.GL_LESS);

//        // Enable backface culling
//        GL11.glEnable(GL11.GL_CULL_FACE);
//        GL11.glCullFace(GL11.GL_BACK);

        // Disable wireframe mode
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
    }

    public void prerender() {
        // Clear the current framebuffer
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

    public void render(Long frame, ShaderManager shaderManager) {
        shaderManager.getActiveShaders(frame).forEach((shaderProgram) -> {
            shaderProgram.bind(frame);

            GL30.glDrawElements(GL15.GL_TRIANGLES, 6, GL15.GL_UNSIGNED_INT, 0);

            ShaderProgram.unbind();
        });
    }
}
