package ro.upb.iotcoreservice.service.impl;

import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.reactive.InfluxDBClientReactive;
import com.influxdb.client.reactive.QueryReactiveApi;
import com.influxdb.client.reactive.WriteReactiveApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ro.upb.common.dto.DeviceRequestDto;
import ro.upb.iotcoreservice.exception.DeviceAlreadyExists;
import ro.upb.iotcoreservice.model.IotDevice;
import ro.upb.iotcoreservice.service.IotDeviceService;

@Service
@RequiredArgsConstructor
@Slf4j
public class IotDeviceServiceImpl implements IotDeviceService {

    private final InfluxDBClientReactive influxDBClient;

    private static IotDevice buildIotDevice(DeviceRequestDto deviceRequestDto) {

        return IotDevice.builder().sensorName(deviceRequestDto.getSensorName()).userId(deviceRequestDto.getUserId()).description(deviceRequestDto.getDescription()).location(deviceRequestDto.getLocation()).rangeValue(deviceRequestDto.getRangeValue()).build();
    }

    @Override
    public void registerDevice(DeviceRequestDto deviceRequestDto) {
        WriteReactiveApi writeApi = influxDBClient.getWriteReactiveApi();
        QueryReactiveApi queryApi = influxDBClient.getQueryReactiveApi();

        String findDeviceQuery = String.format(
                "from(bucket: \"devices-bucket\") " +
                        "|> range(start: 0) " +
                        "|> filter(fn: (r) => r._sensorName == \"%s\" and r.userId == \"%s\")",
                deviceRequestDto.getSensorName(),
                deviceRequestDto.getUserId());
        log.info("Checking if device exists for userId: {} and sensorName: {}.", deviceRequestDto.getUserId(), deviceRequestDto.getSensorName());

        Publisher<IotDevice> devicePublisher = queryApi.query(findDeviceQuery, IotDevice.class);

        Flux.from(devicePublisher)
                .collectList()
                .flatMap(devices -> {
                    if (!devices.isEmpty()) {
                        return Mono.error(new DeviceAlreadyExists("Device already exists for userId: " + deviceRequestDto.getUserId() + " and sensorName: " + deviceRequestDto.getSensorName()));
                    } else {
                        IotDevice iotDevice = buildIotDevice(deviceRequestDto);
                        log.info("Converting IotDeviceRequest: {} to IotDevice entity: {}.", deviceRequestDto, iotDevice);

                        log.info("Persisting IotDevice.");
                        Publisher<WriteReactiveApi.Success> publisher = writeApi.writeMeasurement(WritePrecision.NS, iotDevice);

                        return Mono.from(publisher)
                                .doOnSuccess(info -> log.info("Successfully persisted device: {}.", iotDevice))
                                .then();
                    }
                })
                .subscribe();
    }
}
