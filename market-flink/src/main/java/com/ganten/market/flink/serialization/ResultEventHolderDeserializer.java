package com.ganten.market.flink.serialization;

import java.io.IOException;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ganten.market.common.pojo.ResultEventHolder;

public class ResultEventHolderDeserializer implements DeserializationSchema<ResultEventHolder> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ResultEventHolder deserialize(byte[] message) throws IOException {
        if (message == null || message.length == 0) {
            return null;
        }
        String json = new String(message, "UTF-8");
        return objectMapper.readValue(json, ResultEventHolder.class);
    }

    @Override
    public boolean isEndOfStream(ResultEventHolder nextElement) {
        return false;
    }

    @Override
    public TypeInformation<ResultEventHolder> getProducedType() {
        return TypeInformation.of(ResultEventHolder.class);
    }
}
