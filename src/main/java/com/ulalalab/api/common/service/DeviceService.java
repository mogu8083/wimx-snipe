package com.ulalalab.api.common.service;

import com.ulalalab.api.common.model.Device;
import com.ulalalab.api.common.repository.DeviceRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeviceService {
	private static Logger logger = LogManager.getLogger(DeviceService.class);

	@Autowired
	private DeviceRepository deviceRepository;

	public void insert(Device device) {
		deviceRepository.save(device);
	}

	public void select(Device device) {
		deviceRepository.findByDeviceId("111");
	}
}