package sandbox.resource;

import sandbox.resource.shader.ShaderManager;

/**
 * Class responsible for holding resource managers for textures, shaders, and models
 */
public class ResourceManager {
    private final ShaderManager shaderManager;

    public ResourceManager() {
        shaderManager = new ShaderManager();
    }

    public void destroyAllResources() {
        shaderManager.destroy();
    }
}
