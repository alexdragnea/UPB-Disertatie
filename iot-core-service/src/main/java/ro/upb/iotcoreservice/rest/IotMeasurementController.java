package ro.upb.iotcoreservice.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import ro.upb.common.dto.IotResponseDto;
import ro.upb.iotcoreservice.service.IotMeasurementService;

@RequestMapping("/v1/iot-core")
@RestController
@RequiredArgsConstructor
@Slf4j
public class IotMeasurementController {

    private final IotMeasurementService iotMeasurementService;

    @GetMapping("/{userId}")
    public Flux<IotResponseDto> getMeasurementsByUserId(@PathVariable int userId) {
        log.info("Getting all IotMeasurements for userId: {}.", userId);
        return iotMeasurementService.findAllByUserId(userId);
    }

    @GetMapping("/all")
    public void getAllMeasurements() {
        log.info("Getting all IotMeasurements.");
        iotMeasurementService.findAll();
    }
}
