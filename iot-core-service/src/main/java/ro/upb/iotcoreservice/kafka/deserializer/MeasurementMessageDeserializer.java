package ro.upb.iotcoreservice.kafka.deserializer;

import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.upb.common.avro.MeasurementMessage;

import java.io.IOException;
import java.util.Map;

public class MeasurementMessageDeserializer implements Deserializer<MeasurementMessage> {
    private static final Logger logger = LoggerFactory.getLogger(MeasurementMessageDeserializer.class);
    private final SpecificDatumReader<MeasurementMessage> reader =
            new SpecificDatumReader<>(MeasurementMessage.class);

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        // No configuration needed
    }

    @Override
    public MeasurementMessage deserialize(String topic, byte[] data) {
        if (data == null || data.length == 0) {
            logger.warn("Received null or empty byte array for topic: {}", topic);
            return null;
        }
        try {
            BinaryDecoder decoder = DecoderFactory.get().binaryDecoder(data, null);
            return reader.read(null, decoder);
        } catch (IOException e) {
            logger.error("Failed to deserialize message for topic: {}", topic, e);
            return null;
        }
    }

    @Override
    public void close() {
        // No resources to close
    }
}
