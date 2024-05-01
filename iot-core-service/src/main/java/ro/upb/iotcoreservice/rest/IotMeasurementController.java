package ro.upb.iotcoreservice.rest;

import lombok.RequiredArgsConstructor;
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
public class IotMeasurementController {

    private final IotMeasurementService iotMeasurementService;

    @GetMapping("/measurements/{userId}")
    public Flux<IotResponseDto> getMeasurementsByUserId(@PathVariable int userId) {
        return iotMeasurementService.findAllIotMeasurements(userId);
    }
}
