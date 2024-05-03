package ro.upb.iotbridgeservice.kafka.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;
import ro.upb.common.dto.MeasurementRequestDto;

import java.io.IOException;
import java.util.Map;

public class IotRequestSerializer implements Serializer<MeasurementRequestDto> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public byte[] serialize(String topic, MeasurementRequestDto data) {
        if (data == null)
            return null;

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