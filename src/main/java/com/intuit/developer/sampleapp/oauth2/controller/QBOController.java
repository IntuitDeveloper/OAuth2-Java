package com.intuit.developer.sampleapp.oauth2.controller;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.intuit.developer.sampleapp.oauth2.domain.BearerTokenResponse;
import com.intuit.developer.sampleapp.oauth2.domain.OAuth2Configuration;
import com.intuit.developer.sampleapp.oauth2.helper.HttpHelper;
import com.intuit.developer.sampleapp.oauth2.service.RefreshTokenService;

/**
 * @author dderose
 *
 */
@RestController
public class QBOController {
    
    @Autowired
    public OAuth2Configuration oAuth2Configuration;
    
    @Autowired
    public HttpHelper httpHelper;
    
    @Autowired
    public RefreshTokenService refreshTokenService;
    
    private static final HttpClient CLIENT = HttpClientBuilder.create().build();
    private static final Logger logger = Logger.getLogger(QBOController.class);
    
    /**
     * Sample QBO API call using OAuth2 tokens
     * 
     * @param session
     * @return
     */
    @ResponseBody
    @RequestMapping("/getCompanyInfo")
    public String callQBOCompanyInfo(HttpSession session) {
        
        //Ideally you would fetch the realmId and the accessToken from the data store based on the user account here.
        String realmId = (String)session.getAttribute("realmId");
        if (StringUtils.isEmpty(realmId)) {
            return new JSONObject().put("response","No realm ID.  QBO calls only work if the accounting scope was passed!").toString();
        }
        String companyInfoEndpoint = String.format("%s/v3/company/%s/companyinfo/%s", oAuth2Configuration.getAccountingAPIHost(), realmId, realmId);

        String failureMsg="Failed";

        HttpGet companyInfoReq = new HttpGet(companyInfoEndpoint);

        companyInfoReq.setHeader("Accept", "application/json");
        String accessToken = (String)session.getAttribute("access_token");
        companyInfoReq.setHeader("Authorization","Bearer " + accessToken);

        try {

            HttpResponse response = CLIENT.execute(companyInfoReq);

            logger.info("Response Code : "+ response.getStatusLine().getStatusCode());
            
            /*
             * Handle 401 status code - 
             * If a 401 response is received, refresh tokens should be used to get a new access token,
             * and the API call should be tried again.
             */
            if (response.getStatusLine().getStatusCode() == 401) {
                StringBuffer result = httpHelper.getResult(response);
                logger.debug("raw result for 401 companyInfo= " + result);
                
                //refresh tokens
                logger.info("received 401 during companyinfo call, refreshing tokens now");
                BearerTokenResponse bearerTokenResponse = refreshTokenService.refresh(session);
                session.setAttribute("access_token", bearerTokenResponse.getAccessToken());
                session.setAttribute("refresh_token", bearerTokenResponse.getRefreshToken());
                
                //call company info again using new tokens
                logger.info("calling companyinfo using new tokens");
                companyInfoReq.setHeader("Authorization","Bearer " + bearerTokenResponse.getAccessToken());
                response = CLIENT.execute(companyInfoReq);
            }  
            
            if (response.getStatusLine().getStatusCode() != 200){
                logger.info("failed getting companyInfo");
                return new JSONObject().put("response",failureMsg).toString();
            }
   
            StringBuffer result = httpHelper.getResult(response);
            logger.debug("raw result for companyInfo= " + result);
            return result.toString();
            
        } catch (Exception ex) {
            logger.error("Exception while getting company info ", ex);
            return new JSONObject().put("response",failureMsg).toString();
        }
    }

}
