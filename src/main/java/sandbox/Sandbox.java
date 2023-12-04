package sandbox;

import org.joml.Vector3f;

import sandbox.render.Renderer;
import sandbox.render.VertexDataBuffer;
import sandbox.shader.ShaderManager;

public class Sandbox {
    private final Window window;
    private final ShaderManager shaderManager;
    private final Renderer renderer;

    private long frame;
    private boolean running = true;

    // Temporary square data
    private final float[] vertices = {
        0.5f,  0.5f, 0.0f,  // top right
        0.5f, -0.5f, 0.0f,  // bottom right
        -0.5f, -0.5f, 0.0f,  // bottom left
        -0.5f,  0.5f, 0.0f   // top left
    };
    private final  int[] indices = {  // note that we start from 0!
        0, 1, 3,   // first triangle
        1, 2, 3    // second triangle
    };

    // Used for passing application arguments and instantiating application objects
    public Sandbox(int windowWidth, int windowHeight) {
        window = new Window(windowWidth, windowHeight, "Sandbox");
        shaderManager = new ShaderManager();
        renderer = new Renderer(window);
    }

    // Used for program initialization logic
    public void init() {
        window.init();
        renderer.init();

        try {
            shaderManager.loadShaderProgram("basic");
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

        shaderManager.earlyCleanUp();
        window.showWindow();
        gameLoop();
    }

    // Used for the main game loop
    private void gameLoop() {
        /*
         * TODO: Solution: load each model into a single VAO at the beginning of the program
         *  - Hold shader programs responsible for a single draw call for a particular VAO
         */
        // Renderer -> Shader -> ECS -> Mesh
        // Renderer goes through each shader and calls the render method for each shader while passing in the ECS
        // Shader calculates the MVP matrix and loads all it's required uniforms
        // Shader calls ECS to get all Entities within view that rely on that shader using a mapping
        // Each Entity is looped through and the Mesh is bound and rendered through the Mesh

        VertexDataBuffer vertexDataBuffer = new VertexDataBuffer(vertices, indices);

        while (running) {
            vertexDataBuffer.bind();

            // TODO: Delegate this to the ECS
            shaderManager.useShaderProgram(frame,"basic", (shaderProgram) -> {
                shaderProgram.setUniform("u_scale", new Vector3f((float) Math.random(), (float) Math.random(), (float) Math.random()));
                renderer.prerender();
                renderer.render(frame, shaderManager);
            });

            vertexDataBuffer.unbind();

            update();
        }

        vertexDataBuffer.destroy();
        stop();
    }

    private void update() {
        window.update();

        frame ++;
        frame %= Long.MAX_VALUE;

        if (window.closeRequested()) {
            running = false;
        }
    }

    private void stop() {
        window.destroy();
        shaderManager.cleanUp();
    }
}
