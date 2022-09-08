package com.ulalalab.snipe.device.service;

import com.ulalalab.snipe.device.model.Device;
import com.ulalalab.snipe.device.repository.DeviceRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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