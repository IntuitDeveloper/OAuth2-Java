package com.intuit.developer.sampleapp.oauth2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;

/**
 * @author dderose
 *
 */
@SpringBootApplication
public class Application {

    /**
     * @param args
     */
    public static void main(String[] args) {
    	SpringApplication.run(Application.class, args);
    }
    

}
