package ro.upb.iotbridgeservice.kafka.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;
import ro.upb.common.dto.MeasurementRequestDto;

import java.io.IOException;
import java.util.Map;

public class IotRequestSerializer implements Serializer<MeasurementRequestDto> {

    private final ObjectMapper objectMapper;

    public IotRequestSerializer() {
        this.objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);  // Avoid failure on empty beans
        objectMapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false); // Don't write null map values
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true); // Optimize date handling
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public byte[] serialize(String topic, MeasurementRequestDto data) {
        if (data == null) {
            return null;
        }

        try {
            return objectMapper.writeValueAsBytes(data);
        } catch (IOException e) {
            throw new SerializationException("Error serializing IotRequestDto: " + data, e);
        }
    }

    @Override
    public void close() {
    }
}
