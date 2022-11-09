package sandbox.shader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryUtil;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

public class ShaderProgram {
    private final int programID;
    private final String programName;

    private final Map<String, Integer> attribsMap;

    public ShaderProgram(String programName) {
        this.programName = programName;

        try {
            Reader reader = Files.newBufferedReader(Paths.get("resources", programName + ".json"));

            Gson gson = new Gson();

            JsonObject json = gson.fromJson(reader, JsonObject.class);

            System.out.println(json.get("vertexShader").getAsString());

        } catch (JsonSyntaxException | IOException e) {
            e.printStackTrace();
        }



        // try (FileInputStream inputStream = new FileInputStream(shaderFile)) {
        //     String rawJson = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        //     JsonElement json = JsonParser.parseString(rawJson);

        //     Gson gson = new Gson();

        //     gson.fromJson(rawJson, getClass())

        // } catch (JsonSyntaxException | IOException e) {
        //     e.printStackTrace();
        // }

        programID = GL20.glCreateProgram();
        if (programID == 0) {
            System.err.println("Failed to create shader program!");
        }

        programName = null;

        attribsMap = new HashMap<String, Integer>();
    }


}
