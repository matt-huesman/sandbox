package sandbox.shader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.commons.lang3.tuple.ImmutablePair;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class ShaderManager {
    private final static String LS = System.getProperty("line.separator");

    private final Map<String, ShaderProgram> shaderPrograms;
    private final Map<ImmutablePair<String, ShaderType>, Shader> shaders;

    private final HashSet<ShaderProgram> activeShaders;

    public ShaderManager() {
        shaderPrograms = new HashMap<String, ShaderProgram>();
        shaders = new HashMap<ImmutablePair<String, ShaderType>, Shader>();

        activeShaders = new HashSet<ShaderProgram>();
    }

    public void loadShaderProgram(String programName) throws Exception {
        // Check if the shader program has already been loaded
        if (shaderPrograms.containsKey(programName)) {
            return;
        }

        // Check if the shader program JSON file exists in the assets/shaders/programs directory
        File programFile = new File("assets/shaders/programs", programName + ".json");
        if (!programFile.exists()) {
            throw new FileNotFoundException("Couldn't locate program file: " + programName + ".json");
        }

        // Parse the shader program JSON file
        JsonReader programReader = new JsonReader(new FileReader(programFile));
        JsonObject json = JsonParser.parseReader(programReader).getAsJsonObject();

        // Create the shader program and begins attaching the shaders
        ShaderProgram program = new ShaderProgram(programName);

        // Load the required vertex shader and throw an exception if it doesn't exist
        JsonElement vertexShaderKey = json.get("vertexShader");
        if (vertexShaderKey == null) {
            throw new NullPointerException(
                "Shader program " + programName + " doesn't contain required vertex shader!\n" +
                "JSON Key pair should follow \"vertexShader\": \"[fileName]\""
            );
        }
        String vertexShaderName = vertexShaderKey.getAsString();
        loadShader(vertexShaderName, ShaderType.VERTEX_SHADER);

        // Load the optional geometry shader if it exists
        JsonElement geometryShaderKey = json.get("geometryShader");
        String geometryShaderName = null;
        if (geometryShaderKey != null) {
            geometryShaderName = geometryShaderKey.getAsString();
            loadShader(geometryShaderName, ShaderType.GEOMETRY_SHADER);
        }

        // Load the required fragment shader and throw an exception if it doesn't exist
        JsonElement fragmentShaderKey = json.get("fragmentShader");
        if (fragmentShaderKey == null) {
            throw new NullPointerException(
                "Shader program " + programName + " doesn't contain required fragment shader!\n" +
                "JSON Key pair should follow \"fragmentShader\": \"[fileName]\""
            );
        }
        String fragmentShaderName = fragmentShaderKey.getAsString();
        loadShader(fragmentShaderName, ShaderType.FRAGMENT_SHADER);

        // Attach the shaders to the shader program, link the shader program, and add it to the existing shader programs
        program.attach(
            shaders.get(new ImmutablePair<String, ShaderType>(vertexShaderName, ShaderType.VERTEX_SHADER)),
            shaders.get(new ImmutablePair<String, ShaderType>(fragmentShaderName, ShaderType.FRAGMENT_SHADER))
        ).attachIf(
                geometryShaderKey != null,
                shaders.get(new ImmutablePair<String, ShaderType>(geometryShaderName, ShaderType.GEOMETRY_SHADER))
        ).link();

        program.loadAttributes();
        program.loadUniforms();

        shaderPrograms.put(programName, program);
    }

    private void loadShader(String shaderName, ShaderType type) throws Exception {
        // Check if the shader has already been loaded
        if (shaders.containsKey(new ImmutablePair<String, ShaderType>(shaderName, type))) {
            return;
        }

        // Check if the shader file exists in the assets/shaders directory
        File shaderFile = new File("assets/shaders", shaderName + "." + type.getExtension());
        if (!shaderFile.exists()) {
            throw new FileNotFoundException("Couldn't locate shader file: " + shaderName + "." + type.getExtension());
        }

        BufferedReader shaderFileReader = new BufferedReader(new FileReader(shaderFile));

        // Read the shader file line by line and append it to a StringBuilder
        StringBuilder shaderSourceBuilder = new StringBuilder();
        String nextLine = null;
        while ((nextLine = shaderFileReader.readLine()) != null) {
            shaderSourceBuilder.append(nextLine).append(LS);
        }
        shaderSourceBuilder.deleteCharAt(shaderSourceBuilder.length() - 1);
        shaderFileReader.close();

        // Convert the StringBuilder to a String with the shader source code
        String shaderSource = shaderSourceBuilder.toString();

        // Create the shader and add it to the existing shaders
        Shader shader = new Shader(shaderName, type).buildShader(shaderSource);
        shaders.put(new ImmutablePair<String,ShaderType>(shaderName, type), shader);
    }

    public void useShaderProgram(long frame, String programName, Consumer<ShaderProgram> renderConsumer) {
        ShaderProgram program = shaderPrograms.get(programName);
        if (program != null) {
            program.bind(frame);
            renderConsumer.accept(program);
            ShaderProgram.unbind();

            // TODO: Delegate this to the ECS
            activeShaders.add(program);
        } else {
            throw new NullPointerException("Shader program doesn't exist: " + programName);
        }
    }

    public HashSet<ShaderProgram> getActiveShaders(long frame) {
        activeShaders.removeIf(program -> frame - program.getLastFrame() > 120);
        return activeShaders;
    }

    public void earlyCleanUp() {
        // Free all shaders early because they are no longer needed after linking
        for (Shader shader : shaders.values()) {
            shader.destroy();
        }
    }

    public void cleanUp() {
        // Free all shader programs
        for (ShaderProgram program : shaderPrograms.values()) {
            program.destroy();
        }
    }
}
