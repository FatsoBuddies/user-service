package com.service.user.controller;

import com.service.user.constants.URIConstants;
import com.service.user.service.LoginService;
import com.user.model.entity.Customer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(URIConstants.BASE_USER)
public class UserController {
	
	//==========================
		//   Class variables
		//==========================

		private static final Logger logger = LoggerFactory.getLogger(UserController.class);
		
		//==========================
		//   Instance variables
		//==========================
		
		@Autowired
		private LoginService loginService;
		
		//==========================
		//   Instance methods
		//==========================

		@PostMapping("/google")
		public String googleLogin() throws Exception {
			Customer googleRegisteredCustomer = loginService.googleLoginDetails();
			logger.info("Successfully logged in with google",googleRegisteredCustomer);
			return googleRegisteredCustomer.toString();
		}


}
