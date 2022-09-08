package com.ulalalab.snipe.device.repository;

import com.ulalalab.snipe.device.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.concurrent.ListenableFuture;
import java.time.LocalDateTime;
import java.util.List;

public interface DeviceRepository extends JpaRepository<Device, LocalDateTime> {

    @Async
    ListenableFuture<List<Device>> findByDeviceId(String deviceId);
}
