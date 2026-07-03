package com.dlms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
/**
 * DLMS — Driving License Management System
 * Main Spring Boot entry point.
 * Extends SpringBootServletInitializer to support JSP rendering via embedded Tomcat.
 */
@SpringBootApplication
public class DlmsApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(DlmsApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(DlmsApplication.class, args);
    }

}
