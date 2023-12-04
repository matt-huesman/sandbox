package sandbox;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

import static org.lwjgl.system.MemoryUtil.*;

public class Window {
    private long windowId;
    private int width, height;
    private final String title;

    private GLFWErrorCallback errorCallback;

    public Window(int width, int height, String title) {
        this.width = width;
        this.height = height;
        this.title = title;
    }

    public void init() {
        // Initialize GLFW
        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        errorCallback = GLFWErrorCallback.createPrint(System.err).set();

        // Configure GLFW
		GLFW.glfwDefaultWindowHints();
		GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE,GLFW.GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 4);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 1);

		// Create the window
		windowId = GLFW.glfwCreateWindow(width, height, title, NULL, NULL);
		if (windowId == NULL) {
			throw new RuntimeException("Failed to create the GLFW window");
        }

        // Make the OpenGL context current
        GLFW.glfwMakeContextCurrent(windowId);
        // Enable v-sync
        GLFW.glfwSwapInterval(1);
    }

    public void onResize(WindowResizeCallback windowResizeCallback) {
        GLFW.glfwSetWindowSizeCallback(windowId, (windowId, width, height) -> {
            this.width = width;
            this.height = height;
            windowResizeCallback.onResize(this, width, height);
        });
    }

    // Make the window visible
    public void showWindow() {
        GLFW.glfwShowWindow(windowId);
    }

    public void update() {
        GLFW.glfwSwapBuffers(windowId);
        // Poll for window events. The key callback above will only be invoked during this call.
        GLFW.glfwPollEvents();
    }

    public boolean closeRequested() {
        return GLFW.glfwWindowShouldClose(windowId);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void destroy() {
        // Free the window callbacks and destroy the window
		GLFW.glfwDestroyWindow(windowId);

		// Terminate GLFW and free the error callback
		GLFW.glfwTerminate();
        errorCallback.free();
    }

    public interface WindowResizeCallback {
        void onResize(Window window, int width, int height);
    }
}
