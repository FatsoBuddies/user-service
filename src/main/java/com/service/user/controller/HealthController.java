package com.service.user.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.service.user.constants.SplunkConstants;

@RestController
public class HealthController {
	
	//==========================
	//   Class variables
	//==========================
	
	private static final Logger logger = LoggerFactory.getLogger(HealthController.class);
	
	//==========================
	//   Instance methods
	//==========================
	
	@RequestMapping(value= "/health", method = {RequestMethod.GET,RequestMethod.POST})
	public String getHealth() {
		logger.debug(SplunkConstants.MSG," Health Controller");
		return "200 Ok";
	}
}
