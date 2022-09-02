package com.ulalalab.api.common.service;

import com.ulalalab.api.common.model.Device;
import com.ulalalab.api.common.repository.DeviceRepository;
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
		System.out.println("##@@ device 등록!");
		deviceRepository.save(device);
	}
}