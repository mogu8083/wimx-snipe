package com.ulalalab.api.common.repository;

import com.ulalalab.api.common.model.Device;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.concurrent.ListenableFuture;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

public interface DeviceRepository extends JpaRepository<Device, LocalDateTime> {

    @Async
    ListenableFuture<List<Device>> findByDeviceId(String deviceId);
}
