package com.intuit.developer.sampleapp.oauth2.controller;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.intuit.developer.sampleapp.oauth2.domain.BearerTokenResponse;
import com.intuit.developer.sampleapp.oauth2.domain.OAuth2Configuration;
import com.intuit.developer.sampleapp.oauth2.helper.HttpHelper;
import com.intuit.developer.sampleapp.oauth2.service.RefreshTokenService;

/**
 * @author dderose
 *
 */
@Controller
public class RefreshTokenController {
	
	@Autowired
    public OAuth2Configuration oAuth2Configuration;
	
	@Autowired
	public HttpHelper httpHelper;
	
	@Autowired
	public RefreshTokenService refreshTokenService;
	
	private static final Logger logger = Logger.getLogger(RefreshTokenController.class);
	
    /**
     * Call to refresh tokens 
     * 
     * @param session
     * @return
     */
	@ResponseBody
    @RequestMapping("/refreshToken")
    public String refreshToken(HttpSession session) {
		
    	String failureMsg="Failed";
    
        try {
        	
            BearerTokenResponse bearerTokenResponse = refreshTokenService.refresh(session);
            session.setAttribute("access_token", bearerTokenResponse.getAccessToken());
            session.setAttribute("refresh_token", bearerTokenResponse.getRefreshToken());
            String jsonString = new JSONObject()
                    .put("access_token", bearerTokenResponse.getAccessToken())
                    .put("refresh_token", bearerTokenResponse.getRefreshToken()).toString();
            return jsonString;
        }
        catch (Exception ex) {
        	logger.error("Exception while calling refreshToken ", ex);
        	return new JSONObject().put("response",failureMsg).toString();
        }    
        
    }

}
