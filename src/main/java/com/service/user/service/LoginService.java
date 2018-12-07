package com.service.user.service;

import com.user.model.entity.Customer;

public interface LoginService {

	Customer googleLoginDetails() throws Exception;
	
}
