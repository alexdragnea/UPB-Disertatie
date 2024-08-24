package ro.upb.iotcoreservice.service;

import ro.upb.common.dto.DeviceRequestDto;

public interface IotDeviceService {
    void registerDevice(DeviceRequestDto deviceRequestDto);
}
