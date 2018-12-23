package com.service.user.util;

import org.apache.commons.codec.digest.DigestUtils;

public class AppUtil {
	
	static String SHA256Encryptor(String data) {
		String encryptedString = "";
		encryptedString = DigestUtils.sha256Hex(data);
		return encryptedString;
	}
}

