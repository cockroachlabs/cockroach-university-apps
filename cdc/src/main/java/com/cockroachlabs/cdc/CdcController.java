package com.cockroachlabs.cdc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/cdc/employees")
public class CdcController {

	Logger logger = LoggerFactory.getLogger(CdcController.class);

	@PostMapping
	public ResponseEntity<Void> receiveCdc(@RequestBody Map<String, Object> payload) {
		logger.info("Received CDC Event:");
		logger.info(payload.toString());
		return ResponseEntity.ok().build();
	}
}
