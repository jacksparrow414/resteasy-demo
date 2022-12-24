package com.demo.resteasy;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * there is no need to use web.xml, use ResteasyServletInitializer instead
 *
 * 文章： https://www.baeldung.com/java-ee-oauth2-implementation
 */
@ApplicationPath("/auth")
public class AuthorizationServerApplication extends Application {
}
