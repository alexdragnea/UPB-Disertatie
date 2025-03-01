package ro.upb.iotbridgeservice.kafka.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.upb.common.dto.MeasurementRequest;

import java.io.IOException;

public class IotRequestSerializer implements Serializer<MeasurementRequest> {

    private static final Logger logger = LoggerFactory.getLogger(IotRequestSerializer.class);

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            .configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false)
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);

    @Override
    public byte[] serialize(String topic, MeasurementRequest data) {
        if (data == null) {
            return null;
        }

        try {
            return objectMapper.writeValueAsBytes(data);
        } catch (IOException e) {
            logger.error("Serialization error for topic {}: {}", topic, e.getMessage());
            return new byte[0];
        }
    }
}
