package ro.upb.iotcoreservice.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import ro.upb.iotcoreservice.model.IotDevice;

@Repository
public interface IotDeviceRepository extends ReactiveMongoRepository<IotDevice, String> {
    Flux<IotDevice> findBySensorNameAndUserId(String sensorName, String userId);
}
