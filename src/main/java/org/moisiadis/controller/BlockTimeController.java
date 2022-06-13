package org.moisiadis.controller;

import org.moisiadis.service.IPService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class BlockTimeController {
	private final Logger logger = LoggerFactory.getLogger(BlockTimeController.class);
	@GetMapping("/block-time")
	public int getBlockTime(HttpServletRequest request) {
		final String ip = request.getRemoteAddr();
		logger.info("New connection from: " + ip);
		return IPService.getBlockTime();
	}
}
