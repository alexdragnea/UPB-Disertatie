package ro.upb.iotcoreservice.kafka.deserializer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import ro.upb.common.dto.MeasurementRequest;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IotRequestDeserializer implements Deserializer<MeasurementRequest> {

    private static final Logger logger = LoggerFactory.getLogger(IotRequestDeserializer.class);
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
            .configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);

    @Override
    public MeasurementRequest deserialize(String topic, byte[] data) {
        if (data == null || data.length == 0) {
            return null;
        }

        try {
            return objectMapper.readValue(data, MeasurementRequest.class);
        } catch (IOException e) {
            logger.error("Deserialization error for topic {}: {}", topic, e.getMessage());
            return null;  // Return null instead of crashing consumer
        }
    }
}
