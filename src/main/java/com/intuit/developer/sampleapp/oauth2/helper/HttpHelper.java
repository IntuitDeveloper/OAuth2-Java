package com.intuit.developer.sampleapp.oauth2.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.intuit.developer.sampleapp.oauth2.domain.OAuth2Configuration;

/**
 * @author dderose
 *
 */
@Service
public class HttpHelper {
	
	@Autowired
    public OAuth2Configuration oAuth2Configuration;
	
	public HttpPost addHeader(HttpPost post) {
		String base64ClientIdSec = Base64.encodeBase64String((oAuth2Configuration.getAppClientId() + ":" + oAuth2Configuration.getAppClientSecret()).getBytes());
        post.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
        post.setHeader("Authorization", "Basic " + base64ClientIdSec);
        post.setHeader("Accept", "application/json");
        return post;
	}

	public List<NameValuePair> getUrlParameters(HttpSession session, String action) {
		List<NameValuePair> urlParameters = new ArrayList<>();
        String refreshToken = (String)session.getAttribute("refresh_token");
        if (action == "revoke") {
        	urlParameters.add(new BasicNameValuePair("token", refreshToken));
        } else if (action == "refresh") {
        	urlParameters.add(new BasicNameValuePair("refresh_token", refreshToken));
            urlParameters.add(new BasicNameValuePair("grant_type", "refresh_token"));
        } else {
        	String auth_code = (String)session.getAttribute("auth_code");
        	urlParameters.add(new BasicNameValuePair("code", auth_code));
            urlParameters.add(new BasicNameValuePair("redirect_uri", oAuth2Configuration.getAppRedirectUri()));
            urlParameters.add(new BasicNameValuePair("grant_type", "authorization_code"));
        }
		return urlParameters;
	}
	
	public StringBuffer getResult(HttpResponse response) throws IOException {
		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
		    result.append(line);
		}
		return result;
	}

}
