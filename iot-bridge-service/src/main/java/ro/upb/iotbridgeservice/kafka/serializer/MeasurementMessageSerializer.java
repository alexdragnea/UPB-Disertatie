package ro.upb.iotbridgeservice.kafka.serializer;

import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.kafka.common.serialization.Serializer;
import ro.upb.common.avro.MeasurementMessage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MeasurementMessageSerializer implements Serializer<MeasurementMessage> {

    @Override
    public byte[] serialize(String topic, MeasurementMessage data) {
        if (data == null) return null;

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            DatumWriter<MeasurementMessage> datumWriter = new SpecificDatumWriter<>(MeasurementMessage.class);
            Encoder encoder = EncoderFactory.get().binaryEncoder(outputStream, null);
            datumWriter.write(data, encoder);
            encoder.flush();
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize Avro message", e);
        }
    }
}
