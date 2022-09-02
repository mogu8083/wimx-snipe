package com.ulalalab.api.common.repository;

import com.ulalalab.api.common.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Transactional
public interface DeviceRepository extends JpaRepository<Device, LocalDateTime> {

}
