package org.lazy.core;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.lazy.common.BaseComponentDefinition;
import org.lazy.common.ComponentDefinition;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class JsonFileDefinitionFactory implements ConfigurableDefinitionFactory {

    private String definitionFilePath;
    private final ObjectMapper mapper;

    public JsonFileDefinitionFactory(String definitionFilePath) {
        this.definitionFilePath = definitionFilePath;
        this.mapper = new ObjectMapper();
    }

    @Override
    public ComponentDefinition getDefinition(String componentName) throws CoreException {
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        //try (JsonParser jsonParser = mapper.getFactory().createParser(inputStream)) {
        JsonParser jsonParser;
        try {
            jsonParser = mapper.getFactory().createParser(getClass().getClassLoader().getResourceAsStream(definitionFilePath));
            if (jsonParser.nextToken() != JsonToken.START_OBJECT) {
                throw new CoreException("Malformed Component Definition file");
            }
            while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                if (jsonParser.currentToken() == JsonToken.FIELD_NAME) {
                    if (componentName.equals(jsonParser.currentName())) {
                        if (jsonParser.nextToken() != JsonToken.START_OBJECT) {
                            throw new CoreException("Malformed Component Definition file");
                        }
                        return jsonParser.readValueAs(BaseComponentDefinition.class);
                    } else {
                        jsonParser.nextFieldName();
                        jsonParser.skipChildren();
                    }
                }
            }
        } catch (IOException e) {
            throw new CoreException(e.getMessage());
        }
        return null;
    }

    @Override
    public boolean containsDefinition(String componentName) throws CoreException {
        // TODO make getDefinition implementation generic to use it correctly
        return getDefinition(componentName) != null;
    }

    @Override
    public void registerDefinition(String componentName, ComponentDefinition definition) throws CoreException {
        try {
            File definitionsFile = new File(definitionFilePath);
            JsonNode jsonNode = mapper.readTree(definitionsFile);
            if (!jsonNode.has(componentName)) {
                ((ObjectNode) jsonNode).putPOJO(componentName, definition);
                String json = mapper.writeValueAsString(jsonNode);
                Files.write(definitionsFile.toPath(), json.getBytes(), StandardOpenOption.CREATE);
            }
        } catch (IOException e) {
            throw new CoreException(e.getMessage());
        }
    }
}
