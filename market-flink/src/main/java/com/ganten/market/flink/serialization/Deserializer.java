package com.ganten.market.flink.serialization;

import java.io.IOException;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Deserializer<T> implements DeserializationSchema<T> {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Class<T> clazz;

    public Deserializer(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public T deserialize(byte[] message) throws IOException {
        if (message == null || message.length == 0) {
            return null;
        }
        String json = new String(message, "UTF-8");
        return objectMapper.readValue(json, clazz);
    }

    @Override
    public boolean isEndOfStream(T nextElement) {
        return false;
    }

    @Override
    public TypeInformation<T> getProducedType() {
        return TypeInformation.of(clazz);
    }
}
