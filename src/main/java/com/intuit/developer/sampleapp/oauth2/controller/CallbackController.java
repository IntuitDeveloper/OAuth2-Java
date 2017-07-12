package com.intuit.developer.sampleapp.oauth2.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intuit.developer.sampleapp.oauth2.domain.BearerTokenResponse;
import com.intuit.developer.sampleapp.oauth2.domain.OAuth2Configuration;
import com.intuit.developer.sampleapp.oauth2.helper.HttpHelper;
import com.intuit.developer.sampleapp.oauth2.service.ValidationService;

/**
 * @author dderose
 *
 */
@Controller
public class CallbackController {
    
    @Autowired
    public OAuth2Configuration oAuth2Configuration;
    
    @Autowired
    public ValidationService validationService;
    
    @Autowired
    public HttpHelper httpHelper;
    
    private static final HttpClient CLIENT = HttpClientBuilder.create().build();
    private static ObjectMapper mapper = new ObjectMapper();
    private static final Logger logger = Logger.getLogger(CallbackController.class);
    
    /**
     *  This is the redirect handler you configure in your app on developer.intuit.com
     *  The Authorization code has a short lifetime.
     *  Hence Unless a user action is quick and mandatory, proceed to exchange the Authorization Code for
     *  BearerToken
     *      
     * @param auth_code
     * @param state
     * @param realmId
     * @param session
     * @return
     */
    @RequestMapping("/oauth2redirect")
    public String callBackFromOAuth(@RequestParam("code") String authCode, @RequestParam("state") String state, @RequestParam(value = "realmId", required = false) String realmId, HttpSession session) {   
        logger.debug("inside oauth2redirect " + authCode  );
        
        String csrfToken = (String) session.getAttribute("csrfToken");
        if (csrfToken.equals(state)) {
            session.setAttribute("realmId", realmId);
            session.setAttribute("auth_code", authCode);
            BearerTokenResponse bearerTokenResponse = retrieveBearerTokens(authCode, session);  
            
            /*
             * save token to session
             * In real usecase, this is where tokens would have to be persisted (to a SQL DB, for example). 
             * Update your Datastore here with user's AccessToken and RefreshToken along with the realmId
            */
            session.setAttribute("access_token", bearerTokenResponse.getAccessToken());
            session.setAttribute("refresh_token", bearerTokenResponse.getRefreshToken());
         
            /* 
             * However, in case of OpenIdConnect, when you request OpenIdScopes during authorization,
             * you will also receive IDToken from Intuit. You first need to validate that the IDToken actually came from Intuit.
             */
            if (StringUtils.isNotBlank(bearerTokenResponse.getIdToken())) {
               if(validationService.isValidIDToken(bearerTokenResponse.getIdToken())) {
                   logger.info("IdToken is Valid");
                   //get user info
                   saveUserInfo(bearerTokenResponse.getAccessToken(), session);
               }
            }
            
            return "connected";
        }
        logger.info("csrf token mismatch " );
        return null;
    }

    private BearerTokenResponse retrieveBearerTokens(String auth_code, HttpSession session) {
        logger.info("inside bearer tokens");

        HttpPost post = new HttpPost(oAuth2Configuration.getIntuitBearerTokenEndpoint());

        // add header
        post = httpHelper.addHeader(post);
        List<NameValuePair> urlParameters = httpHelper.getUrlParameters(session, "");

        try {
            post.setEntity(new UrlEncodedFormEntity(urlParameters));
            HttpResponse response = CLIENT.execute(post);

            logger.info("Response Code : "+ response.getStatusLine().getStatusCode());
            if (response.getStatusLine().getStatusCode() != 200) {
                logger.info("failed getting access token");
                return null;
            }

            StringBuffer result = httpHelper.getResult(response);
            logger.debug("raw result for bearer tokens= " + result);

            return mapper.readValue(result.toString(), BearerTokenResponse.class);
            
        } catch (Exception ex) {
            logger.error("Exception while retrieving bearer tokens", ex);
        }
        return null;
    }
    
    private void saveUserInfo(String accessToken, HttpSession session) {
        //Ideally you would fetch the realmId and the accessToken from the data store based on the user account here.
        HttpGet userInfoReq = new HttpGet(oAuth2Configuration.getUserProfileApiHost());
        userInfoReq.setHeader("Accept", "application/json");
        userInfoReq.setHeader("Authorization","Bearer "+accessToken);

        try {
            HttpResponse response = CLIENT.execute(userInfoReq);

            logger.info("Response Code : "+ response.getStatusLine().getStatusCode());
            if (response.getStatusLine().getStatusCode() == 200) {
                
                StringBuffer result = httpHelper.getResult(response);
                logger.debug("raw result for user info= " + result);

                //Save the UserInfo here.
                JSONObject userInfoPayload = new JSONObject(result.toString());
                session.setAttribute("sub", userInfoPayload.get("sub"));
                session.setAttribute("givenName", userInfoPayload.get("givenName"));
                session.setAttribute("email", userInfoPayload.get("email"));
                
            } else {
                logger.info("failed getting user info");
            }

            
        }
        catch (Exception ex) {
            logger.error("Exception while retrieving user info ", ex);
        }
    }

}
