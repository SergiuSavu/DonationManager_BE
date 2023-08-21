package de.msg.javatraining.donationmanager.persistence.model;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.stream.Stream;

public class MyEnumDeserializer extends JsonDeserializer<PermissionEnum> {

    @Override
    public PermissionEnum deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        String type = node.get("type").asText();
        return Stream.of(PermissionEnum.values())
                .filter(enumValue -> enumValue.getType().equals(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("type "+type+" is not recognized"));
    }
}
