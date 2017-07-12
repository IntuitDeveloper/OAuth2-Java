package com.intuit.developer.sampleapp.oauth2.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

/**
 * @author dderose
 *
 */
@Configuration
@PropertySource(value="classpath:/application.properties", ignoreResourceNotFound=true)
public class OAuth2Configuration {
	
	@Autowired
    Environment env;

	private String intuitIdTokenIssuer;
	private String intuitAuthorizationEndpoint;
	private String intuitBearerTokenEndpoint;
	private String intuitRevokeTokenEndpoint;
	private String intuitJsksURI;
	private String intuitUserProfileAPIHost;

    public String getAppClientId() {
    	return env.getProperty("OAuth2AppClientId");
    }

    public String getAppClientSecret() {
        return env.getProperty("OAuth2AppClientSecret");
    }
    
    public String getAppRedirectUri() {
        return env.getProperty("OAuth2AppRedirectUri");
    }

    public String getUserProfileApiHost() {
        return intuitUserProfileAPIHost;
    }   

    public String getAccountingAPIHost() {
        return env.getProperty("IntuitAccountingAPIHost");
    }

	public String getDiscoveryAPIHost() {
		return env.getProperty("DiscoveryAPIHost");
	}
    
	public String getC2QBScope() {
		return env.getProperty("c2qbScope");
	}
	
	public String getSIWIScope() {
		return env.getProperty("siwiScope");
	}
	
	public String getAppNowScope() {
		return env.getProperty("getAppNowScope");
	}
	
    public String getIntuitIdTokenIssuer() {
        return intuitIdTokenIssuer;
    }

    public String getIntuitAuthorizationEndpoint() {
        return intuitAuthorizationEndpoint;
    }
    
    public String getIntuitBearerTokenEndpoint() {
        return intuitBearerTokenEndpoint;
    }
    
    public String getIntuitRevokeTokenEndpoint() {
		return intuitRevokeTokenEndpoint;
	} 

    public String getIntuitJsksURI() {
        return intuitJsksURI;
    }

	public void setIntuitIdTokenIssuer(String intuitIdTokenIssuer) {
		this.intuitIdTokenIssuer = intuitIdTokenIssuer;
	}

	public void setIntuitAuthorizationEndpoint(String intuitAuthorizationEndpoint) {
		this.intuitAuthorizationEndpoint = intuitAuthorizationEndpoint;
	}

	public void setIntuitBearerTokenEndpoint(String intuitBearerTokenEndpoint) {
		this.intuitBearerTokenEndpoint = intuitBearerTokenEndpoint;
	}

	public void setIntuitRevokeTokenEndpoint(String intuitRevokeTokenEndpoint) {
		this.intuitRevokeTokenEndpoint = intuitRevokeTokenEndpoint;
	}

	public void setIntuitJsksURI(String intuitJsksURI) {
		this.intuitJsksURI = intuitJsksURI;
	}

	public String getIntuitUserProfileAPIHost() {
		return intuitUserProfileAPIHost;
	}

	public void setIntuitUserProfileAPIHost(String intuitUserProfileAPIHost) {
		this.intuitUserProfileAPIHost = intuitUserProfileAPIHost;
	}
    
}
