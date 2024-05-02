package ro.upb.iotcoreservice.kafka.dserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import ro.upb.common.dto.MeasurementDto;

import java.io.IOException;
import java.util.Map;

public class IotRequestDeserializer implements Deserializer<MeasurementDto> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public MeasurementDto deserialize(String topic, byte[] data) {
        if (data == null)
            return null;

        try {
            return objectMapper.readValue(data, MeasurementDto.class);
        } catch (IOException e) {
            throw new SerializationException("Error deserializing byte[] to IotRequestDto", e);
        }
    }

    @Override
    public void close() {
    }
}
