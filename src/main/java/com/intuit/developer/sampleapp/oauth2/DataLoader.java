package com.intuit.developer.sampleapp.oauth2;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.intuit.developer.sampleapp.oauth2.domain.OAuth2Configuration;
import com.intuit.developer.sampleapp.oauth2.helper.HttpHelper;

/**
 * @author dderose
 *
 */
@Component
@Configuration
@PropertySource(value="classpath:/application.properties", ignoreResourceNotFound=true)
public class DataLoader implements ApplicationListener<ContextRefreshedEvent> {
	
	@Autowired
    private Environment env;
	
	@Autowired
    public OAuth2Configuration oAuth2Configuration;
	
	@Autowired
	public HttpHelper httpHelper;
	
	private static final HttpClient CLIENT = HttpClientBuilder.create().build();
	private static final Logger logger = Logger.getLogger(DataLoader.class);

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		
		// call discovery document
		callDiscoveryDocument();
			
	}

	/**
	 *  Call discovery document and populate the config - oAuth2Configuration
	 * 
	 */
	private void callDiscoveryDocument() {
		
		 HttpGet discoveryDocumentReq = new HttpGet(env.getProperty("DiscoveryAPIHost"));
		 try {
	            HttpResponse response = CLIENT.execute(discoveryDocumentReq);

	            logger.info("Response Code : "+ response.getStatusLine().getStatusCode());
	            if (response.getStatusLine().getStatusCode() != 200) {
	            	logger.info("failed getting user info");
	            }

	            StringBuffer result = httpHelper.getResult(response);
	            logger.debug("raw result for user info= " + result);
	          
	            JSONObject discoveryPayload = new JSONObject(result.toString());
	            oAuth2Configuration.setIntuitAuthorizationEndpoint(discoveryPayload.getString("authorization_endpoint"));
	            oAuth2Configuration.setIntuitBearerTokenEndpoint(discoveryPayload.getString("token_endpoint"));
	            oAuth2Configuration.setIntuitIdTokenIssuer(discoveryPayload.getString("issuer"));
	            oAuth2Configuration.setIntuitJsksURI(discoveryPayload.getString("jwks_uri"));
	            oAuth2Configuration.setIntuitRevokeTokenEndpoint(discoveryPayload.getString("revocation_endpoint"));
	            oAuth2Configuration.setIntuitUserProfileAPIHost(discoveryPayload.getString("userinfo_endpoint"));
	        }
	        catch (Exception ex) {
	        	logger.error("Exception while calling discovery document", ex);
	        }
		
	}

}
