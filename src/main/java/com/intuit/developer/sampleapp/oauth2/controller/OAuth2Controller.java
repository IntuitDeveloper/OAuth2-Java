package com.intuit.developer.sampleapp.oauth2.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

import com.intuit.developer.sampleapp.oauth2.domain.OAuth2Configuration;
import com.intuit.developer.sampleapp.oauth2.helper.HttpHelper;
import com.intuit.developer.sampleapp.oauth2.service.ValidationService;

/**
 * @author dderose
 *
 */
@Controller
public class OAuth2Controller {
	
	private static final Logger logger = Logger.getLogger(OAuth2Controller.class);
	
	@Autowired
    public OAuth2Configuration oAuth2Configuration;
	
	@Autowired
    public ValidationService validationService;
	
	@Autowired
    public HttpHelper httpHelper;
	    
	@RequestMapping("/")
	public String home() {
		return "home";
	}
	
	@RequestMapping("/connected")
	public String connected() {
		return "connected";
	}
	
	/**
	 * Controller mapping for connectToQuickbooks button
	 * @return
	 */
	@RequestMapping("/connectToQuickbooks")
	public View connectToQuickbooks(HttpSession session) {
		logger.info("inside connectToQuickbooks ");
		return new RedirectView(prepareUrl(oAuth2Configuration.getC2QBScope(), generateCSRFToken(session)), true, true, false);
	}
	
	/**
	 * Controller mapping for signInWithIntuit button
	 * @return
	 */
	@RequestMapping("/signInWithIntuit")
	public View signInWithIntuit(HttpSession session) {
		logger.info("inside signInWithIntuit ");
		return new RedirectView(prepareUrl(oAuth2Configuration.getSIWIScope(), generateCSRFToken(session)), true, true, false);
	}
	
	/**
	 * Controller mapping for getAppNow button
	 * @return
	 */
	@RequestMapping("/getAppNow")
	public View getAppNow(HttpSession session) {
		logger.info("inside getAppNow "  );
		return new RedirectView(prepareUrl(oAuth2Configuration.getAppNowScope(), generateCSRFToken(session)), true, true, false);
	}
	
	private String prepareUrl(String scope, String csrfToken)  {
		try {
			return oAuth2Configuration.getIntuitAuthorizationEndpoint() 
					+ "?client_id=" + oAuth2Configuration.getAppClientId() 
					+ "&response_type=code&scope=" + URLEncoder.encode(scope, "UTF-8") 
					+ "&redirect_uri=" + URLEncoder.encode(oAuth2Configuration.getAppRedirectUri(), "UTF-8") 
					+ "&state=" + csrfToken;
		} catch (UnsupportedEncodingException e) {
			logger.error("Exception while preparing url for redirect ", e);
		}
		return null;
	}
	
	private String generateCSRFToken(HttpSession session)  {
		String csrfToken = UUID.randomUUID().toString();
		session.setAttribute("csrfToken", csrfToken);
		return csrfToken;
	}

}
