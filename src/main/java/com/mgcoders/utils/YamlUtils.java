package com.mgcoders.utils;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Created by rsperoni on 10/05/17.
 */
public class YamlUtils {

    public static String yamlFileContentToJsonString(String yaml) throws IOException {
        ObjectMapper yamlReader = new ObjectMapper(new YAMLFactory());
        Object obj = yamlReader.readValue(yaml, Object.class);
        ObjectMapper jsonWriter = new ObjectMapper();
        return jsonWriter.writeValueAsString(obj);
    }

    public static String jsonFileContentToJsonString(String json) throws IOException {
        ObjectMapper jsonReader = new ObjectMapper(new JsonFactory());
        Object obj = jsonReader.readValue(json, Object.class);
        ObjectMapper jsonWriter = new ObjectMapper();
        return jsonWriter.writeValueAsString(obj);
    }

    public static Map<String, Object> jsonStringToMap(String json) throws IOException {
        ObjectMapper om = new ObjectMapper();
        return (Map<String, Object>) (om.readValue(json, Map.class));
    }

    public static String readFile(String path, Charset encoding)
            throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }
}
