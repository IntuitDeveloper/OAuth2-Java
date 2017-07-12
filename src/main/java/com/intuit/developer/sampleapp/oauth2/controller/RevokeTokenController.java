package com.intuit.developer.sampleapp.oauth2.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.intuit.developer.sampleapp.oauth2.domain.OAuth2Configuration;
import com.intuit.developer.sampleapp.oauth2.helper.HttpHelper;

/**
 * @author dderose
 *
 */
@Controller
public class RevokeTokenController {
    
    @Autowired
    public OAuth2Configuration oAuth2Configuration;
    
    @Autowired
    public HttpHelper httpHelper;
    
    private static final HttpClient CLIENT = HttpClientBuilder.create().build();
    private static final Logger logger = Logger.getLogger(RevokeTokenController.class);
    
    /**
     * Call to revoke tokens 
     * 
     * @param session
     * @return
     */
    @ResponseBody
    @RequestMapping("/revokeToken")
    public String revokeToken(HttpSession session) {
        
        String failureMsg="Failed";
        HttpPost post = new HttpPost(oAuth2Configuration.getIntuitRevokeTokenEndpoint());

        // add header
        post = httpHelper.addHeader(post);
        List<NameValuePair> urlParameters = httpHelper.getUrlParameters(session, "revoke");
        
        try {
            post.setEntity(new UrlEncodedFormEntity(urlParameters));
            HttpResponse response = CLIENT.execute(post);

            logger.info("Response Code : "+ response.getStatusLine().getStatusCode());
            if (response.getStatusLine().getStatusCode() != 200) {
                logger.info("failed getting companyInfo");
                return new JSONObject().put("response",failureMsg).toString();
            }
            
            StringBuffer result = httpHelper.getResult(response);
            logger.debug("raw result for revoke token request= " + result);
            
            return new JSONObject().put("response", "Revoke successful").toString();
        }
        catch (Exception ex) {
            logger.error("Exception while calling revokeToken ", ex);
            return new JSONObject().put("response",failureMsg).toString();
        }    
        
    }

}
