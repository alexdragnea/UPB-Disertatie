package ro.upb.iotcoreservice.kafka.deserializer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import ro.upb.common.dto.MeasurementRequestDto;

import java.io.IOException;
import java.util.Map;

public class IotRequestDeserializer implements Deserializer<MeasurementRequestDto> {

    // Reuse ObjectMapper
    private final ObjectMapper objectMapper;

    public IotRequestDeserializer() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public MeasurementRequestDto deserialize(String topic, byte[] data) {
        if (data == null || data.length == 0) {
            return null;
        }

        try {

            return objectMapper.readValue(data, MeasurementRequestDto.class);
        } catch (IOException e) {
            throw new SerializationException("Error deserializing byte[] to MeasurementRequestDto", e);
        }
    }

    @Override
    public void close() {
    }
}
