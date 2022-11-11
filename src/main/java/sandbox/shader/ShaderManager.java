package sandbox.shader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.ImmutablePair;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import sandbox.resource.IResourceManager;

public class ShaderManager implements IResourceManager {
    private final static String LS = System.getProperty("line.separator");

    private final Map<String, ShaderProgram> shaderPrograms;
    
    private final Map<ImmutablePair<String, ShaderType>, Shader> shaders;

    public ShaderManager() {
        shaderPrograms = new HashMap<String, ShaderProgram>();
        
        shaders = new HashMap<ImmutablePair<String, ShaderType>, Shader>();
    }

    public void loadShaderProgram(String programName) throws Exception {
        if (shaderPrograms.containsKey(programName)) {
            return;
        }

        File programFile = new File("assets/shaders/programs", programName + ".json");
        if (!programFile.exists()) {
            throw new FileNotFoundException("Couldn't locate program file: " + programName + ".json");
        }

        JsonReader programReader = new JsonReader(new FileReader(programFile));

        JsonObject json = JsonParser.parseReader(programReader).getAsJsonObject();

        JsonElement vertexShaderKey = json.get("vertexShader");
        if (vertexShaderKey == null) {
            throw new NullPointerException(
                "Shader program " + programName + " doesn't contain required vertex shader!\n" +
                "JSON Key pair should follow \"vertexShader\": \"[fileName]\""
            );
        }
        String vertexShaderName = vertexShaderKey.getAsString();
        loadShader(vertexShaderName, ShaderType.VERTEX_SHADER);

        // JsonElement geometryShaderKey = json.get("geometryShader");
        // if (geometryShaderKey == null) {
        //     throw new NullPointerException(
        //         "Shader program " + programName + " doesn't contain required geometry shader!\n" +
        //         "JSON Key pair should follow \"geometryShader\": \"[fileName]\""
        //     );
        // }
        // String geometryShaderName = geometryShaderKey.getAsString();
        // loadShader(geometryShaderName, ShaderType.GEOMETRY_SHADER);

        JsonElement fragmentShaderKey = json.get("fragmentShader");
        if (fragmentShaderKey == null) {
            throw new NullPointerException(
                "Shader program " + programName + " doesn't contain required fragment shader!\n" +
                "JSON Key pair should follow \"fragmentShader\": \"[fileName]\""
            );
        }
        String fragmentShaderName = fragmentShaderKey.getAsString();
        loadShader(fragmentShaderName, ShaderType.FRAGMENT_SHADER);

        ShaderProgram program = new ShaderProgram(programName)
            .attach(
                shaders.get(new ImmutablePair<>(vertexShaderName, ShaderType.VERTEX_SHADER)),
                shaders.get(new ImmutablePair<>(fragmentShaderName, ShaderType.FRAGMENT_SHADER))
            ).link();
        shaderPrograms.put(programName, program);
    }

    private void loadShader(String shaderName, ShaderType type) throws Exception {
        if (shaders.containsKey(new ImmutablePair<String, ShaderType>(shaderName, type))) {
            return;
        }

        String nextLine = null;
        BufferedReader shaderFileReader = new BufferedReader(
            new FileReader(
                new File(
                    "assets/shaders",
                    shaderName + "." + type.getExtension()
                )
            )
        );

        StringBuilder shaderSourceBuilder = new StringBuilder();
        while ((nextLine = shaderFileReader.readLine()) != null) {
            shaderSourceBuilder.append(nextLine).append(LS);
        }
        shaderSourceBuilder.deleteCharAt(shaderSourceBuilder.length() - 1);
        shaderFileReader.close();

        String shaderSource = shaderSourceBuilder.toString();

        Shader shader = new Shader(shaderName, type).buildShader(shaderSource);
        shaders.put(new ImmutablePair<String,ShaderType>(shaderName, type), shader);
    }

    public void useShaderProgram(String programName) {
        ShaderProgram program = shaderPrograms.get(programName);
        if (program != null) {
            program.bind();
        } else {
            throw new NullPointerException(programName + " shader program doesn't currently exist.");
        }
    }

    @Override
    public void cleanUp() {
        for (Shader shader : shaders.values()) {
            shader.destory();
        }
    }
}
