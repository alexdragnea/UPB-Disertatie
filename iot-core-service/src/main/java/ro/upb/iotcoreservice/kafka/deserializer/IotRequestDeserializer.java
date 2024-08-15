package ro.upb.iotcoreservice.kafka.deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import ro.upb.common.dto.MeasurementRequestDto;

import java.io.IOException;
import java.util.Map;

public class IotRequestDeserializer implements Deserializer<MeasurementRequestDto> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public MeasurementRequestDto deserialize(String topic, byte[] data) {
        if (data == null)
            return null;

        try {
            return objectMapper.readValue(data, MeasurementRequestDto.class);
        } catch (IOException e) {
            throw new SerializationException("Error deserializing byte[] to IotRequestDto", e);
        }
    }

    @Override
    public void close() {
    }
}
