package com.demo.resteasy;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * there is no need to use web.xml, use ResteasyServletInitializer instead
 *
 * 文章： https://www.baeldung.com/java-ee-oauth2-implementation
 */
@ApplicationPath("/rest")
public class RestApplication extends Application {
}
