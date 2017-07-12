package com.intuit.developer.sampleapp.oauth2.service;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intuit.developer.sampleapp.oauth2.domain.BearerTokenResponse;
import com.intuit.developer.sampleapp.oauth2.domain.OAuth2Configuration;
import com.intuit.developer.sampleapp.oauth2.helper.HttpHelper;

/**
 * @author dderose
 *
 */
@Service
public class RefreshTokenService {
    
    @Autowired
    public OAuth2Configuration oAuth2Configuration;
    
    @Autowired
    public HttpHelper httpHelper;
    
    private static final HttpClient CLIENT = HttpClientBuilder.create().build();
    private static final Logger logger = Logger.getLogger(RefreshTokenService.class);
    private static ObjectMapper mapper = new ObjectMapper();
    

    /**
     * Calls refresh endpoint to generate new tokens
     * 
     * @param session
     * @return
     * @throws Exception
     */
    public BearerTokenResponse refresh(HttpSession session) throws Exception {
        
        HttpPost post = new HttpPost(oAuth2Configuration.getIntuitBearerTokenEndpoint());

        // add header
        post = httpHelper.addHeader(post);
        List<NameValuePair> urlParameters = httpHelper.getUrlParameters(session, "refresh");

        try {
            post.setEntity(new UrlEncodedFormEntity(urlParameters));
            HttpResponse response = CLIENT.execute(post);

            logger.info("Response Code : "+ response.getStatusLine().getStatusCode());
            if (response.getStatusLine().getStatusCode() != 200) {
                logger.info("failed getting companyInfo");
                throw new Exception();
            }
            
            StringBuffer result = httpHelper.getResult(response);
            logger.debug("raw result for refresh token request= " + result);
            
            BearerTokenResponse bearerTokenResponse = mapper.readValue(result.toString(), BearerTokenResponse.class);
            return bearerTokenResponse; 

        }
        catch (Exception ex) {
            logger.error("Exception while calling refreshToken ", ex);
            throw new Exception(ex);
        }
           
    }

}
