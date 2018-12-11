package com.service.user.service.impl;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Tokeninfo;
import com.google.api.services.oauth2.model.Userinfoplus;
import com.service.user.constants.SplunkConstants;
import com.service.user.service.LoginService;
import com.user.model.entity.Customer;
import com.user.model.repository.CustomerRepository;


@Service
public class LoginServiceImpl implements LoginService {

	//==========================
	//   Class variables
	//==========================

	private static final String REDIRECT_URL = "/user-service/health";

	private static final String GOOGLE_USER = "user";

	private static final Logger logger = LoggerFactory.getLogger(LoginServiceImpl.class);

	//==========================
	//   Instance variables
	//==========================

	@Autowired(required=true)
	private CustomerRepository customerRepo; 

	/** Global instance of the HTTP transport. */
	private static HttpTransport httpTransport;

	/** Global instance of the JSON factory. */
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

	private static Oauth2 oauth2;

	private static GoogleClientSecrets clientSecrets;

	/** OAuth 2.0 scopes. */
	private static final List<String> SCOPES = Arrays.asList(
			"https://www.googleapis.com/auth/userinfo.profile",
			"https://www.googleapis.com/auth/userinfo.email");

	private static final String APPLICATION_NAME = "TestPOC";

	private static FileDataStoreFactory dataStoreFactory;

	/** Directory to store user credentials. */
	private static final java.io.File DATA_STORE_DIR =
			new java.io.File(System.getProperty("user.home"), ".store/oauth2_sample");

	//==========================
	//   Instance methods
	//==========================

	/* 
	 * The end user will browse to google sign in and allow access to your web
	 * application. After authenticating, the user will be forwarded
	 * to the callback URL specified when you obtained your application
	 * ID and secret
	 * @see com.retail.poc.service.LoginService#googleLoginDetails()
	 */
	public Customer googleLoginDetails() throws Exception {
		httpTransport = GoogleNetHttpTransport.newTrustedTransport();
		dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
		// authorization
		Credential credential = authorize();
		try {
			// set up global Oauth2 instance
			oauth2 = new Oauth2.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName(
					APPLICATION_NAME).build();
		} catch (Exception exception) {
			throw exception;
		}
		tokenInfo(credential.getAccessToken());
		Userinfoplus userInfo = getUserInfo();
		return saveGoogleUser(userInfo);
	}

	//==========================
	// Private methods
	//==========================

	// Google Private methods

	private Userinfoplus getUserInfo() throws IOException {
		logger.info(SplunkConstants.MSG,"Obtaining User Profile Information");
		Userinfoplus userinfo = oauth2.userinfo().get().execute();
		logger.info(userinfo.toPrettyString());
		return userinfo;
	}

	private void tokenInfo(String accessToken) throws Exception {
		logger.info(SplunkConstants.MSG,"Validating the token");
		Tokeninfo tokeninfo = oauth2.tokeninfo().setAccessToken(accessToken).execute();
		logger.info(tokeninfo.toPrettyString());
		if(!StringUtils.equals(tokeninfo.getAudience(), clientSecrets.getDetails().getClientId())) {
			logger.error(SplunkConstants.ERR_MSG,"ERROR: audience does not match our client ID!");
			throw new Exception("ERROR: audience does not match our client ID!");
		}
	}

	/** Authorizes the installed application to access user's protected data. */
	private static Credential authorize() throws Exception {
		GoogleAuthorizationCodeFlow flow = null;
		// load client secrets
		clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
				new InputStreamReader(LoginServiceImpl.class.getResourceAsStream("/client_secrets.json")));
		//		if (clientSecrets.getDetails().getClientId().startsWith("Enter")
		//				|| clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
		//			System.out.println("Enter Client ID and Secret from https://code.google.com/apis/console/ "
		//					+ "into oauth2-cmdline-sample/src/main/resources/client_secrets.json");
		//		}
		try {
			// set up authorization code flow
			flow = new GoogleAuthorizationCodeFlow.Builder(
					httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
					.setDataStoreFactory(dataStoreFactory).build();

		} catch (Exception exception) {
			logger.error(SplunkConstants.ERR_MSG,exception.getMessage());
			throw exception;
		}
		// authorize
		return new AuthorizationCodeInstalledApp(flow, 
				new LocalServerReceiver.Builder().setPort(8080).setCallbackPath(REDIRECT_URL).build())
				.authorize(GOOGLE_USER);
	}
	
	
	// TODD Encrypt this data && bean mapper.
	private Customer saveGoogleUser(Userinfoplus userinfo) {
		Customer registeredCustomer = new Customer();
		registeredCustomer.setCustomerId(1l);
		registeredCustomer.setEmail(userinfo.getEmail());
		registeredCustomer.setEnabled(userinfo.getVerifiedEmail());
		registeredCustomer.setFirstName(userinfo.getGivenName());
		registeredCustomer.setLastName(userinfo.getFamilyName());
		if(StringUtils.equalsIgnoreCase("male", userinfo.getGender())) registeredCustomer.setGender('M');
		else if(StringUtils.equalsIgnoreCase("female", userinfo.getGender())) registeredCustomer.setGender('F');
		else registeredCustomer.setGender('O');
		return customerRepo.save(registeredCustomer);
	}

}
